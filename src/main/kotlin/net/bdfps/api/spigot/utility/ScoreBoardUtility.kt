package net.bdfps.api.spigot.utility

import net.bdfps.api.spigot.BDF
import net.bdfps.api.spigot.support.BDFPlayer
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.NameTagVisibility
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team

var defaultColor = ChatColor.WHITE

var colors = arrayOf("aqua", "black", "blue", "dark_aqua", "dark_blue", "dark_gray", "dark_green", "dark_purple", "dark_red", "gold", "gray", "green", "light_purple", "red", "white", "yellow")

fun leavePlayerTeam(leave: BDFPlayer) {
    val main = getMainScoreboard()
    leavePlayerTeam(main, leave.bukkitPlayer)
    val players = onlinePlayers
    for (i in players.indices.reversed()) {
        val scoreboard = players.get(i).scoreBoard
        leavePlayerTeam(scoreboard, leave.bukkitPlayer)
    }
}

private fun leavePlayerTeam(scoreboard: Scoreboard, player: Player) {
    val team = getPlayerTeam(scoreboard, player)
    team?.removePlayer(player)
}

private fun getPlayerTeam(scoreboard: Scoreboard, player: Player): Team? {
    val teams = scoreboard.teams
    for (team in teams) {
        if (team != null) {
            for (p in team.players) {
                if (p.isOnline) {
                    if (p.name.equals(player.name, ignoreCase = true)) {
                        return team
                    }
                } else {
                    team.removePlayer(p)
                }
            }
        }
    }
    return null
}

/**
 * プレイヤーをチームに追加する(ゲーム用)
 * @param add
 * @param color
 */
fun addPlayerTeam(add: BDFPlayer, color: String) {
    leavePlayerTeam(add)
    val main = getMainScoreboard()
    addPlayerTeam(main, add.bukkitPlayer, color)
    val players = onlinePlayers
    for (i in players.indices.reversed()) {
        val scoreboard = players.get(i).scoreBoard
        addPlayerTeam(scoreboard, add.bukkitPlayer, color)
    }
}

/**
 * 個人のスコアボードにチームを作って返すメソッド
 * @param scoreboard - 追加するスコアボード
 * @param player - 追加するプレイヤー
 * @param color - 入れる色
 */
private fun addPlayerTeam(scoreboard: Scoreboard, player: Player, color: String): Team {
    val team = addTeam(scoreboard, color)
    team.addPlayer(player)
    //フレンドリーファイアを有効に
    team.setAllowFriendlyFire(false)
    team.setCanSeeFriendlyInvisibles(false)
    //違うチームからタグが見えないようにする。
    team.nameTagVisibility = NameTagVisibility.HIDE_FOR_OTHER_TEAMS
    return team
}

/**
 * 個人のスコアボードにカラーチームを追加して返す。
 */
private fun addTeam(scoreboard: Scoreboard, colorName: String): Team {
    return addTeam(scoreboard, colorName, ColorReplace(colorName).toString() + colorName + ChatColor.RESET.toString(), ColorReplace(colorName).toString(), ChatColor.RESET.toString())
}

/**
 * 個人のスコアボード(scoreboard)にチームを作って返す。
 * @param scoreboard
 * @return
 */
private fun addTeam(scoreboard: Scoreboard, teamName: String, displayName: String, prefix: String, suffix: String): Team {
    var team: Team? = scoreboard.getTeam(teamName)
    if (team == null) {
        team = scoreboard.registerNewTeam(teamName)
        team!!.displayName = displayName
        team.prefix = prefix
        team.suffix = suffix
        team.setAllowFriendlyFire(true)
    }
    return team
}

/**
 * 文字列から色を返す。
 * @param color
 * @return
 */
fun ColorReplace(color: String): ChatColor {
    var check = false
    for (name in colors) {
        if (name == color) {
            check = true
            break
        }
    }
    return if (check) {
        ChatColor.valueOf(color.toUpperCase())
    } else {
        defaultColor
    }
}

/**
 * メインスコアボードを返す。
 * 同期用
 * @return
 */
fun getMainScoreboard(): Scoreboard {
    return BDF.instance.server.scoreboardManager.mainScoreboard
}

/**
 * メインと同期させる。
 */
fun syncMainScoreboard(scoreboard: Scoreboard) {
    val main = getMainScoreboard()
    for (mteam in main.getTeams()) {
        for (mplayer in mteam.players) {
            if (mplayer.isOnline) {
                val add = mplayer as Player
                addPlayerTeam(scoreboard, add, mteam.name)
            }
        }
    }
}
