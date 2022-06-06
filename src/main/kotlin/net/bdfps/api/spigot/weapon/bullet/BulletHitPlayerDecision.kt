package net.bdfps.api.spigot.weapon.bullet

import net.bdfps.api.spigot.support.BDFPlayer
import net.bdfps.api.spigot.weapon.bullet.type.BDFGunBullet
import org.bukkit.Location

object BulletHitPlayerDecision {

    var x = 0.0
    var y = 0.0
    var z = 0.0


    private val frontRadius = 0.45 //正面判定
    private val sideRadius = 0.3 //横判定
    private var height = 1.95


    fun whereDecision(bdfPlayer: BDFPlayer, location: Location): BDFBullet.HitJudge {
        if (isHit(bdfPlayer, location)) {
            val locY = bdfPlayer.location.y
            if(bdfPlayer.bukkitPlayer.isSneaking){
                return when {
                    location.y < locY + 0.6 -> BDFBullet.HitJudge.Leg
                    location.y >= locY + 1.1 -> BDFBullet.HitJudge.Head
                    else -> BDFBullet.HitJudge.Body
                }
            }else {
                return when {
                    location.y < locY + 0.6 -> BDFBullet.HitJudge.Leg
                    location.y >= locY + 1.3 -> BDFBullet.HitJudge.Head
                    else -> BDFBullet.HitJudge.Body
                }
            }
        }
        return BDFBullet.HitJudge.NoneAttack
    }

