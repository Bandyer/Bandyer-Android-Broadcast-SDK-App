/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */

package com.bandyer.demo_broadcast_sdk.configuration

import android.view.MotionEvent
import com.bandyer.demo_broadcast_sdk.broadcast.ui.utils.GlassGestureDetector
import com.bandyer.demo_broadcast_sdk.utls.isGoogleGlassDevice

class QRCodeConfigurationActivity : BaseQRCodeConfigurationActivity() {

    private var glassGestureOnGestureListener = object : GlassGestureDetector.OnGestureListener {
        override fun onGesture(gesture: GlassGestureDetector.Gesture): Boolean {
            when (gesture) {
                GlassGestureDetector.Gesture.TAP ->
                    // Response for TAP gesture
                    return true
                GlassGestureDetector.Gesture.SWIPE_FORWARD ->
                    // Response for SWIPE_FORWARD gesture
                    return true
                GlassGestureDetector.Gesture.SWIPE_BACKWARD ->
                    // Response for SWIPE_BACKWARD gesture
                    return true
                GlassGestureDetector.Gesture.SWIPE_DOWN -> {
                    onBackPressed()
                    return true
                }
                GlassGestureDetector.Gesture.SWIPE_UP -> {
                    return true
                }
                else -> return false
            }
        }
    }

    private val glassGestureDetector: GlassGestureDetector by lazy {
        GlassGestureDetector(this, glassGestureOnGestureListener)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev ?: return false

        return if (isGoogleGlassDevice())
            glassGestureDetector.onTouchEvent(ev)
        else super.dispatchTouchEvent(ev)
    }
}