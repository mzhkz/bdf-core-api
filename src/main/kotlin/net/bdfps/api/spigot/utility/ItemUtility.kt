@file:JvmName("ItemUtility")
package net.bdfps.api.spigot.utility

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.io.IOException
import org.bukkit.Bukkit
import org.bukkit.util.io.BukkitObjectInputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayOutputStream


fun Player.getHeadItem(): ItemStack =
        ItemStack(Material.SKULL_ITEM, 1, 3).apply {
            itemMeta = (itemMeta as SkullMeta).also { meta ->
                meta.owner = name
            }
        }
/**
 * @param item
 * @return
 */
fun ItemStack.getDisplayName(): String {
    return  this.itemMeta.displayName
}

/**
 * アイテム名を変更
 * @param name
 */
infix fun ItemStack.rename(name: String) {
    this.itemMeta = this.itemMeta.apply {
        displayName = name
    }
}

/** 耐久値やアイテムフラグを非表示*/
fun ItemStack.hideTagAndBreakable() {
    this.itemMeta = this.itemMeta.apply {
        spigot().isUnbreakable = true
        addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS)
    }
}

/**
 * @param material
 * @param name
 * @param desc
 * @return
 */
fun createItem(material: Material, name: String, desc: MutableList<String>): ItemStack {
    val item = ItemStack(material)
    item.itemMeta = item.itemMeta.apply {
        displayName = name
        lore = desc
    }
    return item
}

/**
 * @param material
 * @param name
 * @return
 */
fun createItem(material: Material, name: String): ItemStack {
    val item = ItemStack(material)
    item.itemMeta = item.itemMeta.apply {
        displayName = name
    }
    return item
}

/**
 * @param material
 * @param name
 * @param desc
 * @param color
 * @return
 */
fun createWoolItem(material: Material, name: String,  desc: MutableList<String>, color: Byte): ItemStack {
    val item = ItemStack(material, 1, color.toShort())
    item.itemMeta = item.itemMeta.apply {
        displayName = name
        lore = desc
    }
    return item
}

/**
 * @param material
 * @param name
 * @param color
 * @return
 */
fun createWoolItem(material: Material, name: String, color: Byte): ItemStack {
    val item = ItemStack(material, 1, color.toShort())
    item.itemMeta = item.itemMeta.apply {
        displayName = name
    }
    return item
}


/**
 * インベントリが満帆状態であるか
 */
fun Inventory.isFull(): Boolean = this.firstEmpty() == -1


fun Inventory.toBase64(): String {
    try {
        val outputStream = ByteArrayOutputStream()
        val dataOutput = BukkitObjectOutputStream(outputStream)

        // Write the size of the inventory
        dataOutput.writeInt(this.size)

        // Save every element in the list
        for (i in 0 until this.size) {
            dataOutput.writeObject(this.getItem(i))
        }

        // Serialize that array
        dataOutput.close()
        return Base64Coder.encodeLines(outputStream.toByteArray())
    } catch (e: Exception) {
        throw IllegalStateException("Unable to save item stacks.", e)
    }

}


fun fromBase64(data: String): Inventory {
    try {
        val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
        val dataInput = BukkitObjectInputStream(inputStream)
        val inventory = Bukkit.getServer().createInventory(null, dataInput.readInt())

        // Read the serialized inventory
        for (i in 0 until inventory.size) {
            inventory.setItem(i, dataInput.readObject() as ItemStack)
        }
        dataInput.close()
        return inventory
    } catch (e: ClassNotFoundException) {
        throw IOException("Unable to decode class type.", e)
    }

}


/**
 * @param inventory
 * @param itemid
 * @param dat
 * @param amt
 */
fun Inventory.removeItem(itemid: Int, dat: Byte, _amt: Int) {
    var amt = _amt
    val inventory = this
    val items = inventory.contents
    for (slot in items.indices) {
        if (items[slot] != null) {
            val id = items[slot].typeId
            val itmDat = items[slot].data.data.toInt()
            var itmAmt = items[slot].amount

            if (id == itemid && (dat.toInt() == itmDat || dat.toInt() == -1)) {
                if (amt > 0) {
                    if (itmAmt >= amt) {
                        itmAmt -= amt
                        amt = 0
                    } else {
                        amt -= itmAmt
                        itmAmt = 0
                    }
                    if (itmAmt > 0) {
                        inventory.getItem(slot).amount = itmAmt
                    } else {
                        inventory.setItem(slot, null)
                    }
                }
                if (amt <= 0) {
                    return
                }
            }
        }
    }
}

