package net.bdfps.api.spigot.event

import net.bdfps.api.spigot.entity.BDFEntity
import net.bdfps.api.spigot.weapon.bullet.BDFBullet

class BDFBulletDamageEvent(entity: BDFEntity<*>, val bulletDamageCause: BDFEntity.BulletDamageSource) : BDFDamageEvent(entity, bulletDamageCause) {


    val bullet: BDFBullet
        get() = bulletDamageCause.bullet
}
