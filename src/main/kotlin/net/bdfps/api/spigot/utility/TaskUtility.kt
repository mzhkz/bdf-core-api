package net.bdfps.api.spigot.utility

import net.bdfps.api.spigot.BDF
import net.bdfps.api.spigot.BDFConfig
import net.bdfps.api.spigot.managers.BDFManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask



/**
 * @param timer
 * @param callback コールバック関数的な
 ** 同期タイマー
 */
fun syncTimer(timer: Long, callback: () -> Unit): BukkitTask {
    return object : BukkitRunnable() {
        override fun run() {
            callback.invoke()
        }
    }.runTaskLater(BDF.instance, timer)
}

/**
 * @param period 開始遅延
 * @param delay 処理感覚
 * @param callback コールバック関数的な
 ** 同期タスク
 */
fun syncTask(delay: Long, period: Long, callback: () -> Unit): BukkitTask {
    return object : BukkitRunnable() {
        override fun run() {
            callback.invoke()
        }
    }.runTaskTimer(BDF.instance, delay, period)
}

/**
 * @param callback コールバック関数的な
 ** 同期タスク
 */
fun syncRunnable(callback: () -> Unit): BukkitTask {
    return object : BukkitRunnable() {
        override fun run() {
            callback.invoke()
        }
    }.runTask(BDF.instance)
}


/**
 * @param timer
 * @param callback コールバック関数的な
 ** 非同期タイマー
 */
fun asyncTimer(timer: Long, callback: () -> Unit): BukkitTask {
    return object : BukkitRunnable() {
        override fun run() {
            callback.invoke()
        }
    }.runTaskLaterAsynchronously(BDF.instance, timer)
}

/**
 * @param period 開始遅延
 * @param delay 処理感覚
 * @param callback コールバック関数的な
 ** 非同期タスク
 */
fun asyncTask(delay: Long, period: Long, callback: () -> Unit): BukkitTask {
    return object : BukkitRunnable() {
        override fun run() {
            callback.invoke()
        }
    }.runTaskTimerAsynchronously(BDF.instance, delay, period)
}

/**
 * オンラインモード処理
 */
inline fun online(unit: () -> Unit) {
   if (BDFConfig.onlineMode)
       unit.invoke()
}

/**
 * オフラインモード処理
 */
inline fun offline(unit: () -> Unit) {
    if (!BDFConfig.onlineMode)
        unit.invoke()
}


fun shutdown(message: String = ChatColor.RED.toString() + "重大なエラーが発生したためサーバから切断されました。") {
    BDF.onlinePlayers.forEach {
        it.bukkitPlayer.kickPlayer(message)
    }
    sendConsoleMessage(ChatColor.RED.toString() + message)
    Bukkit.shutdown() //終了
}
