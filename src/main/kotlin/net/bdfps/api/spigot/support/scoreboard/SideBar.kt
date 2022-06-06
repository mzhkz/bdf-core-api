package net.bdfps.api.spigot.support.scoreboard

import net.bdfps.api.spigot.support.BDFPlayer
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard

class SideBar(private val playerInfo: BDFPlayer) {
    private val player: Player
    private var scoreboard: Scoreboard//スコアボード
    private val objective: Objective
    private var words: Array<String?>
    private var title: String? = null

    init {
        this.player = playerInfo.bukkitPlayer
        this.scoreboard = playerInfo.scoreBoard

        var obj: Objective? = this.scoreboard.getObjective("bdf_sidebar")
        if (obj == null) {// 取得できなかったら新規作成する
            obj = this.scoreboard.registerNewObjective("bdf_sidebar", "dummy")
        }
        this.objective = obj!!
        words = arrayOfNulls(15)
        objective.displaySlot = DisplaySlot.SIDEBAR
    }

    fun resetWords() {
        words = arrayOfNulls(15)
    }

    /**
     * 0 <= number <= 14
     * @param word
     * @param number
     */
    fun setWord(word: String, number: Int) {
        var word = word
        if (0 <= number && number <= 14) {
            for (i in words.indices.reversed()) {
                //重複チェック
                if (words[i] != null && i != number && words[i] == word) {
                    word += getColor(number)
                    break
                }
            }
            words[number] = word
        }
    }

    private fun getColor(number: Int): ChatColor {
        var i = 0
        for (color in ChatColor.values()) {
            if (i == number) {
                return color
            }
            i++
        }
        return ChatColor.RESET
    }

    /**
     * Listからset
     * @param arrays
     */
    fun setWords(vararg arrays: String) {
        var score = arrays.size
        val line = score
        val max = 15
        var point = 0
        while (1 <= score) {
            if (max <= point) {
                break
            }
            this.setWord(arrays[line - score], score)

            point++
            score--
        }
    }

    fun setTitle(title: String) {
        this.title = title
    }

    /**
     * 更新。
     */
    fun updateScoreBoard() {
        objective.displayName = title
        for (i in words.indices.reversed()) {
            val score = i + 1
            val word = words[i]
            if (word != null) objective.getScore(word).score = score
        }
        for (string in scoreboard.entries) {
            var b = true
            for (i in words.indices.reversed()) {
                //含まれていなかった場合除去
                if (words[i] != null && words[i] == string) {
                    b = false
                    break
                }
            }
            if (b) scoreboard.resetScores(string)
        }
    }
}
