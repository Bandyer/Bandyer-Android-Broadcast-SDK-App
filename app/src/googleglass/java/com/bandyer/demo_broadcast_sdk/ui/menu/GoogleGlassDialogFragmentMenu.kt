/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */

package com.bandyer.demo_broadcast_sdk.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bandyer.demo_broadcast_sdk.R
import com.bandyer.demo_broadcast_sdk.ui.menu.model.GoogleGlassMenuItem
import java.util.*

class GoogleGlassDialogFragmentMenu : DialogFragment() {

    private var items: List<GoogleGlassMenuItem>? = null
    private var googleGlassSwipableMenu: GoogleGlassSwipableMenu? = null
        set(value) {
            field = value
            field ?: return
            itemClickListener?.let {
                field!!.onGoogleGlassMenuItemSelectionListener = it
            }
        }

    companion object {
        private const val ITEMS = "items"

        fun newInstance(items: List<GoogleGlassMenuItem>): GoogleGlassDialogFragmentMenu {
            val dialogFragment = GoogleGlassDialogFragmentMenu()

            dialogFragment.arguments = Bundle().apply {
                this.putSerializable(ITEMS, ArrayList<GoogleGlassMenuItem>().apply { this.addAll(items) })
            }

            return dialogFragment
        }
    }

    var itemClickListener: GoogleGlassSwipableMenu.OnGoogleGlassMenuItemSelectionListener? = null
        set(value) {
            field = value
            googleGlassSwipableMenu?.onGoogleGlassMenuItemSelectionListener = itemClickListener
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
        val itemsArrayList = arguments?.getSerializable(ITEMS)
        items = mutableListOf<GoogleGlassMenuItem>().apply {
            this.addAll(itemsArrayList as ArrayList<GoogleGlassMenuItem>)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        googleGlassSwipableMenu = GoogleGlassSwipableMenu(
                context!!,
                items!!)

        return googleGlassSwipableMenu!!
    }

    override fun onDestroy() {
        super.onDestroy()
        items = null
        googleGlassSwipableMenu = null
        itemClickListener = null
    }
}