package net.bdfps.api.spigot.gui

import net.bdfps.api.spigot.support.BDFPlayer

abstract class InventoryMenu(
    val player: BDFPlayer, //メニューを開くプレイヤー
    val name: String, //メニュー名
    val back: InventoryMenu? = null,
    var popupMenu: PopupMenu,
    private val init: (menu: PopupMenu, inventory: InventoryMenu, player: BDFPlayer) -> Unit
) {

    init {
        initInventory()
    }

    fun initInventory() {
        popupMenu.removeItems()
        init(popupMenu, this, player)
    }


    /** メニューを開ける*/
    fun open() {
        popupMenu.openMenu(player.bukkitPlayer)
    }

    /** メニューを閉じる*/
    fun close() {
        popupMenu.closeMenu(player.bukkitPlayer)
    }

    /**
     * タイトル等を変えない場合のみ
     * @param fromMenu どこから来たか。
     */
    fun update(fromMenu: PopupMenu) {
        fromMenu.updateMenu(this.popupMenu)
        this.popupMenu = fromMenu
    }
}
