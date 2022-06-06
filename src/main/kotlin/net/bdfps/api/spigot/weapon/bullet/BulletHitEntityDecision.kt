package net.bdfps.api.spigot.weapon.bullet

import net.bdfps.api.spigot.entity.BDFLivingEntity
import net.bdfps.api.spigot.weapon.bullet.type.BDFGunBullet
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector

object BulletHitEntityDecision {

    fun whereDecisision(entity: BDFLivingEntity, location: Location): BDFBullet.HitJudge {
        val vector: Vector = entity.owner.velocity.clone()
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

        val entityLocation = entity.location.clone()
        val world: World = entityLocation.world!!
/*
        if (world == location.world) {
            /*
            when (direction) {
                    Direction.South, Direction.North -> {
                        when {
                            location.y >= entityLocation.y + 0.0 && location.y <= entityLocation.y + 0.6  -> {
                                if (location.x <= entityLocation.x + 0.32 && location.x >= entityLocation.x - 0.32) {
                                    if (location.z <= entityLocation.z + 0.2 && location.z >= entityLocation.z - 0.2) return HitPosition.Leg
                                }
                            }

                            location.y >= entityLocation.y + 1.45 && location.y <= entityLocation.y + 1.9 -> {
                                if (location.x <= entityLocation.x + 0.3 && location.x >= entityLocation.x - 0.3) {
                                    if (location.z <= entityLocation.z + 0.13 && location.z >= entityLocation.z - 0.13) return HitPosition.Head
                                }
                            }
                            location.y > entityLocation.y + 0.6 && location.y < entityLocation.y + 1.45 -> {
                                if (location.z <= entityLocation.z + 0.13 && location.z >= entityLocation.z - 0.13) return HitPosition.Body
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
                                if (location.x <= entityLocation.x + 0.13 && location.x >= entityLocation.x - 0.13) return HitPosition.Body

                            }
                        }
                        return HitPosition.NoneAttack

                    }
                    else -> {
                        return HitPosition.NoneAttack
                    }
                }*/
            return HitPosition.Body
            }*/
        return BDFBullet.HitJudge.NoneAttack
    }

    enum class Direction {
        North,
        South,
        West,
        East,
        None;
    }
}
