package es.vicab.web

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SharedTest {

    @Test
    fun translatesKnownKeysAndFallsBackToSpanish() {
        assertEquals("Products", Translations.translate("Productos", Lang.EN))
        assertEquals("Productes", Translations.translate("Productos", Lang.CA))
        assertEquals("Texto sin traducir", Translations.translate("Texto sin traducir", Lang.EU))
    }

    @Test
    fun allRowsHaveFiveLanguages() {
        assertTrue(TRANSLATIONS.all { it.size == Lang.entries.size })
    }

    @Test
    fun langCodes() {
        assertEquals(Lang.CA, Lang.fromCode("va"))
        assertEquals(Lang.GL, Lang.fromCode("GL"))
        assertNull(Lang.fromCode("fr"))
    }

    @Test
    fun whatsappUrlIsEncoded() {
        assertEquals(
            "https://wa.me/34601447591?text=Hola%2C%20qu%C3%A9%20tal",
            Company.whatsappUrl("Hola, qué tal"),
        )
    }

    @Test
    fun spreadPaging() {
        assertEquals(listOf(1), PdfPaging.pagesToShow(1, 10, spread = true))
        assertEquals(listOf(2, 3), PdfPaging.pagesToShow(3, 10, spread = true))
        assertEquals(listOf(10), PdfPaging.pagesToShow(10, 10, spread = true))
        assertEquals(2, PdfPaging.step(1, 10, spread = true, delta = 1))
        assertEquals(4, PdfPaging.step(2, 10, spread = true, delta = 1))
        assertEquals(2, PdfPaging.step(4, 10, spread = true, delta = -1))
        assertEquals(1, PdfPaging.step(2, 10, spread = true, delta = -1))
        assertEquals(10, PdfPaging.step(10, 10, spread = false, delta = 1))
        assertEquals("2–3 / 10", PdfPaging.label(listOf(2, 3), 10))
    }

    @Test
    fun formValidation() {
        val empty = ContactRequest(FormMode.CALL)
        assertEquals(ContactRequest.Field.entries, empty.missingFields())
        val ok = ContactRequest(FormMode.CALL, name = "Pedro García", phone = "600", acceptsPrivacy = true)
        assertTrue(ok.missingFields().isEmpty())
        assertEquals("Pedro", ok.firstName)
    }

    @Test
    fun everyCatalogKeyExists() {
        PRODUCT_LINES.forEach { assertTrue(catalog(it.catalogKey) != null, it.catalogKey) }
        PARTNER_BRANDS.forEach { b -> assertTrue(CATALOGS.any { it.brand == b.name }, b.name) }
        assertEquals(CATALOGS.size, CATALOGS.map { it.key }.distinct().size)
    }

    @Test
    fun catalogSizeLabels() {
        assertEquals("15 MB", catalog("interior")!!.sizeLabel)
        assertEquals("1,5 MB", catalog("mps")!!.sizeLabel)
        assertEquals("296 KB", catalog("borma-profix")!!.sizeLabel)
    }
}
