package net.bdfps.api.spigot.command

import net.bdf.api.data.Mobject
import net.bdfps.api.spigot.gui.TestMenu
import net.bdfps.api.spigot.network.event.BDFEvent
import net.bdfps.api.spigot.support.BDFPlayer

class BDFPlusCommand: BDFCommand("plus") {

    override fun onCommand(info: BDFPlayer, args: Array<String>, name: String): Boolean {
       when (args.size) {
           1 -> {
               when (args[0].toLowerCase()) {
                   "register" -> {
                       info.formPlusAccountRegisterRequest()
                   }
                   "email" -> {
                       info.formPlusAccountChangeEmailRequest()
                   }
                   "admin" -> {
                       if (info.bukkitPlayer.isOp) {
                           TestMenu(info).open()
                       }
                   }
               }
           }
       }
        return false
    }
}
