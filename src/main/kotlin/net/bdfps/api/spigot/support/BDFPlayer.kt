@file:JvmName("BDFPlayer")

package net.bdfps.api.spigot.support

import com.comphenix.protocol.wrappers.EnumWrappers
import net.bdf.api.data.Mobject
import net.bdf.api.data.abc.*
import net.bdf.api.data.utilty.generateRandomSHA1
import net.bdfps.api.spigot.BDF
import net.bdfps.api.spigot.entity.BDFEntity
import net.bdfps.api.spigot.event.*
import net.bdfps.api.spigot.event.base.BDFSyncUserDataEvent
import net.bdfps.api.spigot.event.base.BaseCancellableEvent
import net.bdfps.api.spigot.java.apis.TitleAPI
import net.bdfps.api.spigot.java.packet.PacketUtil
import net.bdfps.api.spigot.java.packet.ParticleAPI
import net.bdfps.api.spigot.java.packet.protocolLib.ProtocolLibPacket
import net.bdfps.api.spigot.java.packet.protocolLib.packetwrapper.WrapperPlayServerEntityEquipment
import net.bdfps.api.spigot.managers.BDFManager
import net.bdfps.api.spigot.network.BDFAPIv1
import net.bdfps.api.spigot.network.APIRoot
import net.bdfps.api.spigot.network.RequestMethod
import net.bdfps.api.spigot.support.abc.*
import net.bdfps.api.spigot.support.scoreboard.SideBar
import net.bdfps.api.spigot.utility.syncMainScoreboard
import net.bdfps.api.spigot.utility.*
import net.bdfps.api.spigot.weapon.*
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.util.Vector
import java.util.*

class BDFPlayer(val bukkitPlayer: Player, divideTask: Boolean = true) : BDFEntity<MoUser>(bukkitPlayer, divideTask) {

    /** ユーザ基本データ*/
    var bdfID: String = ""
    var name: String //名前
    var uuid: String = ""
    var web: String = "" //リンクした場合はWebユーザID

    /** ユーザ数値データ一覧 */
    var exp = 0
    var wins = 0
    var loses = 0
    var kills = 0
    var deaths = 0
    var assists = 0
    var rating = 0
    var haveBP = 0 //ゲーム内通貨
    var haveVP = 0 //ゲーム内投票通貨
    var votes = 0
    var times = 0 //トータルログイン時間
    var kit: String = "m4a1"//選択キット

    val permissions: MutableList<MoUser.BDFPermission> = mutableListOf() //所持権限
    val communities: MutableList<UserCommunity> = mutableListOf() //所持権限
    val haveWeapons: MutableList<BDFWeapon> = mutableListOf()
    val haveAttchments: MutableList<BDFAttachment> = mutableListOf()

    var lastLogin: Date = Date()
    var created: Date = Date()
    var updated: Date = Date()

    var language = "ja_jp"
    var acceptResourcePack = false
    var option = Option()

    val punishments: MutableList<Reason> = mutableListOf()
    val games: MutableList<Game> = mutableListOf()
    val access: MutableList<Access> = mutableListOf()
    val weaponKits: MutableList<WeaponSlot> = mutableListOf()
    val nameHistory: MutableList<NameHistory> = mutableListOf()
    var banned: Reason = Reason() //Ban
    var muted: Reason = Reason() //Mute

    /** 銃が撃てる状態かどうか */
    val isReadyShoot: Boolean
        get() = readyWeapon != null

    var itemHand: ItemStack? = null //手持ちアイテム
    var isWeaponStatusEnable = true //武器の有効かフラグ
    var rock: Boolean = false //フリーズ状態フラグ
        set(value) {
            field = value
                if(rock){
                    bukkitPlayer.walkSpeed = 0.0F
                    val potionEffect = PotionEffect(PotionEffectType.JUMP,99999,99999,false,false)
                    bukkitPlayer.addPotionEffect(potionEffect)
                    bukkitPlayer.foodLevel = 4
                }
                else{
                    bukkitPlayer.walkSpeed = 0.2F
                    bukkitPlayer.removePotionEffect(PotionEffectType.JUMP)
                    bukkitPlayer.foodLevel = 20
                }
        }
    var currentItemSlot = 0 //現在のアイテムスロット

