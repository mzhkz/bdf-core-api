package net.bdfps.api.spigot.support.abc

import net.bdf.api.data.Mutant
import net.bdf.api.data.abc.MoUser
import net.bdf.api.data.utilty.generateRandomSHA1

class WeaponCustomize : BDFMobject<MoUser.MoUserWeaponCustomize>() {
    var id = generateRandomSHA1
    var weaponID = ""
    var customName = ""
    var disableSale = false
    var kills = 0
    var damage = 0L
    var attachments = arrayListOf<String>()

    override fun provide(instance: MoUser.MoUserWeaponCustomize) {
        id = instance.id.toString
        weaponID = instance.weaponID.toString
        customName = instance.customName.toString
        disableSale = instance.disableSale.toBoolean
        kills = instance.kills.toInt
        damage = instance.kills.toLong
        attachments = arrayListOf<String>().apply {
            instance.toList.forEach {
                add(it.toString)
            }
        }

    }

    override fun supply(instance: MoUser.MoUserWeaponCustomize) {
        instance.id set id
        instance.weaponID set weaponID
        instance.customName set customName
        instance.disableSale set disableSale
        instance.kills set kills
        instance.damage set damage
        instance.attachments set arrayListOf<Mutant>().apply {
            attachments.forEach {
                add(Mutant(it))
            }
        }
    }
}
