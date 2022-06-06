package net.bdfps.api.spigot.weapon

import net.bdfps.api.spigot.java.packet.PacketUtil
import net.bdfps.api.spigot.managers.BDFManager
import net.bdfps.api.spigot.utility.rename
import net.bdfps.api.spigot.utility.syncTimer
import net.bdfps.api.spigot.weapon.bullet.type.BDFGunBullet
import net.bdfps.api.spigot.weapon.type.*
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

abstract class BDFGun : BDFWeapon() {
    var reloadType: ReloadType = ReloadType.Normal
    lateinit var gunType: GunType //銃の種類

    /* サウンド系 */
    var shootSound: String = ""

    var hipAccuracy: Double = 0.0 //精度
    var adsAccuracy: Double = 0.0

    var initOneMagazineAmmo: Int = 0 //初期銃弾
    var initPreMagazineAmmo: Int = 0 //初期予備弾数

    var headShotDamage: Double = 0.0
    var bodyDamage: Double = 0.0
    var legDamage: Double = 0.0

    var needAmmo: Int = 0 //消費する弾
    var burst = 1 //バースト (消費する弾の設定ではなく、一回のShootいべんとで何回DelayShootを行うか)

    var secondRate: Int = 0 //1秒間に何発撃てるか
    var fullAutoShootTime: Int = 0 //処理用

    var singleShootTime: Int = 0 //処理用
    var singleOverHeatTime: Int = 0//オーバーヒート時間

    var swapTime = 14 //待機時間
    var reloadTime: Int = 0 //リロード時間

    var scopeMagnification: Int = 0 //スコープ拡大度
    var distance: Double = 0.0 //距離
    var speed: Int = 10 //弾道の速さ
    var dropRate: Double = 1.0//落下率


    var distanceDecayDamage = 10.0 //距離減衰のダメージ
    var distanceDecayDamageInterval = 1.0 //距離を計算する上でのインターバル

    var hipRecoilHipHeight = 0.0f //腰撃ちリコイル高さ
    var adsRecoilAdsHeight = 0.0f //ADS時リコイル高さ

    var hipRecoilHipWidth = 0.0f //腰撃ち横の最大値
    var adsRecoilAdsWidth = 0.0f //ADS時横の最大値

    var hipRecoilSlopePercent = 0 //腰撃ちリコイル左にずれる確率
    var adsRecoilSlopePercent = 0 //ADS時リリコイル左にずれる確率

    var recoilSlope: Slope = Slope.Right //リコイルの向き


    var enableFullAuto: Boolean = false//フルオート有効武器フラグ セミ、フルの切り替え有効もここ
    var enableUseScope = true //スコープを使うかフラグ
    var enableOutScope = false //撃ったときにスコープが外れるフラグ
    var enableAutoReload: Boolean = false //弾が切れたときに自動リロードフラグ
    var overHeatTick = 0

    /* フラグ一覧 **/
    var Shooting = false
    var Reloading = false
    var Scopeing = false
    var Swapping = false
    var OverHeating = false
    var isProcessFullAuto = false //ふるモードでフルオート中
    var isFullAuto = true //せみ、ふる切り替え
    var displayFlash = false

    var hasPreMagazineAmmo = 0
    var hasMagazineAmmo = 0

    /* レート制限用*/
    var allowReserveClick = true
    var clickTick = 0

    var animationTick = 0 //実行するまでの管理
    var reserveAnimation = false //アニメーションを予約する

    /* 弾切れを取得 (例:弾が切れた！！)**/
    abstract val outAmmoMessage: String


    /** ボルト時の1マガジンあたりのリロード時間 */
    private val boltReloadTime: Int by lazy {
        val oneAmmoReload = this.reloadTime.toDouble() / this.initOneMagazineAmmo.toDouble()
        Math.round(oneAmmoReload).toInt()
    }