    val sideBar: SideBar

    /** 初期化したスコアボードを返して保持 */
    val scoreBoard: Scoreboard by lazy {
        val scoreboard = Bukkit.getScoreboardManager().newScoreboard
        bukkitPlayer.scoreboard = scoreboard
        scoreboard //渡す
    }

    init {
        sideBar = SideBar(this)
        syncMainScoreboard(scoreBoard)
        name = bukkitPlayer.name
        uuid = bukkitPlayer.uniqueId.toString()
    }

    /** オンラインかどうかのフラグ */
    val rereadyHandler: Boolean
        get() = !bukkitPlayer.isOnline

    /** 武器の総重量を返す */
    val weight: Int
        get() = TODO("重量の処理書いて")

    /**  データサーバに初期化処理を送る */
    fun dataInitRequest() {
        val user = MoUser().apply {
            id set bukkitPlayer.name
            name set bukkitPlayer.name
            uuid set bukkitPlayer.uniqueId.toString()
            loaded set true
        }
        sendMessage(ChatColor.GRAY.toString() + "ユーザ情報を検証します...")
        BDFAPIv1.async(RequestMethod.POST,
                "${APIRoot.USER}", query = listOf("value" to user.json())) { res, error ->
            if (error) {
                val message = res.get("message").toString
                when (message) {
                    "user already register." -> {
                        sendMessage(ChatColor.GRAY.toString() + "ユーザ情報を同期します...")
                        dataGetRequest()
                    }
                    else -> {
                        syncKick("データの検証時に予期せぬエラーが発生しました: $message")
                        return@async
                    }
                }
            } else {
                sendMessage(ChatColor.GRAY.toString() + "新規登録完了")
                val result: MoUser = res.get("result").to()
                provide(result) //ID等が付与されたデータを再読み込み

                BDFSyncUserDataEvent(this).callEvent() //同期完了イベント呼び出し
            }
        }
    }

    /** ユーザ情報を取得する*/
    fun dataGetRequest() {
        val start = System.currentTimeMillis()
        BDFAPIv1.async(RequestMethod.GET,
                "${APIRoot.USER}/$name") { res, error ->
            if (error) {
                val message = res.get("message").toString
                syncKick("データの同期時に予期せぬエラーが発生しました: $message")
                return@async
            }
            val result: MoUser = res.get("result").to()
            import(result) //適用する
            val finish = System.currentTimeMillis()
            sendMessage(ChatColor.GRAY.toString() + "ユーザ情報の同期完了 ${finish - start}ms")

            BDFSyncUserDataEvent(this).callEvent() //同期完了イベント呼び出し
        }
    }

    /** ユーザー情報を保存する */
    fun dataSaveRequest() {
        val saveData = exportNotIncludeKeys<MoUser>(
                "created", "web"
        ) //セーブデータを作成

        sendConsoleMessage(saveData.json(pretty = true))
        BDFAPIv1.async(RequestMethod.POST, "${APIRoot.USER}/save", query = listOf("value" to saveData.json())) { res, error ->
            if (error) {
                val message = res.get("message").toString
                sendConsoleMessage(ChatColor.RED.toString() + "ユーザ情報の保存に失敗 $name: $message ${res.toString()}")
                return@async
            }
            sendConsoleMessage("ユーザ情報を保存: $name")
        }
    }


