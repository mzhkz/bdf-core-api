package net.bdfps.api.spigot.weapon

import com.google.common.collect.Lists
import net.bdf.api.data.generaterandomSHA1
import net.bdfps.api.spigot.entity.BDFEntity
import net.bdfps.api.spigot.support.BDFPlayer
import net.bdfps.api.spigot.support.abc.WeaponCustomize
import net.bdfps.api.spigot.utility.hideTagAndBreakable
import net.bdfps.api.spigot.utility.rename
import net.minecraft.server.v1_12_R1.NBTTagCompound
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

const val WEAPON_TAG_NAME: String = "weaponUserID"

abstract class BDFWeapon {

    var owner: BDFEntity<*>? = null
    /** 武器の基本情報 */
    var weaponID: String = ""
    var weaponName: String = ""
    var description: String = ""

    /** 武器の種類 */
    lateinit var weaponClass: WeaponClass
    lateinit var slotType: SlotType

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
    var custom = WeaponCustomize()

    /** タスク処理の計算用 */
    protected var tick = 0 //処理の為

    /** 生成したItemStackインスタンスの一覧 */
    protected var stacks: MutableList<ItemStack> = Lists.newArrayList() //武器のスタック一覧

    /** 無限弾薬かどうかフラグ */
    protected val isInfiniteAmmo: Boolean
        get() = owner?.isInfinite ?: false

    protected val isSafety: Boolean
        get() = owner?.isSafety ?: false


    val name: String
        get() = if (custom.customName.isEmpty()) weaponName else custom.customName

    val playerEntity: BDFPlayer?
        get() = owner as? BDFPlayer

    /** タスク処理 */
    fun tick() {
        onTick()
        playerEntity?.sendActionBar(display)
    }

    /* Displayを取得 (例: M4A1 ≪ 30 / 30 ≫)**/
    abstract val display: String

    /** 武器の1tickあたりの処理 */
    protected abstract fun onTick()

    /** キャンセル処理 */
    abstract fun stopProcess()

    /** 武器のアイテムスタック生成時にオプションを追加する場合は行う */
    protected abstract fun itemInstanceOption(itemStack: ItemStack)

    /** 右クリック */
    abstract fun rightClick()

    /** 左クリック */
    abstract fun leftClick()

    /** アイテムドロップアクション */
    abstract fun dropAction()

    /** 武器を持ち替えたとき */
    abstract fun heldAction()

    /** 手持ちのアイテムをSwapとき*/
    abstract fun swapHandAction()

    /** 武器のインスタンスを返す */
    abstract val cloneNewInstance: BDFWeapon

    val bukkitPlayer: Player?
        get() = playerEntity?.bukkitPlayer

    /** 武器のアイテムスタックを返す */
    val itemStack: ItemStack
        get() {
            val model = ItemStack(itemID, 1, itemShortID).apply {
                itemInstanceOption(this)
                if (itemBreakableValue > 0) //耐久値
                    durability = itemBreakableValue
            }
            val item = setNBTTag(model) //アイテムを武器と識別するためのIDを付与
            stacks.add(item) //アイテム一覧を追加
            return item
        }

    /**
     * 処理時間を設定する
     * @param _tick 処理時間(1s / 20 )
     */
    fun setProcess(_tick: Int) {
        tick = _tick
    }

    /**
     * NBTタグを設定したアイテムスタックを返す
     * @param itemStack コピーモデル
     */
    fun setNBTTag(itemStack: ItemStack): ItemStack {
        itemStack.hideTagAndBreakable() //タグ見えなくする
        itemStack.durability = itemBreakableValue //耐久値
        itemStack rename name //名前変更

        val nmsStack = CraftItemStack.asNMSCopy(itemStack) //NMS Stackインタンス生成
        val nmsTag = nmsStack.tag ?: NBTTagCompound()
        nmsTag.setString(WEAPON_TAG_NAME, custom.id)
        nmsStack.tag = nmsTag //タグ設定
        nmsStack.save(nmsTag)

        return CraftItemStack.asBukkitCopy(nmsStack)
    }



    enum class WeaponClass {
        Gun, Throwing, Knife
    }


    enum class SlotType {
        Primary, Secondary, Throwing, Knife;
    }
}
