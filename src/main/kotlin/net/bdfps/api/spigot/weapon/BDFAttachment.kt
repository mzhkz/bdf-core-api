package net.bdfps.api.spigot.weapon

import net.bdf.api.data.generaterandomSHA1
import net.bdfps.api.spigot.utility.hideTagAndBreakable
import net.bdfps.api.spigot.utility.rename
import net.minecraft.server.v1_12_R1.NBTTagCompound
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

/**
 * アタッチメントのインスタンス
 * author: ManjiJapan
 */

const val ATT_TAG_NAME = "attUserID" //タグのID

abstract class BDFAttachment {

    /** アタッチメントの基本情報 */
    lateinit var attID: String
    lateinit var attName: String
    var description: String = ""

    /** アタッチメントの種類*/
    lateinit var attachmentType: AttachmentType
    lateinit var attachmentClass: BDFWeapon.WeaponClass

    var weight: Int = 0 //重量

    /** 購入コスト関係 */
    var unlock: Int = 0 //アンロックされるレベル
    var needBP = 0 //べたーぽいんと価格
    var needVP = 0 //消費DP

    /** アイテム設定 */
    var itemID = 0
    var itemShortID: Short = 0
    var itemBreakableValue: Short = 0

    /** ユーザの設定 */
    lateinit var userCustomize: AttUserCustomize

    /** 武器のアイテムスタック生成時にオプションを追加する場合は行う */
    abstract fun itemInstanceOption(itemStack: ItemStack)

    /** 武器のアイテムスタックを返す */
    val itemStack: ItemStack
        get() = ItemStack(itemID, 1, itemShortID).clone().apply {
            itemInstanceOption(itemStack)
            if (itemBreakableValue > 0) //耐久値
                durability = itemBreakableValue
            setNBTTag(this) //アイテムを武器と識別するためのIDを付与
        }

    /**
     * NBTタグを設定したアイテムスタックを返す
     * @param itemStack コピーモデル
     */
    fun setNBTTag(itemStack: ItemStack): ItemStack {
        itemStack.hideTagAndBreakable() //タグ見えなくする
        itemStack.durability = itemBreakableValue //耐久値
        itemStack.rename(attName) //名前変更

        val nmsStack = CraftItemStack.asNMSCopy(itemStack) //NMS Stackインタンス生成
        val nmsTag = nmsStack.tag ?: NBTTagCompound()
        nmsTag.setString(ATT_TAG_NAME, userCustomize.id)
        nmsStack.tag = nmsTag //タグ設定
        nmsStack.save(nmsTag)

        return CraftItemStack.asBukkitCopy(nmsStack)
    }

    /** ユーザ個別保存用 */
    class AttUserCustomize {
        var id: String = generaterandomSHA1
        var attID: String = ""
    }



    /** アタッチメントのタイプ一覧 */
    enum class AttachmentType(val weaponClass: BDFWeapon.WeaponClass) {
        Ammo(BDFWeapon.WeaponClass.Gun),
        Suppressor(BDFWeapon.WeaponClass.Gun),
        Grip(BDFWeapon.WeaponClass.Gun),
        Muzzle(BDFWeapon.WeaponClass.Gun),
        Scope(BDFWeapon.WeaponClass.Gun),
        Magazine(BDFWeapon.WeaponClass.Gun),
        Barrel(BDFWeapon.WeaponClass.Gun),
        Stock(BDFWeapon.WeaponClass.Gun),
        UnderRail(BDFWeapon.WeaponClass.Gun),
        Accessories(BDFWeapon.WeaponClass.Gun);

        companion object {
            fun getAttachmentType(name: String): AttachmentType =
                    values().find { name.equals(it.name, true) }?: throw Exception("att type name invalid.")
        }

    }
}
