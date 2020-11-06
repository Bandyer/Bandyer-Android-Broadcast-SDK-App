/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.bandyer.demo_broadcast_sdk.broadcast

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.FragmentManager
import com.bandyer.core_av.capturer.video.provider.FrameQuality
import com.bandyer.demo_broadcast_sdk.R
import com.bandyer.demo_broadcast_sdk.ui.menu.GoogleGlassDialogFragmentMenu
import com.bandyer.demo_broadcast_sdk.ui.menu.model.GoogleGlassMenuItem
import com.bandyer.demo_broadcast_sdk.broadcast.ui.utils.GlassGestureDetector
import com.bandyer.demo_broadcast_sdk.ui.menu.GoogleGlassSwipableMenu
import com.bandyer.demo_broadcast_sdk.utls.displayConfirmationAlert
import kotlinx.android.synthetic.googleglass.activity_broadcast.camera_icon
import kotlinx.android.synthetic.googleglass.activity_broadcast.mic_icon

class BroadcastActivity : BaseBroadcastActivity() {

    companion object {

        const val LONG_PRESS_START_BROADCAST_DURATION = 3000L

        fun show(fromActivity: Activity, broadcastUrl: String) {
            fromActivity.startActivity(Intent(fromActivity, BroadcastActivity::class.java).apply {
                putExtra(BROADCAST_URL, broadcastUrl)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info!!.text = resources.getString(R.string.long_press_to_start_broadcast)
    }

    private fun openMenu() {
        capturer ?: return

        val fm: FragmentManager = supportFragmentManager

        val googleGlassDialogFragmentMenu =
                GoogleGlassDialogFragmentMenu.newInstance(
                        listOf(
                                if (!isMicrophoneEnabled) GoogleGlassMenuItem.EnableMicrophoneGoogleGlassMenuItem()
                                else GoogleGlassMenuItem.DisableMicrophoneGoogleGlassMenuItem(),
                                if (!isCameraEnabled) GoogleGlassMenuItem.EnableCameraGoogleGlassMenuItem()
                                else GoogleGlassMenuItem.DisableCameraGoogleGlassMenuItem()))

        googleGlassDialogFragmentMenu.itemClickListener = object : GoogleGlassSwipableMenu.OnGoogleGlassMenuItemSelectionListener {
            override fun onSelected(itemGoogle: GoogleGlassMenuItem) {
                googleGlassDialogFragmentMenu.dismiss()
                when (itemGoogle) {
                    is GoogleGlassMenuItem.DisableMicrophoneGoogleGlassMenuItem -> {
                        isMicrophoneEnabled = false
                        capturer!!.audio!!.audioEnabled = false
                        mic_icon.setImageResource(R.drawable.mic_off)
                    }
                    is GoogleGlassMenuItem.EnableMicrophoneGoogleGlassMenuItem -> {
                        isMicrophoneEnabled = true
                        capturer!!.audio!!.audioEnabled = true
                        mic_icon.setImageResource(R.drawable.mic)
                    }
                    is GoogleGlassMenuItem.DisableCameraGoogleGlassMenuItem -> {
                        isCameraEnabled = false
                        capturer!!.video!!.videoEnabled = false
                        camera_icon.setImageResource(R.drawable.camera_off)
                    }
                    is GoogleGlassMenuItem.EnableCameraGoogleGlassMenuItem -> {
                        isCameraEnabled = true
                        capturer!!.video!!.videoEnabled = true
                        camera_icon.setImageResource(R.drawable.camera)
                    }
                }
            }

            override fun onDismissRequested() {
                googleGlassDialogFragmentMenu.dismiss()
            }
        }
        googleGlassDialogFragmentMenu.show(fm, "googleGlassDialogFragmentMenu")
    }

    override fun onDestroy() {
        super.onDestroy()
        startBroadcastLongPressHandler.removeCallbacksAndMessages(null)
    }

    override fun createCapturer() {
        super.createCapturer()
        with(capturer!!.video!!) {
            val quality = frameProvider.getNearestCaptureQualitySupported(FrameQuality(640, 480, 15))
            this.frameProvider.frameQuality = quality
            this.frameDispatcher?.frameQuality = quality
        }
    }

    override fun onLocalPreviewStarted() {
        info!!.visibility = View.VISIBLE
        mic_icon.visibility = View.VISIBLE
        camera_icon.visibility = View.VISIBLE
    }

    ////////////////////////////////////////////// TOUCHES ///////////////////////////////////////////////////////////////////////

    private val startBroadcastLongPressHandler = Handler(Looper.getMainLooper())

    private val startBroadcastLongPressListener = {
        displayConfirmationAlert(
                this,
                R.string.dialog_start_broadcast,
                confirmCallback = {
                    info!!.text = resources.getString(R.string.broadcast_connecting)
                    startBroadcast()
                },
                cancelCallback = {}
        )
    }

    private val glassGestureDetector: GlassGestureDetector by lazy {
        GlassGestureDetector(this, object : GlassGestureDetector.OnGestureListener {
            override fun onGesture(gesture: GlassGestureDetector.Gesture): Boolean {
                when (gesture) {
                    GlassGestureDetector.Gesture.TAP ->
                        return true
                    GlassGestureDetector.Gesture.SWIPE_FORWARD -> {
                        openMenu()
                        return true
                    }
                    GlassGestureDetector.Gesture.SWIPE_BACKWARD ->
                        return true
                    GlassGestureDetector.Gesture.SWIPE_DOWN -> {
                        displayConfirmationAlert(
                                this@BroadcastActivity,
                                R.string.dialog_end_broadcast,
                                confirmCallback = { onBackPressed() },
                                cancelCallback = {}
                        )
                        return true
                    }
                    GlassGestureDetector.Gesture.SWIPE_UP -> {
                        return true
                    }
                    else -> return false
                }
            }
        })
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev ?: return false

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> startBroadcastLongPressHandler.postDelayed(
                    startBroadcastLongPressListener,
                    LONG_PRESS_START_BROADCAST_DURATION
            )
            MotionEvent.ACTION_UP -> startBroadcastLongPressHandler.removeCallbacksAndMessages(null)
            MotionEvent.ACTION_CANCEL -> startBroadcastLongPressHandler.removeCallbacksAndMessages(null)
        }

        return glassGestureDetector.onTouchEvent(ev)
    }
}
