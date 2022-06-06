@file:JvmName("BDFConfig")
package net.bdfps.api.spigot

import net.bdfps.api.spigot.utility.info
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.file.Files

/**
 * Created by hsyhrs on 2017/09/30.
 */
object BDFConfig {

    @JvmStatic
    lateinit var config: YamlConfiguration

    @JvmStatic
    val serverName: String by lazy {
        config.getString("server_name")!!
    }

    @JvmStatic
    val resourceServerAddress: String by lazy {
        config.getString("resource_server_address")!!
    }

    @JvmStatic
    val authorizeServerAddress: String by lazy {
        config.getString("authorize_server_address")!!
    }

    @JvmStatic
    val onlineMode: Boolean by lazy {
        config.getBoolean("online_Mode")
    }

    @JvmStatic
    val oauthClientID: String by lazy {
        config.getString("authorize_client_id")!!
    }

    @JvmStatic
    val oauthClientSecret: String by lazy {
        config.getString("authorize_client_secret")!!
    }

    @JvmStatic
    val apiResponseLogger: Boolean by lazy {
        config.getBoolean("api_response_logger")
    }


    /** 初回起動時に設定ファイルをJarファイルからコピーします*/
    private fun copyFile(): File {
        val str = javaClass.getResourceAsStream("/config.yml")
        val folder = File(BDF.getPluginFolder())
        val config = File(BDF.getPluginFolder() + "/config.yml")
        if (!folder.exists()) {
            folder.mkdirs()
            info("*** 初回起動です！プラグインフォルダーを新規生成しました ***")
            info("元ファイル: resource/config.yml")
            info("コピー先: " + config.toPath())
            Files.copy(str, config.toPath())
        }
        return config
    }

    /** 設定ファイルを読み込みます*/
    fun loadConfig() {
        var file = File(BDF.getPluginFolder(), "config.yml")
        if (!file.exists())
           file = copyFile()
        config = YamlConfiguration.loadConfiguration(file)
    }
}
