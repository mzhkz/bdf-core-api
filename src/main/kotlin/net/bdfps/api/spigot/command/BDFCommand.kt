package net.bdfps.api.spigot.command

import net.bdfps.api.spigot.BDF
import net.bdfps.api.spigot.support.BDFPlayer
import net.bdfps.api.spigot.utility.toBDFPlayer
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class BDFCommand(val command: String) : CommandExecutor {

    abstract fun onCommand(info: BDFPlayer, args: Array<String>, name: String): Boolean

    override fun onCommand(sender: CommandSender, arg1: Command, arg2: String,
                           arg3: Array<String>): Boolean {
        if (sender is Player) {
            val player = sender.toBDFPlayer()
            if (player != null)
                onCommand(player, arg3, arg2)
            else
                sender.sendMessage(ChatColor.RED.toString() + "致命的なエラーが発生しました。再ログインしてください。")
        } else
            sender.sendMessage("このコマンドはCONSOLEから実行は出来ないよ!")
        return true
    }

    /** コマンドをBukkitに登録する*/
    fun registerBukkit() {
        BDF.instance.getCommand(command).executor = this
    }

}
