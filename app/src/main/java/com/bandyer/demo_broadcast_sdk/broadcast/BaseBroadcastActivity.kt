/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.bandyer.demo_broadcast_sdk.broadcast

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bandyer.broadcast_sdk.broadcast.Broadcast
import com.bandyer.broadcast_sdk.broadcast.BroadcastJoinException
import com.bandyer.broadcast_sdk.client.BroadcastClient
import com.bandyer.broadcast_sdk.client.BroadcastClientObserver
import com.bandyer.core_av.BaseUser
import com.bandyer.core_av.OnStreamListener
import com.bandyer.core_av.Stream
import com.bandyer.core_av.capturer.Capturer
import com.bandyer.core_av.capturer.audio.AudioController
import com.bandyer.core_av.capturer.video.VideoController
import com.bandyer.core_av.publisher.BroadcastingException
import com.bandyer.core_av.publisher.BroadcastingListener
import com.bandyer.core_av.publisher.Publisher
import com.bandyer.core_av.publisher.RecordingException
import com.bandyer.core_av.publisher.RecordingListener
import com.bandyer.core_av.room.Room
import com.bandyer.core_av.room.RoomToken
import com.bandyer.core_av.view.OnViewStatusObserver
import com.bandyer.core_av.view.ScaleType
import com.bandyer.core_av.view.StreamView
import com.bandyer.core_av.view.VideoStreamView
import com.bandyer.demo_broadcast_sdk.R
import com.bandyer.demo_broadcast_sdk.broadcast.observers.BroadcastCapturerObserver
import com.bandyer.demo_broadcast_sdk.broadcast.observers.BroadcastLocalPreviewObserver
import com.bandyer.demo_broadcast_sdk.broadcast.observers.BroadcastPublisherObserver
import com.bandyer.demo_broadcast_sdk.broadcast.observers.BroadcastRoomObserver
import com.bandyer.demo_broadcast_sdk.extensions.CameraCapturer
import com.bandyer.demo_broadcast_sdk.extensions.capturer
import com.google.android.material.snackbar.Snackbar

abstract class BaseBroadcastActivity :
        AppCompatActivity(),
        BroadcastCapturerObserver,
        BroadcastRoomObserver,
        BroadcastPublisherObserver,
        BroadcastClientObserver {

    companion object {
        const val TAG = "BroadcastActivity"
        const val BROADCAST_URL = "BROADCAST_URL"
    }

    protected var videoStream: VideoStreamView? = null
    protected var info: TextView? = null
    protected var recordingLabel: View? = null

    protected var capturer: CameraCapturer? = null
    protected var publisher: Publisher? = null
    private var room: Room? = null

    protected var isMicrophoneEnabled = true
    protected var isCameraEnabled = true

    private val reconnectingSnackbar by lazy {
        Snackbar.make(findViewById(android.R.id.content), resources.getString(R.string.reconnecting), Snackbar.LENGTH_INDEFINITE)
    }

    private val broadcastingListener = object: BroadcastingListener {
        override fun onSuccess(broadcastId: String, isBroadcasting: Boolean) {
            info?.text = resources.getString(R.string.on_air)
            info?.visibility = View.VISIBLE
        }

        override fun onError(broadcastId: String?, isBroadcasting: Boolean, reason: BroadcastingException) {
            Toast.makeText(applicationContext, "Broadcasting error: $reason", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private val videoStreamStatusObserver = object : BroadcastLocalPreviewObserver {
        override fun onFirstFrameRendered() {
            onLocalPreviewStarted()
        }
    }

    private var broadcastUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        broadcastUrl = intent.getStringExtra(BROADCAST_URL) ?: kotlin.run {
            finish()
            return
        }

        setContentView(R.layout.activity_broadcast)

        info = findViewById(R.id.info)
        videoStream = findViewById(R.id.video_stream)
        recordingLabel = findViewById(R.id.broadcast_recording)

        createCapturer()
    }

    override fun onDestroy() {
        super.onDestroy()
        capturer?.removeAllObservers()
        capturer?.destroy()
        leaveBroadcast()
    }

    fun startBroadcast() {
        BroadcastClient.get().addObserver(this)
        BroadcastClient.get().join(broadcastUrl!!)
    }

    private fun leaveBroadcast() {
        Toast.makeText(applicationContext, "Broadcast left!", Toast.LENGTH_SHORT).show()
        BroadcastClient.get().leave()
        BroadcastClient.get().removeObserver(this)
    }

    protected open fun createCapturer() {
        capturer = capturer(this) {
            video = camera()
            audio = default()
            observer = this@BaseBroadcastActivity
        }
        capturer!!.start()
    }

    private fun createRoom(roomToken: RoomToken) {
        room = Room.get(roomToken)
        room!!.addRoomObserver(this)
        room!!.join()
    }

    private fun createPublisher(user: BaseUser, capturer: CameraCapturer) {
        room ?: return

        publisher = room!!.create(user)
                .addPublisherObserver(this)
                .setCapturer(capturer)

        publisher!!.setView(videoStream!!, object: OnStreamListener {
            override fun onReadyToPlay(view: StreamView, stream: Stream) {
                view.play(stream)
            }
        })
    }

    abstract fun onLocalPreviewStarted()

    //////////////////////// BROADCAST OBSERVER /////////////////////////////////////////////////////

    override fun onBroadcastJoinSuccess(broadcast: Broadcast) = createRoom(broadcast.roomToken)

    override fun onBroadcastJoinError(reason: BroadcastJoinException) {
        Toast.makeText(applicationContext, "Broadcast error!\n${reason.message}", Toast.LENGTH_SHORT).show()
        finish()
    }

    //////////////////////// CAPTURER OBSERVER /////////////////////////////////////////////////////

    override fun onCapturerStarted(capturer: Capturer<VideoController<*, *>, AudioController>, stream: Stream) {
        videoStream!!.setScaleType(ScaleType.FILL)
        videoStream!!.addViewStatusObserver(videoStreamStatusObserver)
        videoStream!!.play(stream)
    }

    //////////////////////// ROOM OBSERVER /////////////////////////////////////////////////////////

    override fun onRoomEnter() {
        BroadcastClient.get().broadcast ?: return
        reconnectingSnackbar.dismiss()
        createPublisher(BroadcastClient.get().broadcast!!.user, capturer!!)
        room!!.publish(publisher!!)
    }

    override fun onRoomReconnecting() {
        reconnectingSnackbar.show()
    }

    override fun onRoomExit() {
        reconnectingSnackbar.dismiss()
    }

    override fun onRoomError(reason: String) {
        Toast.makeText(applicationContext, "Broadcast room error: $reason", Toast.LENGTH_LONG).show()
        finish()
    }

    //////////////////////// PUBLISHER OBSERVER /////////////////////////////////////////////////////////

    override fun onLocalPublisherJoined(publisher: Publisher) {
        BroadcastClient.get().broadcast ?: return
        publisher.startBroadcast(broadcastingListener)
        if (!BroadcastClient.get().broadcast!!.record) return
        publisher.startRecording(object : RecordingListener {
            override fun onSuccess(recordId: String, isRecording: Boolean) {
                Log.d(TAG, "Broadcast recording started.")
                recordingLabel!!.visibility = View.VISIBLE
            }

            override fun onError(recordId: String?, isRecording: Boolean, reason: RecordingException) {
                Log.d(TAG, "Broadcast recording error: $reason")
            }
        })
    }
}
