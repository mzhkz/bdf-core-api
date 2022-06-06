@file:JvmName("ChatUtility")
package net.bdfps.api.spigot.utility

import net.bdf.api.data.abc.MoUser
import org.bukkit.Bukkit
import org.bukkit.ChatColor


fun second(timer: Float): Float {
    val sec = timer / 20
    var s = (sec * 10).toInt().toFloat()
    s /= 10f
    return s
}

/**
 * 日本語変換
 * @param text 変換するテキスト
 */
fun japanease(text: String): String {
    val nihongo = net.bdfps.api.spigot.java.apis.japanese.KanaConverter.conv(text)
    return net.bdfps.api.spigot.java.apis.japanese.IMEConverter.convByGoogleIME(nihongo)
}

/**
 * @return 改行コード
 */
fun newLine(): String {
    return "\n"
}

/**
 * @param title いれこむもじ
 * 指定されたフォーマットのPrefixを取得する
 */
fun getPrefix(title: String): String =
        getPrefix(title, ChatColor.GRAY)

/**
 * @param title いれこむもじ
 * @param color デザインのメインカラー
 * 指定されたフォーマットのPrefixを取得する
 */
fun getPrefix(title: String, color: ChatColor): String =
        color.toString() + ChatColor.BOLD + "*" + title + "*" + ChatColor.RESET + " "

/**
 * @param message
 */
fun broadcast(message: String) {
    sendMessageToAll(message)
    sendConsoleMessage(message)
}

/**
 * 全てのプレイヤーにメッセージを送信
 * @param message
 */
fun sendMessageToAll(message: String) {
    for (info in Bukkit.getOnlinePlayers()) {
        info.sendMessage(message)
    }
}

/**
 * allow use color code
 * @param message
 */
fun sendConsoleMessage(message: String) {
    Bukkit.getConsoleSender().sendMessage(message)
}

fun MoUser.BDFPermission.getColor(): ChatColor {
    return ChatColor.valueOf(accentColor)
}
