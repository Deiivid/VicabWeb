package es.vicab.web.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import es.vicab.web.AppState
import es.vicab.web.CATALOGS
import es.vicab.web.CATALOG_BRANDS
import es.vicab.web.Catalog
import es.vicab.web.DocKind
import es.vicab.web.FormMode
import es.vicab.web.Icon
import es.vicab.web.Link
import es.vicab.web.MediaTab
import es.vicab.web.PARTNER_BRANDS
import es.vicab.web.PartnerBrand
import es.vicab.web.VIDEOS
import es.vicab.web.Video
import es.vicab.web.reveal
import es.vicab.web.t
import kotlinx.browser.document
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.type
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

/** Documentos y vídeos visibles antes de pulsar "Ver todos". */
private const val CATALOGS_PREVIEW = 6
private const val VIDEOS_PREVIEW = 5

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
                MediaPanel(MediaTab.CATALOGS) { CatalogsPanel() }
                MediaPanel(MediaTab.VIDEOS) { VideosPanel() }
            }
        }
    }
}

// ---------------------------------------------------------------- Catálogos

@Composable
private fun CatalogsPanel() {
    var expanded by remember { mutableStateOf(false) }
    val brand = AppState.catalogBrand
    val filtered = if (brand == null) CATALOGS else CATALOGS.filter { it.brand == brand }
    // Con una marca elegida se ve todo; en "Todos" se empieza por los primeros
    val shown = if (brand == null && !expanded) filtered.take(CATALOGS_PREVIEW) else filtered

    Div({ classes("doc-filters"); attr("role", "group"); attr("aria-label", t("Filtrar por marca")) }) {
        FilterChip(t("Todos"), CATALOGS.size, selected = brand == null) { AppState.catalogBrand = null }
        CATALOG_BRANDS.forEach { b ->
            FilterChip(b, CATALOGS.count { it.brand == b }, selected = brand == b) { AppState.catalogBrand = b }
        }
    }
    Div({ classes("cats") }) {
        shown.forEach { CatalogCard(it) }
    }
    Div({ classes("media-foot") }) {
        if (shown.size < filtered.size) {
            Button({
                classes("btn", "btn-line", "btn-sm", "more-btn")
                type(ButtonType.Button)
                onClick { expanded = true }
            }) { Text(t("Ver todos los documentos") + " (${filtered.size})"); Icon("chev-r") }
        }
        Link("#contacto", mode = FormMode.INFO, attrs = { classes("link") }) { Text(t("Pedir catálogo impreso")) }
    }
}

@Composable
private fun FilterChip(label: String, count: Int, selected: Boolean, onClick: () -> Unit) {
    Button({
        classes("doc-chip")
        if (selected) classes("on")
        type(ButtonType.Button)
        attr("aria-pressed", selected.toString())
        onClick { onClick() }
    }) {
        Text(label)
        I { Text(count.toString()) }
    }
}

@Composable
private fun CatalogCard(c: Catalog) {
    CatalogButton(c.key, attrs = { classes("cat"); attr("title", t(c.title)) }) {
        Div({ classes("cover") }) {
            Img(c.cover, "") { attr("loading", "lazy") }
            Span({ classes("doc-kind"); if (c.kind == DocKind.SHEET) classes("sheet") }) { Text(t(c.kind.label)) }
            c.language?.let { Span({ classes("doc-lang") }) { Text(it) } }
            Span({ classes("open") }) {
                Icon("book") { attr("width", "16"); attr("height", "16") }
                Text(t("Abrir"))
            }
        }
        H4 { Text(t(c.title)) }
        Span({ classes("meta") }) { Text(c.brand) }
        Span({ classes("meta", "meta-2") }) {
            Text("${c.pages} ${t(if (c.pages == 1) "pág." else "págs.")} · PDF ${c.sizeLabel}")
        }
    }
}

// ---------------------------------------------------------------- Vídeos

@Composable
private fun VideosPanel() {
    var expanded by remember { mutableStateOf(false) }
    val shown = if (expanded) VIDEOS else VIDEOS.take(VIDEOS_PREVIEW)
    Div({ classes("videos") }) {
        shown.forEachIndexed { i, v -> VideoCard(v, i) }
    }
    if (!expanded && VIDEOS.size > VIDEOS_PREVIEW) {
        Div({ classes("media-foot") }) {
            Button({
                classes("btn", "btn-line", "btn-sm", "more-btn")
                type(ButtonType.Button)
                onClick { expanded = true }
            }) { Text(t("Ver todos los vídeos") + " (${VIDEOS.size})"); Icon("chev-r") }
        }
    }
}

@Composable
private fun VideoCard(v: Video, index: Int) {
    Button({
        classes("vid")
        if (v.big) classes("big")
        reveal(if (index == 0) 0 else (index - 1) % 2 + 1)
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

// ---------------------------------------------------------------- Marcas

/** Tarjeta de marca: muestra sus catálogos en la sección de catálogos. */
@Composable
private fun PartnerBrandCard(brand: PartnerBrand) {
    val docs = CATALOGS.count { it.brand == brand.name }
    Button({
        classes("bmini")
        type(ButtonType.Button)
        title(t(brand.description))
        onClick {
            AppState.catalogBrand = brand.name
            AppState.mediaTab = MediaTab.CATALOGS
            document.getElementById("catalogos")?.asDynamic()?.scrollIntoView(js("({behavior:'smooth'})"))
        }
    }) {
        Span({ classes("bm-logo") }) { Img(brand.logo, brand.name) }
        Span({ classes("bm-txt") }) {
            Small { Text(t(brand.category)) }
            B { Text(brand.name) }
            Em { Text(t(if (docs == 1) "Ver catálogo" else "Ver catálogos") + " ($docs) →") }
        }
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
