package net.bdfps.api.spigot.managers

import java.util.HashMap

class LanguageManager {
    private val languageMessages: MutableList<Messages> = mutableListOf()

    fun load() {
        //TODO 初期化処理
    }

    fun getMessage(lang: Language, vararg type: String): String {
        val messages = getLanguageMessages(lang)
        return  messages?.getMessage(type) ?:  "not exist language. ${lang.name}"
    }

    private fun getLanguageMessages(language: Language): Messages? =
        languageMessages.find { it.language == language }

    inner class Messages {
        lateinit var language: Language
        private val messages = HashMap<String, String>()


        fun putMessage(key: String, value: String) {
            if (this.getMessage(arrayOf(key)).startsWith("not.exist.")) {
                this.messages[key] = value
            }
        }

        fun getMessage(type: Array<out String>): String {
            for (key in messages.keys) {
                if (key.equals(type[0], ignoreCase = true)) {
                    var message = messages[key] ?: ""
                    for (i in 1 until type.size) {
                        message = message.replace(("%" + i.toString()).toRegex(), type[i])
                    }
                    return message
                }
            }
            return "not.exist." + language.name + "." + type[0] + ""
        }

    }

    enum class Language constructor(name: String) {
        Japanese("ja_JP"), English("en_US"), Chinese("zh_CH");

        companion object {
            fun getLanguage(name: String): Language =
                    values().find { it.name.equals(name, true) } ?: Japanese
        }
    }
}