    /** モードの表示を返す*/
    protected val status: String
        get() {
            if (enableFullAuto) {
                return if (isFullAuto) "${ChatColor.GRAY}${ChatColor.BOLD}[AUTO]${ChatColor.RESET}" else "${ChatColor.GRAY}${ChatColor.BOLD}[SINGLE]${ChatColor.RESET}"
            }
            return String()
        }

    /*  撃ったときの処理* */
    protected abstract fun doShoot()

    /* 発射するときの処理* */
    protected abstract fun doDelayShoot()

    /* reloadときの処理* */
    protected abstract fun doReload()

    /*  覗いたときの処理 * */
    protected abstract fun doScope()

    /*  持ち替えたときの処理**/
    protected abstract fun doSwap()


    /** インスタンスを返します */
    override val cloneNewInstance: BDFWeapon
        get() = when (gunType) {
            GunType.Assault -> AssaultRifle()
            GunType.SubMachineGun -> SubMachineGun()
            GunType.LightMachineGun -> LightMachineGun()
            GunType.ShotGun -> ShotGun()
            GunType.Sniper -> SniperRifle()
            GunType.HandGun -> HandGun()
        }

    /** タスク処理を止める*/
    override fun stopProcess() {
        Swapping = false
        Shooting = false
        if (Scopeing) { //スコープを閉じる
            Scope(checkRate = false)
        }
        Reloading = false
    }

    /** スコープ */
    override fun leftClick() {
        Scope()

    }

    override fun swapHandAction() {
        fullAuto(!isFullAuto)
    }

    /** 撃つ */
    override fun rightClick() {
        Shoot()
    }

    /** リロード */
    override fun dropAction() {
        syncTimer(1) {
            Reload()
        }
    }

    /** 武器持ち替え時にスワップ */
    override fun heldAction() {
        Swap()
    }

    /** 武器のタスク管理 */
    override fun onTick() {
        /*レート制限 */
        if (!allowReserveClick) {
            if (clickTick == 0) {
                allowReserveClick = true //許可する
            }
            clickTick--
        }
        if (OverHeating) {
            if (overHeatTick == 0) {
                OverHeating = false //許可する
            }
            overHeatTick--
        }
        /* Animation */
        if (reserveAnimation) {
            if (animationTick == 0) {
                reserveAnimation = false
                playerEntity?.disguiseHoldWeaponInHand() //アイテム所持のパケットを再送信することで構えが解除される
            }
            animationTick--
        }
        when {
            Swapping -> {
                if (tick == 0) { //Swap処理終わり
                    Swapping = false
                    finishFullAuto()
                }
            }
            Shooting && !Reloading && !Swapping  -> { //撃っている

                if (isFullAuto) { //フルオート now
                    if (canShootByRate()) {//レート制御
                        delayShoot()
                    }
                } else {
                    if (tick > singleOverHeatTime) //待機時間をおーバしていないか
                        if (canShootByRate()) //レート制御
                            delayShoot()
                }

                if (tick == 0) {
                    Shooting = false
                    finishFullAuto() //フルートフラグを解除
                }
            }
            Reloading -> {
                playReloadSound(tick)

                if (tick % 2 == 0)  //リロードアニメーション
                    flash()

                if (tick == 0) { //リロード終了
                    finishReloadSpendAmmo()

                    when (reloadType) {
                        ReloadType.Bolt -> {
                            Reloading = false //フラグ解除
                            when {
                                hasMagazineAmmo < initOneMagazineAmmo -> { //リロード完了するまで繰り返し
                                    playerEntity!!.playSound("bdf.action.reload", 1f, 1f)

                                    Reload()
                                }
                                hasMagazineAmmo == initOneMagazineAmmo -> { //リロード完了
                                    playerEntity!!.playSound("bdf.action.reloadout", 1f, 1f)

                                }
                            }
                        }
                        ReloadType.Normal -> {
                            Reloading = false //フラグ解除
                            playerEntity!!.playSound("bdf.action.reloadout", 1f, 1f)

                        }
                    }
                }
            }
        }
        if (Shooting || Reloading || Swapping) //処理実行時のみ実行
            tick--
    }

