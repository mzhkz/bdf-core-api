package net.bdfps.api.spigot.weapon.bullet.type

import net.bdfps.api.spigot.entity.BDFEntity
import net.bdfps.api.spigot.entity.BDFLivingEntity
import net.bdfps.api.spigot.event.BDFBulletDamageEvent
import net.bdfps.api.spigot.java.packet.ParticleAPI
import net.bdfps.api.spigot.java.packet.protocolLib.ProtocolLibPacket
import net.bdfps.api.spigot.managers.BDFManager
import net.bdfps.api.spigot.support.BDFPlayer
import net.bdfps.api.spigot.utility.toBDFPlayer
import net.bdfps.api.spigot.weapon.BDFGun
import net.bdfps.api.spigot.weapon.bullet.BDFBullet
import net.bdfps.api.spigot.weapon.bullet.BulletHitBlockDecision
import net.bdfps.api.spigot.weapon.bullet.BulletHitPlayerDecision
import org.bukkit.*
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin
import org.bukkit.entity.Snowball
import java.util.*

/**
 * BDFGunBulletは通常の弾丸の発砲、弾道計算、当たり判定の確認を行います.
 * BDFGunBullet is Fire original bullets , Ballistic calculation and confirm hit determination of Player,Entity,Blocks.
 *
 * @param bdfEntity this BDFEntity is Bullet Owner (equals Person who Fire BDFGun) . If the bdfEntity cannot be {@code null}.
 * @param gun       this BDFGun is Bullet Firing Gun. Bullets speck depends this gun speck If the gun cannot be {@code null}.
 *
 */
class BDFGunBullet(private val bdfEntity: BDFEntity<*>, val gun: BDFGun) : BDFBullet(gun, bdfEntity) {

    /**
     * Gun Firing Position.
     * Used by Ballistic calculation.
     */
    private var firingPoint: Location
    /**
     * Firing Entity (= bdfEntity) Looking Yaw in Firing Time.
     * Used by Ballistic calculation.
     */
    val yaw: Double
    /**
     * Firing Entity (= bdfEntity) Looking Pitch in Firing Time.
     * Used by Ballistic calculation.
     */
    val pitch: Double
    /**
     * Elapsed ticks since Firing time (= From spawn Bullet).
     */
    private var tickCounter: Int = 0
    /**
     * Bullet are Alive or Death.
     * If Alive true.
     * If Death false.
     */
    var isAlive = true

    /**
     *
     */
    init {
        if (bdfEntity.owner is Player) {

            val projectile = bdfEntity.owner.launchProjectile(Snowball::class.java)
            firingPoint = projectile.location
            //見えなくする
            ProtocolLibPacket.sendEntityDestroy(projectile)
            projectile.remove()

        } else {

            firingPoint = bdfEntity.location
            firingPoint.y = firingPoint.y + 1.625
            val vector: Vector = bdfEntity.owner.velocity
            val x: Double = vector.x

            if (x >= -45 && x < 45) {
                firingPoint.z = firingPoint.z + 0.35
            } else if (x >= 45 && x < 135) {
                firingPoint.x = firingPoint.x - 0.35
            } else if (x >= 135 && x < -135) {
                firingPoint.z = firingPoint.z - 0.35
            } else if (x >= -135 && x < -45) {
                firingPoint.x = firingPoint.x + 0.35

            }
            if (bdfEntity is BDFPlayer) {
                if (bdfEntity.bukkitPlayer.isSneaking) {
                    firingPoint = bdfEntity.location
                    firingPoint.y = firingPoint.y + 1.40

                }
            }
        }

        val loca = bdfEntity.owner.location
        if (weapon is BDFGun) {

            var acc = weapon.hipAccuracy
            if (weapon.isFullAuto) acc *= 1.2
            if (bdfEntity is BDFPlayer) {
                if (bdfEntity.bukkitPlayer.isSprinting) {
                    acc *= 1.2
                }
                if (!bdfEntity.bukkitPlayer.isOnGround && !bdfEntity.bukkitPlayer.isFlying) {
                    acc *= 1.4
                }
                if (bdfEntity.bukkitPlayer.isSneaking) {
                    acc *= 0.8
                }
            }
            if (weapon.Scopeing) acc = weapon.adsAccuracy
            var acci: Int = (acc * 1000).toInt()
            if (acci <= 0) {
                acci = 1
            }
            val random = Random()
            val yaws = ((random.nextInt(acci) - random.nextInt(acci) + 0.5) / 200.0)
            val pitchs = ((random.nextInt(acci) - random.nextInt(acci) + 0.5) / 200.0)
            yaw = loca.yaw + yaws
            pitch = loca.pitch + pitchs

        } else {
            yaw = loca.yaw.toDouble()
            pitch = loca.pitch.toDouble()

        }

    }
    /**
     * Death Bullet.
     * Remove from bullets in BulletManager and isAlive flag change to false.
     */
    private fun dead() {
        isAlive = false
        BDFManager.bulletManager.remove(this)
    }



