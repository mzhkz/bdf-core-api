/*
import com.github.kittinunf.fuel.Fuel
import java.util.concurrent.TimeUnit
import com.launchdarkly.eventsource.EventSource
import kotlinx.coroutines.runBlocking
import net.bdfps.api.spigot.BDFConfig
import java.net.URI


fun main(args: Array<String>) {
    runBlocking {
        val (request, response, result) = Fuel.get("https://stream.wikimedia.org/v2/stream/recentchange").timeoutRead(3000).awaitStringResponse()

        result.fold({ data ->
            println(data) // "{"origin":"127.0.0.1"}"
        }, { error ->
            println("An error of type ${error.exception} happened: ${error.message}")
        })
    }
}
*/