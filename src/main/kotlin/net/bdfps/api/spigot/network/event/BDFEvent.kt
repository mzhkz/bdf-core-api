package net.bdfps.api.spigot.network.event

import net.bdf.api.data.Mobject
import net.bdf.api.data.MobjectParser
import net.bdf.api.data.utilty.currentUnixTime
import net.bdfps.api.spigot.BDFConfig
import net.bdfps.api.spigot.event.network.BDFNetworkEvent
import net.bdfps.api.spigot.network.BDFAPIv1
import net.bdfps.api.spigot.network.RequestMethod
import net.bdfps.api.spigot.utility.callEvent
import net.bdfps.api.spigot.utility.sendConsoleMessage
import org.bukkit.ChatColor

class BDFEvent {
    var requestId = ""
    var type = EventType.None
    var body: Mobject = Mobject()
    var timestamp = currentUnixTime
    var server = "central.bdfps.net"
    var user = "central.bdfps.net"

    /* イベント呼び出し **/
    fun callEvent() {
        BDFAPIv1.async(RequestMethod.POST,
                "/stream/event",
                body = json()) { res, error ->
            if (error) {
                val message = res.get("message").toString
                sendConsoleMessage(ChatColor.RED.toString() + "イベント呼び出し時にエラーが発生しました: $message")
                return@async
            }
        }
    }

    fun receiveEvent() {
        when (type) {
            EventType.Kick -> BDFNetworkEvent.BDFNetworkKickEvent(this).callEvent()
            else -> {}
        }
    }


    /** Jsonで出力する */
    private fun json(): String {
        return Mobject().apply {
            "request_id" mo requestId
            "type" mo type.name
            "timestamp" mo timestamp
            "body" mo body
            "server" mo server
            "user" mo user
        }.json()
    }

    /** Jsonのデータを読み込む */
    fun json(json: String): BDFEvent {
        json(MobjectParser.mobject<Mobject>(json))
        return this
    }

    /** Jsonのデータを読み込む */
    fun json(mox: Mobject) {
        requestId = mox.get("request_id").toString
        type = EventType.valueOf(mox.get("type").toString)
        body = mox.get("body").to()
        timestamp = mox.get("timestamp").toLong
        server = mox.get("server").toString
        user = mox.get("user").toString
    }

    enum class EventType {
        FriendRequest,
        FriendRequestAccept,
        Ban,
        Mute,
        Kick,
        None,
    }
}
