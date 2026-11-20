package es.vicab.web

/**
 * Paginación del visor de catálogos.
 * En pantallas anchas se muestran dos páginas a la vez como una revista
 * (portada sola y luego pares 2–3, 4–5…).
 */
object PdfPaging {

    fun useSpread(viewportWidth: Int, numPages: Int): Boolean = viewportWidth > 900 && numPages > 2

    fun pagesToShow(page: Int, numPages: Int, spread: Boolean): List<Int> {
        if (!spread || page == 1) return listOf(page)
        val left = if (page % 2 == 0) page else page - 1
        return listOf(left, left + 1).filter { it <= numPages }
    }

    /** Página a la que se llega al avanzar ([delta] > 0) o retroceder desde [page]. */
    fun step(page: Int, numPages: Int, spread: Boolean, delta: Int): Int {
        val shown = pagesToShow(page, numPages, spread)
        var p = if (delta > 0) shown.last() + 1 else shown.first() - 1
        if (spread && delta < 0 && p > 1) p = if (p % 2 == 0) p else p - 1
        return p.coerceIn(1, numPages)
    }

    fun label(shown: List<Int>, numPages: Int): String =
        (if (shown.size > 1) "${shown.first()}–${shown.last()}" else "${shown.first()}") + " / " + numPages
}
