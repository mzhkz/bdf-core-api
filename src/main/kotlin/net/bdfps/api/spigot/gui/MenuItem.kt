package net.bdfps.api.spigot.gui

import net.bdfps.api.spigot.support.BDFPlayer
import org.bukkit.Color
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import java.util.*


open class MenuItem
@JvmOverloads
constructor(var text: String?, var icon: MaterialData, var number: Int = 1, val onClick: (player: BDFPlayer) -> Unit = {}) {

    constructor(text: String, id: Int, onClick: (player: BDFPlayer) -> Unit = {}) : this(text = text, icon = MaterialData(id), onClick = onClick)

    constructor(text: String, id: Int, bytes: Int, onClick: (player: BDFPlayer) -> Unit = {}) : this(text = text, icon = MaterialData(id), onClick = onClick) {
        durability = bytes.toShort()
    }

    var menu: PopupMenu? = null
    private var durability: Short = 0
    private var descriptions: MutableList<String> = ArrayList()
    var useHead = false //頭を使うか
    var headName: String? = null  //ユーザーネーム
    var useColor = false //色を使用する
    var rgb: Array<Int> = arrayOf() //カラー

    val itemStack: ItemStack
        get() {
            val slot = ItemStack(icon.itemType, number, icon.data.toShort())
            val meta = slot.itemMeta!!
            meta.lore = descriptions
            slot.durability = durability
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            meta.addItemFlags(ItemFlag.HIDE_DESTROYS)
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
            meta.addItemFlags(ItemFlag.HIDE_PLACED_ON)
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)
            meta.isUnbreakable = true
            meta.displayName = text
            slot.itemMeta = meta
            if (useHead) {
                slot.typeId = 397
                val myPlayerSkullMeta = slot.itemMeta as SkullMeta
                myPlayerSkullMeta.owner = headName
                slot.itemMeta = myPlayerSkullMeta
                slot.durability = 3.toShort()
            } else if (useColor) {
                val leatherArmorMeta = slot.itemMeta as LeatherArmorMeta
                leatherArmorMeta.color = Color.fromBGR(rgb[0], rgb[1], rgb[2])
                slot.itemMeta = leatherArmorMeta
            }
            return slot
        }


    fun setHead(name: String) {
        this.headName = name
        useHead = true
    }

    fun setRGB(red: Int, green: Int, blue: Int) {
        rgb = Arrays.asList(red, green, blue).toTypedArray()
        useColor = true
    }

    fun addToMenu(menu: PopupMenu) {
        this.menu = menu
    }

    fun removeFromMenu(menu: PopupMenu) {
        if (this.menu == menu) {
            this.menu = null
        }
    }


    fun setID(id: Int) {
        this.icon = MaterialData(id)
    }


    fun addDescriptions(vararg lore: String) {
        descriptions.addAll(lore)
    }
}
