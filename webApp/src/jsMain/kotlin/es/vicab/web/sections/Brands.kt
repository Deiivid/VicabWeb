package es.vicab.web.sections

import androidx.compose.runtime.Composable
import es.vicab.web.AppState
import es.vicab.web.CATALOGS
import es.vicab.web.FormMode
import es.vicab.web.Icon
import es.vicab.web.Link
import es.vicab.web.MediaTab
import es.vicab.web.PARTNER_BRANDS
import es.vicab.web.PartnerBrand
import es.vicab.web.VIDEOS
import es.vicab.web.reveal
import es.vicab.web.t
import org.jetbrains.compose.web.dom.B
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Em
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.H4
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLImageElement

@Composable
fun BrandsAndCatalogs() {
    Section({ classes("sec", "soft"); id("marcas") }) {
        Span({ id("catalogos"); classes("anchor") })
        Span({ id("videos"); classes("anchor") })
        Div({ classes("wrap", "bc") }) {
            Div({ classes("bc-l"); reveal() }) {
                Span({ classes("eyebrow") }) { Text(t("Distribuciones en exclusiva")) }
                H2({ classes("h2", "bc-h") }) { Text(t("Marcas europeas líderes, un solo proveedor")) }
                P({ classes("bc-p") }) { Text(t("Trabajamos sin marcas blancas: solo producto original de fabricantes de referencia.")) }
                Div({ classes("borma") }) {
                    Span({ classes("badge-ex") }) { Text(t("Exclusiva")) }
                    Span({ classes("bo-logo") }) { Img("img/marcas/borma.png", "Borma Wachs") }
                    Div {
                        Small { Text(t("Marca principal")) }
                        B { Text("Borma Wachs") }
                        P { Text(t("Cosmética profesional para la madera: aceites, ceras, barnices, tintes y productos de restauración y retoque, con certificaciones CATAS, IBR y EN71.")) }
                    }
                }
                Div({ classes("bminis") }) { PARTNER_BRANDS.forEach { PartnerBrandCard(it) } }
            }
            Div({ classes("bc-r"); reveal(1) }) {
                Div({ classes("bc-top") }) {
                    Div {
                        Span({ classes("eyebrow") }) { Text(t("Catálogos y vídeos")) }
                        H2({ classes("h2", "bc-h") }) { Text(t("Consulta, aprende y vende más")) }
                    }
                    Div({ classes("media-tabs"); attr("role", "tablist") }) {
                        MediaTabButton(MediaTab.CATALOGS, "book", "Catálogos", CATALOGS.size)
                        MediaTabButton(MediaTab.VIDEOS, "play", "Vídeos", VIDEOS.size)
                    }
                }
                MediaPanel(MediaTab.CATALOGS) {
                    Div({ classes("cats") }) {
                        CATALOGS.forEachIndexed { i, c ->
                            CatalogButton(c.key, attrs = { classes("cat"); reveal(i.coerceAtMost(4)) }) {
                                Div({ classes("cover") }) {
                                    Img(c.cover, "") { attr("loading", "lazy") }
                                    Span({ classes("open") }) {
                                        Icon("book") { attr("width", "16"); attr("height", "16") }
                                        Text(t("Abrir"))
                                    }
                                }
                                H4 { Text(t(c.title)) }
                                Span({ classes("meta") }) { Text("${c.brand} · PDF ${c.sizeMb} MB") }
                            }
                        }
                    }
                    P({ classes("media-foot") }) {
                        Link("#contacto", mode = FormMode.INFO, attrs = { classes("link") }) { Text(t("Pedir catálogo impreso")) }
                    }
                }
                MediaPanel(MediaTab.VIDEOS) {
                    Div({ classes("videos") }) {
                        VIDEOS.forEachIndexed { i, v ->
                            Button({
                                classes("vid")
                                if (v.big) classes("big")
                                reveal(if (i == 0) 0 else (i - 1) % 2 + 1)
                                onClick { AppState.videoId = v.youtubeId }
                            }) {
                                Img(v.thumbnail, "") {
                                    attr("loading", "lazy")
                                    // Si YouTube no tiene la miniatura en alta, usa la normal
                                    ref { img ->
                                        val image = img.unsafeCast<HTMLImageElement>()
                                        image.onload = {
                                            if (image.naturalWidth <= 120) image.src = image.src.replace("maxresdefault", "hqdefault")
                                        }
                                        onDispose { image.onload = null }
                                    }
                                }
                                Span({ classes("play") }) { Icon("play") }
                                Span({ classes("cap") }) { Span { Text(t(v.category)) }; B { Text(t(v.title)) } }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PartnerBrandCard(brand: PartnerBrand) {
    val key = brand.catalogKey
    if (key != null) {
        CatalogButton(key, attrs = { classes("bmini"); title(t(brand.description)) }) { PartnerBrandBody(brand) }
    } else {
        Link("#contacto", mode = FormMode.INFO, attrs = { classes("bmini"); title(t(brand.description)) }) { PartnerBrandBody(brand) }
    }
}

@Composable
private fun PartnerBrandBody(brand: PartnerBrand) {
    Span({ classes("bm-logo") }) { Img(brand.logo, brand.name) }
    Span({ classes("bm-txt") }) {
        Small { Text(t(brand.category)) }
        B { Text(brand.name) }
        Em { Text(t(if (brand.catalogKey != null) "Ver catálogo" else "Solicitar catálogo") + " →") }
    }
}

@Composable
private fun MediaTabButton(tab: MediaTab, icon: String, label: String, count: Int) {
    Button({
        attr("role", "tab")
        attr("aria-selected", (AppState.mediaTab == tab).toString())
        onClick { AppState.mediaTab = tab }
    }) {
        Icon(icon); Text(t(label) + " "); I { Text(count.toString()) }
    }
}

@Composable
private fun MediaPanel(tab: MediaTab, content: ContentBuilder<HTMLElement>) {
    Div({
        classes("media-panel")
        if (AppState.mediaTab != tab) attr("hidden", "")
    }, content)
}
