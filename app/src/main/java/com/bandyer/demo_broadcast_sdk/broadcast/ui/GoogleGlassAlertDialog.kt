/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */

package com.bandyer.demo_broadcast_sdk.broadcast.ui

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.view.MotionEvent
import androidx.appcompat.app.AlertDialog
import com.bandyer.demo_broadcast_sdk.R
import com.bandyer.demo_broadcast_sdk.broadcast.ui.utils.GlassGestureDetector

class GoogleGlassAlertDialog(context: Context,
                             message: String,
                             confirmCallback: (() -> Unit),
                             cancelCallback: (() -> Unit)) : AlertDialog(context) {

    private val glassGestureDetector: GlassGestureDetector = GlassGestureDetector(
            context,
            object : GlassGestureDetector.OnGestureListener {
                override fun onGesture(gesture: GlassGestureDetector.Gesture): Boolean {
                    when (gesture) {
                        GlassGestureDetector.Gesture.TAP -> {
                            (getButton(DialogInterface.BUTTON_POSITIVE)
                                    .takeIf { it.isFocused }
                                    ?: getButton(DialogInterface.BUTTON_NEGATIVE)
                                            .takeIf { it.isFocused })
                                    ?.performClick()
                            return true
                        }
                        GlassGestureDetector.Gesture.SWIPE_FORWARD -> {
                            getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(Color.TRANSPARENT)
                            getButton(DialogInterface.BUTTON_POSITIVE).apply {
                                isFocusable = true
                                isFocusableInTouchMode = true
                                requestFocus()
                                setBackgroundColor(Color.LTGRAY)
                            }
                            return true
                        }
                        GlassGestureDetector.Gesture.SWIPE_BACKWARD -> {
                            getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(Color.TRANSPARENT)
                            getButton(DialogInterface.BUTTON_NEGATIVE).apply {
                                isFocusable = true
                                isFocusableInTouchMode = true
                                requestFocus()
                                setBackgroundColor(Color.LTGRAY)
                            }
                            return true
                        }
                        GlassGestureDetector.Gesture.SWIPE_DOWN -> {
                            dismiss()
                            return true
                        }
                        GlassGestureDetector.Gesture.SWIPE_UP -> {
                            return true
                        }
                        else -> return false
                    }
                }
            })

    init {
        setMessage(message)
        setButton(DialogInterface.BUTTON_POSITIVE, context.getText(R.string.dialog_confirm_button)) { dialog, _ ->
            dialog.dismiss()
            confirmCallback.invoke()
        }
        setButton(DialogInterface.BUTTON_NEGATIVE, context.getText(R.string.dialog_cancel_button)) { dialog, _ ->
            dialog.dismiss()
            cancelCallback.invoke()
        }
        setOnCancelListener { cancelCallback.invoke() }
        setOnDismissListener { cancelCallback.invoke() }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        glassGestureDetector.onTouchEvent(event)
        return true
    }
}