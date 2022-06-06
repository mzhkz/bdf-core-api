package net.bdfps.api.spigot.command

import net.bdf.api.data.Mobject
import net.bdfps.api.spigot.gui.TestMenu
import net.bdfps.api.spigot.managers.BDFManager
import net.bdfps.api.spigot.managers.PlayerManager
import net.bdfps.api.spigot.network.event.BDFEvent
import net.bdfps.api.spigot.support.BDFPlayer
import org.bukkit.Bukkit
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class AdminCommand: BDFCommand("admin") {

    override fun onCommand(info: BDFPlayer, args: Array<String>, name: String): Boolean {
        when (args.size) {
            1 -> {
                when (args[0].toLowerCase()) {
                    "m40a1" -> {
                        info.kit = "m40a1"
                        BDFManager.playerManager.giveDebugKit(info)
                        info.isSafety = false
                    }
                    "m4a1" -> {
                        info.kit = "m4a1"
                        BDFManager.playerManager.giveDebugKit(info)
                        info.isSafety = false
                    }
                    "m1887" -> {
                        info.kit = "m1887"
                        BDFManager.playerManager.giveDebugKit(info)
                        info.isSafety = false
                    }
                    "debug" -> {

                        info.bukkitPlayer.addPotionEffect(PotionEffect(PotionEffectType.SLOW,999999,4,false,false))
//140%
                        info.bukkitPlayer.walkSpeed = 1.00F
                        Bukkit.broadcastMessage(info.bukkitPlayer.walkSpeed.toString())
                    }
                    "debugle" -> {
                        info.bukkitPlayer.walkSpeed = 0.25F
                    }

                }
            }
        }
        return false
    }
}