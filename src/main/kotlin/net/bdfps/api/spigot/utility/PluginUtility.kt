package net.bdfps.api.spigot.utility

import net.bdfps.api.spigot.BDF
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin


fun Listener.registerBukkit() = this.registerEvents(BDF.instance)

fun Listener.registerEvents(plugin: JavaPlugin) = Bukkit.getPluginManager().registerEvents(this, plugin)

fun Event.callEvent() = Bukkit.getPluginManager().callEvent(this)

fun info(_msg: String) = Bukkit.getLogger().info(_msg)

fun warn(_msg: String) = Bukkit.getLogger().warning(_msg)
