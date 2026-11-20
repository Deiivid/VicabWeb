package es.vicab.web.sections

import androidx.compose.runtime.Composable
import es.vicab.web.AppState
import es.vicab.web.Company
import es.vicab.web.FormMode
import es.vicab.web.Icon
import es.vicab.web.Lang
import es.vicab.web.Link
import es.vicab.web.NAV_LINKS
import es.vicab.web.PhoneLink
import es.vicab.web.t
import org.jetbrains.compose.web.dom.B
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Header
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Nav
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun SiteHeader() {
    Header({
        classes("header")
        id("header")
        if (AppState.scrollY > 10) classes("scrolled")
    }) {
        Div({ classes("wrap", "nav") }) {
            Link("#top", attrs = { classes("logo"); attr("aria-label", t("Vicab, inicio")) }) {
                // Versiones ya reducidas a 1x/2x/3x: el navegador no tiene que encoger el logo grande y se ve nítido
                Img("img/logo-header@2x.png", t("Vicab · Pasión por la madera")) {
                    attr("srcset", "img/logo-header.png 1x, img/logo-header@2x.png 2x, img/logo-header@3x.png 3x")
                    attr("width", "92"); attr("height", "62")
                }
            }
            Nav({ classes("menu"); id("menu"); attr("aria-label", t("Principal")) }) {
                NAV_LINKS.forEach { link ->
                    Link(link.href, mode = link.mode, attrs = {
                        if (AppState.activeSection != null && link.href == "#${AppState.activeSection}") classes("active")
                    }) { Text(t(link.label)) }
                }
            }
            Div({ classes("nav-cta") }) {
                LangSwitcher(index = 0)
                PhoneLink({ classes("phone") }) {
                    I { Icon("phone") }
                    Span { Small { Text(t("Llámanos")) }; Text(Company.PHONE_DISPLAY) }
                }
                Link("#contacto", mode = FormMode.DEMO, attrs = { classes("btn", "btn-primary", "btn-sm") }) {
                    Text(t("Demo gratis"))
                }
            }
            Button({
                classes("burger")
                id("burger")
                attr("aria-label", t("Abrir menú"))
                attr("aria-expanded", AppState.menuOpen.toString())
                attr("aria-controls", "menu")
                onClick { AppState.menuOpen = !AppState.menuOpen }
            }) { Span() }
            LangSwitcher(index = 1, mobile = true)
        }
    }
}

/** Selector de idioma. Hay dos: el de escritorio ([index] 0) y el móvil (1). */
@Composable
private fun LangSwitcher(index: Int, mobile: Boolean = false) {
    val open = AppState.openLangMenu == index
    Div({
        classes("lang")
        if (mobile) classes("lang-m")
        if (open) classes("open")
    }) {
        Button({
            classes("lang-btn")
            attr("aria-haspopup", "true")
            attr("aria-expanded", open.toString())
            attr("aria-label", "Idioma / Language")
            onClick { e ->
                e.stopPropagation()
                AppState.openLangMenu = if (open) null else index
            }
        }) {
            Icon("globe", viewBox = "0 0 24 24")
            Span({ classes("lang-cur") }) { Text(AppState.lang.short) }
        }
        Div({ classes("lang-menu"); attr("role", "menu") }) {
            Lang.entries.forEach { lang ->
                Button({
                    attr("role", "menuitemradio")
                    attr("aria-checked", (lang == AppState.lang).toString())
                    onClick {
                        AppState.changeLang(lang)
                        AppState.openLangMenu = null
                    }
                }) {
                    B { Text(lang.short) }
                    Text(lang.label)
                }
            }
        }
    }
}
