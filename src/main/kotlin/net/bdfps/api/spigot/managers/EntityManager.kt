package net.bdfps.api.spigot.managers

import net.bdfps.api.spigot.entity.BDFEntity

class EntityManager {

    /** ローカルEntity保存場所 */
    val entities = hashSetOf<BDFEntity<*>>()

    /** 共通処理 */
    fun tick() {
        entities.filter { !it.divideTask }.forEach { it.tick() } //共通タスク
    }

    /** 対象のEntityを見つける */
    fun find(entID: Int): BDFEntity<*>? = entities.find { it.entId == entID}


    /**
     * Entityを登録する。すでに同じEntityIDが登録されていた場合はパス
     * @param entity 登録するBDFEntity
     * @return 結果フラグ
     */
    fun add(entity: BDFEntity<*>): Boolean {
        val already = find(entity.entId)
        if (already == null) {
            entities.add(entity)
            return true
        }
        return false
    }

    /**
     * 登録されているEntityを消去する
     * @param entity 対象
     */
    fun remove(entity: BDFEntity<*>) = remove(entity.entId)

    /**
     * 登録されているEntityを消去する
     * @param entid 対象のEntityID
     */
    fun remove(entid: Int) {
        val entity = find(entid)
        entity?.stopTask() //タスクを止める
        entities.removeIf { it.entId == entid }
    }
}
