@file:OptIn(ExperimentalComposeWebSvgApi::class)

package es.vicab.web

import androidx.compose.runtime.Composable
import kotlinx.browser.document
import org.jetbrains.compose.web.ExperimentalComposeWebSvgApi
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.TagElement
import org.jetbrains.compose.web.svg.Svg
import org.jetbrains.compose.web.svg.Use
import org.w3c.dom.Element
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLElement

// ---------------------------------------------------------------- Iconos

/** Icono del sprite SVG de index.html (`<svg><use href="#i-name"/></svg>`). */
@Composable
fun Icon(name: String, viewBox: String? = null, attrs: AttrBuilderContext<org.w3c.dom.svg.SVGElement>? = null) {
    Svg(viewBox = viewBox, attrs = attrs) { Use("#i-$name") }
}

@Composable
fun GoogleLogo() {
    Icon("google", viewBox = "0 0 48 48") {
        classes("g")
        attr("aria-label", "Google")
    }
}

@Composable
fun Stars() {
    org.jetbrains.compose.web.dom.Span({ classes("stars") }) { repeat(5) { Icon("star") } }
}

// ---------------------------------------------------------------- Etiquetas sin helper en Compose HTML

@Composable
fun Figure(attrs: AttrBuilderContext<HTMLElement>? = null, content: ContentBuilder<HTMLElement>? = null) =
    TagElement("figure", attrs, content)

@Composable
fun FigCaption(attrs: AttrBuilderContext<HTMLElement>? = null, content: ContentBuilder<HTMLElement>? = null) =
    TagElement("figcaption", attrs, content)

@Composable
fun Blockquote(attrs: AttrBuilderContext<HTMLElement>? = null, content: ContentBuilder<HTMLElement>? = null) =
    TagElement("blockquote", attrs, content)

@Composable
fun Strong(attrs: AttrBuilderContext<HTMLElement>? = null, content: ContentBuilder<HTMLElement>? = null) =
    TagElement("strong", attrs, content)

@Composable
fun Details(attrs: AttrBuilderContext<HTMLElement>? = null, content: ContentBuilder<HTMLElement>? = null) =
    TagElement("details", attrs, content)

@Composable
fun Summary(attrs: AttrBuilderContext<HTMLElement>? = null, content: ContentBuilder<HTMLElement>? = null) =
    TagElement("summary", attrs, content)

// ---------------------------------------------------------------- Animación de entrada

/**
 * Marca el elemento para que aparezca con animación al entrar en pantalla
 * (clases `.rv` → `.in` de styles.css). [delay] añade `.d1`…`.d4`.
 */
fun AttrsScope<out Element>.reveal(delay: Int = 0) {
    classes("rv")
    if (delay > 0) classes("d$delay")
    ref { el ->
        Dom.revealOnScroll(el)
        onDispose { }
    }
}

// ---------------------------------------------------------------- Enlaces

/**
 * Enlace interno o de contacto con el comportamiento de la web:
 * - [mode] abre el formulario en ese modo (demo, distribuidor…).
 * - `#videos` / `#catalogos` cambian la pestaña de la sección de catálogos.
 * - [whatsapp] genera el enlace de WhatsApp con ese mensaje.
 */
@Composable
fun Link(
    href: String = "#",
    mode: FormMode? = null,
    whatsapp: String? = null,
    attrs: AttrBuilderContext<HTMLAnchorElement>? = null,
    content: ContentBuilder<HTMLAnchorElement>? = null,
) {
    val target = whatsapp?.let { Company.whatsappUrl(t(it)) } ?: href
    A(target, {
        if (whatsapp != null || href.startsWith("http")) {
            target(ATarget.Blank)
            attr("rel", "noopener")
        }
        onClick { e ->
            AppState.menuOpen = false
            if (mode != null) AppState.formMode = mode
            when (href) {
                "#videos" -> {
                    e.preventDefault()
                    AppState.mediaTab = MediaTab.VIDEOS
                    document.getElementById("catalogos")?.asDynamic()?.scrollIntoView(js("({behavior:'smooth'})"))
                }
                "#catalogos" -> AppState.mediaTab = MediaTab.CATALOGS
            }
        }
        attrs?.invoke(this)
    }, content)
}

@Composable
fun PhoneLink(attrs: AttrBuilderContext<HTMLAnchorElement>? = null, content: ContentBuilder<HTMLAnchorElement>) =
    A("tel:${Company.PHONE_TEL}", attrs, content)
