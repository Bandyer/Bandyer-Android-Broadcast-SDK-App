/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */

package com.bandyer.demo_broadcast_sdk.ui.menu.model

import com.bandyer.demo_broadcast_sdk.R
import java.util.*

sealed class GoogleGlassMenuItem constructor(val resIcon: Int, val resText: Int) {

    class DisableCameraGoogleGlassMenuItem : GoogleGlassMenuItem(R.drawable.camera_off, R.string.disable_camera)
    class DisableMicrophoneGoogleGlassMenuItem : GoogleGlassMenuItem(R.drawable.mic_off, R.string.disable_microphone)
    class EnableCameraGoogleGlassMenuItem : GoogleGlassMenuItem(R.drawable.camera, R.string.enable_camera)
    class EnableMicrophoneGoogleGlassMenuItem : GoogleGlassMenuItem(R.drawable.mic, R.string.enable_microphone)

    override fun hashCode(): Int {
        return Objects.hash(resIcon, resText)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is GoogleGlassMenuItem) {
            return false
        }
        return resIcon == other.resIcon &&
                resText == other.resText
    }

    override fun toString(): String {
        return "$resIcon $resText"
    }
}