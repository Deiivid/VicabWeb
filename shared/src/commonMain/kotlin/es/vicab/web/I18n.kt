package es.vicab.web

/** Idiomas de la web. El catalán y el valenciano comparten traducción. */
enum class Lang(val code: String, val short: String, val label: String) {
    ES("es", "ES", "Español"),
    CA("ca", "CA", "Català · Valencià"),
    EU("eu", "EU", "Euskara"),
    GL("gl", "GL", "Galego"),
    EN("en", "EN", "English");

    companion object {
        /** Admite `va` como alias de `ca`; cualquier otro valor desconocido cae en español. */
        fun fromCode(code: String?): Lang? {
            val c = code?.trim()?.lowercase() ?: return null
            if (c == "va") return CA
            return entries.firstOrNull { it.code == c }
        }
    }
}

/**
 * Traducciones con el texto en español como clave.
 * Si un texto no está en la tabla se muestra tal cual (en español).
 */
object Translations {
    private val dict: Map<String, List<String>> = TRANSLATIONS.associateBy { it[0] }

    fun translate(key: String, lang: Lang): String =
        dict[key]?.getOrNull(lang.ordinal)?.takeIf { it.isNotEmpty() } ?: key

    fun has(key: String): Boolean = key in dict

    fun pageTitle(lang: Lang): String = PAGE_TITLE[lang.ordinal]
}