    /**
     * 物理的なリコイルを与える
     */
    protected fun recoil(pitch: Float, yaw: Float) {
        val player = playerEntity
        if (player != null) {
            val location = player.bukkitPlayer.eyeLocation.clone()
            var f1 = location.yaw - yaw
            f1 -= location.yaw
            var f2 = location.pitch - pitch
            f2 -= location.pitch
            PacketUtil.setPosition(player.bukkitPlayer, 0.0, 0.0, 0.0, f1, f2)
        }
    }

    /** スコープの処理 */
    private fun Scope(checkRate: Boolean = true) {
        if (checkRate && !checkClickRate()) //レート制限
            return

        if (!enableUseScope) //スコープの仕様が許可されていない
            return

        val scopeLevel = scopeMagnification //スコープ倍率
        if (scopeLevel <= 0) //意味ない
            return

        if (Reloading || Swapping) //リロード中とスワップ中はスコープ無効
            return

        val owner = playerEntity
        if (owner != null) { // 人間だった場合
            val player = owner.bukkitPlayer
            Scopeing = !Scopeing //フラグ操作
            if (Scopeing) {
                player.addPotionEffect(
                    PotionEffect(
                        PotionEffectType.SLOW, //タイプ
                        99999, //時間
                        scopeLevel - 1,
                        false
                    )
                )
            } else {
                player.removePotionEffect(PotionEffectType.SLOW)
                playerEntity?.disguiseHoldWeaponInHand() //アニメーションをキャンセル
            }
            owner.setOffHandScope(this, Scopeing) //左手パケット・武器構えパケットを送信
            doScope() //カスタム処理
        }
    }


    /** 銃弾を発射する */
    private fun delayShoot() {
        if (checkAmmo()) { //弾切れチェック
            for (i in 0 until burst) {
                val bdfGunBullet = BDFGunBullet(owner!!, this)
                BDFManager.bulletManager.addBullet(bdfGunBullet)
            }

            /* リコイル */
            val baseHeightRecoil = if (Scopeing) adsRecoilAdsHeight else hipRecoilHipHeight //縦ブレ
            val baseWeightRecoil = if (Scopeing) adsRecoilAdsWidth else hipRecoilHipWidth //横ぶれ
            val baseWeightSlopePercent = if (Scopeing) adsRecoilSlopePercent else hipRecoilSlopePercent

            val integerWeight = (baseWeightRecoil * 100f).toInt()

            var processWeightRecoil =
                Math.abs(integerWeight - Random().nextInt(integerWeight)).toFloat() / 100f //ブレ幅求める
            val dif = 100 - baseWeightSlopePercent //100% - 確率
            val random = Random().nextInt(100)  //確率をもとに判定
            when (recoilSlope) {
                Slope.Right -> {
                    if (random >= dif)
                        processWeightRecoil *= -1f //右に
                }
                Slope.Left -> {
                    if (random < dif)
                        processWeightRecoil *= -1f //右に
                }
            }
            recoil(baseHeightRecoil, processWeightRecoil)

            //bukkitPlayer?.playSound(bukkitPlayer?.location, shootSound, 1.2f, 1.0f)

            if (enableOutScope && Scopeing) //スコープを解除する銃であったら解除
                doScope()

            flash() //武器引っ込み
            spendAmmo() //銃弾消費
            doDelayShoot() //カスタム処理
            playerEntity?.playSound(shootSound, 1f, 1f)
            playShootSoundToEveryone(playerEntity!!.location, shootSound)
            if (enableOutScope) { //撃った時にスコープを解除する武器
                if (Scopeing)
                    Scope(checkRate = false) //解除
            }

        } else {
            if (enableAutoReload) {
                Reload() //弾が込められてなかったらリロードする。
            }
        }
    }

    private fun playShootSoundToEveryone(shootPoint: Location, shootSound: String) {
        BDFManager.playerManager.localPlayers.forEach {
            it.playshootSound(shootSound, 1.0F, shootPoint)
        }

    }

