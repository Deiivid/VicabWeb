package es.vicab.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.attributes.max
import org.jetbrains.compose.web.attributes.min
import org.jetbrains.compose.web.css.marginTop
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.B
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLScriptElement
import org.w3c.dom.TouchEvent
import org.w3c.dom.events.Event
import org.w3c.dom.get
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.js.Promise
import kotlin.math.abs
import kotlin.math.min

/**
 * Visor de catálogos PDF con PDF.js: doble página en escritorio, miniaturas,
 * zoom, pantalla completa, teclado y gestos.
 * La interfaz es Compose; las páginas se pintan en <canvas> directamente.
 */
class CatalogViewer {
    enum class Status { LOADING, READY, ERROR }

    var catalog by mutableStateOf<Catalog?>(null)
        private set
    var status by mutableStateOf(Status.LOADING)
        private set
    var numPages by mutableStateOf(1)
        private set
    var page by mutableStateOf(1)
        private set
    var shown by mutableStateOf(listOf(1))
        private set

    val isOpen: Boolean get() = catalog != null

    private var zoom = 1.0
    private var doc: dynamic = null
    private var renderId = 0
    private val scope = MainScope()

    internal var root: HTMLElement? = null
    internal var stage: HTMLElement? = null
    internal var pagesBox: HTMLElement? = null
    internal var thumbsBox: HTMLElement? = null

    fun open(c: Catalog) {
        catalog = c
        page = 1; zoom = 1.0; numPages = 1; shown = listOf(1)
        status = Status.LOADING
        pagesBox?.innerHTML = ""
        thumbsBox?.innerHTML = ""
        scope.launch {
            try {
                val lib = PdfJs.load()
                val loaded: dynamic = lib.getDocument(c.file).promise.unsafeCast<Promise<dynamic>>().await()
                if (catalog != c) { loaded.destroy(); return@launch }
                doc = loaded
                numPages = loaded.numPages as Int
                buildThumbs()
                render()
            } catch (e: Throwable) {
                status = Status.ERROR
            }
        }
    }

    fun close() {
        catalog = null
        doc?.destroy()
        doc = null
        if (document.asDynamic().fullscreenElement != null) document.asDynamic().exitFullscreen()
    }

    fun go(delta: Int) {
        if (doc == null) return
        page = PdfPaging.step(page, numPages, spread(), delta)
        rerender()
    }

    fun goTo(p: Int) {
        if (doc == null) return
        page = p.coerceIn(1, numPages)
        rerender()
    }

    fun zoomIn() { zoom = min(3.0, zoom + 0.25); rerender() }
    fun zoomOut() { zoom = maxOf(0.5, zoom - 0.25); rerender() }

    fun toggleFullscreen() {
        val d = document.asDynamic()
        if (d.fullscreenElement != null) d.exitFullscreen() else root?.asDynamic()?.requestFullscreen?.call(root)
    }

    fun rerender() {
        if (doc != null) scope.launch { render() }
    }

    private fun spread() = PdfPaging.useSpread(window.innerWidth, numPages)

    private suspend fun render() {
        val d = doc ?: return
        val stageEl = stage ?: return
        val id = ++renderId
        val list = PdfPaging.pagesToShow(page, numPages, spread())
        val availableW = stageEl.clientWidth - 140.0
        val availableH = stageEl.clientHeight - 56.0
        val pages = list.map { d.getPage(it).unsafeCast<Promise<dynamic>>().await() }
        if (id != renderId) return
        val totalW = pages.sumOf { (it.getViewport(scale(1.0)).width as Number).toDouble() }
        val maxH = pages.maxOf { (it.getViewport(scale(1.0)).height as Number).toDouble() }
        val fit = min(availableW / totalW, availableH / maxH) * zoom
        val dpr = min(window.devicePixelRatio, 2.0)
        val canvases = pages.map { pg ->
            val vp = pg.getViewport(scale(fit * dpr))
            val cv = document.createElement("canvas") as HTMLCanvasElement
            cv.width = (vp.width as Number).toInt()
            cv.height = (vp.height as Number).toInt()
            cv.style.width = "${(vp.width as Number).toDouble() / dpr}px"
            cv.style.height = "${(vp.height as Number).toDouble() / dpr}px"
            cv to pg.render(renderParams(cv, vp)).promise.unsafeCast<Promise<dynamic>>()
        }
        canvases.forEach { it.second.await() }
        if (id != renderId) return
        pagesBox?.let { box ->
            box.innerHTML = ""
            canvases.forEach { box.appendChild(it.first) }
        }
        shown = list
        status = Status.READY
        highlightThumbs(list)
    }

