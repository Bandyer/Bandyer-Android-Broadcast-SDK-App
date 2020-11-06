/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */
package com.bandyer.demo_broadcast_sdk

import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import androidx.multidex.MultiDexApplication
import com.bandyer.broadcast_sdk.BroadcastCenter
import com.bandyer.broadcast_sdk.Environment

class DemoApplication : MultiDexApplication(), CameraXConfig.Provider {

    override fun onCreate() {
        super.onCreate()
        BroadcastCenter.init(BroadcastCenter.Builder(this, resources.getString(R.string.app_id)).setEnvironment(Environment.sandbox()))
    }

    override fun getCameraXConfig() = Camera2Config.defaultConfig()
}