    /**
     * データを適用します
     * @param userReader 適用するユーザ
     * @return インスタンスを返す(this)
     */
    override fun provide(instance: MoUser) {
        bdfID = instance.id.toString
        name = instance.name.toString
        uuid = instance.uuid.toString

        exp = instance.exp.toInt
        wins = instance.wins.toInt
        loses = instance.loses.toInt
        kills = instance.kills.toInt
        deaths = instance.deaths.toInt
        assists = instance.assists.toInt
        rating = instance.rating.toInt
        haveBP = instance.haveBP.toInt
        haveVP = instance.haveVP.toInt
        votes = instance.votes.toInt
        times = instance.times.toInt
        kit = instance.kit.toString
        language = instance.language.toString

        lastLogin = Date(instance.lastLogin.toLong * 1000)
        created = Date(instance.created.toLong * 1000)
        updated = Date(instance.updated.toLong * 1000)

        acceptResourcePack = instance.acceptResourcePack.toBoolean

        option = Option().apply { import(instance.option.to()) }
        banned = Reason().apply { import(instance.banned.to()) }
        muted = Reason().apply { import(instance.muted.to()) }

        instance.permissions.toList.forEach { addPermission(MoUser.BDFPermission.permission(it.toInt)) }  /** 権限を追加 */
        instance.communities.toList.forEach { communities.add(UserCommunity().apply { import(it.to()) }) }  /* フレンド管理 */
        instance.nameHistory.toList.forEach { nameHistory.add(NameHistory().apply { import(it.to()) }) } /* ユーザ名管理 */
        instance.punishments.toList.forEach { punishments.add(Reason().apply { import(it.to()) }) }  /* 処罰管理 */
        instance.games.toList.forEach { games.add(Game().apply { import(it.to()) }) }/* ゲーム管理 */
        instance.accesses.toList.forEach { access.add(Access().apply { import(it.to()) }) } /* IP管理 */
        instance.kits.toList.forEach { weaponKits.add( WeaponSlot().apply { import(it.to()) }) }   /*kits管理 */

        /** 武器を追加 */
        instance.haveWeapons.toList.forEach {
            val custom = WeaponCustomize().apply { import(instance.option.to()) }
            val weapon = BDFManager.weaponManager.getWeaponWithID(custom.id)

            if (weapon != null) {
                weapon.custom = custom
                //TODO 武器読み込み処理
                putWeapon(weapon) //武器追加
            } else {
                val nullWeapon = BDFNullWeapon()
                nullWeapon.custom = custom
                putWeapon(nullWeapon) //武器は存在しなかったがデータとして保持する
            }
        }
        //TODO アタッチメントを追加してください
        sendConsoleMessage("読み込みました: $name $bdfID")
        BDF.updateLocalData() //サーバ情報更新
        notification()
    }

    /**
     * 更新に必要なことだけ不要なものは書かない(上書きしない)
     */
    override fun supply(save: MoUser) {
        save.set("name", name)
        save.set("id", bdfID)
        save.set("uuid", uuid)

        save.set("exp", exp)
        save.set("wins", wins)
        save.set("loses", loses)
        save.set("kills", kills)
        save.set("deaths", deaths)
        save.set("assists", assists)
        save.set("rating", rating)
        save.set("haveBP", haveBP)
        save.set("haveVP", haveVP)
        save.set("votes", votes)
        save.set("times", times)
        save.set("language", language)
        save.set("kit", kit)

        save.set("lastLogin", Date().toInstant().epochSecond)
        save.set("acceptResourcePack", acceptResourcePack)

        save.set("option", option.export<MoUser.MoOption>())
        save.set("banned", banned.export<MoReason>())
        save.set("muted", muted.export<MoReason>())

        save.set("permissions", permissions.map { it.permitID })
        save.set("haveWeapons", haveWeapons.map { it.custom.export<MoUser.MoUserWeaponCustomize>() })
        save.set("communities", communities.map { it.export<MoUser.MoUserCommunity>() })
        save.set("nameHistory", nameHistory.map { it.export<MoUser.MoNameHistory>() })
        save.set("punishments", punishments.map { it.export<MoReason>() })
        save.set("games", games.map { it.export<MoGame>() })
        save.set("kits", weaponKits.map { it.export<MoWeaponSlot>() })

        save.set("loaded", true) //最後まで正常に読み込みました
    }

