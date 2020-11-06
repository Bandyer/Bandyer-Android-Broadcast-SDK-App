/*
 *  Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 *  See LICENSE.txt for licensing information
 */

package com.bandyer.demo_broadcast_sdk.broadcast.observers

import com.bandyer.core_av.Stream
import com.bandyer.core_av.publisher.Publisher
import com.bandyer.core_av.room.RoomActor
import com.bandyer.core_av.room.RoomObserver
import com.bandyer.core_av.room.RoomState
import com.bandyer.core_av.subscriber.Subscriber

interface BroadcastRoomObserver : RoomObserver {
    override fun onRoomStateChanged(state: RoomState) = Unit
    override fun onRoomActorUpdateStream(roomActor: RoomActor) = Unit
    override fun onRemotePublisherJoined(stream: Stream) = Unit
    override fun onRemotePublisherLeft(stream: Stream) = Unit
    override fun onRemotePublisherUpdateStream(stream: Stream) = Unit
    override fun onLocalPublisherRemoved(publisher: Publisher) = Unit
    override fun onLocalSubscriberJoined(subscriber: Subscriber) = Unit
    override fun onLocalPublisherUpdateStream(publisher: Publisher) = Unit
    override fun onLocalSubscriberRemoved(subscriber: Subscriber) = Unit
    override fun onLocalSubscriberUpdateStream(subscriber: Subscriber) = Unit
}