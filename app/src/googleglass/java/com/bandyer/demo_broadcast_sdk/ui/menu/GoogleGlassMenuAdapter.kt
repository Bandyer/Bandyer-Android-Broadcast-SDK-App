/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */

package com.bandyer.demo_broadcast_sdk.ui.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bandyer.demo_broadcast_sdk.R
import com.bandyer.demo_broadcast_sdk.ui.menu.GoogleGlassMenuAdapter.MenuItemViewHolder
import com.bandyer.demo_broadcast_sdk.ui.menu.model.GoogleGlassMenuItem
import kotlinx.android.synthetic.googleglass.glass_menu_item.view.*

/**
 * Adapter for the menu horizontal recycler view.
 */
class GoogleGlassMenuAdapter internal constructor(private val menuItems: List<GoogleGlassMenuItem>) : RecyclerView.Adapter<MenuItemViewHolder>() {

    var itemItemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.glass_menu_item, parent, false)
        return MenuItemViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        holder.bind(menuItems[position])
    }

    override fun getItemCount(): Int {
        return menuItems.size
    }

    inner class MenuItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        fun bind(googleGlassMenuItem: GoogleGlassMenuItem) {
            itemView.icon.background = ContextCompat.getDrawable(itemView.context!!, googleGlassMenuItem.resIcon)
            itemView.text.text = itemView.context.resources.getString(googleGlassMenuItem.resText)
        }

        override fun onClick(v: View?) {
            itemItemClickListener?.onItemClick(adapterPosition, v)
        }

        override fun onLongClick(v: View?): Boolean {
            itemItemClickListener?.onItemLongClick(adapterPosition, v)
            return false
        }
    }

    interface ItemClickListener {
        fun onItemClick(position: Int, v: View?)
        fun onItemLongClick(position: Int, v: View?)
    }
}