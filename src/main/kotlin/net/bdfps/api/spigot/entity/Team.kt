package net.bdfps.api.spigot.entity

class Team(val name: String) {
    val hash = hashCode()
    val joined = mutableListOf<BDFEntity<*>>()

    fun equals(other: Team): Boolean = other.hash == hash

    /**
     * チームに参加する
     * @param entity 参加させるEntity
     */
    fun join(entity: BDFEntity<*>) {
        val temp = joined.find { it.entId == entity.entId }
        if (temp == null) {
            entity.team = this
            joined.add(entity)
        }
    }

    /**
     * チームから離脱する
     * @param entity 離脱するEntity
     */
    fun leave(entity: BDFEntity<*>) = {
        val temp = joined.find { it.entId == entity.entId }
        temp?.team = null
        joined.removeIf { it.entId == temp?.entId }
    }

}
