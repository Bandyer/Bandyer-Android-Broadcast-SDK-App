/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */

package com.bandyer.demo_broadcast_sdk.broadcast.observers

import com.bandyer.core_av.publisher.Publisher
import com.bandyer.core_av.publisher.PublisherObserver
import com.bandyer.core_av.publisher.PublisherState

interface BroadcastPublisherObserver: PublisherObserver {
    override fun onLocalPublisherAdded(publisher: Publisher) = Unit
    override fun onLocalPublisherAudioMuted(publisher: Publisher, muted: Boolean) = Unit
    override fun onLocalPublisherConnected(publisher: Publisher, connected: Boolean) = Unit
    override fun onLocalPublisherError(publisher: Publisher, reason: String) = Unit
    override fun onLocalPublisherStateChanged(publisher: Publisher, state: PublisherState) = Unit
    override fun onLocalPublisherVideoMuted(publisher: Publisher, muted: Boolean) = Unit
}