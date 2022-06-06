package net.bdfps.api.spigot.managers

import net.bdfps.api.spigot.weapon.BDFWeapon

class WeaponManager {
    val localWeapons: MutableList<BDFWeapon> = mutableListOf()

    /**　データサーバから武器データを取得*/
    fun loadWeapons() {
        //TODO データサーバから取得
    }

    /**
     * 武器を取得する
     * @param name 武器名
     */
    fun getWeaponWithName(name: String): BDFWeapon? {
        return localWeapons.find { it.weaponName == name }
    }

    /**
     * 武器を取得する
     * @param id 武器のID
     */
    fun getWeaponWithID(id: String): BDFWeapon? {
        return localWeapons.find { it.weaponID == id }
    }

    /**
     * 武器をローカルデータに追加する
     * @param bdfWeapon 追加する武器
     * @return 武器を追加できたか
     */
    fun addWeapon(bdfWeapon: BDFWeapon): Boolean {
        val weapon = getWeaponWithID(bdfWeapon.weaponID)
        if (weapon == null)
            localWeapons.add(bdfWeapon)

        return weapon == null
    }
}
