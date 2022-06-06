package net.bdfps.api.spigot.event

import net.bdfps.api.spigot.entity.BDFEntity

class BDFDamageByEntityEvent(entity: BDFEntity<*>, damageCause: BDFEntity.EntityDamageSource) : BDFDamageEvent(entity, damageCause)  {

    val entityDamageCause: BDFEntity.EntityDamageSource by lazy {
        damageSource as BDFEntity.EntityDamageSource
    }

    val damager: BDFEntity<*>
        get() = entityDamageCause.damager
}