    /**
     * 権限を追加する
     * @param permission 追加すする権限
     */
    fun addPermission(permission: MoUser.BDFPermission) {
        val perm = permissions.find { it.permitID == permission.permitID }
        if (perm == null)
            permissions.add(permission)
    }

    /** URLを返します */
    fun formPlusAccountRegisterRequest() {
        sendMessage(
                "${ChatColor.GRAY}アカウント登録のリクエストを送っています。しばらくお待ち下さい..."
        )
        BDFAPIv1.async(RequestMethod.POST,
                "${APIRoot.USER}/plus/code/register", query = listOf("bdfId" to bdfID)) { res, error ->
            if (error) {
                val message = res.get("message").toString
                sendMessage(
                        "",
                        "${ChatColor.RED}申し訳ございません。アカウント登録のリクエスト中にエラーが発生しました",
                        "${ChatColor.RED}$message",
                        "",
                        "改善されない場合はお手数ですがサポートまでご連絡ください",
                        ""
                )
            } else {
                val reObject: Mobject = res.get("result").to()
                val url = reObject.get("url").toString
                sendMessage(
                        "",
                        "${ChatColor.AQUA}Plusアカウント登録のURLを発行しました。以下のリンクから登録フォームに移動してください",
                        "${ChatColor.UNDERLINE}$url",
                        ""
                )
            }
        }
    }

    /** URLを返します */
    fun formPlusAccountChangeEmailRequest() {
        sendMessage(
                "${ChatColor.GRAY}メールアドレス変更のリクエストを送っています。しばらくお待ち下さい..."
        )
        BDFAPIv1.async(RequestMethod.POST,
                "${APIRoot.USER}/plus/code/email", query = listOf("bdfId" to bdfID)) { res, error ->
            if (error) {
                val message = res.get("message").toString
                sendMessage(
                        "",
                        "${ChatColor.RED}申し訳ございません。メールアドレス変更のリクエスト中にエラーが発生しました",
                        "${ChatColor.RED}$message",
                        "",
                        "改善されない場合はお手数ですがサポートまでご連絡ください",
                        ""
                )
            } else {
                val reObject: Mobject = res.get("result").to()
                val url = reObject.get("url").toString
                sendMessage(
                        "",
                        "${ChatColor.AQUA}メールアドレス変更のURLを発行しました。以下のリンクから登録フォームに移動してください",
                        "${ChatColor.UNDERLINE}$url",
                        ""
                )
            }
        }
    }


    /** メインと同期させる。*/
    fun syncMainScoreboard() {
        syncMainScoreboard(scoreBoard)
    }


    /** 移動速度を重量に基づき適用する */
    fun updateWalkSpeed() {
        if (rock)
            bukkitPlayer.walkSpeed = 0.0f
        else {
            if (weight > 0) {
                val speed = BDFManager.playerManager.defaultWalkSpeed - weight.toFloat() / 100000f
                bukkitPlayer.walkSpeed = speed
            }
        }
    }

    /** 個人的なお知らせを行う*/
    fun notification() {
        nameHistory.forEach {
            if (!it.notification) {
                sendMessage(ChatColor.GRAY.toString() + "名前変更が確認されました: ${it.beforeName} -> ${it.afterName}")
                it.notification = true
            }
        }
    }


    /**
     * @param item 検出するアイテム
     * 武器を手持ちから検出します
     * @return 結果
     */
    infix fun itemStackAsWeapon(item: ItemStack?): BDFWeapon? {
        itemHand = item
        val nmsItem = CraftItemStack.asNMSCopy(item ?: return null)
        val tag = nmsItem?.tag
        if (tag != null) {
            if (tag.hasKey(WEAPON_TAG_NAME)) {
                val uuid = tag.getString(WEAPON_TAG_NAME)
                return if (uuid != null) weapon(uuid) else null
            }
        }
        return null
    }

    /**
     * 手持ち武器をチェックする
     */
    fun checkReadyWeaponInHand() {
        itemStackAsReadyWeapon(bukkitPlayer.inventory.itemInMainHand)
    }

