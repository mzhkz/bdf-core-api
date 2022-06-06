package net.bdfps.api.spigot.listener

import net.bdfps.api.spigot.event.BDFBulletDeathEvent
import net.bdfps.api.spigot.event.BDFPlayerLoginEvent
import net.bdfps.api.spigot.event.network.BDFNetworkEvent
import net.bdfps.api.spigot.managers.BDFManager
import net.bdfps.api.spigot.support.BDFPlayer
import net.bdfps.api.spigot.utility.sendConsoleMessage
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PluginListener : Listener {

    @EventHandler
    fun kick(event: BDFNetworkEvent.BDFNetworkKickEvent) {
        val body = event.source.body

        val target = body.get("target").toString
        val reason = body.get("reason").toString

        val user = BDFManager.playerManager.findWithID(target)
        user?.syncKick(reason)

        sendConsoleMessage(ChatColor.AQUA.toString() + event.source.requestId)
    }

    @EventHandler
    fun join(event: BDFPlayerLoginEvent) {
//        BDFManager.playerManager.giveDebugKit(event.player)
    }

//    @EventHandler
//    fun syncData(event: BDFSyncUserDataEvent) {
//        val player = event.player
//        player.kills += 1
//        player.haveBP += 100
//
//        player.sideBar.setTitle(ChatColor.BOLD.toString() + "INFO")
//        player.sideBar.setWords(
//            "キル数: ${player.kills}",
//            "所持金: ${player.haveBP}"
//        )
//        player.sideBar.updateScoreBoard() //スコアボード更新。これがないと適用されない
//
//        BDFAPIv1.async(RequestMethod.GET, "${APIRoot.USER}/leaderboard/kills/${player.bdfID}") { res, error ->
//            if (error) {
//                player.sendMessage(ChatColor.RED.toString() + "エラーが発生しました")
//            } else {
//                player.sendMessage(
//                    "ランキング",
//                    "キル: #${res.get("result").toInt}"
//                )
//            }
//        }
//    }

    @EventHandler
    fun deathEvent(event: BDFBulletDeathEvent) {
        val shooter = event.lastDamage.bullet.owner
        if (shooter is BDFPlayer) {
            shooter.playSound("entity.arrow.hit_player", 1F, 0.7F)
        }
    }
}
