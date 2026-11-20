package es.vicab.web.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import es.vicab.web.AppState
import es.vicab.web.CERTIFICATIONS
import es.vicab.web.Company
import es.vicab.web.Dom
import es.vicab.web.FOOTER_COMPANY
import es.vicab.web.FOOTER_PRODUCTS
import es.vicab.web.FormMode
import es.vicab.web.Icon
import es.vicab.web.Link
import es.vicab.web.PhoneLink
import es.vicab.web.SocialLinks
import es.vicab.web.reveal
import es.vicab.web.storageGet
import es.vicab.web.storageSet
import es.vicab.web.t
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.B
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Footer
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.H4
import org.jetbrains.compose.web.dom.Iframe
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Nav
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Ul
import kotlin.js.Date

@Composable
fun FinalCta() {
    Section({ classes("sec", "final") }) {
        Div({ classes("wrap"); reveal() }) {
            Span({ classes("eyebrow", "light"); style { justifyContent(JustifyContent.Center) } }) { Text(t("Pasión por la madera")) }
            H2 { Text(t("Haz que la madera sea tu mejor línea de negocio")) }
            P { Text(t("Más de 15 años ayudando a tiendas y profesionales a vender más y mejor. Hablemos.")) }
            Div({ classes("cta") }) {
                PhoneLink({ classes("btn", "btn-primary", "btn-lg") }) { Icon("phone"); Text(t("Llamar ahora")) }
                Link(whatsapp = Company.WA_INFO, attrs = { classes("btn", "btn-ghost", "btn-lg") }) {
                    Icon("wa"); Text(t("Escribir por WhatsApp"))
                }
            }
        }
    }
}

@Composable
fun SiteFooter() {
    Footer({ classes("footer") }) {
        Div({ classes("wrap") }) {
            Div({ classes("f-grid") }) {
                Div {
                    Span({ classes("f-logo") }) { Img("img/logo-web.png", "Vicab") }
                    P { Text(t("Distribución profesional de productos para el cuidado, la reparación y la decoración de la madera.")) }
                    Div({ classes("certs") }) { CERTIFICATIONS.forEach { Span { Text(it) } } }
                    SocialLinks("f-social")
                }
                Div {
                    H4 { Text(t("Productos")) }
                    Ul { FOOTER_PRODUCTS.forEach { Li { Link("#lineas") { Text(t(it)) } } } }
                }
                Div {
                    H4 { Text("Vicab") }
                    Ul { FOOTER_COMPANY.forEach { l -> Li { Link(l.href, mode = l.mode) { Text(t(l.label)) } } } }
                }
                Div {
                    H4 { Text(t("Contacto")) }
                    Ul {
                        Li { PhoneLink { Text(Company.PHONE_DISPLAY) } }
                        Li { A("mailto:${Company.EMAIL}") { Text(Company.EMAIL) } }
                        Li { Text(t("L–V · 8:00–13:00 y 15:30–19:00")) }
                        Li { Text(t("Barberà del Vallès (Barcelona)")) }
                    }
                }
            }
            Div({ classes("legal") }) {
                Span { Text("© ${Date().getFullYear()} " + t("Vicab · Pasión por la madera")) }
                // ⚑ Pendiente: páginas de aviso legal, privacidad y cookies
                Nav {
                    A("#") { Text(t("Aviso legal")) }
                    A("#") { Text(t("Privacidad")) }
                    A("#") { Text(t("Cookies")) }
                }
            }
        }
    }
}

/** WhatsApp flotante, botón de volver arriba y barra de contacto en móvil. */
@Composable
fun FloatingActions() {
    var hint by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(4000); hint = true
        delay(7000); hint = false
    }
    Div({ classes("wa"); id("wa"); if (hint) classes("hint") }) {
        Span({ classes("bubble") }) { Text(t("¿Hablamos? Te respondemos por WhatsApp")) }
        Link(whatsapp = Company.WA_INFO, attrs = { attr("aria-label", t("Escribir por WhatsApp")) }) { Icon("wa") }
    }
    Button({
        classes("to-top")
        id("toTop")
        if (AppState.scrollY > 900) classes("on")
        attr("aria-label", t("Volver arriba"))
        onClick { Dom.scrollToTop() }
    }) { Icon("chev-u") }
    Nav({ classes("mbar"); attr("aria-label", t("Contacto rápido")) }) {
        PhoneLink({ classes("call") }) { Icon("phone"); Text(t("Llamar")) }
        Link(whatsapp = Company.WA_INFO, attrs = { classes("wapp") }) { Icon("wa"); Text("WhatsApp") }
        Link("#contacto", mode = FormMode.DEMO, attrs = { classes("demo") }) { Text(t("Demo gratis")) }
    }
}

private const val COOKIES_KEY = "vicab-cookies"

/** Aviso de cookies (RGPD). Se recuerda la elección en el navegador. */
@Composable
fun CookieBanner() {
    var visible by remember { mutableStateOf(storageGet(COOKIES_KEY) == null) }
    if (!visible) return
    fun choose(choice: String) { storageSet(COOKIES_KEY, choice); visible = false }
    Div({ classes("cookie"); id("cookie"); attr("role", "dialog"); attr("aria-label", t("Aviso de cookies")) }) {
        B { Text(t("Tu privacidad importa 🍪")) }
        Text(t("Usamos cookies técnicas necesarias y, solo si nos das permiso, cookies de análisis para mejorar la web."))
        Div({ classes("acts") }) {
            Button({ classes("btn", "btn-line"); onClick { choose("reject") } }) { Text(t("Rechazar")) }
            Button({ classes("btn", "btn-blue"); onClick { choose("accept") } }) { Text(t("Aceptar")) }
            Button({ classes("cfg"); onClick { choose("config") } }) { Text(t("Configurar preferencias")) }
        }
    }
}

/** Reproductor de vídeo de YouTube a pantalla completa. */
@Composable
fun VideoLightbox() {
    val id = AppState.videoId
    Div({
        classes("lightbox")
        id("lightbox")
        if (id != null) classes("on")
        attr("role", "dialog"); attr("aria-modal", "true"); attr("aria-label", t("Vídeo"))
        onClick { e -> if (e.target == e.currentTarget) AppState.videoId = null }
    }) {
        Button({ classes("v-btn"); id("lbClose"); attr("aria-label", t("Cerrar")); onClick { AppState.videoId = null } }) { Icon("x") }
        Div({ classes("frame"); id("lbFrame") }) {
            if (id != null) {
                Iframe({
                    attr("src", "https://www.youtube-nocookie.com/embed/$id?autoplay=1&rel=0")
                    attr("allow", "autoplay; encrypted-media; picture-in-picture")
                    attr("allowfullscreen", "")
                    attr("title", t("Vídeo de demostración"))
                })
            }
        }
    }
}