    /**
     * @param item 持ち替えるアイテム
     * 武器持ち替え処理
     */
    fun itemStackAsReadyWeapon(item: ItemStack?) {
        val weaponInHand = itemStackAsWeapon(item) //手持ち武器を取得して
        if (weaponInHand != readyWeapon) {
            readyWeapon?.stopProcess() //タスク処理を止める
            readyWeapon = weaponInHand
            readyWeapon?.heldAction()
        }
    }

    /** リスナから呼び出し PlayerInteractEvent*/
    fun onHeld(event: PlayerItemHeldEvent) {
        itemStackAsReadyWeapon(
                bukkitPlayer.inventory.getItem(event.newSlot))
        setInventorySlot(event.newSlot) //スロット更新
    }

    /** リスナから呼び出し PlayerInteractEvent*/
    fun onShoot(event: PlayerInteractEvent) {
        when (event.action!!) {
            Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR -> {
                readyWeapon?.rightClick()
            }

            Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> {
                readyWeapon?.leftClick()
            }
        }
    }

    /** リスナから呼び出し PlayerAnimationEvent*/
    fun onScope(event: PlayerAnimationEvent) {
        event.isCancelled = true

        /**
         * 判定のないブロックチェック？
         * @param block チェック
         */
        fun checkArmSwingCancelBlock(block: Block): Boolean {
            when (block.typeId) {
                63, 85, 113, 189, 190, 191, 192 -> return true
            }
            return false
        }

        val weapon = readyWeapon //手持ち武器
        if (weapon != null) {
            if (event.animationType == PlayerAnimationType.ARM_SWING) {
                val vector = location.direction
                vector.multiply(1.0)
                val location = bukkitPlayer.eyeLocation.clone()
                for (i in 1..4) { //４ブロック先まで検知する
                    if (checkArmSwingCancelBlock(location.block)) { //判定のないブロックのチェック
                        return
                    }
                    location.add(vector)
                }
            }
            readyWeapon?.leftClick()
        }
    }

    /** リスナから呼び出し PlayerSwapHandItemsEvent */
    fun onSwapLeftHand(event: PlayerSwapHandItemsEvent) {
        event.isCancelled = true //ユーザがアイテムを左手に移動させないように
        /* フルオート切り替え */

        readyWeapon?.swapHandAction()
    }


    /** リスナから呼び出し PlayerDropItemEvent */
    fun onReload(event: PlayerDropItemEvent) {
        if (isReadyShoot) {
            /* フルオート切り替え */
            readyWeapon?.dropAction()
            event.isCancelled = true
        }
    }


    /**
     * @param slotID スロット番号
     * @param slotType 武器の種類
     * @param weapon セットする武器(nullでセットを外す・空にする)
     * 指定したスロットに武器をセットする
     */
    fun setWeapon(slotID: String, slotType: BDFWeapon.SlotType, weapon: BDFWeapon?) {
        val slotData = getKit(slotID) ?: return
        if (weapon != null)
            putWeapon(weapon)
        setWeapon(slotData, slotType, weapon)
    }

    /**
     * 武器をスロットにセットする
     * @param slot セットするスロットオブジェクト
     * @param slotType スロットのどこにセットするか
     * @param weapon セットする武器 NULLの場合はセット解除
     */
    private fun setWeapon(slot: WeaponSlot, slotType: BDFWeapon.SlotType, weapon: BDFWeapon?) {
        when (slotType) {
            BDFWeapon.SlotType.Primary -> slot.primary = weapon?.custom?.id ?: ""
            BDFWeapon.SlotType.Secondary -> slot.secondary = weapon?.custom?.id ?: ""
            BDFWeapon.SlotType.Throwing -> slot.grenade = weapon?.custom?.id ?: ""
            BDFWeapon.SlotType.Knife -> slot.knife = weapon?.custom?.id ?: ""
        }
    }

