package net.bdfps.api.spigot.gui

import org.bukkit.entity.Player


interface MenuCloseBehaviour {

    /**
     * Called when a player closes a menu
     *
     * @param player The player closing the menu
     */
    fun onClose(player: Player)
}