    private fun isHit(bdfPlayer: BDFPlayer, location: Location): Boolean {
        val vector = bdfPlayer.bukkitPlayer.velocity
        val x = vector.x
        val y = vector.y
        val z = vector.z

        var position = Direction.None

        if (x == 0.0 && y == 0.0 && z == 0.0) {
            this.x = x
            this.y = y
            this.z = z
        }
        if (x >= -45 && x < 45) {
            position = Direction.South
        } else if (x >= 45 && x < 135) {
            position = Direction.West
        } else if (x >= 135 && x < -135) {
            position = Direction.North
        } else if (x >= -135 && x < -45) {
            position = Direction.East
        }

        val playerLocation = bdfPlayer.bukkitPlayer.location
        val world = playerLocation.world

        if (world !== location.world) {
            throw IllegalArgumentException("world != world")
        }

        val locX = playerLocation.x
        val locY = playerLocation.y
        val locZ = playerLocation.z

        var sideUp: Location? = null
        var sideDown: Location? = null
        if(bdfPlayer.bukkitPlayer.isSneaking){
            height = 1.5
        }
        /*if (bdfPlayer.bukkitPlayer.isSneaking) {

            val step = bdfPlayer.bukkitPlayer.location.clone()
            sideUp = step.clone().add(0.0, 0.0, frontRadius)
            val dire = bdfPlayer.bukkitPlayer.velocity.clone()
            for (i in 0..1) {
                step.add(dire)
            }
            sideDown = step.clone().add(frontRadius, 0.0, 0.0)
        } else {*/
           if (position === Direction.North || position === Direction.South) { //x
                sideUp = Location(world, locX + frontRadius, locY + height, locZ + sideRadius)
                sideDown = Location(world, locX - frontRadius, locY, locZ - sideRadius)
            } else if (position === Direction.West || position === Direction.East) { //z
                sideUp = Location(world, locX + sideRadius, locY + height, locZ + frontRadius)
                sideDown = Location(world, locX - sideRadius, locY, locZ - frontRadius)
            }
    //    }
        if (sideUp != null && sideDown != null) {
            //X Box Data
            val maxX = Math.max(sideUp.x, sideDown.x)
            val minX = Math.min(sideUp.x, sideDown.x)

            //Y Box Data
            val maxY = Math.max(sideUp.y, sideDown.y)
            val minY = Math.min(sideUp.y, sideDown.y)

            //X Box Data
            val maxZ = Math.max(sideUp.z, sideDown.z)
            val minZ = Math.min(sideUp.z, sideDown.z)

            if (location.x in minX..maxX && location.y in minY..maxY && location.z in minZ..maxZ) {
                return true
            }
        }

        height  = 1.95

        return false
    }

/*
    fun whereDecisision(location: Location): HitPosition {
        val vector: Vector = player.bukkitPlayer.velocity
        val x: Double = vector.x
        var direction: Direction = Direction.None

        if (x >= -45 && x < 45) {
            direction = Direction.South
        } else if (x >= 45 && x < 135) {
            direction = Direction.West
        } else if (x >= 135 && x < -135) {
            direction = Direction.North
        } else if (x >= -135 && x < -45) {
            direction = Direction.East
        }

        val entityLocation = player.location
        val world: World = entityLocation.world

        if (world == location.world) {


            if (player.bukkitPlayer.isSneaking) {
                when (direction) {
                        Direction.South, Direction.North -> {

                            if (location.z <= entityLocation.z + 0.31 && location.z >= entityLocation.z - 0.33) {

                                return when {
                                    location.y <= entityLocation.y + 0.45 -> if (location.x <= entityLocation.x + 0.3 && location.x >= entityLocation.x - 0.3) HitPosition.Head
                                    else HitPosition.NoneAttack
                                    location.y >= entityLocation.y + 1.1 -> if (location.x <= entityLocation.x + 0.3 && location.x >= entityLocation.x - 0.3) HitPosition.Leg
                                    else HitPosition.NoneAttack
                                    else -> HitPosition.Body
                                }
                            }
                        }
                        Direction.East, Direction.West -> {
                            if (location.x <= entityLocation.x + 0.31 && location.x >= entityLocation.x - 0.33) {
                                return when {
                                    location.y <= entityLocation.y + 0.45 -> if (location.z <= entityLocation.z + 0.3 && location.z >= entityLocation.z - 0.3) HitPosition.Leg
                                    else HitPosition.NoneAttack
                                    location.y >= entityLocation.y + 1.1 -> if (location.z <= entityLocation.z + 0.3 && location.z >= entityLocation.z - 0.3) HitPosition.Head
                                    else HitPosition.NoneAttack
                                    else -> HitPosition.Body
                                }
                            }

                        }
                        else -> return HitPosition.NoneAttack

                    }
                } else {
                when (direction) {
                        Direction.South, Direction.North -> {

                            when {
                                location.y <= entityLocation.y + 0.6 -> {

                                    if (location.x <= entityLocation.x + 0.32 && location.x >= entityLocation.x - 0.32) {
                                        if (location.z <= entityLocation.z + 0.2 && location.z >= entityLocation.z - 0.2) return HitPosition.Leg
                                    }
                                }
                                location.y >= entityLocation.y + 1.45 && location.y <= entityLocation.y + 1.9 -> {
                                    if (location.x <= entityLocation.x + 0.3 && location.x >= entityLocation.x - 0.3) {
                                        if (location.z <= entityLocation.z + 0.13 && location.z >= entityLocation.z - 0.13) return HitPosition.Head
                                    }
                                }
                                else -> {
                                    if (location.x <= entityLocation.x + 0.45 && location.x >= entityLocation.x - 0.45) {

                                        if (location.z <= entityLocation.z + 0.13 && location.z >= entityLocation.z - 0.13) return HitPosition.Body
                                    }
                                }
                            }
                            return HitPosition.NoneAttack

                        }
                        Direction.East, Direction.West -> {
                            when {
                                location.y <= entityLocation.y + 0.6 -> {
                                    if (location.z <= entityLocation.z + 0.32 && location.x >= entityLocation.z - 0.32) {
                                        if (location.x <= entityLocation.x + 0.2 && location.x >= entityLocation.x - 0.2) return HitPosition.Leg
                                    }
                                }
                                location.y >= entityLocation.y + 1.45 && location.y <= entityLocation.y + 1.9 -> {
                                    if (location.z <= entityLocation.z + 0.3 && location.z >= entityLocation.z - 0.3) {
                                        if (location.x <= entityLocation.x + 0.13 && location.x >= entityLocation.x - 0.13) return HitPosition.Head
                                    }
                                }
                                else -> {
                                    if (location.z <= entityLocation.z + 0.45 && location.z >= entityLocation.z - 0.45) {

                                        if (location.x <= entityLocation.x + 0.13 && location.x >= entityLocation.x - 0.13) return HitPosition.Body
                                    }
                                }
                            }
                            return HitPosition.NoneAttack

                        }
                        else -> {
                            return HitPosition.NoneAttack
                        }
                    }
                }


            return HitPosition.Body
        }
        return HitPosition.NoneAttack
    }
    */

    enum class Direction {
        North,
        South,
        West,
        East,
        None;
    }
}
