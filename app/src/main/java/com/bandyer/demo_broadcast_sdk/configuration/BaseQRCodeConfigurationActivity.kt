/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */

package com.bandyer.demo_broadcast_sdk.configuration

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bandyer.app_configuration.external_configuration.ui.ViewFinderView
import com.bandyer.demo_broadcast_sdk.broadcast.BroadcastActivity
import com.bandyer.demo_broadcast_sdk.R
import com.bandyer.demo_broadcast_sdk.utls.getScreenSize
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.android.synthetic.main.activity_qr_code_configuration.camera_preview
import kotlinx.android.synthetic.main.activity_qr_code_configuration.root
import kotlinx.android.synthetic.main.activity_qr_code_configuration.scan_info
import java.util.concurrent.Executors

abstract class BaseQRCodeConfigurationActivity : AppCompatActivity() {

    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var camera: Camera? = null
    private lateinit var captureSize: Size
    private var hasResult = false
    private var scanOverlayView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_configuration)
        val screenSize = getScreenSize()
        captureSize = Size(screenSize.x, screenSize.y)
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
    }

    override fun onResume() {
        super.onResume()
        if (camera != null) return
        hasResult = false
        if (!allPermissionsGranted())
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        else getCameraProvider()
    }

    private fun getCameraProvider() {
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            startCamera(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun startCamera(cameraProvider: ProcessCameraProvider) {
        val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

        val preview = Preview.Builder().also {
            it.setTargetResolution(captureSize)
        }.build()

        val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(captureSize)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, QrCodeAnalyzer { qrResult ->
                        if (!hasResult && qrResult.text.contains("https")) {
                            hasResult = true
                            camera_preview.post {
                                openBroadcastActivity(qrResult.text)
                            }
                        }
                    })
                }

        cameraProvider.unbindAll()

        camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
        preview.setSurfaceProvider(camera_preview.surfaceProvider)

        if (scanOverlayView == null) {
            scanOverlayView = ViewFinderView(this)
            root.addView(scanOverlayView, root.indexOfChild(scan_info))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted())
                getCameraProvider()
            else {
                Toast.makeText(
                        this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun openBroadcastActivity(broadcastUrl: String) {
        cameraProviderFuture.get().unbindAll()
        camera = null
        BroadcastActivity.show(this@BaseQRCodeConfigurationActivity, broadcastUrl)
    }
}