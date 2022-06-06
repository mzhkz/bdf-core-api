package net.bdfps.api.spigot.network

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.bdf.api.data.Mobject
import net.bdf.api.data.MobjectParser
import net.bdfps.api.spigot.BDFConfig
import net.bdfps.api.spigot.utility.sendConsoleMessage
import org.bukkit.ChatColor
import java.nio.charset.Charset

/**
 * APIサーバにアクセスするための処理達
 * @author ManjiJapan
 */
object BDFAPIv1 {

    private fun connection(requestMethod: RequestMethod, path: String, body: String, list: List<Pair<String, *>>): Request {
        val endPoint = "http://${BDFConfig.resourceServerAddress}/v1$path"
        val request: Request =
        when (requestMethod) {
            RequestMethod.GET -> Fuel.get(endPoint, list) //取得
            RequestMethod.POST -> Fuel.post(endPoint, list) //追加・更新
            RequestMethod.PUT -> Fuel.put(endPoint, list) //更新
            RequestMethod.PATCH -> Fuel.patch(endPoint, list) //更新・一部更新
            RequestMethod.DELETE -> Fuel.delete(endPoint, list) //削除
        }.header("Authorization" to "Bearer ${OAuthAuthorize.token}").timeout(4000)
        return if (body.isEmpty()) request else request.body(body.toByteArray(Charset.forName("UTF-8")))
    }

    /**
     * 同期取得
     * @param requestMethod メソッド名
     * @param path APIのパス
     * @param query クエリ一覧
     */
    fun response(requestMethod: RequestMethod, path: String, body: String, query: List<Pair<String,*>>): Mobject =
            parser(String(connection(requestMethod, path, body, query).response().second.data))

    /**
     * 同期取得
     * @param requestMethod メソッド名
     * @param path APIのパス
     * @param query クエリ一覧
     */
    fun sync(requestMethod: RequestMethod, path: String, body: String = "", query: List<Pair<String,*>> = listOf()): Mobject {
        val res = response(requestMethod, path, body, query.toList())
        val statusCode = res.get("code").toInt
        if (statusCode != 200) {
            throw BDFAPIv1RequestException(res.get("message").toString, res)
        }
        return res
    }

    /**
     * 非同期処理 コールバックで返す
     * @param requestMethod メソッド名
     * @param path APIのパス
     * @param query クエリ一覧
     * @param callback データ取得後のコールバック処理
     */
    fun async(requestMethod: RequestMethod, path: String, body: String = "", query: List<Pair<String,*>> = listOf(), callback: (res: Mobject, error: Boolean) -> Unit) {
        GlobalScope.launch {
            val response = response(requestMethod, path, body, query.toList())
            val statusCode = response.get("code").toInt
            callback(response, statusCode != 200)
        }
    }

    /**
     * サーバから取得した情報を解析して中身を取り出す
     */
    private fun parser(res: String): Mobject {
        if (BDFConfig.apiResponseLogger) { //APIの通信内容をコンソールに出力する
            sendConsoleMessage(ChatColor.GOLD.toString() + res)
        }
        val parse: Mobject =
        try {
            MobjectParser.mobject(res) //解析的なければエラー。APIサーバからは絶対にJSONで返される
        } catch (e: Exception) {
            Mobject().apply { //クライアントでの内部エラー
                "code" mo 501 //Connection timed out
                "message" mo "APIサーバの接続に失敗しました"
                "result" mo "{}" //空のJSONを一応入れておく
            }
        }
        return parse
    }
}
