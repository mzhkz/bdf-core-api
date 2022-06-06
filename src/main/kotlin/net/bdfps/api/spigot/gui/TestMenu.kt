package net.bdfps.api.spigot.gui

import net.bdfps.api.spigot.managers.BDFManager
import net.bdfps.api.spigot.support.BDFPlayer
import org.bukkit.ChatColor
import org.bukkit.Sound

class TestMenu(bdfPlayer: BDFPlayer) : InventoryMenu(
    player = bdfPlayer,
    name = "test",
    popupMenu = PopupMenuAPI.createMenu("簡単自己管理", 2),
    init = { menu, inv, player ->
        //無敵
        menu.addMenuItem(MenuItem("無敵になる", 443) {
            player.isMuteki = !player.isMuteki
            player.sendMessage(ChatColor.GRAY.toString() + "》無敵モード: ${player.isMuteki}")
            player.playSound(Sound.BLOCK_NOTE_PLING, 1f, 1f)
            inv.initInventory()
        }.apply {
            addDescriptions(ChatColor.GRAY.toString() + "クリックして無敵モードを切り替える", ChatColor.GOLD.toString() + "現在: ${player.isMuteki}")
        }, 0)
        //無限弾薬
        menu.addMenuItem(MenuItem("弾薬を無限", 264, 0) {
            player.isInfinite = !player.isInfinite
            player.sendMessage(ChatColor.GRAY.toString() + "》弾薬無限モード: ${player.isInfinite}")
            player.playSound(Sound.BLOCK_NOTE_PLING, 1f, 1f)
            inv.initInventory()
        }.apply {
            addDescriptions(ChatColor.GRAY.toString() + "クリックして弾薬無限モードを切り替える", ChatColor.GOLD.toString() + "現在: ${player.isInfinite}")
        }, 1)

        //武器配布
        menu.addMenuItem(MenuItem("武器を配布する", 258, 0) {
            BDFManager.playerManager.giveDebugKit(player)
            player.sendMessage(ChatColor.GRAY.toString() + "》武器を配布したよ")
            player.playSound(Sound.BLOCK_NOTE_PLING, 1f, 1f)
            inv.close()
        }, 2)

        //メニューを閉じる
        menu.addMenuItem(MenuItem("閉じる", 152, 0) { player ->
            inv.close()
        }, 8, 1)
    }
)
