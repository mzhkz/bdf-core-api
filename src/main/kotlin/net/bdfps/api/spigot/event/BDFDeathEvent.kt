package net.bdfps.api.spigot.event

import net.bdfps.api.spigot.entity.BDFEntity
import net.bdfps.api.spigot.event.base.BaseEvent

open class BDFDeathEvent(val entity: BDFEntity<*>) : BaseEvent() {

}
