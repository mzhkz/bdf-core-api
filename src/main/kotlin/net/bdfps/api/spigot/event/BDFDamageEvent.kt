package net.bdfps.api.spigot.event

import net.bdfps.api.spigot.entity.BDFEntity
import net.bdfps.api.spigot.event.base.BaseCancellableEvent
import net.bdfps.api.spigot.support.BDFPlayer

open class BDFDamageEvent(val entity: BDFEntity<*>, val damageSource: BDFEntity.DamageSource) : BaseCancellableEvent() {

    val damage: Double
        get() = damageSource.damage


    val cause: BDFPlayer.BDFDamageCause
        get() = damageSource.cause
}
