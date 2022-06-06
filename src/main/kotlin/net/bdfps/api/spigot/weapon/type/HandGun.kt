package net.bdfps.api.spigot.weapon.type

import net.bdfps.api.spigot.utility.second
import net.bdfps.api.spigot.weapon.BDFGun
import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack

class HandGun: BDFGun() {
    override fun itemInstanceOption(itemStack: ItemStack) {
        //無し
    }

    override val display: String
        get() {
            return when {
                Reloading -> {
                    return when (reloadType) {
                        BDFGun.ReloadType.Normal -> "${ChatColor.BOLD}$name ${ChatColor.GRAY}Reloading ${ChatColor.DARK_AQUA}${ChatColor.BOLD}${second(tick.toFloat())}"
                        BDFGun.ReloadType.Bolt -> {
                            if (isInfiniteAmmo)
                                "${ChatColor.BOLD}$name ${ChatColor.WHITE}$hasMagazineAmmo${ChatColor.GRAY} | ∞ ${ChatColor.DARK_AQUA}${ChatColor.BOLD}${second(tick.toFloat())}"
                            else
                                "${ChatColor.BOLD}$name ${ChatColor.WHITE}$hasMagazineAmmo${ChatColor.GRAY} | $hasPreMagazineAmmo ${ChatColor.DARK_AQUA}${ChatColor.BOLD}${second(tick.toFloat())}"
                        }
                    }
                }
                Swapping -> "${ChatColor.BOLD}$name ${ChatColor.GRAY}Swapping ${ChatColor.DARK_AQUA}${ChatColor.BOLD}${second(tick.toFloat())}"
                else -> {
                    if (isInfiniteAmmo)
                        "${ChatColor.BOLD}$name ${ChatColor.WHITE}$hasMagazineAmmo${ChatColor.GRAY} | ∞ $status"
                    else
                        "${ChatColor.BOLD}$name ${ChatColor.WHITE}$hasMagazineAmmo${ChatColor.GRAY} | $hasPreMagazineAmmo $status"
                }
            }
        }

    override val outAmmoMessage: String
        get() = ChatColor.RED.toString() + "弾が切れた"

    override fun doShoot() {
    }

    override fun doSwap() {
        //TODO スワップ音
    }

    override fun doScope() {
        //TODO スコープ音
    }

    override fun doDelayShoot() {
    }

    override fun doReload() {
        //TODO 武器のリロード音
    }
}

