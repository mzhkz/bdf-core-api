package net.bdfps.api.spigot.managers

object BDFManager {

    /** プレイヤー関係 */
    @JvmStatic
    val playerManager: PlayerManager = PlayerManager()

    /** 武器関係 */
    @JvmStatic
    val weaponManager: WeaponManager = WeaponManager()

    /** 言語関係 */
    @JvmStatic
    val languageManager: LanguageManager = LanguageManager()

    @JvmStatic
    val entityManager: EntityManager = EntityManager()

    @JvmStatic
    val bulletManager: BulletManager = BulletManager()

    @JvmStatic
    val matchManager: MatchManager = MatchManager()

    fun initialize() {

    }
}

