/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */

package com.bandyer.demo_broadcast_sdk.extensions

import androidx.appcompat.app.AppCompatActivity
import com.bandyer.core_av.capturer.Capturer
import com.bandyer.core_av.capturer.CapturerFactory
import com.bandyer.core_av.capturer.audio.AudioController
import com.bandyer.core_av.capturer.video.VideoController
import com.bandyer.core_av.capturer.video.dispatcher.FrameDispatcher
import com.bandyer.core_av.capturer.video.provider.camera.CameraFrameProvider
import com.bandyer.core_av.capturer.video.provider.screen.ScreenFrameProvider

/**
 * @suppress
 * A capturer for the camera device
 */
interface CameraCapturer : Capturer<VideoController<CameraFrameProvider, FrameDispatcher>, AudioController>

/**
 * @suppress
 * A capturer for the screen
 */
interface ScreenCapturer : Capturer<VideoController<ScreenFrameProvider, FrameDispatcher>, AudioController>

/**
 * @suppress
 * utility
 */
inline fun <reified T : Capturer<*, *>> capturer(context: AppCompatActivity, noinline block: CapturerFactory.() -> Unit): T = CapturerFactory(context, block).create(T::class)