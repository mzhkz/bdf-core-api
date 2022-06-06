package net.bdfps.api.spigot.weapon

import org.bukkit.inventory.ItemStack

/**
 * 武器が存在しなかった場合、武器データを受け継ぐためのクラス
 * @author ManjiJapan
 */
class BDFNullWeapon : BDFWeapon() {
    override val cloneNewInstance: BDFWeapon = BDFNullWeapon()

    override fun itemInstanceOption(itemStack: ItemStack) {
        //何も書かなくてよい
    }

    override fun onTick() {
        //何も書かなくてよい
    }

    override fun dropAction() {
        //何も書かなくてよい
    }

    override fun heldAction() {
        //何も書かなくてよい
    }

    override fun leftClick() {
        //何も書かなくてよい
    }

    override fun rightClick() {
        //何も書かなくてよい
    }

    override fun swapHandAction() {
        //何も書かなくてよい
    }

    override val display: String
        get() = "未登録武器"

    override fun stopProcess() {
        //何も書かなくてよい
    }
}
