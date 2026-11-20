package es.vicab.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import es.vicab.web.sections.BrandsAndCatalogs
import es.vicab.web.sections.Contact
import es.vicab.web.sections.CookieBanner
import es.vicab.web.sections.FinalCta
import es.vicab.web.sections.FloatingActions
import es.vicab.web.sections.GoldenRules
import es.vicab.web.sections.Hero
import es.vicab.web.sections.HowWeWork
import es.vicab.web.sections.Offer
import es.vicab.web.sections.ProductLines
import es.vicab.web.sections.ReviewsAndFaq
import es.vicab.web.sections.SiteFooter
import es.vicab.web.sections.SiteHeader
import es.vicab.web.sections.Stats
import es.vicab.web.sections.VideoLightbox
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.Main
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

fun main() {
    AppState.changeLang(AppState.lang) // aplica idioma inicial al <html> y al título
    renderComposable(rootElementId = "root") { VicabApp() }
}

@Composable
fun VicabApp() {
    GlobalListeners()
    BodyClasses()

    SiteHeader()
    Main({ id("top") }) {
        Hero()
        Stats()
        GoldenRules()
        ProductLines()
        Offer()
        HowWeWork()
        BrandsAndCatalogs()
        ReviewsAndFaq()
        Contact()
        FinalCta()
    }
    SiteFooter()
    FloatingActions()
    CatalogViewerDialog(AppState.viewer)
    VideoLightbox()
    CookieBanner()
}

/** Clases y estilos del <body> que dependen del estado (menú móvil abierto, modales). */
@Composable
private fun BodyClasses() {
    val menuOpen = AppState.menuOpen
    val modalOpen = AppState.viewer.isOpen || AppState.videoId != null
    SideEffect {
        val body = document.body ?: return@SideEffect
        if (menuOpen) body.classList.add("menu-open") else body.classList.remove("menu-open")
        if (modalOpen) body.style.setProperty("overflow", "hidden") else body.style.removeProperty("overflow")
    }
}

/** Scroll, teclado, redimensionado y clics fuera del selector de idioma. */
@Composable
private fun GlobalListeners() {
    DisposableEffect(Unit) {
        val onScroll: (Event) -> Unit = {
            AppState.scrollY = window.scrollY
            AppState.activeSection = sectionInView()
        }
        val onKey: (Event) -> Unit = { e ->
            val key = e.unsafeCast<KeyboardEvent>().key
            val viewer = AppState.viewer
            when {
                viewer.isOpen -> when (key) {
                    "ArrowRight" -> viewer.go(1)
                    "ArrowLeft" -> viewer.go(-1)
                    "Escape" -> viewer.close()
                }
                AppState.videoId != null && key == "Escape" -> AppState.videoId = null
            }
        }
        var resizeTimer = 0
        val onResize: (Event) -> Unit = {
            window.clearTimeout(resizeTimer)
            resizeTimer = window.setTimeout({ AppState.viewer.rerender() }, 150)
        }
        val onFullscreen: (Event) -> Unit = { window.setTimeout({ AppState.viewer.rerender() }, 100) }
        val onDocClick: (Event) -> Unit = { AppState.openLangMenu = null }

        window.addEventListener("scroll", onScroll)
        window.addEventListener("keydown", onKey)
        window.addEventListener("resize", onResize)
        document.addEventListener("fullscreenchange", onFullscreen)
        document.addEventListener("click", onDocClick)
        onScroll(Event("scroll"))
        onDispose {
            window.removeEventListener("scroll", onScroll)
            window.removeEventListener("keydown", onKey)
            window.removeEventListener("resize", onResize)
            document.removeEventListener("fullscreenchange", onFullscreen)
            document.removeEventListener("click", onDocClick)
        }
    }
}

/** Sección del menú que ocupa la franja central de la pantalla (para resaltarla). */
private fun sectionInView(): String? {
    val line = window.innerHeight * 0.47
    return NAV_LINKS.map { it.href.removePrefix("#") }.distinct().firstOrNull { id ->
        val el = document.getElementById(id) ?: return@firstOrNull false
        val r = el.getBoundingClientRect()
        r.height > 0 && r.top <= line && r.bottom >= line
    }
}
