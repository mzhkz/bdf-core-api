package net.bdfps.api.spigot.event.network

import net.bdfps.api.spigot.event.base.BaseCancellableEvent
import net.bdfps.api.spigot.network.event.BDFEvent

open class BDFNetworkEvent(val source: BDFEvent) : BaseCancellableEvent() {

    class BDFNetworkKickEvent(source: BDFEvent) : BDFNetworkEvent(source)
}