    /**
     * Confirm hit judgement for Near Entity.
     * And Damage to the Hit Entity
     *
     * @param location this Location is Bullet location.
     *
     * @return is Hit Entity? If Hit True , Not Hit False.
     */
    private fun checkNearEntity(location: Location): Boolean {

        //すべてのMobを取得
        loop@ for (entity in location.chunk.entities) {
            /* 自分自身 */
            if (entity == bdfEntity.owner) continue

            /*プレイヤーの判定*/
            if (entity is Player) {

                val bdfPlayer = entity.toBDFPlayer()!!
                if (owner.team != null && bdfPlayer.team != null) {
                    if (owner.team!!.equals(bdfPlayer.team!!)){
                        continue
                    }
                }

                when {
                    bdfPlayer.bukkitPlayer.gameMode == GameMode.CREATIVE -> continue@loop
                    bdfPlayer.bukkitPlayer.gameMode == GameMode.SPECTATOR -> continue@loop
                    bdfPlayer.spectator -> continue@loop
                }
                if (bdfPlayer.isMuteki) continue


                val hitPortion = BulletHitPlayerDecision.whereDecision(bdfPlayer, location)
                var sound = ""
                var title = ""

                when (hitPortion) {
                    HitJudge.Head -> {
                        title = ChatColor.RED.toString() + "∧"
                        sound = "entity.arrow.hit_player"
                    }
                    HitJudge.Body -> {
                        title = ChatColor.GREEN.toString() + "∧"
                        sound = "bdf.action.hit"
                    }
                    HitJudge.Leg -> {
                        title = ChatColor.YELLOW.toString() + "∧"
                        sound = "bdf.action.hit2"
                    }
                    else -> {
                        continue@loop
                    }
                }

                if (addPlayerGunBulletDamage(entity.toBDFPlayer()!!, hitPortion, sound, title)) {
                    BDFManager.playerManager.localPlayers.forEach {
                        if (bdfPlayer != it) { //自分には送らない
                            it.sendParticle(
                                ParticleAPI.EnumParticle.BLOCK_CRACK.setItemID(152),
                                location,
                                0f,
                                0f,
                                0f,
                                0f,
                                8,
                                150.0
                            )
                        }
                    }
                    return true
                } else {
                    continue
                }
            }
        }
        return false
    }

    private fun playBlockBreakEffect(location: Location) {
        val loc2 = location.clone()
        loc2.x += 0.05
        loc2.z += 0.05
        loc2.y += 0.05
        BDFManager.playerManager.localPlayers.forEach {
            it.sendParticle(
                ParticleAPI.EnumParticle.BLOCK_CRACK.setBlock(location.block),
                location,
                0f,
                0f,
                0f,
                0f,
                6,
                150.0
            )
            it.sendParticle(ParticleAPI.EnumParticle.BARRIER, loc2, 0f, 0f, 0f, 0f, 12, 150.0)
        }
        location.world!!.playSound(location, Sound.BLOCK_STONE_STEP, 0.8f, 1.3f)
    }


    /**
     * Confirm judgement location is wall.
     *
     * @param location this Location is Bullet Location.
     * @return is Hit Wall? If Hit True, Not Hit False.
     *
     */
    private fun checkWall(location: Location): Boolean {
        if (BulletHitBlockDecision.isHitBlockDecision(location)) {
            playBlockBreakEffect(location)
            return true
        }
        return false
    }

