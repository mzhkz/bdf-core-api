@file:JvmName("BDF")

package net.bdfps.api.spigot

import net.bdf.api.data.Mobject
import net.bdf.api.data.abc.MoServer
import net.bdf.api.data.utilty.currentUnixTime
import net.bdf.api.data.utilty.generateRandomSHA1
import net.bdfps.api.spigot.command.AdminCommand
import net.bdfps.api.spigot.command.BDFPlusCommand
import net.bdfps.api.spigot.listener.GuiListener
import net.bdfps.api.spigot.listener.PlayerListener
import net.bdfps.api.spigot.listener.PluginListener
import net.bdfps.api.spigot.listener.WorldListener
import net.bdfps.api.spigot.managers.BDFManager
import net.bdfps.api.spigot.network.*
import net.bdfps.api.spigot.network.event.BDFEventAPIv1
import net.bdfps.api.spigot.support.BDFPlayer
import net.bdfps.api.spigot.utility.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/** BDF API メインクラス */
class BDF : JavaPlugin() {
    companion object {
        @JvmStatic
        lateinit var instance: BDF

        /** プラグインフォルダーを返す */
        @JvmStatic
        fun getPluginFolder(): String? = instance.dataFolder?.absolutePath


        /** プラグインのバージョンを返します*/
        @JvmStatic
        val version: String by lazy {
            this.instance.description.version
        }

        /** Jarファイルを返す */
        @JvmStatic
        val jar: File by lazy {
            instance.file
        }

        /** オンラインプレイヤーを返す */
        @JvmStatic
        val onlinePlayers: Set<BDFPlayer>
            get() = BDFManager.playerManager.localPlayers.toSet()

        /** ローカルのサーバデータを返す */
        @JvmStatic
        val local: MoServer by lazy {
            MoServer().apply {
                serverId set generateRandomSHA1 //IDを付与
                created set currentUnixTime //作成日時
                serverType set "spigot" //Spigot only
            }
        }

        /** サーバの状態を管理する*/
        @JvmStatic
        var status: String
            get() = local.status.toString
            set(value) {
                local.status set value
                updateLocalData() //サーバデータを更新する
            }

        /** 進行中のマップを管理する */
        @JvmStatic
        var map: String
            get() = local.map.toString
            set(value) {
                local.map set value
                updateLocalData() //サーバデータを更新する
            }

        /** ローカルサーバデータを更新*/
        @JvmStatic
        fun updateLocalData() {
            local.serverName set BDFConfig.serverName
            local.maxPlayers set Bukkit.getMaxPlayers()
            local.serverPort set Bukkit.getPort()
            local.updated set currentUnixTime//最終更新日時
            local.players set onlinePlayers.map {
                Mobject().apply {
                    "id" mo it.bdfID
                    "name" mo it.name
                    "uuid" mo it.uuid
                }
            }
            local.loaded set true



            BDFAPIv1.async(RequestMethod.POST, "${APIRoot.SERVER}/", query = listOf("value" to local.json())) { res, _ ->
                if (res.get("message").toString != "success")
                    throw Exception("ローカルデータの更新に失敗しました ${res.json(pretty = true)} ${local.json()}")
            }
        }
    }

    override fun onEnable() {
        instance = this
        BDFConfig.loadConfig()
        BDFManager.initialize()

        offline {
            val warn = "** サーバはオフラインモードで起動しています!! データは共有されません **"
            broadcast(ChatColor.RED.toString() + warn)
            broadcast(ChatColor.RED.toString() + warn)
        }

        online {
            BDFAPIv1Handler.connect()
            updateLocalData() //サーバを登録
        }

        PlayerListener().registerBukkit()
        WorldListener().registerBukkit()
        PluginListener().registerBukkit()
        GuiListener().registerBukkit()

        BDFPlusCommand().registerBukkit()
        AdminCommand().registerBukkit()

        BDFManager.playerManager.reload()

        //TODO Entityのダメージ関係のイベントリスナを追加してください

        BDFCommonTask.start() //タスク開始

        info("読み込みました v$version")

//        AsyncBLockController(px = 1020, py = 4, pz = 785, length = 5).exec()
    }

    override fun onDisable() {
        online {
            OAuthAuthorize.shutdownUpdater() //タスクを止める
            BDFEventAPIv1.close() //SSE閉じる
        }
        BDFCommonTask.stop() //タスク終了
    }
}
