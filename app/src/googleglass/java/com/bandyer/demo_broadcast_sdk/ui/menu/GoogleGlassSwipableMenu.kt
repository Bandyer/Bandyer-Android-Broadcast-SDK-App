/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */

package com.bandyer.demo_broadcast_sdk.ui.menu

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bandyer.demo_broadcast_sdk.R
import com.bandyer.demo_broadcast_sdk.ui.menu.model.GoogleGlassMenuItem
import com.bandyer.demo_broadcast_sdk.broadcast.ui.utils.GlassGestureDetector
import com.bandyer.demo_broadcast_sdk.utls.isGoogleGlassDevice

@SuppressLint("ViewConstructor")
class GoogleGlassSwipableMenu(
        context: Context,
        private val items: List<GoogleGlassMenuItem>) : LinearLayout(context) {

    interface OnGoogleGlassMenuItemSelectionListener {
        fun onSelected(itemGoogle: GoogleGlassMenuItem)
        fun onDismissRequested()
    }

    private val recyclerView = RecyclerView(context)
    private val adapter: GoogleGlassMenuAdapter = GoogleGlassMenuAdapter(items)
    private var currentMenuItemIndex = 0
    var onGoogleGlassMenuItemSelectionListener: OnGoogleGlassMenuItemSelectionListener? = null

    private val glassGestureDetector: GlassGestureDetector = GlassGestureDetector(
            context,
            object : GlassGestureDetector.OnGestureListener {
                override fun onGesture(gesture: GlassGestureDetector.Gesture): Boolean {
                    return when (gesture) {
                        GlassGestureDetector.Gesture.TAP -> {
                            onGoogleGlassMenuItemSelectionListener?.onSelected(items[currentMenuItemIndex])
                            true
                        }
                        GlassGestureDetector.Gesture.SWIPE_DOWN -> {
                            val currentOnGoogleGlassMenuItemSelectionListener = onGoogleGlassMenuItemSelectionListener
                            dispose()
                            currentOnGoogleGlassMenuItemSelectionListener?.onDismissRequested()
                            true
                        }
                        else -> true
                    }
                }
            })

    init {
        setBackgroundColor(ContextCompat.getColor(context, R.color.semi_transparent))
        addView(recyclerView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.isFocusable = true

        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val foundView = snapHelper.findSnapView(layoutManager) ?: return
                currentMenuItemIndex = layoutManager.getPosition(foundView)
            }
        })

        if (!isGoogleGlassDevice()) {
            adapter.itemItemClickListener = object : GoogleGlassMenuAdapter.ItemClickListener {
                override fun onItemClick(position: Int, v: View?) {
                    onGoogleGlassMenuItemSelectionListener?.onSelected(items[currentMenuItemIndex])
                }

                override fun onItemLongClick(position: Int, v: View?) {
                    onGoogleGlassMenuItemSelectionListener?.onSelected(items[currentMenuItemIndex])
                }
            }
        }
    }

    private fun dispose() {
        onGoogleGlassMenuItemSelectionListener = null
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        glassGestureDetector.onTouchEvent(event)
        return super.dispatchTouchEvent(event)
    }
}