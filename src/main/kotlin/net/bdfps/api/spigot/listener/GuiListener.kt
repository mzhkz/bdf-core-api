package net.bdfps.api.spigot.listener

import org.bukkit.entity.Player
import net.bdfps.api.spigot.gui.PopupMenu
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent


class GuiListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onMenuItemClicked(event: InventoryClickEvent) {
        val inventory = event.inventory
        if (inventory.holder is PopupMenu) {
            val menu = inventory.holder as PopupMenu
            if (event.whoClicked is Player) {
                val player = event.whoClicked as Player
                if (event.slotType == InventoryType.SlotType.OUTSIDE) {
                    // Quick exit for a menu, click outside of it
                    if (menu.exitOnClickOutside()) {
                        menu.closeMenu(player)
                    }
                } else {
                    val index = event.rawSlot
                    if (index < inventory.size) {
                        menu.selectMenuItem(player, index)
                    } else {
                        // If they want to mess with their inventory they don't need to do so in a menu
                        if (menu.exitOnClickOutside()) {
                            menu.closeMenu(player)
                        }
                    }
                }
            }
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onMenuClosed(event: InventoryCloseEvent) {
        if (event.player is Player) {
            val inventory = event.inventory
            if (inventory.holder is PopupMenu) {
                val menu = inventory.holder as PopupMenu
                val menuCloseBehaviour = menu.menuCloseBehaviour
                menuCloseBehaviour?.let {
                    menuCloseBehaviour.onClose(event.player as Player)
                }
            }
        }
    }
}
