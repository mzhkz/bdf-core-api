package net.bdfps.api.spigot.weapon

import net.bdfps.api.spigot.utility.removeItem
import net.bdfps.api.spigot.utility.rename
import org.bukkit.inventory.ItemStack

abstract class BDFThrow : BDFWeapon() {

    lateinit var throwType: ThrowType //投げもの種類

    var initAmmo = 0
    var hasAmmo = 0

    var speed: Int = 10 //弾道の速さ
    var explodeTime: Int = 15
    var radius: Int = 5
    var enablePullPin = false
    var leftClickShoot = false
    var swapTime = 0
    var overHeatTick = 0
    var overHeatTime: Int = 0//オーバーヒート時間

    //判定系
    var Pullpining = false
    var Waiting = false
    var Swapping = false
    var OverHeating = false

    abstract fun doPullPinThrow()
    abstract fun doNormalThrow()
    abstract fun doSwap()

    private fun Swap() {
        Swapping = true //フラグON
        playerEntity!!.playSound("bdf.action.swap", 1f, 1f)

        tick = swapTime //スワップ時間を設定
    }


    override fun onTick() {
        if (OverHeating) {
            if (overHeatTick == 0) {
                OverHeating = false //許可する
            }
            overHeatTick--
        }
        when {
            Swapping -> {
                if (tick == 0) { //Swap処理終わり
                    Swapping = false
                }
            }
            Pullpining -> {
                if (tick == 0) {
                    leftClickShoot = false
                    resetPullPinAction()
                    doPullPinThrow()
                    spendAmmo()
                }
            }
            Waiting -> {
                if (tick == 0) {
                    leftClickShoot = false
                    Waiting = false
                }
            }
        }
        if (Swapping || Pullpining || Waiting) //処理実行時のみ実行
            tick--
    }


    override fun heldAction() {
        Swap()
    }

    override fun leftClick() {
        leftClickShoot = true
        rightClick()
    }

    override fun rightClick() {
        if (isSafety) return //セーフティがかかっている
        if (checkAmmo() && checkBurstRate()) { //残弾チェック + 連打防止
            spendAmmo() //弾消費
            if (Pullpining) {
                resetPullPinAction()
                doPullPinThrow()
            } else {
                doNormalThrow()
            }
        }
    }

    override fun swapHandAction() {
        if (isSafety) return //セーフティがかかっている
        if (enablePullPin) {
            if (!Pullpining) {
                tick = explodeTime
            }
            if (checkAmmo()) Pullpining = true
            if (playerEntity != null) {
                playerEntity!!.setOffHandScope(this, Pullpining) //左手パケット・武器構えパケットを送信
                if (Pullpining) {
                    playerEntity!!.bukkitPlayer.inventory.setItem(playerEntity!!.currentItemSlot, ItemStack(318).apply {
                        rename(" ")
                    })
                    playerEntity!!.bukkitPlayer.inventory.itemInOffHand = ItemStack(371).apply {
                        rename(" ")
                    }
                }
                playerEntity!!.playSound("bdfps.gun.grenade.pullpin", 1f, 1.3f)
            }
        }
    }

    override fun stopProcess() {
        if (checkAmmo()) {
            if (Pullpining) {
                resetPullPinAction()
                doPullPinThrow()

                spendAmmo()
                Pullpining = false
            }
        }
    }

    override fun dropAction() {
        //無し
    }

    override val cloneNewInstance: BDFWeapon
        get() = when (throwType) {
            ThrowType.Grenade -> net.bdfps.api.spigot.weapon.type.Grenade()
        }

    /** 弾を消費する */
    private fun spendAmmo() {
        if (isInfiniteAmmo) return //無限弾薬
        hasAmmo -= 1
        playerEntity?.let {
            this.playerEntity!!.bukkitPlayer.inventory.removeItem(itemID, itemShortID.toByte(), 1)
            this.playerEntity!!.bukkitPlayer.updateInventory()
        }
    }

    /** 武器の総弾数をリセット*/
    fun resetAmmo() {
        hasAmmo = initAmmo
    }

    /** レート制限を書けるため */
    private fun checkBurstRate(): Boolean {
        if (!OverHeating) {
            overHeatTick = overHeatTime
            OverHeating = true
            return true
        }
//        return true
        return false
    }

    private fun resetPullPinAction() {
        if(Pullpining) {
            playerEntity!!.setOffHandScope(this, false) //左手パケット・武器構えパケットを送信

            this.playerEntity!!.bukkitPlayer.inventory.itemInOffHand = null
            this.playerEntity!!.bukkitPlayer.inventory.setItem(playerEntity!!.currentItemSlot, this.itemStack)
        }
    }

    /**
     * 弾数チェック
     * 撃つ処理に使う
     */
    private fun checkAmmo(): Boolean = hasAmmo >= 1


    enum class ThrowType(val weaponClass: WeaponClass) {
        Grenade(WeaponClass.Throwing)
    }

}
