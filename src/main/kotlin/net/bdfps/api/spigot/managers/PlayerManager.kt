package net.bdfps.api.spigot.managers

import net.bdf.api.data.generaterandomSHA1
import net.bdfps.api.spigot.BDF
import net.bdfps.api.spigot.event.BDFPlayerLoginEvent
import net.bdfps.api.spigot.event.BDFPlayerQuitEvent
import net.bdfps.api.spigot.java.packet.protocolLib.ProtocolLibPacket
import net.bdfps.api.spigot.support.BDFPlayer
import net.bdfps.api.spigot.utility.*
import net.bdfps.api.spigot.weapon.BDFGun
import net.bdfps.api.spigot.weapon.BDFThrow
import net.bdfps.api.spigot.weapon.BDFWeapon
import net.bdfps.api.spigot.weapon.type.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class PlayerManager {
    /** ローカルオンラインユーザ保存場所 */
    val localPlayers = hashSetOf<BDFPlayer>()

    /** デフォルトの歩く速度*/
    val defaultWalkSpeed = 0.25f

    /** ユーザ一覧を取得する */
    fun loadPlayers() {
        //TODO データサーバからユーザを取得
    }

    /** ユーザ情報をロードする*/
    fun reload() {
        Bukkit.getOnlinePlayers().forEach {
            join(it)
        }
    }

    /**
     * ユーザーを取得する
     * @param UserID
     */
    fun findWithID(id: String): BDFPlayer? =
            localPlayers.find { it.bdfID == id }

    /**
     * ユーザーを取得する
     * @param name ユーザネーム
     */
    fun findWithName(name: String): BDFPlayer? =
            localPlayers.find { it.name == name }

    /**
     * ログイン時の処理
     * @param player ログインしたプレイヤー
     */
    fun join(player: Player) {
        val bdfPlayer = BDFPlayer(player)

        BDFManager.entityManager.add(bdfPlayer) //追加
        localPlayers.add(bdfPlayer)
        ProtocolLibPacket.sendResourcepack(bdfPlayer.bukkitPlayer, "http://texture.bdfps.net/texture.zip?randomcode=1122")
        /*
        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED,999999,7,false,false))
//140%
        player.walkSpeed = 0.0833F
*/
        online {
            bdfPlayer.dataInitRequest() //データサーバと同期する
        }
        BDFPlayerLoginEvent(bdfPlayer).callEvent() //イベント呼び出し
    }

    /**
     * ログアウト時の処理
     * @param player ログアウトしたプレイヤー
     */
    fun quit(player: Player) {
        val user = player.toBDFPlayer()
        localPlayers.removeIf { it.bukkitPlayer == player }
        if (user != null) {
            BDFManager.entityManager.remove(user)
            online {
                user.dataSaveRequest() //保存
            }
            BDFPlayerQuitEvent(user).callEvent()

        }
        offline {
            broadcast(ChatColor.GRAY.toString() + "** サーバはオフラインモードです。データは共有されません **")
        }
        online {
            BDF.updateLocalData() //サーバ情報更新
        }

    }

    /*
     * ##########################
     * 以下デバッグ用
     *
     * ##########################
     */


    fun getM4A1Kit() : MutableList<BDFWeapon>{
        val list: MutableList<BDFWeapon> = mutableListOf()

        val Mk2Grenade = Grenade().apply {
            custom.apply {
                id = generaterandomSHA1
            }
            throwType = BDFThrow.ThrowType.Grenade
            weaponID = "mk_2_grenade"
            weaponName = "Mk.2 Grenade"
            itemID = 341
            itemBreakableValue = 0
            slotType = BDFWeapon.SlotType.Throwing
            weight = 670
            swapTime = 4
            speed = 15
            enablePullPin = true
            explodeTime = 60
            radius = 6
            initAmmo = 2
            hitDamage = 15.3
            directHitDamage = 25.0
            directHitRadius = 3.0
            overHeatTime = 30

            resetAmmo()

        }
        val FiveSeveN = HandGun().apply {
            custom.apply {
                id = generaterandomSHA1
            }
            weaponID = "fiveseven"
            weaponName = "FiveSeveN"
            itemID = 261
            itemBreakableValue = 29
            gunType = BDFGun.GunType.HandGun
            reloadType = BDFGun.ReloadType.Normal
            slotType = BDFWeapon.SlotType.Secondary
            weight = 845

            hipAccuracy = 0.042
            adsAccuracy =  0.33
            initOneMagazineAmmo = 20
            initPreMagazineAmmo = 480
            bodyDamage = 5.2
            legDamage = 3.4
            headShotDamage = 7.6

            secondRate = 20
            singleShootTime = 6
            singleOverHeatTime = 5

            distance = 250.0
            burst = 1
            needAmmo = 1
            swapTime = 1
            reloadTime = 30
            speed = 32
            scopeMagnification = 2
            dropRate = 0.1

            hipRecoilHipHeight = 0.5f
            adsRecoilAdsHeight = 0.35f

            hipRecoilHipWidth = 0.45f
            adsRecoilAdsWidth = 0.333f

            hipRecoilSlopePercent = 50
            adsRecoilSlopePercent = 50

            recoilSlope = BDFGun.Slope.Right

            enableUseScope = true
            enableAutoReload = false

            shootSound = "bdf.gun.GLOCKC.shoot"
            resetAmmo()
        }

        val M4A1 = AssaultRifle().apply {
            custom.apply {
                id = generaterandomSHA1
            }
            weaponID = "m4a1"
            weaponName = "M4A1"
            itemID = 261
            itemBreakableValue = 1
            gunType = BDFGun.GunType.Assault
            reloadType = BDFGun.ReloadType.Normal
            slotType = BDFWeapon.SlotType.Primary
            weight = 2680

            hipAccuracy = 0.476
            adsAccuracy =  0.042
            initOneMagazineAmmo = 30
            initPreMagazineAmmo = 120
            bodyDamage = 3.4
            legDamage = 2.6
            headShotDamage = 5.9
            secondRate = 13
            fullAutoShootTime = 6
            overHeatTick = 2
            singleShootTime = 4
            singleOverHeatTime = 3

            distance = 250.2
            burst = 1
            needAmmo = 1
            swapTime = 5
            reloadTime = 58
            speed = 60
            scopeMagnification = 2
            dropRate = 0.1

            hipRecoilHipHeight = 0.74f
            adsRecoilAdsHeight = 0.43f

            hipRecoilHipWidth = 0.47f
            adsRecoilAdsWidth = 0.35f

            hipRecoilSlopePercent = 75
            adsRecoilSlopePercent = 50

            enableUseScope = true
            enableFullAuto = true
            enableAutoReload = true

            shootSound = "bdf.gun.M4A1.shoot"
            resetAmmo()

        }
        list.add(M4A1)
        list.add(FiveSeveN)
        list.add(Mk2Grenade)
        return list
    }

    fun getM1887Kit() : MutableList<BDFWeapon>{
        val list: MutableList<BDFWeapon> = mutableListOf()

        val Mk2Grenade = Grenade().apply {
            custom.apply {
                id = generaterandomSHA1
            }
            throwType = BDFThrow.ThrowType.Grenade
            weaponID = "mk_2_grenade"
            weaponName = "Mk.2 Grenade"
            itemID = 341
            itemBreakableValue = 0
            slotType = BDFWeapon.SlotType.Throwing
            weight = 670
            swapTime = 4
            speed = 15
            enablePullPin = true
            explodeTime = 60
            radius = 6
            initAmmo = 2
            hitDamage = 15.3
            directHitDamage = 25.0
            directHitRadius = 3.0
            overHeatTime = 30

            resetAmmo()

        }
        val FiveSeveN = HandGun().apply {
            custom.apply {
                id = generaterandomSHA1
            }
            weaponID = "fiveseven"
            weaponName = "FiveSeveN"
            itemID = 261
            itemBreakableValue = 29
            gunType = BDFGun.GunType.HandGun
            reloadType = BDFGun.ReloadType.Normal
            slotType = BDFWeapon.SlotType.Secondary
            weight = 845

            hipAccuracy = 0.042
            adsAccuracy =  0.33
            initOneMagazineAmmo = 20
            initPreMagazineAmmo = 480
            bodyDamage = 5.2
            legDamage = 3.4
            headShotDamage = 7.6

            secondRate = 20
            singleShootTime = 6
            singleOverHeatTime = 5

            distance = 250.0
            burst = 1
            needAmmo = 1
            swapTime = 1
            reloadTime = 30
            speed = 32
            scopeMagnification = 2
            dropRate = 0.1

            hipRecoilHipHeight = 0.5f
            adsRecoilAdsHeight = 0.35f

            hipRecoilHipWidth = 0.45f
            adsRecoilAdsWidth = 0.333f

            hipRecoilSlopePercent = 50
            adsRecoilSlopePercent = 50

            recoilSlope = BDFGun.Slope.Right

            enableUseScope = true
            enableAutoReload = false

            shootSound = "bdf.gun.GLOCKC.shoot"
            resetAmmo()
        }

        val M1887 = ShotGun().apply {
            custom.apply {
                id = generaterandomSHA1
            }
            weaponID = "m1887"
            weaponName = "M1887"
            itemID = 261
            itemBreakableValue = 9
            gunType = BDFGun.GunType.ShotGun
            reloadType = BDFGun.ReloadType.Bolt
            slotType = BDFWeapon.SlotType.Primary
            weight = 3600

            hipAccuracy = 0.776
            adsAccuracy =  0.242
            initOneMagazineAmmo = 5
            initPreMagazineAmmo = 25
            bodyDamage = 3.1
            legDamage = 1.3
            headShotDamage = 4.8
            secondRate = 0
            singleShootTime = 28
            singleOverHeatTime = 13

            distance = 70.5
            burst = 8
            needAmmo = 1
            swapTime = 10
            reloadTime = 120
            speed = 60
            scopeMagnification = 2
            dropRate = 0.02

            hipRecoilHipHeight = 5.3f
            adsRecoilAdsHeight = 3.1f

            hipRecoilHipWidth = 1.3f
            adsRecoilAdsWidth = 1.12f

            hipRecoilSlopePercent = 50
            adsRecoilSlopePercent = 50

            enableUseScope = true
            enableFullAuto = false

            shootSound = "bdf.gun.M1887.shoot"
            resetAmmo()

        }
        list.add(M1887)
        list.add(FiveSeveN)
        list.add(Mk2Grenade)
        return list
    }
    fun getM40A1Kit() : MutableList<BDFWeapon>{
        val list: MutableList<BDFWeapon> = mutableListOf()

        val Mk2Grenade = Grenade().apply {
            custom.apply {
                id = generaterandomSHA1
            }
            throwType = BDFThrow.ThrowType.Grenade
            weaponID = "mk_2_grenade"
            weaponName = "Mk.2 Grenade"
            itemID = 341
            itemBreakableValue = 0
            slotType = BDFWeapon.SlotType.Throwing
            weight = 670
            swapTime = 4
            speed = 15
            enablePullPin = true
            explodeTime = 60
            radius = 6
            initAmmo = 2
            hitDamage = 15.3
            directHitDamage = 25.0
            directHitRadius = 3.0
            overHeatTime = 30

            resetAmmo()

        }
        val FiveSeveN = HandGun().apply {
            custom.apply {
                id = generaterandomSHA1
            }
            weaponID = "fiveseven"
            weaponName = "FiveSeveN"
            itemID = 261
            itemBreakableValue = 29
            gunType = BDFGun.GunType.HandGun
            reloadType = BDFGun.ReloadType.Normal
            slotType = BDFWeapon.SlotType.Secondary
            weight = 845

            hipAccuracy = 0.042
            adsAccuracy =  0.33
            initOneMagazineAmmo = 20
            initPreMagazineAmmo = 480
            bodyDamage = 5.2
            legDamage = 3.4
            headShotDamage = 7.6

            secondRate = 20
            singleShootTime = 6
            singleOverHeatTime = 5

            distance = 250.0
            burst = 1
            needAmmo = 1
            swapTime = 1
            reloadTime = 30
            speed = 32
            scopeMagnification = 2
            dropRate = 0.1

            hipRecoilHipHeight = 0.5f
            adsRecoilAdsHeight = 0.35f

            hipRecoilHipWidth = 0.45f
            adsRecoilAdsWidth = 0.333f

            hipRecoilSlopePercent = 50
            adsRecoilSlopePercent = 50

            recoilSlope = BDFGun.Slope.Right

            enableUseScope = true
            enableAutoReload = false

            shootSound = "bdf.gun.GLOCKC.shoot"
            resetAmmo()
        }

        val M40A1 = SniperRifle().apply {
            custom.apply {
                id = generaterandomSHA1
            }
            weaponID = "m40a1"
            weaponName = "M40A1"
            itemID = 261
            itemBreakableValue = 4
            gunType = BDFGun.GunType.Sniper
            reloadType = BDFGun.ReloadType.Normal
            slotType = BDFWeapon.SlotType.Primary
            weight = 6570

            hipAccuracy = 0.776
            adsAccuracy =  0.042
            initOneMagazineAmmo = 5
            initPreMagazineAmmo = 30
            bodyDamage = 14.4
            legDamage = 10.0
            headShotDamage = 20.3
            secondRate = 0
            singleShootTime = 14
            singleOverHeatTime = 13

            distance = 460.2
            burst = 1
            needAmmo = 1
            swapTime = 5
            reloadTime = 62
            speed = 180
            scopeMagnification = 5
            dropRate = 0.1

            hipRecoilHipHeight = 6.2f
            adsRecoilAdsHeight = 3.6f

            hipRecoilHipWidth = 1.9f
            adsRecoilAdsWidth = 1.30f

            hipRecoilSlopePercent = 50
            adsRecoilSlopePercent = 50

            enableUseScope = true
            enableFullAuto = false
            enableAutoReload = false

            shootSound = "bdf.gun.M40A1.shoot"
            resetAmmo()

        }
        list.add(M40A1)
        list.add(FiveSeveN)
        list.add(Mk2Grenade)
        return list
    }
    fun getKit(kit: String): MutableList<BDFWeapon>{
        return when (kit) {
            "m4a1" -> getM4A1Kit()
            "m1887" -> getM1887Kit()
            "m40a1" -> getM40A1Kit()
            else -> getM4A1Kit()
        }
    }

    /** 開発用キットを配布する */
    fun giveDebugKit(player: BDFPlayer) {
        player.giveDebugWeapon(BDFManager.playerManager.getKit(player.kit))
    }

    /** 指定したキットから配布する */
    private fun BDFPlayer.giveDebugWeapon(weapons: List<BDFWeapon>){
        inventoryWeapons.clear() //武器をすべてクリア
        weapons.forEach {
            when { //弾数初期化
                it is BDFGun -> it.resetAmmo()
                it is BDFThrow -> it.resetAmmo()
            }
            putWeapon(it) //武器を登録
            when(it.slotType) { //武器をインベントリにセット
                BDFWeapon.SlotType.Primary -> bukkitPlayer.inventory.setItem(0, it.itemStack)
                BDFWeapon.SlotType.Secondary -> bukkitPlayer.inventory.setItem(1, it.itemStack)
                BDFWeapon.SlotType.Throwing -> bukkitPlayer.inventory.setItem(2, it.itemStack)
                else -> bukkitPlayer.inventory.setItem(3, it.itemStack)
            }
        }
        checkReadyWeaponInHand()
    }
}
