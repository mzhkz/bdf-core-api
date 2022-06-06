package net.bdfps.api.spigot.support.abc

import net.bdf.api.data.abc.MoMatch
import net.bdf.api.data.utilty.generateRandomSHA1
import net.bdfps.api.spigot.BDF
import net.bdfps.api.spigot.BDFConfig
import java.util.*

class Match: BDFMobject<MoMatch>() {

    var id = generateRandomSHA1
    var server = BDFConfig.serverName
    var map = BDF.local.map.toString
    var gameMode = BDF.local.gameMode.toString
    var started = Date()
    var finished = Date()
    var playerStatistics = arrayListOf<PlayerStatistics>()
    var loaded = false

    override fun provide(instance: MoMatch) {
        //TODO MAPの読み込み関係まだ
    }

    override fun supply(instance: MoMatch) {
        instance.id set id
        instance.server set server
        instance.map set map
        instance.gameMode set gameMode
        instance.started set started.toInstant().epochSecond
        instance.finished set finished.toInstant().epochSecond
        instance.playerStatistics set playerStatistics.map { it.export<MoMatch.MoPlayerStatistics>() }
        instance.loaded set true
    }

    class PlayerStatistics : BDFMobject<MoMatch.MoPlayerStatistics>() {
        var id = ""
        var kills = 0
        var deaths = 0
        var assists = 0
        var timePlayed = 0
        var weaponStatistics = arrayListOf<WeaponStatisticsData>()

        override fun provide(instance: MoMatch.MoPlayerStatistics) {

        }

        override fun supply(instance: MoMatch.MoPlayerStatistics) {
            instance.id set id
            instance.kills set kills
            instance.deaths set deaths
            instance.assists set assists
            instance.timePlayed set timePlayed
            instance.weaponStatistics set weaponStatistics.map { it.export<MoMatch.MoWeaponStatisticsData>() }
        }
    }

    class WeaponStatisticsData : BDFMobject<MoMatch.MoWeaponStatisticsData>() {

        var id = ""
        var kills = 0
        var bulletAccuracy = 0
        var bulletHeadShots = 0
        var timePlayed = 0

        override fun provide(instance: MoMatch.MoWeaponStatisticsData) {

        }

        override fun supply(instance: MoMatch.MoWeaponStatisticsData) {
            instance.id set id
            instance.kills set kills
            instance.bulletAccuracy set bulletAccuracy
            instance.bulletHeadShots set bulletHeadShots
            instance.timePlayed set timePlayed
        }
    }
}
