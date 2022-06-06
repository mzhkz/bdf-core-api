package net.bdfps.api.spigot.support.abc

import net.bdf.api.data.abc.MoUser
import java.util.*

class UserCommunity: BDFMobject<MoUser.MoUserCommunity>() {
    var target = ""
    var blacked = false
    var friend = false
    var created = Date()
    var updated = Date()

    override fun provide(instance: MoUser.MoUserCommunity) {
        target = instance.target.toString
        blacked = instance.blacked.toBoolean
        friend = instance.friend.toBoolean
        created = Date(instance.created.toLong * 1000)
        updated = Date(instance.updated.toLong * 1000)
    }

    override fun supply(instance: MoUser.MoUserCommunity) {
        instance.target set target
        instance.blacked set blacked
        instance.friend set friend
        instance.created set created.toInstant().epochSecond
        instance.updated set  updated.toInstant().epochSecond
    }
}
