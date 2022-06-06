package net.bdfps.api.spigot.entity

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import net.bdf.api.data.Mobject
import net.bdfps.api.spigot.support.BDFPlayer
import net.bdfps.api.spigot.support.abc.BDFMobject
import net.bdfps.api.spigot.utility.syncTask
import net.bdfps.api.spigot.weapon.BDFWeapon
import net.bdfps.api.spigot.weapon.bullet.BDFBullet
import net.bdfps.api.spigot.weapon.bullet.type.BDFGrenadeBullet
import net.bdfps.api.spigot.weapon.bullet.type.BDFGunBullet
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.scheduler.BukkitTask
import java.util.*

abstract class BDFEntity<T : Mobject>(val owner: Entity, val divideTask: Boolean = false): BDFMobject<T>() {

    var task: BukkitTask? = null

    val entId: Int //EntityID
        get() = owner.entityId
    val uniqueId: UUID //ユニークなID
        get() = owner.uniqueId

    var isMuteki: Boolean = false //無敵フラグ
    var isInfinite: Boolean = false //むげんだんやくふらぐ
    var isSafety: Boolean = false //セーフティフラグ
    var isMoving: Boolean = false //動いているかフラグ

    val inventoryWeapons: MutableList<BDFWeapon> = mutableListOf() //一時的な所持武器一覧
    var readyWeapon: BDFWeapon? = null //最後に所持した武器

    var team: Team? = null //所属チーム

    val location: Location //現在位置
        get() = owner.location

    init {
        if (divideTask) { //タスクを分ける
            GlobalScope.async {
                task = syncTask(1, 1) {
                    tick() //プレイヤークラスのみユーザ毎にタスクを動かす
                }
            }
        }
    }

    /** タスク処理 */
    fun tick() {
        readyWeapon?.tick()
    }

    /** タスクが動いていた場合止める*/
    fun stopTask() = task?.cancel()

    /**
     * 武器を追加する
     * @param weapon 追加する武器オブジェクト
     * */
    fun putWeapon(weapon: BDFWeapon): Boolean {
        val had = weapon(weapon.custom.id)
        if (had == null) {
            weapon.owner = this
            inventoryWeapons.add(weapon)
        }

        return true
    }

    /** Weaponを取得します */
    fun weapon(id: String): BDFWeapon? =
            inventoryWeapons.find { it.custom.id == id }


    /** ダメージを追加する
     * @param damage ダメージ量
     * @param source ダメージの種類 (キルログなどに使用)
     * */
    abstract fun addDamage(source: DamageSource): Boolean


    /** ダメージ種類管理クラス*/
    open class DamageSource(
        val damage: Double = 0.0,
        val cause: BDFPlayer.BDFDamageCause = BDFPlayer.BDFDamageCause.VOID
    )

    /** 銃弾によるダメージ */
    open class BulletDamageSource(val bullet: BDFBullet, val judge: BDFBullet.HitJudge, damage: Double) : DamageSource(
        damage = damage,
        cause = BDFPlayer.BDFDamageCause.BULLET
    )


    /** 敵Mobや別プレイヤーによるマインクラフトバニラのダメージ*/
    class EntityDamageSource(val damager: BDFEntity<*>, damage: Double) : DamageSource(
        cause = BDFPlayer.BDFDamageCause.ENTITY_ATTACK
    )
}
