package net.bdfps.api.spigot.gui

import org.bukkit.entity.Player


object PopupMenuAPI {
    /**
     * Create a new pop-up menu and stores it for later use
     *
     * @param title The menu title
     * @param rows The number of rows on the menu
     * @return The menu
     */
    fun createMenu(_title: String, rows: Int): PopupMenu {
        var title = _title
        if (32 < title.length) {
            title = title.substring(0, 32)
        }
        return PopupMenu(title, rows)
    }

    /**
     * Creates an exact copy of an existing pop-up menu. This is intended to be
     * used for creating dynamic pop-up menus for individual players. Be sure to
     * call destroyMenu for menus that are no longer needed.
     *
     * @param menu The menu to clone
     * @return The cloned copy
     */
    fun cloneMenu(menu: PopupMenu): PopupMenu {
        return menu.clone()
    }

    /**
     * Destroys an existing menu, and closes it for any viewers
     *
     * Please note: you should not store any references to destroyed menus
     *
     * @param menu The menu to destroy
     */
    fun removeMenu(menu: PopupMenu) {
        for (viewer in menu.inventory.viewers) {
            if (viewer is Player) {
                menu.closeMenu(viewer)
            } else {
                viewer.closeInventory()
            }
        }
    }

    /**
     * Due to a bug with inventories, switching from one menu to another in the
     * same tick causes glitchiness. In order to prevent this, the opening must
     * be done in the next tick. This is a convenience method to perform this
     * task for you.
     *
     * @param player The player switching menus
     * @param fromMenu The menu the player is currently viewing
     * @param toMenu The menu the player is switching to
     */
    fun switchMenu(player: Player, fromMenu: PopupMenu, toMenu: PopupMenu) {
        fromMenu.closeMenu(player)
        toMenu.openMenu(player)
    }
}
