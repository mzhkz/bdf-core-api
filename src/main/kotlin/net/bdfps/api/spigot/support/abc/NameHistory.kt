package net.bdfps.api.spigot.support.abc

import net.bdf.api.data.abc.MoUser
import java.util.*

class NameHistory: BDFMobject<MoUser.MoNameHistory>() {
    var beforeName = ""
    var afterName = ""
    var created = Date()
    var notification = false

    override fun provide(instance: MoUser.MoNameHistory) {
        beforeName = instance.beforeName.toString
        afterName = instance.afterName.toString
        created = Date(instance.created.toLong * 1000)
        notification = instance.notification.toBoolean
    }

    override fun supply(instance: MoUser.MoNameHistory) {
        instance.beforeName set beforeName
        instance.beforeName set afterName
        instance.created set created.toInstant().epochSecond
        instance.notification set notification
    }
}