    /** Bukkitのプレイヤーをリセット? する */
    fun heal() {
        bukkitPlayer.run {
            health = maxHealth
            foodLevel = 20
            walkSpeed = BDFManager.playerManager.defaultWalkSpeed
            maximumNoDamageTicks = 15
            noDamageTicks = 0
            fireTicks = 0
            remainingAir = maximumAir
            activePotionEffects.forEach {
                removePotionEffect(it.type)
            }
        }
    }

    /**
     * キットを取得する
     * @param id キットID
     * @return 見つかった場合はきっとオブジェクトを返す。見つからなかった場合はNULL
     */
    fun getKit(id: String): WeaponSlot? =
            weaponKits.find { it.id == id }


    /**
     * 「&」から始まるカラーコードを変換して送信します
     * @param messages 送信するメッセージ
     */
    fun sendMessage(vararg messages: String) =
            messages.forEach { sendMessage(it) }

    private fun sendMessage(message: String) {
        bukkitPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', message))
    }

    /**
     * パーティクルを送信する
     * https://github.com/kotake545/Splatoon/blob/master/src/com/github/kotake545/splatoon/Packet/ParticleAPI.java
     */
    fun sendParticle(enumParticle: ParticleAPI.EnumParticle, location: Location, x: Float, y: Float, z: Float, f: Float, count: Int, distance: Double) {
        if (bukkitPlayer.eyeLocation.world !== location.world) return
        if (bukkitPlayer.eyeLocation.distance(location) > distance) return
        sendParticle(enumParticle, location, x, y, z, f, count, true)
    }

    /**
     * パーティクルを送信する
     * https://github.com/kotake545/Splatoon/blob/master/src/com/github/kotake545/splatoon/Packet/ParticleAPI.java
     */
    fun sendParticle(enumParticle: ParticleAPI.EnumParticle, location: Location, x: Float, y: Float, z: Float, f: Float, count: Int, j: Boolean) {
        ParticleAPI.sendPlayer(bukkitPlayer, enumParticle, location, x, y, z, f, count, j)
    }

