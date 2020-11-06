/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */

package com.bandyer.demo_broadcast_sdk.broadcast.observers

import android.graphics.Bitmap
import com.bandyer.core_av.view.OnViewStatusObserver

interface BroadcastLocalPreviewObserver : OnViewStatusObserver{
    override fun onViewSizeChanged(width: Int, height: Int, rotationDegree: Int) = Unit
    override fun onFrameCaptured(bitmap: Bitmap?) = Unit
    override fun onRenderingStarted() = Unit
    override fun onRenderingStopped() = Unit
    override fun onRenderingPaused() = Unit
}