    private fun buildThumbs() {
        val box = thumbsBox ?: return
        val d = doc
        for (p in 1..numPages) {
            val button = document.createElement("button") as HTMLElement
            button.className = "v-thumb"
            button.setAttribute("data-p", p.toString())
            button.innerHTML = """<canvas width="110" height="155"></canvas><small>$p</small>"""
            button.addEventListener("click", { goTo(p) })
            box.appendChild(button)
            // Las miniaturas se pintan solo cuando se van a ver
            Dom.observeOnce(button, rootMargin = "200px") {
                scope.launch {
                    if (doc !== d) return@launch
                    val pg: dynamic = d.getPage(p).unsafeCast<Promise<dynamic>>().await()
                    val w = (pg.getViewport(scale(1.0)).width as Number).toDouble()
                    val vp = pg.getViewport(scale(220.0 / w))
                    val cv = button.querySelector("canvas") as HTMLCanvasElement
                    cv.width = (vp.width as Number).toInt()
                    cv.height = (vp.height as Number).toInt()
                    pg.render(renderParams(cv, vp))
                }
            }
        }
    }

    private fun highlightThumbs(list: List<Int>) {
        val box = thumbsBox ?: return
        val children = box.children
        for (i in 0 until children.length) {
            val el = children[i] ?: continue
            val p = el.getAttribute("data-p")?.toIntOrNull()
            if (p in list) el.classList.add("on") else el.classList.remove("on")
        }
        box.querySelector(".v-thumb[data-p=\"${list.first()}\"]")?.asDynamic()?.scrollIntoView(js("({block:'nearest'})"))
    }

    private fun scale(s: Double): dynamic {
        val o: dynamic = js("({})")
        o.scale = s
        return o
    }

    private fun renderParams(cv: HTMLCanvasElement, viewport: dynamic): dynamic {
        val o: dynamic = js("({})")
        o.canvasContext = cv.getContext("2d")
        o.viewport = viewport
        return o
    }
}

/** Carga perezosa de PDF.js desde el CDN la primera vez que se abre un catálogo. */
private object PdfJs {
    private const val BASE = "https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/"
    private var lib: dynamic = null

    suspend fun load(): dynamic {
        if (lib != null) return lib
        suspendCancellableCoroutine { cont ->
            val s = document.createElement("script") as HTMLScriptElement
            s.src = BASE + "pdf.min.js"
            s.onload = { cont.resume(Unit) }
            s.onerror = { _, _, _, _, _ -> cont.resumeWithException(IllegalStateException("PDF.js")) }
            document.head?.appendChild(s)
        }
        val pdfjs: dynamic = window.asDynamic().pdfjsLib
        pdfjs.GlobalWorkerOptions.workerSrc = BASE + "pdf.worker.min.js"
        lib = pdfjs
        return pdfjs
    }
}

