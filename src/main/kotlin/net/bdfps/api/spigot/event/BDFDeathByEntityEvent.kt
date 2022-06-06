package net.bdfps.api.spigot.event

import net.bdfps.api.spigot.entity.BDFEntity

class BDFDeathByEntityEvent(entity: BDFEntity<*>, val killer: BDFEntity<*>) : BDFDeathEvent(entity)