    /**
     * Processing Bullets every tick.
     * Check Wall,Entity,Distance. And Draw , Calculation Bullet Line.
     */
    override fun onTick() {
        if (isAlive) {
            for (i in 0 until gun.speed * 100) {
                val location: Location = calcBulletPosition(tickCounter + (i / gun.speed.toDouble() / 100))
                if (gun.speed * (tickCounter + (i / gun.speed.toDouble() / 100)) >= 0.5 && gun.speed * (tickCounter + (i / gun.speed.toDouble() / 100)) % 2.0 == 1.0) {
                    location.world!!.spawnParticle(Particle.WATER_BUBBLE, location, 1)
                }
                /* 敵判定 */
                if (checkNearEntity(location)) {
                    dead()
                    return
                }
                /* 壁判定 */
                if (checkWall(location)) {
                    dead()
                    return
                }
                /* 下に打った時の対策 */
                if (location.y < 0) {
                    dead()
                    return
                }
                /* 上に打った時の対策 */
                if (gun.speed * (tickCounter + (i / gun.speed.toDouble() / 100)) >= gun.distance) {
                    dead()
                    return
                }
            }
            tickCounter++
            /*
            var double =0.0
            while (double < 1.0){
                val location:Location = calcBulletPosition(tickCounter + double)
                if(weapon.speed * (tickCounter + double) >= 1.0 ) {
                    location.world.spawnParticle(Particle.CRIT, location, 1)
                }

                if (checkNearEntity(location)) {
                    dead()
                    return
                }
                if (checkWall(location)) {
                    dead()
                    return
                }

                if(location.y < 0){
                    dead()
                    return
                }
                double += 0.1
                count ++
            }
            tickCounter++
        }
        */
        }
    }


    /**
     * Calculation Bullet Position.
     * g = 9.80665 this is Gravity Acceleration.
     * Bullet Position calculation From Oblique projection.
     *
     * @see <a href ="http://wakariyasui.sakura.ne.jp/p/mech/rakutai/syahou.html">Oblique projection Calculation</a>.
     *
     *
     * @param tick this Double is Bullet Alive Tick since Firing time (=Spawn Bullet).
     * @return Bullet Position When tick.
     */
    private fun calcBulletPosition(tick: Double): Location {
        val g = 9.80
        val x0 = firingPoint.x
        val y0 = firingPoint.y
        val z0 = firingPoint.z
        val speed = gun.speed
        val dz = -pitch
        val d = -yaw

        val xt: Double = x0 + (speed * sin(toRadians(d)) * cos(toRadians(dz)) * tick)
        val yt: Double = if (gun.dropRate <= 0.0) {
            y0 + (speed * sin(toRadians(dz)) * tick)
        } else {
            y0 + (speed * sin(toRadians(dz)) * tick) - ((g / 20 * gun.dropRate * tick * tick) / 2)
        }
        val zt: Double = z0 + (speed * cos(toRadians(d)) * cos(toRadians(dz)) * tick)

        return Location(firingPoint.world, xt, yt, zt)
    }

    fun addEntityBulletDamage(
        bdfEntity: BDFLivingEntity,
        judge: HitJudge,
        sound: String,
        title: String
    ): Boolean {
        when {
            cheatGameMode(bdfEntity) -> return false
            judgeFriendlyFire(bdfEntity) -> return false
        }
        val source = BDFEntity.BulletDamageSource(this, judge, getDamageFromHitPosition(judge))
        val event = BDFBulletDamageEvent(bdfEntity, source)
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled)
            return true
        bdfEntity.addDamage(source)

        if (owner is BDFPlayer) {
            owner.playSound(sound, 1F, 1F)
            owner.sendTitle(0, 4, 3, title, "")
        }
        return true

    }

    private fun addPlayerGunBulletDamage(bdfPlayer: BDFPlayer, judge: HitJudge, sound: String, title: String): Boolean {
        if(addPlayerBulletDamage(bdfPlayer, judge, getDamageFromHitPosition(judge))) {
            if (owner is BDFPlayer) {
                owner.playSound(sound, 1F, 1F)
                owner.sendTitle(0, 4, 3, title, "")
            }
        }
        return true
    }


    private fun getDamageFromHitPosition(judge: HitJudge): Double {
        return when (judge) {
            HitJudge.Body -> gun.bodyDamage
            HitJudge.Head -> gun.headShotDamage
            HitJudge.Leg -> gun.legDamage
            HitJudge.Toe -> gun.bodyDamage
            else -> 0.0
        }
    }
}
