package net.bdfps.api.spigot.support.abc

import net.bdf.api.data.abc.MoUser

class Option: BDFMobject<MoUser.MoOption>() {
    var chatJapaneseConversion = true
    var playDisplayHitAction = true
    var playRequestMusic = true
    var playBloodshed = true //流血表現

    override fun provide(instance: MoUser.MoOption) {
        chatJapaneseConversion = instance.chatJapaneseConversion.toBoolean
        playDisplayHitAction = instance.playDisplayHitAction.toBoolean
        playRequestMusic = instance.playRequestMusic.toBoolean
        playBloodshed = instance.playBloodshed.toBoolean
    }

    override fun supply(instance: MoUser.MoOption) {
        instance.chatJapaneseConversion set chatJapaneseConversion
        instance.playDisplayHitAction set playDisplayHitAction
        instance.playRequestMusic set playRequestMusic
        instance.playBloodshed set playBloodshed
    }
}
