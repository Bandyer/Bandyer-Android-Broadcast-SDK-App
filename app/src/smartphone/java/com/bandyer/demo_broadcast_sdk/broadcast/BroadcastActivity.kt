/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */

package com.bandyer.demo_broadcast_sdk.broadcast

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bandyer.core_av.Stream
import com.bandyer.core_av.capturer.CameraCapturer
import com.bandyer.core_av.capturer.Capturer
import com.bandyer.core_av.capturer.audio.AudioController
import com.bandyer.core_av.capturer.video.VideoController
import com.bandyer.core_av.capturer.video.provider.FrameQuality
import com.bandyer.core_av.capturer.video.provider.camera.CameraFeederChangeListener
import com.bandyer.core_av.capturer.video.provider.camera.CameraFrameProvider
import com.bandyer.core_av.capturer.video.provider.camera.CameraVideoFeeder
import com.bandyer.demo_broadcast_sdk.R
import com.bandyer.demo_broadcast_sdk.utls.displayConfirmationAlert
import kotlinx.android.synthetic.smartphone.activity_broadcast.camera
import kotlinx.android.synthetic.smartphone.activity_broadcast.microphone
import kotlinx.android.synthetic.smartphone.activity_broadcast.start_broadcast
import kotlinx.android.synthetic.smartphone.activity_broadcast.switch_camera
import kotlinx.android.synthetic.smartphone.activity_broadcast.video_stream

class BroadcastActivity : BaseBroadcastActivity(), CameraFeederChangeListener {

    companion object {
        fun show(fromActivity: Activity, broadcastUrl: String) {
            fromActivity.startActivity(Intent(fromActivity, BroadcastActivity::class.java).apply {
                putExtra(BROADCAST_URL, broadcastUrl)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initClickListeners()
    }

    private fun initClickListeners() {
        start_broadcast.setOnClickListener {
            displayConfirmationAlert(
                    this,
                    R.string.dialog_start_broadcast,
                    confirmCallback = {
                        start_broadcast.visibility = View.GONE
                        camera.visibility = View.VISIBLE
                        microphone.visibility = View.VISIBLE
                        switch_camera.visibility = View.VISIBLE
                        info!!.text = resources.getString(R.string.broadcast_connecting)
                        info!!.visibility = View.VISIBLE
                        startBroadcast()
                    },
                    cancelCallback = {})
        }

        camera.setOnClickListener {
            isCameraEnabled = !isCameraEnabled
            camera.setImageResource(if (!isCameraEnabled) R.drawable.camera else R.drawable.camera_off)
            capturer!!.video!!.videoEnabled = isCameraEnabled
        }

        microphone.setOnClickListener {
            isMicrophoneEnabled = !isMicrophoneEnabled
            microphone.setImageResource(if (!isMicrophoneEnabled) R.drawable.mic else R.drawable.mic_off)
            capturer!!.audio!!.audioEnabled = isMicrophoneEnabled
        }

        switch_camera.setOnClickListener {
            capturer?.video?.frameProvider?.switchVideoFeeder(this)
        }
    }

    override fun onBackPressed() {
        displayConfirmationAlert(
                this,
                R.string.dialog_end_broadcast,
                confirmCallback = {
                    super.onBackPressed()
                },
                cancelCallback = {})
    }

    override fun onCapturerStarted(capturer: Capturer<VideoController<*, *>, AudioController>, stream: Stream) {
        super.onCapturerStarted(capturer, stream)
        start_broadcast.isEnabled = true
        videoStream!!.setMirror((capturer.video?.frameProvider as? CameraFrameProvider)?.currentCameraFeeder is CameraVideoFeeder.FRONT_CAMERA)
    }

    override fun onLocalPreviewStarted() = Unit

    override fun onFeederChanged(feeder: CameraVideoFeeder, switched: Boolean) {
        video_stream.setMirror(feeder is CameraVideoFeeder.FRONT_CAMERA)
    }
}


