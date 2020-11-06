/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */

package com.bandyer.demo_broadcast_sdk.utls

import android.app.AlertDialog
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import com.bandyer.demo_broadcast_sdk.R
import com.bandyer.demo_broadcast_sdk.broadcast.ui.GoogleGlassAlertDialog
import java.nio.ByteBuffer

/**
 * Checks if running device is a Google Glass device
 * @return Boolean true if Google Glass else otherwise
 */
fun isGoogleGlassDevice(): Boolean = Build.DEVICE == "glass_v3"

/**
 * Displays alert message with confirmation and cancel callbacks
 * @param context Context
 * @param messageRes Int message int resource
 * @param confirmCallback Function0<Unit> confirm callback
 * @param cancelCallback Function0<Unit> cancel callback
 */
fun displayConfirmationAlert(context: Context, messageRes: Int, confirmCallback: (() -> Unit), cancelCallback: (() -> Unit)) {
    if (!isGoogleGlassDevice())
        AlertDialog.Builder(context)
            .setMessage(context.getString(messageRes))
            .setPositiveButton(R.string.dialog_confirm_button) { dialogInterface, _ ->
                dialogInterface.dismiss()
                confirmCallback.invoke()
            }
            .setNegativeButton(R.string.dialog_cancel_button) { dialogInterface, _ ->
                dialogInterface.dismiss()
                cancelCallback.invoke()
            }
            .setOnDismissListener {
                cancelCallback.invoke()
            }.show()
    else
        GoogleGlassAlertDialog(context, context.resources.getString(messageRes), confirmCallback, cancelCallback).show()
}

/**
 * Calculates screen's size
 * @receiver Context
 * @return Point
 */
fun Context.getScreenSize(): Point {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val size = Point()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        display.getRealSize(size)
    } else {
        display.getSize(size)
    }
    return size
}

/**
 * Converts ByteByffer to ByteArray
 * @receiver ByteBuffer
 * @return ByteArray
 */
fun ByteBuffer.toByteArray(): ByteArray {
    rewind()
    val data = ByteArray(remaining())
    get(data)
    return data
}