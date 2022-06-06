package net.bdfps.api.spigot.listener

import net.bdfps.api.spigot.BDF
import net.bdfps.api.spigot.event.BDFDeathEvent
import net.bdfps.api.spigot.managers.BDFManager
import net.bdfps.api.spigot.network.OAuthAuthorize
import net.bdfps.api.spigot.utility.broadcast
import net.bdfps.api.spigot.utility.callEvent
import net.bdfps.api.spigot.utility.online
import net.bdfps.api.spigot.utility.toBDFPlayer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*

class PlayerListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerLogin(event: PlayerLoginEvent) {
        online {
            if (!OAuthAuthorize.ready) {
                event.disallow(
                    PlayerLoginEvent.Result.KICK_FULL, ChatColor.GOLD.toString() + "" +
                            "サーバは現在、起動処理を行っています。\n " +
                            "しばらく待ってから再ログインをお願いします。\n"
                )
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun join(event: PlayerJoinEvent) {
        event.joinMessage = ""
        val player = event.player
        BDFManager.playerManager.join(player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun quit(event: PlayerQuitEvent) {
        event.quitMessage = ""
        BDFManager.playerManager.quit(event.player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val playerInfo = event.player.toBDFPlayer()
        event.isCancelled = true
        if (playerInfo != null) {
            if (playerInfo.muted.valid) { //ミュート中
                playerInfo.sendMessage(ChatColor.RED.toString() + "あなたは現在、サーバからミュートされているため、チャットに参加することはできません。")
                return
            }
            broadcast("${ChatColor.GRAY}[0] ${ChatColor.DARK_GRAY}${playerInfo.name} ≫ ${ChatColor.DARK_AQUA}${event.message}")
            BDF.onlinePlayers.forEach {
                it.playSound(Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.5f)
            }
        } else { //ユーザ未登録
            event.player.sendMessage(ChatColor.RED.toString() + "致命的なバグが発生しました。再ログインしてください。")
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onClick(event: PlayerInteractEvent) {
        event.player.toBDFPlayer()?.onShoot(event)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onTapR(event: PlayerDropItemEvent) {
        event.player.toBDFPlayer()?.onReload(event)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun hold(event: PlayerItemHeldEvent) {
        val player = event.player.toBDFPlayer()
        player?.onHeld(event)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onHoldLeft(event: PlayerSwapHandItemsEvent) {
        val player = event.player.toBDFPlayer()
        player?.onSwapLeftHand(event)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onAnimation(event: PlayerAnimationEvent) {
        val player = event.player.toBDFPlayer()
        player?.onScope(event)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.entity.toBDFPlayer()
        player?.let {
            player.heal()
            event.deathMessage = "" //デフォルトのメッセージは無
            BDFDeathEvent(player).callEvent()
        }
    }
}
