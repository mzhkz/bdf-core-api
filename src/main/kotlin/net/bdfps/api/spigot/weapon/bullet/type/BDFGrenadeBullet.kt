package net.bdfps.api.spigot.weapon.bullet.type

import net.bdfps.api.spigot.entity.BDFEntity
import net.bdfps.api.spigot.java.packet.ParticleAPI
import net.bdfps.api.spigot.managers.BDFManager
import net.bdfps.api.spigot.support.BDFPlayer
import net.bdfps.api.spigot.utility.toBDFPlayer
import net.bdfps.api.spigot.weapon.BDFGun
import net.bdfps.api.spigot.weapon.bullet.BDFBullet
import net.bdfps.api.spigot.weapon.type.Grenade
import org.bukkit.*
import org.bukkit.entity.*
import org.bukkit.util.Vector
import org.bukkit.inventory.ItemStack

/**
 * BDFGrenadeBulletはグレネードの投てき、弾道計算、当たり判定の確認を行います.
 * BDFGrenadeBullet is Fire original bullets , Ballistic calculation and confirm hit determination of Player,Entity,Blocks.
 *
 * @param bdfEntity this BDFEntity is Bullet Owner (equals Person who Fire BDFGun) . If the bdfEntity cannot be {@code null}.
 * @param gun       this BDFGun is Bullet Firing Gun. Bullets speck depends this gun speck If the gun cannot be {@code null}.
 *
 */
