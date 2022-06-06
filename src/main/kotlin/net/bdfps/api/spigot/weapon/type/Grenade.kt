package net.bdfps.api.spigot.weapon.type

import net.bdfps.api.spigot.managers.BDFManager
import net.bdfps.api.spigot.utility.second
import net.bdfps.api.spigot.weapon.BDFThrow
import net.bdfps.api.spigot.weapon.bullet.type.BDFGrenadeBullet
import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack

class Grenade : BDFThrow() {

    var directHitRadius: Double = 1.5
    var directHitDamage = 0.0
    var hitDamage = 0.0

    override fun itemInstanceOption(itemStack: ItemStack) {
        itemStack.amount = hasAmmo
    }

    override fun doSwap() {
        //何もなし
    }

    override fun doNormalThrow() {
        val bdfGunBullet = BDFGrenadeBullet(owner!!, this, explodeTime, leftClickShoot)
        BDFManager.bulletManager.addBullet(bdfGunBullet)
        Waiting = true
        leftClickShoot = false
    }

    override fun doPullPinThrow() {
        val bdfGunBullet = BDFGrenadeBullet(owner!!, this, tick, leftClickShoot)
        BDFManager.bulletManager.addBullet(bdfGunBullet)
        Pullpining = false
        leftClickShoot = false
        Waiting = true
    }


    override val display: String
        get() {
            return when {
                Swapping -> "${ChatColor.BOLD}$name ${ChatColor.GRAY}Swapping ${ChatColor.DARK_AQUA}${ChatColor.BOLD}${second(
                    tick.toFloat()
                )}"
                Pullpining -> "${ChatColor.BOLD}$name ${ChatColor.GRAY}Pulling ${ChatColor.DARK_AQUA}${ChatColor.BOLD}${second(
                    tick.toFloat()
                )}"
                else -> {
                    if (isInfiniteAmmo)
                        "${ChatColor.BOLD}$name ${ChatColor.WHITE}∞${ChatColor.GRAY}"
                    else
                        "${ChatColor.BOLD}$name ${ChatColor.WHITE}$hasAmmo${ChatColor.GRAY}"
                }
            }
        }
}