@Composable
fun CatalogViewerDialog(viewer: CatalogViewer) {
    val c = viewer.catalog
    Div({
        classes("viewer")
        id("viewer")
        if (c != null) classes("on")
        attr("role", "dialog"); attr("aria-modal", "true"); attr("aria-labelledby", "vTitle")
        ref { viewer.root = it; onDispose { viewer.root = null } }
    }) {
        Div({ classes("v-top") }) {
            Div({ classes("t") }) {
                B({ id("vTitle") }) { Text(t(c?.viewerTitle ?: "Catálogo")) }
                Span({ id("vSub") }) { Text("${c?.brand ?: "Borma Wachs"} · ${t("Catálogo")}") }
            }
            ViewerButton("zout", "Alejar", hideOnMobile = true) { viewer.zoomOut() }
            ViewerButton("zin", "Acercar", hideOnMobile = true) { viewer.zoomIn() }
            ViewerButton("full", "Pantalla completa", hideOnMobile = true) { viewer.toggleFullscreen() }
            A(c?.file ?: "#", {
                classes("v-btn"); id("vDown")
                attr("aria-label", t("Descargar PDF"))
                attr("download", "")
            }) { Icon("download") }
            ViewerButton("x", "Cerrar") { viewer.close() }
        }
        Div({ classes("v-body") }) {
            Div({ classes("v-thumbs"); id("vThumbs"); ref { viewer.thumbsBox = it; onDispose { viewer.thumbsBox = null } } })
            Div({
                classes("v-stage")
                id("vStage")
                ref { el ->
                    viewer.stage = el
                    var startX: Double? = null
                    val onStart: (Event) -> Unit = { e -> startX = e.unsafeCast<TouchEvent>().touches[0]?.clientX?.toDouble() }
                    val onEnd: (Event) -> Unit = { e ->
                        val x0 = startX
                        val x1 = e.unsafeCast<TouchEvent>().changedTouches[0]?.clientX?.toDouble()
                        if (x0 != null && x1 != null && abs(x1 - x0) > 50) viewer.go(if (x1 < x0) 1 else -1)
                        startX = null
                    }
                    el.addEventListener("touchstart", onStart)
                    el.addEventListener("touchend", onEnd)
                    onDispose {
                        viewer.stage = null
                        el.removeEventListener("touchstart", onStart)
                        el.removeEventListener("touchend", onEnd)
                    }
                }
            }) {
                Div({ classes("v-pages"); id("vPages"); ref { viewer.pagesBox = it; onDispose { viewer.pagesBox = null } } })
                when (viewer.status) {
                    CatalogViewer.Status.READY -> Unit
                    CatalogViewer.Status.LOADING -> Div({ classes("v-load"); id("vLoad") }) {
                        Div { Div({ classes("spin") }); Text(t("Cargando catálogo…")) }
                    }
                    CatalogViewer.Status.ERROR -> Div({ classes("v-load"); id("vLoad") }) {
                        Div({ style { textAlign("center"); maxWidth(360.px) } }) {
                            Text(t("No se ha podido abrir el catálogo aquí."))
                            Br()
                            A(c?.file ?: "#", {
                                classes("btn", "btn-primary"); style { marginTop(16.px) }
                                target(ATarget.Blank)
                            }) { Text(t("Abrir el PDF")) }
                        }
                    }
                }
                Button({
                    classes("v-nav", "v-prev"); id("vPrev")
                    attr("aria-label", t("Página anterior"))
                    if (viewer.page <= 1) attr("disabled", "")
                    onClick { viewer.go(-1) }
                }) { Icon("chev-l") }
                Button({
                    classes("v-nav", "v-next"); id("vNext")
                    attr("aria-label", t("Página siguiente"))
                    if (viewer.shown.last() >= viewer.numPages) attr("disabled", "")
                    onClick { viewer.go(1) }
                }) { Icon("chev-r") }
            }
        }
        Div({ classes("v-bot") }) {
            Span({ id("vPage") }) { Text(PdfPaging.label(viewer.shown, viewer.numPages)) }
            Input(InputType.Range) {
                id("vRange")
                min("1"); max(viewer.numPages.toString())
                value(viewer.page)
                attr("aria-label", t("Ir a página"))
                onChange { e -> e.value?.toInt()?.let(viewer::goTo) }
            }
        }
    }
}

@Composable
private fun ViewerButton(icon: String, label: String, hideOnMobile: Boolean = false, onClick: () -> Unit) {
    Button({
        classes("v-btn")
        if (hideOnMobile) classes("hide-sm")
        attr("aria-label", t(label))
        onClick { onClick() }
    }) { Icon(icon) }
}