    /** 撃つ */
    private fun Shoot() {
        if (isSafety) return //セーフティがかかっている
        if (Reloading && reloadType == ReloadType.Bolt) { //リロードキャンセル
            if (hasMagazineAmmo < initOneMagazineAmmo) { //悪用防止
                Reloading = false
                return
            }
        }

        if (Swapping || Reloading)
            return

        /* 残弾数確認 */
        if (hasMagazineAmmo <= 0 && hasPreMagazineAmmo <= 0) {
            bukkitPlayer?.run {
                playSound(location, Sound.ENTITY_ITEM_BREAK, 1f, 1.3f)
                sendMessage(outAmmoMessage) //弾切れメッセージ
            }
            return
        }

        /* フルオートモード */
        if (isFullAuto) {
            if (checkClickRate()) { //クリックレート制限
                isProcessFullAuto = Shooting //前回の処理中に新規処理を受けた場合はフルオート判定
                setProcess(fullAutoShootTime) //シュート処理時間
                Shooting = true //フラグON
            }
        } else { //単発モード
            if (!Shooting) {
                isProcessFullAuto = false
                setProcess(singleShootTime) //シュート処理時間
                Shooting = true //フラグON
            }
        }
        playerEntity?.disguiseShootingAnimation() //構えているアニメーション
        reserveAnimation = false //構成アニメーション実行をキャンセル
    }

    /**
     * リロード実行
     * Boltのリロード時はReloadフラグをfalseにしてから！！！
     */
    private fun Reload() {
        if (Scopeing) //スコープ解除
            Scope(checkRate = false)

        if (!Swapping && !Reloading) //スワッピングとすでにリロード中の場合は実行できない
            if (hasMagazineAmmo >= 0 && hasPreMagazineAmmo > 0) { //込めている弾が0発以上 and 予備弾数が
                if (hasMagazineAmmo != initOneMagazineAmmo) {
                    Reloading = true //フラグON
                    playerEntity!!.playSound("bdf.action.reload", 1f, 1f)

                    Shooting = false
                    /* リロード方法により時間が異なる */
                    when (reloadType) {
                        ReloadType.Normal -> setProcess(reloadTime)
                        ReloadType.Bolt -> setProcess(boltReloadTime)
                    }
                    finishFullAuto() //処理終了
                    doReload() //カスタム処理
                }
            } else {
                playerEntity?.playSound(Sound.ENTITY_ITEM_BREAK, 1f, 1.3f)
            }
    }

    /** Swap処理 */
    private fun Swap() {
        finishFullAuto()
        Swapping = true //フラグON
        tick = swapTime //スワップ時間を設定
        playerEntity!!.playSound("bdf.action.swap", 1f, 1f)

        doSwap()
    }

    /**
     * 発射レート
     * @return
     */
    private fun canShootByRate(): Boolean {
        var rate = this.secondRate
        if (rate > 20) rate = 20
        var tick = this.tick
        when (rate) {
            20 -> return true
            19 -> return tick != 20
            18 -> return tick % 10 != 0
            17 -> return tick % 6 != 0
            16 -> return tick % 5 != 0
            15 -> return tick % 4 != 0
            14 -> return tick % 3 != 0
            13 -> {
                tick %= 6
                return tick != 2 && tick != 0
            }
            12 -> {
                tick %= 5
                return tick == 1 || tick == 2 || tick == 4
            }
            10 -> return tick % 2 == 0
            11 -> return tick == 2 || tick % 2 == 1
            9 -> {
                tick %= 7
                return tick == 1 || tick == 3 || tick == 5
            }
            8 -> {
                tick %= 5
                return tick == 1 || tick == 3
            }
            7 -> return tick % 3 == 1
            6 -> {
                tick %= 7
                return tick == 1 || tick == 4
            }
            5 -> return tick % 4 == 1
            4 -> return tick % 5 == 0
            3 -> return tick % 6 == 0
            2 -> return tick == 20 || tick % 10 == 0
            1 -> return tick == 20
            0 -> {
                if (isFullAuto)
                    return this.tick == this.fullAutoShootTime
                else
                    return this.tick == this.singleShootTime
            }
        }
        return false
    }

