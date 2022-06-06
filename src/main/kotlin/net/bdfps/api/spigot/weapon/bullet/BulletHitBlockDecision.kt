package net.bdfps.api.spigot.weapon.bullet

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.material.Door


/**
 *
 * BulletHitBlockDecision はブロックへの当たり判定を行うクラスです
 *
 *
 */
object BulletHitBlockDecision {

    /**
     * Bullets hit to Block
     */
    fun isHitBlockDecision(location: Location): Boolean {
        val block = location.block
        val blockLocation = block.location
        val material: Material = block.type
        val data: Byte = block.data
        return when (material) {
            Material.ACACIA_STAIRS,
            Material.BIRCH_WOOD_STAIRS,
            Material.BRICK_STAIRS,
            Material.COBBLESTONE_STAIRS,
            Material.DARK_OAK_STAIRS,
            Material.JUNGLE_WOOD_STAIRS,
            Material.NETHER_BRICK_STAIRS,
            Material.PURPUR_STAIRS,
            Material.QUARTZ_STAIRS,
            Material.RED_SANDSTONE_STAIRS,
            Material.SANDSTONE_STAIRS,
            Material.SMOOTH_STAIRS,
            Material.SPRUCE_WOOD_STAIRS,
            Material.WOOD_STAIRS
            -> isHitStairs(blockLocation, location, data)
            Material.PURPUR_SLAB,
            Material.STONE_SLAB2,
            Material.WOOD_STEP,
            Material.STEP
            -> isHitSteps(blockLocation, location, data)
            Material.ANVIL
            -> isHitAnvil(blockLocation, location, data)
            Material.ACACIA_DOOR,
            Material.BIRCH_DOOR,
            Material.DARK_OAK_DOOR,
            Material.IRON_DOOR_BLOCK,
            Material.JUNGLE_DOOR,
            Material.SPRUCE_DOOR,
            Material.WOODEN_DOOR
            -> isHitDoor(blockLocation, location, data)
            Material.TRAP_DOOR,
            Material.IRON_TRAPDOOR
            -> isHitTrapDoor(blockLocation, location, data)
            Material.HOPPER
            -> isHitHopper(blockLocation, location, data)
            Material.DAYLIGHT_DETECTOR,
            Material.DAYLIGHT_DETECTOR_INVERTED
            -> isHitDetector(blockLocation, location)
            Material.SAPLING,
            Material.WATER,
            Material.STATIONARY_WATER,
            Material.LEAVES,
            Material.LEAVES_2,
            Material.GLASS,
            Material.STAINED_GLASS_PANE,
            Material.THIN_GLASS,
                //Material.STAINED_GLASS,
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
            Material.AIR,
            Material.LAVA,
            Material.STATIONARY_LAVA
            -> false
            else -> true

        }
    }

    private fun isHitStairs(blockLocation: Location, location: Location, byte: Byte): Boolean {
        if (byte == 0.toByte()) {
            return if (location.y <= blockLocation.y + 0.5)
                true
            else
                location.x > blockLocation.x + 0.5
        }
        if (byte == 1.toByte()) {
            return if (location.y <= blockLocation.y + 0.5)
                true
            else
                location.x <= blockLocation.x + 0.5
        }
        if (byte == 2.toByte()) {
            return if (location.y <= blockLocation.y + 0.5)
                true
            else
                location.z > blockLocation.z + 0.5
        }
        if (byte == 3.toByte()) {
            return if (location.y <= blockLocation.y + 0.5)
                true
            else
                location.z <= blockLocation.z + 0.5
        }
        if (byte == 4.toByte()) {
            return if (location.y > blockLocation.y + 0.5)
                true
            else
                location.x > blockLocation.x + 0.5
        }
        if (byte == 5.toByte()) {
            return if (location.y > blockLocation.y + 0.5)
                true
            else
                location.x <= blockLocation.x + 0.5
        }
        if (byte == 6.toByte()) {
            return if (location.y > blockLocation.y + 0.5)
                true
            else
                location.z > blockLocation.z + 0.5
        }
        if (byte == 7.toByte()) {
            return if (location.y > blockLocation.y + 0.5)
                true
            else
                location.z <= blockLocation.z + 0.5
        }
        return false
    }

    private fun isHitSteps(blockLocation: Location, location: Location, data: Byte): Boolean {
        return if (data < 8.toByte())
            location.y <= blockLocation.y + 0.5
        else
            location.y > blockLocation.y + 0.5
    }

    private fun isHitAnvil(blockLocation: Location, location: Location, data: Byte): Boolean {
        return if (data % 2.toByte() != 0) //横置き
            if (location.y <= blockLocation.y + 0.2 || location.y >= blockLocation.y + 0.6)
                location.x >= blockLocation.x + 0.18 && location.x <= blockLocation.x + 0.82
            else
                location.x >= blockLocation.x + 0.35 && location.x <= blockLocation.x + 0.65
        else
            if (location.y <= blockLocation.y + 0.2 || location.y >= blockLocation.y + 0.6)
                location.z >= blockLocation.z + 0.18 && location.z <= blockLocation.z + 0.82
            else
                location.z >= blockLocation.z + 0.35 && location.z <= blockLocation.z + 0.65

    }

