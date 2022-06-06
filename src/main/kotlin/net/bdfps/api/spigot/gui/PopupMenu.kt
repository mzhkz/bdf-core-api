package net.bdfps.api.spigot.gui

import net.bdfps.api.spigot.utility.toBDFPlayer
import java.util.ArrayList
import java.util.HashMap
import org.bukkit.entity.Player
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder


class PopupMenu(private val title: String, private val rows: Int) : InventoryHolder {

    val items = HashMap<Int, MenuItem>()
    private var inventory: Inventory? = null
    var exitOnClickOutside = true
    var menuCloseBehaviour: MenuCloseBehaviour? = null
    private var closed = false

    fun addMenuItem(item: MenuItem, x: Int, y: Int): Boolean {
        return addMenuItem(item, y * 9 + x)
    }

    fun setMenuItem(item: MenuItem, x: Int, y: Int): Boolean {
        return setMenuItem(item, y * 9 + x)
    }

    /**
     * インベントリにアイテムを追加します。
     * 上書きする場合はsetMenuItemを使用してください。
     *
     * @param item 追加するアイテム
     * @param index インベントリの座標
     * @return True 設定完了
     */
    fun addMenuItem(item: MenuItem, index: Int): Boolean {
        val slot = getInventory().getItem(index)
        if (slot != null && slot.type !== Material.AIR) {
            return false
        }
        val itemStack = item.itemStack
        getInventory().setItem(index, itemStack)
        items[index] = item
        item.addToMenu(this)
        return true
    }

    /**
     * インベントリにアイテムをセットします。
     * 既にアイテムが合った場合は上書きします。
     *
     * @param item 追加するアイテム
     * @param index インベントリの座標
     * @return True 設定完了
     */
    fun setMenuItem(item: MenuItem, index: Int): Boolean {
        val itemStack = item.itemStack
        getInventory().setItem(index, itemStack)
        items[index] = item
        item.addToMenu(this)
        return true
    }


    fun removeMenuItem(x: Int, y: Int): Boolean {
        return removeMenuItem(y * 9 + x)
    }


    fun removeMenuItem(index: Int): Boolean {
        val slot = getInventory().getItem(index)
        if (slot == null || slot.typeId == 0) {
            return false
        }
        getInventory().clear(index)
        items.remove(index)?.removeFromMenu(this)
        return true
    }

    fun selectMenuItem(player: Player, index: Int) {
        if (items.containsKey(index)) {
            val item = items[index]!!
            item.onClick(player.toBDFPlayer()!!)
        }
    }

    /**
     * Opens a menu for a player.
     *
     * Important note: This should not be used to switch from one menu to
     * another within the same tick as there is an error with Bukkit inventories
     * that will cause it to glitch. Instead delay 1 tick before opening or use
     * the switchMenu method to do it for you.
     *
     * Be aware that if you make changes to a menu with multiple viewers it will
     * change for all of them. You should use the PopupMenuAPI.cloneMenu method
     * if you want a copy of a menu that can be safely changed. Be sure to
     * destroy it if you do not intend to use it again.
     *
     * @param player The player to open the menu for
     */
    fun openMenu(player: Player) {
        if (getInventory().viewers.contains(player)) {
            throw IllegalArgumentException(player.name + " is already viewing " + getInventory().title)
        }
        player.openInventory(getInventory())
    }

    /**
     * Closes a menu for a player
     *
     * @param player
     */
    fun closeMenu(player: Player) {
        if (getInventory().viewers.contains(player)) {
            this.closed = true
            getInventory().viewers.remove(player)
            player.closeInventory()
        }
    }

    fun isClosed(player: Player): Boolean {
        return if (!getInventory().viewers.contains(player)) { true } else closed
    }


    fun switchMenu(player: Player, toMenu: PopupMenu) {
        PopupMenuAPI.switchMenu(player, this, toMenu)
    }

    override fun getInventory(): Inventory {
        if (inventory == null) {
            inventory = Bukkit.createInventory(this, rows * 9, title)
        }
        return inventory!!
    }

    fun exitOnClickOutside(): Boolean {
        return exitOnClickOutside
    }

    fun clone(): PopupMenu {
        val clone = PopupMenu(title, rows)
        clone.exitOnClickOutside = exitOnClickOutside
        clone.menuCloseBehaviour = menuCloseBehaviour
        for (index in items.keys) {
            addMenuItem(items[index]!!, index)
        }
        return clone
    }

    /**
     * Updates this menu after changes are made so that viewers can instantly
     * see them
     */
    fun updateMenu() {
        for (entity in getInventory().viewers) {
            if (entity is Player) {
                entity.updateInventory()
            }
        }
    }

    /**
     * タイトル等を変える場合
     * @param toMenu
     */
    fun updateMenu(toMenu: PopupMenu) {
        removeItems()
        for (i in toMenu.items.keys) {
            addMenuItem(toMenu.items[i]!!, i)
        }
        exitOnClickOutside = toMenu.exitOnClickOutside
        this.menuCloseBehaviour = toMenu.menuCloseBehaviour
        updateMenu()
    }

    fun removeItems() {
        val a = ArrayList<Int>()
        for (i in items.keys) {
            a.add(i)
        }
        for (i in a) {
            removeMenuItem(i)
        }
    }
}
