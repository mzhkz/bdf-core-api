package net.bdfps.api.spigot.managers

import net.bdfps.api.spigot.weapon.bullet.BDFBullet

class BulletManager {
    var bullets: MutableList<BDFBullet> = mutableListOf()

    fun tick() {
        bullets.toList().forEach { it.onTick() }
    }

    fun addBullet(bullet: BDFBullet) {
        bullets.add(bullet)
    }

    fun remove(bullet: BDFBullet) {
        bullets.remove(bullet)
    }

}
