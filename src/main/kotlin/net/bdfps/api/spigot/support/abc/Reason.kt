package net.bdfps.api.spigot.support.abc

import net.bdf.api.data.abc.MoReason
import net.bdf.api.data.generaterandomSHA1
import net.bdf.api.data.utilty.generateRandomSHA1
import java.util.*

class Reason: BDFMobject<MoReason>() {
    var id = generateRandomSHA1
    var reason = ""
    var targetID = ""
    var valid = false
    var notification = false
    var created = Date()
    var expires = Date()
    var sendServer = ""
    var representative  = "console" //処罰者のID
    var loaded = false

    override fun provide(instance: MoReason) {
        id = instance.id.toString
        reason = instance.reason.toString
        targetID = instance.targetID.toString
        valid = instance.valid.toBoolean
        notification = instance.notification.toBoolean
        created = Date(instance.created.toLong * 1000)
        expires = Date(instance.expires.toLong * 1000)
        sendServer = instance.server.toString
        representative = instance.representative.toString
        loaded = instance.loaded.toBoolean
    }

    override fun supply(instance: MoReason) {
        instance.id set id
        instance.reason set reason
        instance.targetID set targetID
        instance.valid set valid
        instance.notification set notification
        instance.created set created.toInstant().epochSecond
        instance.expires set expires.toInstant().epochSecond
        instance.server set sendServer
        instance.representative set representative
        instance.loaded set loaded
    }
}
