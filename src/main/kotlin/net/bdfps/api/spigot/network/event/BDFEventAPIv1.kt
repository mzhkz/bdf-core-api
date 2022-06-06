package net.bdfps.api.spigot.network.event

import com.launchdarkly.eventsource.EventSource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.bdfps.api.spigot.BDFConfig
import net.bdfps.api.spigot.network.OAuthAuthorize
import okhttp3.Headers
import java.net.URI
import java.util.concurrent.TimeUnit

object BDFEventAPIv1 {

    private var eventSource: EventSource? = null

    /** イベントストリームを開始する*/
    fun connect() {
        val eventHandler = BDFEventHandler()
        val url = "http://${BDFConfig.resourceServerAddress}/v1/stream/event"

        val builder = EventSource.Builder(eventHandler, URI.create(url))
                .headers(Headers.of(mutableMapOf("Authorization" to "Bearer ${OAuthAuthorize.token}")))
        GlobalScope.launch {
            builder.build().use{
                eventSource = it
                eventSource?.start()
                TimeUnit.HOURS.sleep(24) //スリープさせる
            }
        }
    }

    /** ストリームを閉じる*/
    fun close() {
        eventSource?.close()
    }
}
