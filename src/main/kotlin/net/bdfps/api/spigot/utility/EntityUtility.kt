package net.bdfps.api.spigot.utility

import net.bdfps.api.spigot.entity.BDFLivingEntity
import net.bdfps.api.spigot.managers.BDFManager
import org.bukkit.entity.LivingEntity

/**
 * BDFLivingEntityを返します
 * 存在しなかった場合は新規登録を行って返します
 */
fun LivingEntity.toBDFLivingEntity(): BDFLivingEntity {
    val bdf = BDFManager.entityManager.find(this.entityId)
    if (bdf == null) { //存在しなかった場合は追加
        val entityInstance = BDFLivingEntity(this, divideTask = false) //タスク処理共通
        BDFManager.entityManager.add(entityInstance)
        return entityInstance
    }
    return bdf as BDFLivingEntity //LivingEntityから呼び出しているので強制キャスト
}
