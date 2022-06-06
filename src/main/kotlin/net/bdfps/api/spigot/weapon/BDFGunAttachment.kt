package net.bdfps.api.spigot.weapon

import org.bukkit.inventory.ItemStack

/**
 * 銃のアタッチメント
 */
class BDFGunAttachment: BDFAttachment() {

    var hipAccuracy: Double = 0.0 //精度
    var adsAccuracy: Double = 0.0

    var hipRecoil: Double = 0.0 //リコイル
    var adsRecoil: Double = 0.0

    var sneakingAccuracy: Double = 0.0
    var sprintingAccuracy: Double = 0.0
    var jumpingAccuracy: Double = 0.0
    var fullAutoAccuracy: Double = 0.0

    var initOneMagazineAmmo: Int = 0 //初期銃弾追加
    var initPreMagazineAmmo: Int = 0 //初期予備弾数追加

    var headShotDamage: Double = 0.0
    var bodyDamage: Double = 0.0
    var legDamage: Double = 0.0

    var swapTime = 14 //待機時間
    var reloadTime: Int = 0 //リロード時間

    var scopeMagnification: Int = 0 //スコープ拡大度
    var distance: Double = 0.0 //距離
    var speed: Double = 0.0 //弾道の速さ

    var distanceDecayDamage = 10.0 //距離減衰のダメージ
    var distanceDecayDamageInterval = 1.0 //距離を計算する上でのインターバル

    override fun itemInstanceOption(itemStack: ItemStack) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
