package net.bdfps.api.spigot

import org.bukkit.plugin.java.JavaPlugin

/** 拡張プラグインを作る際に継承するクラス */
abstract class BDFPlugin: JavaPlugin() {

    abstract fun onStart()

    abstract fun onStop()

    override fun onEnable() {
       //TODO 起動処理
        onStart()
    }

    override fun onDisable() {
       //TODO 更新処理
        onStop()
    }
}

