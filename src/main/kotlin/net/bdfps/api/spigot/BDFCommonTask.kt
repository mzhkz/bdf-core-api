package net.bdfps.api.spigot

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import net.bdfps.api.spigot.managers.BDFManager
import net.bdfps.api.spigot.utility.asyncTask
import net.bdfps.api.spigot.utility.syncTask
import org.bukkit.scheduler.BukkitTask

/**
 * 共通部分のタスク処理
 * @author ManjiJapan
 * @Date 10/03/2018
 */
object BDFCommonTask {

    var syncTask: BukkitTask? = null
    var asyncTask: BukkitTask? = null

    /** タスクを開始する */
    fun start() {
        asyncTask = asyncTask(1,1) {
            BDFManager.entityManager.tick()
        }

        GlobalScope.async {
            syncTask = syncTask(1,1){
                BDFManager.bulletManager.tick()
            }
        }
    }

    /** タスクを止める */
    fun stop() {
        syncTask?.cancel()
        asyncTask?.cancel()
    }
}
