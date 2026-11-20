package es.vicab.web

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.w3c.dom.url.URLSearchParams

enum class MediaTab { CATALOGS, VIDEOS }

/** Estado global de la página. Cualquier lectura desde un @Composable se recompone al cambiar. */
object AppState {
    var lang by mutableStateOf(initialLang())
        private set
    var formMode by mutableStateOf(FormMode.CALL)
    var mediaTab by mutableStateOf(MediaTab.CATALOGS)
    /** Marca seleccionada en el filtro de catálogos (null = todas). */
    var catalogBrand by mutableStateOf<String?>(null)
    var menuOpen by mutableStateOf(false)
    var openLangMenu by mutableStateOf<Int?>(null)
    var videoId by mutableStateOf<String?>(null)
    var scrollY by mutableStateOf(0.0)
    var activeSection by mutableStateOf<String?>(null)

    val viewer = CatalogViewer()

    fun changeLang(l: Lang) {
        lang = l
        storageSet(LANG_KEY, l.code)
        document.documentElement?.setAttribute("lang", l.code)
        document.title = Translations.pageTitle(l)
    }

    private const val LANG_KEY = "vicab-lang"

    private fun initialLang(): Lang =
        Lang.fromCode(URLSearchParams(window.location.search).get("lang"))
            ?: Lang.fromCode(storageGet(LANG_KEY))
            ?: Lang.ES
}

/** Traduce un texto en español al idioma activo. */
fun t(spanish: String): String = Translations.translate(spanish, AppState.lang)

// localStorage puede no estar disponible (modo privado, cookies bloqueadas…)
fun storageGet(key: String): String? = try { localStorage.getItem(key) } catch (_: Throwable) { null }
fun storageSet(key: String, value: String) { try { localStorage.setItem(key, value) } catch (_: Throwable) {} }
