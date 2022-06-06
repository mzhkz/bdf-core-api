package net.bdfps.api.spigot.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.LeavesDecayEvent
import org.bukkit.event.weather.WeatherChangeEvent

/** ワールド関係のイベント管理リスナ */
class WorldListener: Listener {

    /** 葉っぱを枯らせないようにする*/
    @EventHandler
    fun notDestroyLeaves(event: LeavesDecayEvent) {
        event.isCancelled = true
    }


    /** 雨を振らせないようにする */
    @EventHandler
    fun notWeatherChanged(event: WeatherChangeEvent) {
        event.isCancelled = true
    }
}
