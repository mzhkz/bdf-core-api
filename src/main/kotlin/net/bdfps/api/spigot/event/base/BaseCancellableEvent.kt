package net.bdfps.api.spigot.event.base

import org.bukkit.event.Cancellable


abstract class BaseCancellableEvent : BaseEvent(), Cancellable {

    private var isCancelled = false

    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(cancel: Boolean) {
        isCancelled = cancel
    }

    fun cancel() {
        isCancelled = true
    }
}
