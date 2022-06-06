package net.bdfps.api.spigot.utility

import net.bdfps.api.spigot.managers.BDFManager
import net.bdfps.api.spigot.support.BDFPlayer
import org.bukkit.entity.Player

/**
 *  Player系
 */

fun Player.toBDFPlayer() = BDFManager.playerManager.findWithName(this.name)

fun getBDFPlayer(name: String) = BDFManager.playerManager.findWithName(name)

val onlinePlayers: Array<BDFPlayer>
    get() = BDFManager.playerManager.localPlayers.toTypedArray()
