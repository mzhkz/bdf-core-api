package net.bdfps.api.spigot.support.abc

import net.bdf.api.data.abc.MoWeaponSlot
import net.bdf.api.data.utilty.generateRandomSHA1

class WeaponSlot: BDFMobject<MoWeaponSlot>() {
    var id: String = generateRandomSHA1 //認識ID
    var primary ="" //メイン武器
    var secondary = ""//サブ武器
    var grenade = ""//グレネード
    var knife = "" //ナイフ

    override fun provide(instance: MoWeaponSlot) {
        id = instance.id.toString
        primary = instance.primary.toString
        secondary = instance.secondary.toString
        grenade = instance.grenade.toString
        knife = instance.knife.toString
    }

    override fun supply(instance: MoWeaponSlot) {
        instance.id set id
        instance.primary set primary
        instance.secondary set secondary
        instance.grenade set grenade
        instance.knife set knife
    }
}
