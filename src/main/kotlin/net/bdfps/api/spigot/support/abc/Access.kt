package net.bdfps.api.spigot.support.abc

import net.bdf.api.data.abc.MoAccess
import java.util.*

class Access: BDFMobject<MoAccess>() {
    var address = ""
    var host = ""
    var created = Date()

    override fun provide(instance: MoAccess) {
        address = instance.address.toString
        host = instance.address.toString
        created = Date(instance.created.toLong * 1000)
    }

    override fun supply(instance: MoAccess) {
        instance.address set address
        instance.address set host
        instance.created set created.toInstant().epochSecond
    }
}
