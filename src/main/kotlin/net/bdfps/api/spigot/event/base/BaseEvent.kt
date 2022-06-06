package net.bdfps.api.spigot.event.base

import net.bdfps.api.spigot.utility.callEvent
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

abstract class BaseEvent : Event() {

    companion object {
        private val handler: HandlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handler
    }

    override fun getHandlers(): HandlerList = handler

}
