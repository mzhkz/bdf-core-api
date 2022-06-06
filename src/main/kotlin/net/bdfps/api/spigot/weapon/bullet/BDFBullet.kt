package net.bdfps.api.spigot.weapon.bullet

import net.bdfps.api.spigot.entity.BDFEntity
import net.bdfps.api.spigot.support.BDFPlayer
import net.bdfps.api.spigot.weapon.BDFWeapon
import org.bukkit.Bukkit
import org.bukkit.GameMode

abstract class BDFBullet(val weapon: BDFWeapon, val owner: BDFEntity<*>) {

    val playerEntity: BDFPlayer?
        get() = owner as? BDFPlayer

    abstract fun onTick()

    fun judgeFriendlyFire(bdfEntity: BDFEntity<*>): Boolean {
        if (owner.team != null && bdfEntity.team != null) {
            if (owner.team!!.equals(bdfEntity.team!!)){
                if(weapon.slotType == BDFWeapon.SlotType.Throwing){
                    if(weapon.owner!! == bdfEntity) return false
                }
                return true
            }
        }
        return false
    }

    fun cheatGameMode(bdfEntity: BDFEntity<*>): Boolean {
        if (bdfEntity is BDFPlayer) {
            when {
                bdfEntity.bukkitPlayer.gameMode == GameMode.CREATIVE -> return true
                bdfEntity.bukkitPlayer.gameMode == GameMode.SPECTATOR -> return true
                bdfEntity.spectator -> return true
            }
        }
        if (bdfEntity.isMuteki) return true
        return false
    }

    /**
     * プレイヤーにダメージを入れる
     */
    fun addPlayerBulletDamage(bdfPlayer: BDFPlayer, judge: HitJudge, damage: Double): Boolean {
        when {
            cheatGameMode(bdfPlayer) -> return false
            judgeFriendlyFire(bdfPlayer) -> return false
        }
        return bdfPlayer.addDamage(BDFEntity.BulletDamageSource(this, judge, damage))
    }

    enum class HitJudge {
        //銃弾
        Head,
        Leg,
        Body,
        Toe,
        //投げもの関係
        Hittable,
        ShallowlyHit,
        //その他
        NoneAttack;
    }
}
