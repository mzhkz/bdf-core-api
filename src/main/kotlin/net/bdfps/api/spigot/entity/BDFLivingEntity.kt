package net.bdfps.api.spigot.entity

import net.bdf.api.data.abc.MoUser
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity

class BDFLivingEntity(val bukkitLivingEntity: LivingEntity, divideTask: Boolean = false) : BDFEntity<MoUser>(bukkitLivingEntity, divideTask) {

    /**
     * ダメージを与える
     * @param damage ダメージ
     * @param source ダメージの情報
     */
    override fun addDamage(source: DamageSource) : Boolean {
        //TODO ダメージ処理
        return false
    }

    override fun provide(instance: MoUser) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun supply(instance: MoUser) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
