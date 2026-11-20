package es.vicab.web

import kotlinx.browser.window
import org.w3c.dom.DOMRect
import org.w3c.dom.Element

/** Utilidades del navegador que no tienen API en Kotlin/JS (IntersectionObserver). */
object Dom {

    /**
     * Llama a [onVisible] una sola vez, cuando [el] entra en pantalla.
     * [threshold] y [rootMargin] funcionan igual que en IntersectionObserver.
     */
    fun observeOnce(el: Element, threshold: Double = 0.0, rootMargin: String = "0px", onVisible: (Element) -> Unit) {
        val options: dynamic = js("({})")
        options.threshold = threshold
        options.rootMargin = rootMargin
        val callback: (dynamic, dynamic) -> Unit = { entries, observer ->
            val list = entries.unsafeCast<Array<dynamic>>()
            for (entry in list) {
                if (entry.isIntersecting as Boolean) {
                    observer.unobserve(entry.target)
                    onVisible(entry.target.unsafeCast<Element>())
                }
            }
        }
        val io = newIntersectionObserver(callback, options)
        io.observe(el)
    }

    /**
     * Muestra [el] con animación al entrar en pantalla.
     * Se marca con el atributo `data-in` y no con una clase: Compose reescribe la lista de clases
     * cuando cambia el estado (p. ej. el modo del formulario) y borraría una clase añadida a mano.
     */
    fun revealOnScroll(el: Element) {
        observeOnce(el, threshold = 0.12, rootMargin = "0px 0px -40px 0px") { it.setAttribute("data-in", "") }
        // Lo que ya está en pantalla al cargar se muestra al momento
        window.requestAnimationFrame {
            if (rect(el).top < window.innerHeight) el.setAttribute("data-in", "")
        }
    }

    private val newIntersectionObserver: (dynamic, dynamic) -> dynamic =
        js("(function (cb, opts) { return new IntersectionObserver(cb, opts); })")

    fun rect(el: Element): DOMRect = el.getBoundingClientRect()

    fun scrollToTop() {
        window.asDynamic().scrollTo(js("({top:0,behavior:'smooth'})"))
    }
}