    /**
     * 観戦モードになる
     * 透明化・空中浮遊
     */
    var spectator: Boolean = false
        set(value) {
            if (value) {
                isMuteki = true
                isSafety = true
                hidePlayer = true

                bukkitPlayer.run {
                    allowFlight = true
                    isFlying = true
                    health = maxHealth
                    noDamageTicks = 9999 * 9999
                    maximumNoDamageTicks = 9999 * 9999
                    addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 99999, 255))
                }

            } else {
                isMuteki = false
                isSafety = false
                hidePlayer = false
                bukkitPlayer.run {
                    allowFlight = false
                    isFlying = false
                }
                heal()
            }
        }


    /**
     * 外部のプレイヤーから見て透明化
     */
    private var hidePlayer: Boolean = false
        set(value) {
            if (value)
                for (send in Bukkit.getOnlinePlayers()) {
                    send.hidePlayer(BDF.instance, bukkitPlayer)
                }
            else
                for (send in Bukkit.getOnlinePlayers()) {
                    send.showPlayer(BDF.instance, bukkitPlayer)
                }
        }


    /**
     * @param reason BAN理由 BAN利用規定に基づく
     * @param runUserId 実行者 虚偽の実行の対策
     * @param sync 全てのサーバと同期するか
     */
    fun banned(reason: String = "N/A", runUserId: Int = 0, sync: Boolean = true, expires: Date = Date()) {
        banned.apply {
            valid = true //BANフラグ
        }
        syncKick(ChatColor.RED.toString() + "あなたは当サーバから追放されました: \n"
                + "理由: ${banned.reason} \n"
                + "実行日時: ${banned.created.toString()} \n"
                + "有効期限: ${banned.expires.toString()}")
    }

    /**
     * @param reason BAN理由 BAN利用規定に基づく
     * @param runUserId 実行者 虚偽の実行の対策
     * @param sync 全てのサーバと同期するか
     */
    fun muted(reason: String = "N/A", runUserId: Int = 0, sync: Boolean = true) {
        //TODO Mute設定
    }


    /**
     * 同期処理でユーザをキックする
     * BukkitRunnable
     * @param message キックするメッセージ
     */
    fun syncKick(message: String = ChatColor.RED.toString() + "サーバからキックされました") {
        syncRunnable {
            kick(ChatColor.RED.toString() + message)
        }
    }

    /**
     * 同期処理でユーザをキックする
     * BukkitRunnable
     * @param message キックするメッセージ
     */
    fun kick(message: String = ChatColor.RED.toString() + "サーバからキックされました") {
        bukkitPlayer.kickPlayer(message)
    }


    /**
     * @param reason BAN理由 BAN利用規定に基づく
     * @param runUserId 実行者 虚偽の実行の対策
     * @param sync 全てのサーバと同期するか
     */
    fun unBanned(reason: String = "N/A", runUserId: Int = 0, sync: Boolean = true) {
        //TODO BAN解除
    }

    /**
     * @param reason BAN理由 BAN利用規定に基づく
     * @param runUserId 実行者 虚偽の実行の対策
     * @param sync 全てのサーバと同期するか
     */
    fun unMuted(reason: String = "N/A", runUserId: Int = 0, sync: Boolean = true) {
        //TODO Mute解除
    }

    /**
     * ダメージを与える
     * @param damage ダメージ
     * @param source ダメージの情報
     */
    override fun addDamage(source: DamageSource): Boolean {
        val damage = source.damage
        val event: BaseCancellableEvent
        when {
            source is BulletDamageSource -> event = BDFBulletDamageEvent(this, source)
            source is EntityDamageSource -> event = BDFDamageByEntityEvent(this, source)
            else -> event = BDFDamageEvent(this, source)
        }
        event.callEvent() //イベント呼び出し
        val cancelled = event.isCancelled

        if (!cancelled) {
            val limit = bukkitPlayer.health - damage

            if (limit > 0.0){ //まだ生き残る力がある！！
                bukkitPlayer.health = limit
            }
            else { //死んだ

                inventoryWeapons.forEach { it.stopProcess() }
                syncRunnable { bukkitPlayer.health = 0.0 }
                when {
                    source is BulletDamageSource -> BDFBulletDeathEvent(this, source).callEvent()
                    source is EntityDamageSource -> BDFDeathByEntityEvent(this, killer = source.damager).callEvent()
                    else -> BDFDeathEvent(this).callEvent()
                }
            }
            bukkitPlayer.damage(0.0) //ダメージエフェクト
        }else{
            Bukkit.broadcastMessage("canceled")
        }


        return !cancelled
    }

    /**
     * @param weapon 左手に移動させる武器。nullだった場合はフラグオフ処理
     */
    fun setOffHandScope(weapon: BDFWeapon, scoping: Boolean) {
        val inventory = bukkitPlayer.inventory
        if (!scoping) { //スコープ解除時
            inventory.setItem(currentItemSlot, null) //石炭を消す
            inventory.itemInOffHand = null //左手アイテム消去
            inventory.itemInMainHand = null //石炭を消す
            inventory.setItem(currentItemSlot, weapon.itemStack) //右手にアイテムを設置
        } else { //スコープ展開時
            inventory.setItem(currentItemSlot, ItemStack(263).apply {
                rename(" ")
            }) //石炭をセット
            inventory.itemInOffHand = weapon.itemStack
        }

        disguiseHoldWeaponInHand() //右手に武器を持っているパケットを送信
        if (scoping)
            syncTimer(1) {
                disguiseShootingAnimation() //構えているアニメーション
            }
    }

    /**
     * 左手に持たせいるときに右手に武器を持たせているようにパケット送る
     * 尚、シュートアニメーションをキャンセルする際にも使用する
     */
    fun disguiseHoldWeaponInHand() {
        val stack = readyWeapon?.itemStack
        if (stack != null) {
            val main = WrapperPlayServerEntityEquipment().apply {
                //右手に武器を持たせる
                slot = EnumWrappers.ItemSlot.MAINHAND
                entityID = entId
                item = stack
            }

            val sub = WrapperPlayServerEntityEquipment().apply {
                //左手
                slot = EnumWrappers.ItemSlot.OFFHAND
                entityID = entId
                item = ItemStack(0)
            }
            //遅延起こして送らないとダメ見たい
            syncTimer(1) {
                Bukkit.getOnlinePlayers().forEach {
                    if (it != bukkitPlayer) {
                        main.sendPacket(it)
                        sub.sendPacket(it)
                    }
                }
            }
        }
    }

    /**　 銃を構えているアニメーションをを送信*/
    fun disguiseShootingAnimation() {
        PacketUtil.sendFakeBowDraw(bukkitPlayer) //構えてるアニメーションを送信
    }

    /**
     * Bukkitではなくパケットで送信
     * (Bukkitの場合は一定間隔を開けないと連続送信ができないため)
     */
    fun updateInventory() {
        ProtocolLibPacket.updateItem(bukkitPlayer) //武器を更新して引っ込ませるアニメーション
    }

    /**
     * ActionBarを送信する
     * @param message メッセージ
     */
    fun sendActionBar(message: String) =
            ProtocolLibPacket.sendActionBar(bukkitPlayer, message)

    /**
     * タイトルを送信する
     * @param fadein 表示されるまでの時間
     * @param stay 表示時間
     * @param fadeout 消えるまでのフェードアウト時間
     * @param main タイトルメッセージメイン
     * @param sub サブメッセージ
     */
    fun sendTitle(fadein: Int, stay: Int, fadeout: Int,
                  main: String, sub: String) {
        TitleAPI.sendTitle(bukkitPlayer, fadein, stay, fadeout, main, sub)
    }

    /**
     * サウンドを再生する
     * @param sound サウンド名
     * @param pitch
     * @param pitch
     */
    fun playSound(sound: String, volume: Float, pitch: Float) {
        bukkitPlayer.playSound(bukkitPlayer.location, sound, volume, pitch)
    }

    fun playshootSound(sound: String,  pitch: Float,location: Location) {
        val distance:Float = bukkitPlayer.location.distance(location).toFloat()

        if(distance <= 30){
            val lx = location.x - bukkitPlayer.location.x
            val ly = location.y - bukkitPlayer.location.y
            val lz = location.z - bukkitPlayer.location.z
            val vector = Vector(lx,ly,lz).normalize().multiply(2)
            val volume2:Float  = (1.0F - (distance/30.0F))


            bukkitPlayer.playSound(bukkitPlayer.location.add(vector), sound, SoundCategory.VOICE, volume2, pitch)
        }
    }

    fun playSound(sound: Sound, volume: Float, pitch: Float,location: Location) {
        bukkitPlayer.playSound(location, sound, volume, pitch)
    }

    /**
     * サウンドを再生する
     * @param sound サウンド名
     * @param volume
     * @param pitch
     */
    fun playSound(sound: Sound, volume: Float, pitch: Float) {
        bukkitPlayer.playSound(bukkitPlayer.location, sound, volume, pitch)
    }

    /**
     * 手持ちスロットをセットする.
     * 左手は弾く
     * @param slot スロット番号
     */
    fun setInventorySlot(slot: Int) {
        if (slot == 45) //左手の場合
            return
        this.currentItemSlot = slot
    }



    enum class BDFDamageCause {
        CONTACT,
        ENTITY_ATTACK,
        PROJECTILE,
        SUFFOCATION,
        FALL,
        FIRE,
        FIRE_TICK,
        MELTING,
        LAVA,
        DROWNING,
        BLOCK_EXPLOSION,
        ENTITY_EXPLOSION,
        VOID,
        LIGHTNING,
        SUICIDE,
        STARVATION,
        POISON,
        MAGIC,
        WITHER,
        FALLING_BLOCK,
        THORNS,
        DRAGON_BREATH,
        CUSTOM,
        FLY_INTO_WALL,
        BULLET, //銃弾によるダメージ
        HOT_FLOOR;
    }
}
