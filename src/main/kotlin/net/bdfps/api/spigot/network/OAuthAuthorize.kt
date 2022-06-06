package net.bdfps.api.spigot.network

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.result.Result
import net.bdf.api.data.Mobject
import net.bdf.api.data.MobjectParser
import net.bdfps.api.spigot.BDFConfig
import net.bdfps.api.spigot.utility.shutdown
import java.util.*
import kotlin.concurrent.timer

/**
 * 認証サーバにアクセストークンを発行リクエストを送る
 * @author ManjiJapan
 */
object OAuthAuthorize {

    private var tick = 0
    private var accessToken: String = "" //アクセストークン
    private var timer: Timer? = null

    /** アクセストークン */
    val token: String
        get() = accessToken

    /** 準備ができたか*/
    val ready: Boolean
        get() = !token.isEmpty()

    /** Connection */
    private val connection: Request
        get() {
            val tokenURI = "http://${BDFConfig.authorizeServerAddress}/oauth/api/token"
            return Fuel.post(tokenURI,
                    listOf(
                            "client_id" to BDFConfig.oauthClientID, //アプリのID
                            "client_secret" to BDFConfig.oauthClientSecret, //アプリのシークレット
                            "grant_type" to "client_credentials", //コード取得のみ
                            "scope" to "client" //スコープ範囲「Client」
                    )
            )
        }

    /** 同期認証 */
    fun syncAuthorize() {
        val response = connection.response().second.data
        val result = String(response)
        setResponse(result) //データを適用
    }

    /** 非同期認証 更新などに*/
    fun asyncAuthorize() {
        connection.response { request, response, result ->
            when (result) {
                is Result.Success -> {
                    setResponse(String(response.data))
                }
                is Result.Failure -> {
                    shutdown("BDF認証サーバの接続に失敗 ${String(response.data)}")
                }
            }
        }
    }

    /** 自動更新を有効にする*/
    fun enableUpdater() {
        timer = startUpdateTask()
    }

    /** タイマーを止める */
    fun shutdownUpdater() = timer?.cancel()

    /** 一定時間おきに更新する */
    private fun startUpdateTask() = timer(initialDelay = 1000, period = 1000) {
        val updateDelay = 1740 //期限の少し前に更新 29分30秒
        if (tick % updateDelay == 0 && tick > 1) {
            syncAuthorize() //非同期で更新
            tick = 0
        }
        tick++
    }

    /**
     * 受け取ったデータを適用する
     * @param json 帰ってきたデータ
     */
    private fun setResponse(json: String) {
        val parse: Mobject =
                try {
                    MobjectParser.mobject(json) //解析的なければエラー。APIサーバからは絶対にJSONで返される
                } catch (e: Exception) {
                    Mobject().apply {
                        //クライアントでの内部エラー
                        "code" mo 408 //Connection timed out
                        "message" mo e.localizedMessage
                        "result" mo "{}" //空のJSONを一応入れておく
                    }
                }
        val statusCode = parse.find("code")
        if (statusCode != null) { //エラーが発生した場合
            val message = parse.get("message").toString //エラーメッセージを取得
            shutdown("BDF認証サーバの検証に失敗: $message")
        }
        /* 各種必要なデータを取得 */
        accessToken = parse.get("access_token").toString
        val scope = parse.get("scope").toString
        val token_type = parse.get("token_type").toString
        val expires_in = parse.get("expires_in").toString
    }
}