    private fun isHitDoor(blockLocation: Location, location: Location, data: Byte): Boolean {
        val door = location.block.state.data
        if (door is Door) {
            if (!door.isTopHalf) {
                if (door.isOpen) {
                    if (!door.hinge) {/*hingeがみぎ*/

                        if (data == 4.toByte()) {
                            return location.z > blockLocation.z + 0.76
                        }
                        if (data == 5.toByte()) {
                            return location.x <= blockLocation.x + 0.23
                        }
                        if (data == 6.toByte()) {
                            return location.z <= blockLocation.z + 0.23
                        }
                        if (data == 7.toByte()) {
                            return location.x > blockLocation.x + 0.76
                        }
                    } else {

                        if (data == 7.toByte()) {
                            return location.x <= blockLocation.x + 0.23
                        }
                        if (data == 4.toByte()) {
                            return location.z <= blockLocation.z + 0.23
                        }
                        if (data == 5.toByte()) {
                            return location.x > blockLocation.x + 0.76
                        }
                        if (data == 6.toByte()) {
                            return location.z > blockLocation.z + 0.76
                        }
                    }
                }

                if (data == 0.toByte()) {
                    return location.x <= blockLocation.x + 0.23
                }
                if (data == 1.toByte()) {
                    return location.z <= blockLocation.z + 0.23
                }
                if (data == 2.toByte()) {
                    return location.x > blockLocation.x + 0.76
                }
                if (data == 3.toByte()) {
                    return location.z > blockLocation.z + 0.76
                }
            }
            else {
                val loca: Location = location
                loca.y = loca.y- 1
                val blockloca:Location = blockLocation
                blockloca.y = blockloca.y -1
                val doors = loca.block.state.data
                if (doors is Door) {
                    if (doors.isOpen) {
                        if (doors.hinge) {/*hingeがみぎ*/

                            if (data == 4.toByte()) {
                                return loca.z > blockloca.z + 0.76
                            }
                            if (data == 5.toByte()) {
                                return loca.x <= blockloca.x + 0.23
                            }
                            if (data == 6.toByte()) {
                                return loca.z <= blockloca.z + 0.23
                            }
                            if (data == 7.toByte()) {
                                return loca.x > blockloca.x + 0.76
                            }
                        } else {

                            if (data == 7.toByte()) {
                                return loca.x <= blockloca.x + 0.23
                            }
                            if (data == 4.toByte()) {
                                return loca.z <= blockloca.z + 0.23
                            }
                            if (data == 5.toByte()) {
                                return loca.x > blockloca.x + 0.76
                            }
                            if (data == 6.toByte()) {
                                return loca.z > blockloca.z + 0.76
                            }
                        }
                    }

                    if (data == 0.toByte()) {
                        return loca.x <= blockloca.x + 0.23
                    }
                    if (data == 1.toByte()) {
                        return loca.z <= blockloca.z + 0.23
                    }
                    if (data == 2.toByte()) {
                        return loca.x > blockloca.x + 0.76
                    }
                    if (data == 3.toByte()) {
                        return loca.z > blockloca.z + 0.76
                    }
                }
            }

        }
        return false

    }

    private fun isHitTrapDoor(blockLocation: Location, location: Location, data: Byte): Boolean {

        if (data <= 3.toByte()) {
            return location.y < blockLocation.y + 0.18750
        }
        if (data == 4.toByte() || data == 12.toByte()) {
            return location.z > blockLocation.z + 0.8125
        }
        if (data == 5.toByte() || data == 13.toByte()) {
            return location.z < blockLocation.z + 0.18750
        }
        if (data == 6.toByte() || data == 14.toByte()) {
            return location.x > blockLocation.x + 0.8125
        }
        if (data == 7.toByte() || data == 15.toByte()) {
            return location.x < blockLocation.x + 0.18750
        }
        return location.y > blockLocation.y + 0.8125
    }

    private fun isHitHopper(blockLocation: Location, location: Location, data: Byte): Boolean {


        if (data == 0.toByte()) {
            if (location.y > blockLocation.y + 0.65) {
                return true
            }
            if (location.y > blockLocation.y + 0.20) {
                return location.x > blockLocation.x + 0.25 && location.x < blockLocation.x + 0.75 && location.z > blockLocation.z + 0.25 && location.z < blockLocation.z + 0.75
            }
            return location.x > blockLocation.x + 0.4 && location.x < blockLocation.x + 0.6 && location.z > blockLocation.z + 0.4 && location.z < blockLocation.z + 0.6
        } else if (data == 2.toByte()) {
            if (location.y > blockLocation.y + 0.65) {
                return true
            }
            if (location.y > blockLocation.y + 0.20) {
                return location.x > blockLocation.x + 0.25 && location.x < blockLocation.x + 0.75 && location.z < blockLocation.z + 0.75
            }
        } else if (data == 3.toByte()) {
            if (location.y > blockLocation.y + 0.65) {
                return true
            }
            if (location.y > blockLocation.y + 0.20) {
                return location.x > blockLocation.x + 0.25 && location.x < blockLocation.x + 0.75 && location.z < blockLocation.z + 0.75
            }
        } else if (data == 4.toByte()) {
            if (location.y > blockLocation.y + 0.65) {
                return true
            }
            if (location.y > blockLocation.y + 0.20) {
                return location.x < blockLocation.x + 0.75 && location.z > blockLocation.z + 0.25 && location.z < blockLocation.z + 0.75
            }
        } else {
            if (location.y > blockLocation.y + 0.65) {
                return true
            }
            if (location.y > blockLocation.y + 0.20) {
                return location.x > blockLocation.x + 0.25 && location.z > blockLocation.z + 0.25 && location.z < blockLocation.z + 0.75
            }
        }
        return false

    }

    private fun isHitDetector(blockLocation: Location, location: Location): Boolean {
        return location.y <= blockLocation.y + 0.38
    }
}
