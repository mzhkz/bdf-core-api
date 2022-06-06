package net.bdfps.api.spigot.support.abc

import net.bdf.api.data.abc.MoGame

class Game : BDFMobject<MoGame>() {
    var name = ""
    var playing = 0
    var win = 0
    var point = 0
    var kills = 0
    var playingMin = 0

    override fun provide(instance: MoGame) {
        name = instance.name.toString
        playing = instance.playing.toInt
        win = instance.win.toInt
        point = instance.point.toInt
        kills = instance.kills.toInt
        playingMin = instance.playingMin.toInt
    }

    override fun supply(instance: MoGame) {
        instance.name set name
        instance.playing set playing
        instance.win set win
        instance.point set point
        instance.kills set kills
        instance.playingMin set playingMin
    }
}