class BDFGrenadeBullet(
    private val bdfEntity: BDFEntity<*>,
    val grenade: Grenade,
    explodeTicks: Int,
    leftClickShoot: Boolean = false
) : BDFBullet(grenade, bdfEntity) {

    /**
     * Gun Firing Position.
     * Used by Ballistic calculation.
     */
    private var firingPoint: Location
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
     * Bullet projectile Entity
     * Used by Vector and ItemSet
     */
    var projectile: Entity

    var lastloc: Location

    val c: Double = grenade.radius.toDouble()

    /**
     *
     */
    init {
        val item = ItemStack(grenade.itemID)
        firingPoint = bdfEntity.location
        val location: Location = firingPoint
        location.y += 1.6
        projectile = bdfEntity.location.world.dropItem(location, item)
        val vector: Vector = Vector(0, 0, 0)
        val speed: Double = if (leftClickShoot) {
            grenade.speed / 30.0
        } else {
            grenade.speed / 10.0
        }
        vector.add(bdfEntity.location.direction.clone().normalize().multiply(speed))
        projectile.velocity = vector

        if (projectile is Item) {
            (projectile as Item).pickupDelay = 9999999
            val itemMeta = (projectile as Item).itemStack.itemMeta
            itemMeta.displayName = "" + hashCode()
            (projectile as Item).itemStack.itemMeta = itemMeta
        }
        lastloc = projectile.location.clone()
        tickCounter = explodeTicks

    }

    /**
     * Death Bullet.
     * Remove from bullets in BulletManager and isAlive flag change to false.
     */
    private fun dead() {
        isAlive = false
        if (!projectile.isDead) projectile.remove()
        BDFManager.bulletManager.remove(this)
    }


    private fun playBlockBreakEffect(location: Location) {
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
            it.sendParticle(ParticleAPI.EnumParticle.VILLAGER_HAPPY, location, 0f, 0f, 0f, 0f, 6, 150.0)
        }
        location.world.playSound(location, Sound.BLOCK_STONE_STEP, 0.8f, 1.3f)
    }


    /**
     * Processing Bullets every tick.
     * Check Wall,Entity,Distance. And Draw , Calculation Bullet Line.
     */
    override fun onTick() {
        if (isAlive) {
            if (tickCounter >= 0) {
                lastloc = projectile.location.clone()
                if (projectile.isOnGround) {
                    val location = lastloc.clone().add(0.0, 0.6, 0.0)
                    BDFManager.playerManager.localPlayers.forEach {
                        it.sendParticle(
                            ParticleAPI.EnumParticle.RED_DUST.setColor(Color.fromRGB(237, 117, 43)),
                            location,
                            0f,
                            0f,
                            0f,
                            0F,
                            1,
                            150.0
                        )
                    }
                } else {
                    val location = lastloc.clone().add(0.0, 0.2, 0.0)
                    BDFManager.playerManager.localPlayers.forEach {
                        it.sendParticle(
                            ParticleAPI.EnumParticle.RED_DUST.setColor(Color.GRAY),
                            location,
                            0f,
                            0f,
                            0f,
                            0F,
                            1,
                            150.0
                        )
                    }
                }
                tickCounter--
            } else {
                Explosion()
            }
        } else {
            Explosion()
        }
    }

    fun Explosion() {
        dead()
        if (c > 0) {
            BDFManager.playerManager.localPlayers.forEach {
                it.sendParticle(
                    ParticleAPI.EnumParticle.EXPLOSION_HUGE,
                    lastloc.clone(),
                    0f,
                    0f,
                    0f,
                    0F,
                    1,
                    150.0
                )
                it.bukkitPlayer.playSound(lastloc.clone(), Sound.ENTITY_GENERIC_EXPLODE, 1F, 1F)
            }

            for (entity in projectile.getNearbyEntities(c, c, c)) {

                val entityLoc = entity.location
                val pLoc = projectile.location
                if (distanceBlocks(pLoc, entityLoc)) {
                    continue
                }
                if (entity is Player) {
                    val hitPosition =
                        if (projectile.location.clone().distance(entity.location) <= grenade.directHitRadius) {
                            HitJudge.Hittable
                        } else {
                            HitJudge.ShallowlyHit
                        }
                    if (addPlayerGrenadeBulletDamage(entity.toBDFPlayer()!!, hitPosition, "", "")) {
                        continue
                    } else {
                        continue
                    }

                }
            }

            // TODO ブロックへのダメージ、プレイヤーへのダメージを書く

        }

    }

    fun distanceBlocks(loc1: Location, loc2: Location): Boolean {
        val loc1clone = loc1.clone()
        val loc1clone2 = loc1.clone()
        val lx = loc2.x - loc1.x
        val ly = loc2.y - loc1.y
        val lz = loc2.z - loc1.z
        val locfix = loc2
        locfix.y += 1.7
        val lx2 = locfix.x - loc1.x
        val ly2 = locfix.y - loc1.y
        val lz2 = locfix.z - loc1.z
        val dist = ((loc1.distance(loc2)) * 100).toInt()
        val dist2 = ((loc1.distance(locfix)) * 100).toInt()
        val vector = Vector(lx / dist, ly / dist, lz / dist)
        val vector2 = Vector(lx2 / dist2, ly2 / dist2, lz2 / dist2)
        var attackleg = true
        var attackhead = true
        var attack = false
        for (i in 0 until dist) {
            val loc = loc1clone.add(vector)

            if (isHit(loc)) {
                attackleg = false
                break
            }
        }
        for (i in 0 until dist2) {
            val loc2l = loc1clone2.add(vector2)

            if (isHit(loc2l)) {
                attackhead = false
                break
            }
        }
        if (attackhead || attackleg) attack = true
        return !attack
    }

    private fun addPlayerGrenadeBulletDamage(
        bdfPlayer: BDFPlayer,
        judge: HitJudge,
        sound: String,
        title: String
    ): Boolean {
        if (addPlayerBulletDamage(bdfPlayer, judge, getDamageFromHitPosition(judge))) {
            if (owner is BDFPlayer) {
                owner.playSound(sound, 1F, 1F)
                owner.sendTitle(0, 4, 3, title, "")
            }
            return true
        }
        return false
    }

    private fun getDamageFromHitPosition(judge: HitJudge): Double {
        return when (judge) {
            HitJudge.Hittable -> grenade.directHitDamage
            HitJudge.ShallowlyHit -> grenade.hitDamage
            else -> 0.0
        }
    }

    private fun isHit(location: Location): Boolean {
        val block = location.block
        val material: Material = block.type
        return when (material) {
            Material.PURPUR_SLAB,
            Material.STONE_SLAB2,
            Material.WOOD_STEP,
            Material.STEP,
            Material.ACACIA_DOOR,
            Material.BIRCH_DOOR,
            Material.DARK_OAK_DOOR,
            Material.IRON_DOOR,
            Material.JUNGLE_DOOR,
            Material.SPRUCE_DOOR,
            Material.WOODEN_DOOR,
            Material.TRAP_DOOR,
            Material.IRON_TRAPDOOR,
            Material.DAYLIGHT_DETECTOR,
            Material.DAYLIGHT_DETECTOR_INVERTED,
            Material.SAPLING,
            Material.WATER,
            Material.STATIONARY_WATER,
            Material.LEAVES,
            Material.LEAVES_2,
            Material.RAILS,
            Material.ACTIVATOR_RAIL,
            Material.POWERED_RAIL,
            Material.DETECTOR_RAIL,
            Material.WEB,
            Material.GRASS_PATH,
            Material.LONG_GRASS,
            Material.FLOWER_POT,
            Material.DEAD_BUSH,
            Material.CHORUS_FLOWER,
            Material.YELLOW_FLOWER,
            Material.RED_ROSE,
            Material.BROWN_MUSHROOM,
            Material.RED_MUSHROOM,
            Material.TORCH,
            Material.REDSTONE_TORCH_OFF,
            Material.REDSTONE_TORCH_ON,
            Material.SIGN,
            Material.SIGN_POST,
            Material.WALL_SIGN,
            Material.LADDER,
            Material.LEVER,
            Material.GOLD_PLATE,
            Material.IRON_PLATE,
            Material.STONE_PLATE,
            Material.WOOD_PLATE,
            Material.SNOW,
            Material.IRON_FENCE,
            Material.VINE,
            Material.WATER_LILY,
            Material.TRIPWIRE,
            Material.TRIPWIRE_HOOK,
            Material.CARROT,
            Material.POTATO,
            Material.STONE_BUTTON,
            Material.WOOD_BUTTON,
            Material.REDSTONE,
            Material.REDSTONE_COMPARATOR,
            Material.REDSTONE_COMPARATOR_OFF,
            Material.REDSTONE_COMPARATOR_ON,
            Material.REDSTONE_WIRE,
            Material.DIODE,
            Material.DIODE_BLOCK_OFF,
            Material.DIODE_BLOCK_ON,
            Material.BARRIER,
            Material.CARPET,
            Material.DOUBLE_PLANT,
            Material.STANDING_BANNER,
            Material.WALL_BANNER,
            Material.BANNER,
            Material.END_ROD,
            Material.ITEM_FRAME,
            Material.PAINTING,
            Material.ARMOR_STAND,
            Material.STAINED_GLASS_PANE,
            Material.THIN_GLASS,
            Material.AIR
            -> false
            else -> true
        }
    }
}
