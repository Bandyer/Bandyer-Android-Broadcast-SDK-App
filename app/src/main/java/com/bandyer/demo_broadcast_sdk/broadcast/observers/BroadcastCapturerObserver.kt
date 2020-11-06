/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */

package com.bandyer.demo_broadcast_sdk.broadcast.observers

import com.bandyer.core_av.capturer.Capturer
import com.bandyer.core_av.capturer.CapturerException
import com.bandyer.core_av.capturer.CapturerObserver
import com.bandyer.core_av.capturer.audio.AudioController
import com.bandyer.core_av.capturer.video.VideoController

interface BroadcastCapturerObserver : CapturerObserver<VideoController<*, *>, AudioController> {
    override fun onCapturerResumed(capturer: Capturer<VideoController<*, *>, AudioController>) = Unit
    override fun onCapturerPaused(capturer: Capturer<VideoController<*, *>, AudioController>) = Unit
    override fun onCapturerDestroyed(capturer: Capturer<VideoController<*, *>, AudioController>) = Unit
    override fun onCapturerError(capturer: Capturer<VideoController<*, *>, AudioController>, error: CapturerException) = Unit
}