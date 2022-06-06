package net.bdfps.api.spigot.network

import net.bdf.api.data.Mobject
import net.bdfps.api.spigot.network.event.BDFEventAPIv1

object BDFAPIv1Handler {

    fun connect() {
        OAuthAuthorize.syncAuthorize() //初回のアクセストークンを取得
        OAuthAuthorize.enableUpdater() //タスクを動かす
        BDFEventAPIv1.connect() //イベント受信用の通信
    }
}

enum class APIRoot(val path: String) {
    USER("/users"),
    SERVER("/servers"),
    MATCH("/matches"),
    CLAN("/clans");

    override fun toString(): String = path
}

enum class RequestMethod {
    GET,POST,PUT,PATCH,DELETE
}

class BDFAPIv1RequestException(override val message: String, val response: Mobject) : Exception(message)
