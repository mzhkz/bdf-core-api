package net.bdfps.api.spigot.event

import net.bdfps.api.spigot.entity.BDFEntity
import net.bdfps.api.spigot.weapon.bullet.BDFBullet

class BDFBulletDeathEvent(entity: BDFEntity<*>, val lastDamage: BDFEntity.BulletDamageSource) : BDFDeathEvent(entity) {
}
