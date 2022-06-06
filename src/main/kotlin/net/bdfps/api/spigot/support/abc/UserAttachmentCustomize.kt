package net.bdfps.api.spigot.support.abc

import net.bdf.api.data.abc.MoUser
import net.bdf.api.data.utilty.generateRandomSHA1

class UserAttachmentCustomize : BDFMobject<MoUser.MoUserAttachmentCustomize>() {
    var id = generateRandomSHA1
    var attachmentID = ""
    var disableSale = false

    override fun provide(instance: MoUser.MoUserAttachmentCustomize) {
        id = instance.id.toString
        attachmentID = instance.attachmentID.toString
        disableSale = instance.disableSale.toBoolean
    }

    override fun supply(instance: MoUser.MoUserAttachmentCustomize) {
        instance.id set id
        instance.attachmentID set attachmentID
        instance.disableSale set disableSale
    }
}
