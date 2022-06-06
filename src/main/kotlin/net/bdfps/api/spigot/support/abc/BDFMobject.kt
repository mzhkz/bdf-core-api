package net.bdfps.api.spigot.support.abc

import net.bdf.api.data.Mobject

abstract class BDFMobject<T : Mobject> {

    val properties = hashMapOf<String, Any?>()

    /**
     * Objectに適用。値を渡す
     */
    protected abstract fun provide(instance: T)

    /**
     * Mobjectに適用
     */
    abstract fun supply(instance: T)


    fun getProperty(key: String) = properties[key]

    fun setProperty(vararg pair: Pair<String, Any?>) {
       pair.forEach {
           properties[it.first] = it.second
       }
    }

    fun removeProperty(key: String) {
        properties.remove(key)
    }

    /**
     * Mobjectインスタンスを生成して返す
     */
    inline fun <reified M : T>export(): T {
        val instance = M::class.java.newInstance()
        supply(instance) //Mobjectに適用
        properties.forEach {
            instance.entry(it.key, it.value)
        }
        return instance
    }

    /**
     * 指定されたキーを含まないMobjectを返す
     */
    inline fun <reified M : T> exportIncludeKeys(vararg keys: String): Mobject {
        val ex = export<M>() //通常エクスポート
        val mobject = Mobject()
        ex.entries.forEach {
            if (keys.contains(it.first)) { //含める
                mobject.entry(it.first, it.second)
            }
        }
        return mobject
    }

    /**
     * 指定されたキーを含まないMobjectを返す
     */
    inline fun <reified M : T> exportNotIncludeKeys(vararg keys: String): Mobject {
        val ex = export<M>() //通常エクスポート
        val mobject = Mobject()
        ex.entries.forEach {
            if (!keys.contains(it.first)) { //含めない
                mobject.entry(it.first, it.second)
            }
        }
        return mobject
    }

    /**
     * ObjectにMobjectをインポート
     */
    infix fun import(instance: T) {
        provide(instance)
        /* サブエントリを登録 */
        instance.subEntry.forEach {
            properties[it.first] =  it.second
        }
    }
}