    /** FullAutoを停止する */
    private fun finishFullAuto() {
        isProcessFullAuto = false
        if (!Scopeing) { //dont スコープ除き
            reserveAnimation = true
            animationTick = 10 //0.5s
        }
    }

    /** アイテムが引っ込むように */
    private fun flash() {
        if (Scopeing)
            return
        if (displayFlash)
            stacks.forEach {
                it rename ChatColor.WHITE.toString() + ""
            }
        else
            stacks.forEach {
                it rename ChatColor.BOLD.toString() + ""
            }
        displayFlash = !displayFlash

        playerEntity?.updateInventory() //インベントリを更新
    }

    /**
     * リロード音を再生
     * @param tick 処理用のtick
     */
    private fun playReloadSound(tick: Int) {

    }

    /** リロード後の弾数設定*/
    private fun finishReloadSpendAmmo() {
        when (reloadType) {
            /* 通常リロード */
            ReloadType.Normal -> {
                val dif: Int
                if (isInfiniteAmmo) //無限弾薬
                    dif = 9999
                else
                    dif = hasPreMagazineAmmo - (initOneMagazineAmmo - hasMagazineAmmo)

                if (dif > -1) {
                    hasMagazineAmmo = initOneMagazineAmmo
                    hasPreMagazineAmmo = dif
                } else {
                    hasMagazineAmmo += hasPreMagazineAmmo //必ずしも0初の状態でリロードしているとは限らない
                    hasPreMagazineAmmo = 0
                }
            }
            /* 一発ずつリロード */
            ReloadType.Bolt -> {
                val dif: Int
                if (isInfiniteAmmo) //無限弾薬
                    dif = 9999
                else
                    dif = hasPreMagazineAmmo - 1

                if (dif > -1) {
                    hasMagazineAmmo += 1
                    hasPreMagazineAmmo = dif
                } else {
                    hasMagazineAmmo = hasPreMagazineAmmo //必ずしも0初の状態でリロードしているとは限らない
                    hasPreMagazineAmmo = 0
                }
            }
        }
    }


    /**
     * 弾数チェック
     * 撃つ処理に使う
     */
    private fun checkAmmo(): Boolean = hasMagazineAmmo >= needAmmo

    /** 武器の総弾数をリセット*/
    fun resetAmmo() {
        hasMagazineAmmo = initOneMagazineAmmo
        hasPreMagazineAmmo = initPreMagazineAmmo
    }

    /** 弾を消費する */
    private fun spendAmmo() {
        hasMagazineAmmo -= needAmmo
        if (hasMagazineAmmo <= 0) {
            if (enableAutoReload) //弾が無くなったらリロード
                doReload()
            playerEntity?.playSound("bdf.gun.shoot.cocking", 1.0f, 1.0f)
        }
    }

    /**
     * 武器の切り替え
     * @param fullAuto フルオートフラグ
     */
    private fun fullAuto(fullAuto: Boolean, checkRate: Boolean = true) {
        if (checkRate && !checkClickRate()) //レート制限
            return
        if (!Shooting && enableFullAuto) {
            isFullAuto = fullAuto
            playerEntity?.playSound(Sound.UI_BUTTON_CLICK, 1f, 1.3f)
        }
    }

    /** レート制限を書けるため */
    private fun checkClickRate(): Boolean {
        if (allowReserveClick) {
            clickTick = 2
            allowReserveClick = false
            return true
        }
//        return true
        return false
    }

    enum class ReloadType {
        Normal, Bolt
    }

    enum class Slope {
        Right, Left
    }

    enum class ShootType {
        Single, Auto, Burst;
    }

    enum class GunType(val weaponClass: WeaponClass) {
        Assault(WeaponClass.Gun),
        Sniper(WeaponClass.Gun),
        ShotGun(WeaponClass.Gun),
        SubMachineGun(WeaponClass.Gun),
        LightMachineGun(WeaponClass.Gun),
        HandGun(WeaponClass.Gun),
    }
}

