package net.bdfps.api.spigot.network.event

import com.launchdarkly.eventsource.EventHandler
import com.launchdarkly.eventsource.MessageEvent
import net.bdfps.api.spigot.utility.sendConsoleMessage
import net.bdf.api.data.Mobject
import net.bdf.api.data.MobjectParser
import org.bukkit.ChatColor


class BDFEventHandler: EventHandler {

    override fun onOpen() {
    }

    override fun onClosed() {
    }

    override fun onComment(comment: String?) {
    }

    override fun onError(t: Throwable?) {
    }

    override fun onMessage(event: String, messageEvent: MessageEvent) {
        val data = messageEvent.data
        sendConsoleMessage(ChatColor.DARK_AQUA.toString() + data)
        when (data) {
            "----- connection keeping -----" -> return //接続維持用
            else ->  BDFEvent().json(data).receiveEvent()
        }
    }
}
