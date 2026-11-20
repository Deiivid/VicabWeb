package es.vicab.web

/*
 * Contenido de la web de Vicab.
 * Todos los textos están en español: la capa web los traduce con [Translations].
 * Los iconos son el nombre del símbolo SVG (`phone` → `#i-phone`).
 */

object Company {
    const val PHONE_DISPLAY = "93 566 28 35"
    const val PHONE_TEL = "+34935662835"
    const val EMAIL = "comercial@vicab.es"

    // ⚑ CONFIGURAR: móvil de WhatsApp (formato internacional, sin + ni espacios)
    const val WHATSAPP = "34601447591"

    const val GOOGLE_RATING = "4,9"
    const val GOOGLE_REVIEW_COUNT = 10
    const val GOOGLE_REVIEWS_URL =
        "https://www.google.com/maps/place/VICAB+PASI%C3%93N+POR+LA+MADERA/@41.5114197,2.1558118,17z/data=!4m8!3m7!1s0x12a4be1bf03dc0e5:0x20ffc1d2c6ff1a3f!8m2!3d41.5114197!4d2.1558118!9m1!1b1!16s%2Fg%2F11f1047p97"
    const val MAP_EMBED_URL =
        "https://maps.google.com/maps?q=Pol%C3%ADgono%20Industrial%20Can%20Salvatella%2C%20Barber%C3%A0%20del%20Vall%C3%A8s&z=15&output=embed"

    const val DIRECTIONS_URL = "https://www.google.com/maps/dir/?api=1&destination=41.5114197,2.1558118"

    const val WA_INFO = "Hola, me gustaría recibir información sobre Vicab."
    const val WA_DEMO = "Hola, me gustaría pedir una demostración gratuita en mi tienda."

    fun whatsappUrl(message: String): String = "https://wa.me/$WHATSAPP?text=${urlEncode(message)}"
}

/** Codificación de URL equivalente a `encodeURIComponent`. */
fun urlEncode(s: String): String {
    val safe = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.!~*'()"
    val sb = StringBuilder()
    for (b in s.encodeToByteArray()) {
        val c = (b.toInt() and 0xFF)
        if (c < 128 && c.toChar() in safe) sb.append(c.toChar())
        else sb.append('%').append("0123456789ABCDEF"[c shr 4]).append("0123456789ABCDEF"[c and 15])
    }
    return sb.toString()
}

// ---------------------------------------------------------------- Menú

data class NavLink(val label: String, val href: String, val mode: FormMode? = null)

val NAV_LINKS = listOf(
    NavLink("Productos", "#lineas"),
    NavLink("Marcas", "#marcas"),
    NavLink("Catálogos", "#catalogos"),
    NavLink("Vídeos", "#videos"),
    NavLink("Distribuidores", "#contacto", FormMode.DIST),
    NavLink("Contacto", "#contacto"),
)

// ---------------------------------------------------------------- Portada

data class HeroPhoto(val css: String, val src: String, val alt: String, val caption: String, val position: String? = null)

val HERO_PHOTOS = listOf(
    HeroPhoto("b1", "img/nino.jpg", "Niño sobre un parquet tratado con Borma Wachs", "Apto para juguetes · EN71", "14% 40%"),
    HeroPhoto("b2", "img/cabana.jpg", "Casa de madera", "Exterior"),
    HeroPhoto("b3", "img/barco.jpg", "Cubierta de madera de un velero", "Náutica"),
    HeroPhoto("b4", "img/salon.jpg", "Salón con parquet brillante", "Interior"),
)

data class Chip(val icon: String, val label: String)

val HERO_CHIPS = listOf(
    Chip("shield", "CATAS · IBR · EN71"),
    Chip("store", "Solo profesionales"),
    Chip("users", "Formación técnica"),
)

data class Brand(val logo: String, val name: String)

val BRANDS = listOf(
    Brand("img/marcas/borma.png", "Borma Wachs"),
    Brand("img/marcas/mps.png", "MPS"),
    Brand("img/marcas/famag.png", "Famag"),
    Brand("img/marcas/pizzi.png", "Pizzi"),
    Brand("img/marcas/whale.png", "Whale Spray"),
    Brand("img/marcas/ducasse.png", "Ducasse Industrial"),
)

// ---------------------------------------------------------------- Cifras

data class Stat(val prefix: String, val value: Int, val suffix: String, val label: String)

val STATS = listOf(
    Stat("+", 15, "", "años de experiencia en el sector de la madera"),
    Stat("", 6, "", "marcas europeas distribuidas en exclusiva"),
    Stat("", 100, "%", "B2B: nunca vendemos al cliente final"),
    Stat("", 0, "", "marcas blancas. Solo producto original"),
)

// ---------------------------------------------------------------- Normas de oro

data class Rule(val icon: String, val title: String, val detail: String)

val GOLDEN_RULES = listOf(
    Rule("award", "Calidad certificada", "CATAS, IBR y EN71"),
    Rule("gift", "Regalo seguro", "a partir de 10 referencias"),
    Rule("barcode", "Código EAN", "en todos los productos"),
    Rule("shield", "Sin marcas blancas", "solo producto original"),
    Rule("globe", "Etiquetado europeo", "según normativa"),
    Rule("store", "Tu negocio, primero", "no vendemos al usuario final"),
    Rule("box", "Unidades sueltas", "rompemos la caja si lo necesitas"),
    Rule("handshake", "Vicab Club", "fidelización anual"),
)

// ---------------------------------------------------------------- Catálogos

data class Catalog(
    val key: String,
    val title: String,
    val viewerTitle: String,
    val brand: String,
    val file: String,
    val cover: String,
    val sizeMb: Int,
)

val CATALOGS = listOf(
    Catalog("interior", "Línea Interior", "Línea Interior", "Borma Wachs", "catalogos/interior.pdf", "img/portadas/interior.jpg", 16),
    Catalog("exterior", "Línea Exterior", "Línea Exterior", "Borma Wachs", "catalogos/exterior.pdf", "img/portadas/exterior.jpg", 17),
    Catalog("restauracion", "Restauración", "Restauración", "Borma Wachs", "catalogos/restauracion.pdf", "img/portadas/restauracion.jpg", 8),
    Catalog("retoque", "Retoque", "Retoque", "Borma Wachs", "catalogos/retoque.pdf", "img/portadas/retoque.jpg", 3),
    Catalog("serie-oro", "Serie Oro", "Serie Oro", "Borma Wachs", "catalogos/serie-oro.pdf", "img/portadas/serie-oro.jpg", 4),
    Catalog("mps", "Sierras MPS", "Sierras oscilobatientes", "MPS", "catalogos/mps.pdf", "img/portadas/mps.jpg", 2),
)

fun catalog(key: String): Catalog? = CATALOGS.firstOrNull { it.key == key }

// ---------------------------------------------------------------- Líneas de producto

data class ProductLine(
    val css: String,
    val name: String,
    val icon: String,
    val color: String,
    val photo: String,
    val photoPosition: String,
    val headline: String,
    val description: String,
    val tags: List<String>,
    val catalogKey: String,
)

val PRODUCT_LINES = listOf(
    ProductLine("l-int", "Línea Interior", "home", "#9a5b2e", "img/fotos/f-escalera.jpg", "50% 38%",
        "Suelos y muebles que duran años como el primer día",
        "Aceites, Hardwax, barnices y tintes para parquet, mobiliario y carpintería interior.",
        listOf("Parquet", "Muebles", "Puertas"), "interior"),
    ProductLine("l-ext", "Línea Exterior", "sun", "#4f9a3a", "img/fotos/f-porche.jpg", "50% 60%",
        "Protección frente al sol, la lluvia y el salitre",
        "Lasures, aceites y protectores para tarimas, fachadas, casas de madera y náutica.",
        listOf("Tarimas", "Fachadas", "Náutica"), "exterior"),
    ProductLine("l-res", "Línea Restauración", "wand", "#7a4a2b", "img/fotos/f-panal.jpg", "50% 50%",
        "Devuelve la vida a cualquier pieza",
        "Decapantes, ceras de abeja, aceites y complementos para restaurar.",
        listOf("Decapar", "Nutrir", "Proteger"), "restauracion"),
    ProductLine("l-ret", "Línea Retoque", "brush", "#0090d9", "img/fotos/f-encerar.jpg", "50% 45%",
        "Golpes y arañazos que desaparecen en minutos",
        "Ceras de relleno, masillas rápidas, fondos y selladores.",
        listOf("Golpes", "Arañazos", "Juntas"), "retoque"),
    ProductLine("l-oro", "Línea Serie Oro", "crown", "#c99a2e", "img/fotos/f-oro.jpg", "50% 40%",
        "El acabado dorado de los grandes artesanos",
        "Pan de oro, mordientes, pátinas y herramientas de dorador.",
        listOf("Pan de oro", "Pátinas", "Dorado"), "serie-oro"),
)

// ---------------------------------------------------------------- Oferta y pasos

val OFFER_FEATURES = listOf(
    Chip("calendar", "Demostración en vivo"),
    Chip("users", "Formación para tu equipo"),
    Chip("layers", "Asesoramiento de surtido"),
    Chip("shield", "Sin compromiso de compra"),
)

data class Step(val icon: String, val title: String, val text: String)

val STEPS = listOf(
    Step("phone", "Nos llamas o escribes", "Por teléfono, WhatsApp o formulario. Te respondemos en horario comercial."),
    Step("store", "Te visitamos", "Un técnico va a tu tienda con una demostración y te asesora sobre el surtido."),
    Step("box", "Empiezas a vender", "Pedidos ágiles, unidades sueltas si las necesitas y soporte técnico continuo."),
)

// ---------------------------------------------------------------- Antes / después

/**
 * Fotos del comparador antes/después.
 * ⚑ Sustituir por fotos reales de un trabajo de Vicab (misma pieza, mismo encuadre y misma luz).
 * Mientras [before] y [after] sean la misma imagen, la web simula el "antes" con un filtro
 * y muestra el aviso de prueba piloto.
 */
data class BeforeAfterPhotos(val before: String, val after: String) {
    val simulated: Boolean get() = before == after
}

val BEFORE_AFTER = BeforeAfterPhotos(
    before = "img/fotos/f-tarima.jpg",
    after = "img/fotos/f-tarima.jpg",
)

// ---------------------------------------------------------------- Marcas secundarias

/** Marca con catálogo en PDF ([catalogKey]) o que se solicita por formulario. */
data class PartnerBrand(
    val logo: String,
    val name: String,
    val category: String,
    val description: String,
    val catalogKey: String? = null,
)

// ⚑ Confirmar las descripciones de Pizzi, Whale Spray y Ducasse con Vicab.
val PARTNER_BRANDS = listOf(
    PartnerBrand("img/marcas/mps.png", "MPS", "Corte", "Sierras y hojas de sierra alemanas para el trabajo profesional de la madera.", "mps"),
    PartnerBrand("img/marcas/famag.png", "Famag", "Perforación", "Brocas y fresas para madera fabricadas en Alemania desde 1865."),
    PartnerBrand("img/marcas/pizzi.png", "Pizzi", "Herramienta", "Herramienta profesional para carpintería y trabajo de la madera."),
    PartnerBrand("img/marcas/whale.png", "Whale Spray", "Sprays técnicos", "Sprays técnicos para limpieza, lubricación y mantenimiento. Empresa certificada ISO 9001 e ISO 14001."),
    PartnerBrand("img/marcas/ducasse.png", "Ducasse Industrial", "Herrajes", "Sistemas correderos y herrajes para puertas de madera y vidrio."),
)

// ---------------------------------------------------------------- Vídeos

data class Video(val youtubeId: String, val category: String, val title: String, val big: Boolean = false) {
    val thumbnail: String
        get() = "https://i.ytimg.com/vi/$youtubeId/${if (big) "maxresdefault" else "hqdefault"}.jpg"
}

val VIDEOS = listOf(
    Video("2GazdMvEfwg", "Restauración", "Reparar madera con Ecostucco fibrorreforzado", big = true),
    Video("US98AYUTpeU", "Retoque", "Holzmasse: masilla de secado rápido"),
    Video("eM7ytRJfQlI", "Decoración", "Efecto vintage en una mesita"),
    Video("8ReLIb2F4N0", "Interior", "Recrear un canto en roble con Hardwax"),
    Video("u9uqJiRn8Bg", "Exterior", "Limpieza y aceite en borde de piscina"),
)

// ---------------------------------------------------------------- Opiniones (reales, de Google)

data class Review(val author: String, val color: String, val meta: String, val text: String) {
    val initial: String get() = author.take(1)
}

// ⚑ Solo se muestran las reseñas con texto visibles sin iniciar sesión en Google.
val REVIEWS = listOf(
    Review("Victor Manuel Guareño Areses", "#0090d9", "Local Guide · hace un año",
        "No hay que buscar más. Son los mejores profesionales y asesores para el tratamiento y cuidado de cualquier tipo de madera. Sus productos de una altísima calidad y a precios competitivos. Son una referencia en nuestros proyectos."),
    Review("David", "#e0308a", "Local Guide · hace un año",
        "Empresa de confianza, especializada en productos para madreas, trato exquisito, repetiré seguro ha sido una experiencia de 10"),
    Review("Lara Pino", "#1aa35a", "Local Guide · hace 3 años",
        "Gracias a Vicab por el asesoramiento y ayuda que nos ha proporcionado. Excelente producto el aceite para madera termotratada."),
    Review("Pichimichi", "#f5a300", "Local Guide · hace 5 años",
        "Grandes profesionales. Confianza absoluta."),
)

// ---------------------------------------------------------------- Preguntas frecuentes

data class Faq(val question: String, val answer: String)

val FAQS = listOf(
    Faq("¿Vendéis a particulares?", "No. Vicab trabaja exclusivamente con profesionales y puntos de venta. Respetamos a nuestros distribuidores y a sus clientes, por eso nunca vendemos al usuario final."),
    Faq("¿Cómo puedo ser distribuidor?", "Rellena el formulario o llámanos. Un comercial de tu zona se pondrá en contacto contigo, te visitará y te explicará las condiciones."),
    Faq("¿La demostración en tienda tiene coste?", "No. Realizamos demostraciones y jornadas técnicas en el punto de venta sin coste para que tu equipo conozca el producto de primera mano."),
    Faq("¿Puedo pedir unidades sueltas?", "Sí. Si necesitas unidades sueltas en lugar de cajas completas, te escuchamos y lo hacemos posible."),
    Faq("¿Cómo consigo las fichas de seguridad (FDS)?", "Escríbenos a comercial@vicab.es indicando los datos de tu empresa. Todos los productos que comercializamos están dados de alta en el Instituto Nacional de Toxicología según la legislación vigente."),
    Faq("¿Qué es el Vicab Club?", "Nuestro programa de fidelización anual. No solo queremos ser tu proveedor, queremos ser tu partner, y recompensamos tu confianza."),
)

// ---------------------------------------------------------------- Contacto y formulario

enum class FormMode(val title: String, val subtitle: String, val button: String, val fullForm: Boolean) {
    CALL("Te llamamos nosotros", "Déjanos tu teléfono y un comercial te llamará en horario de atención.", "Quiero que me llamen", false),
    DEMO("Reserva tu demostración gratuita", "Un técnico visitará tu tienda para enseñar los productos a tu equipo, sin compromiso.", "Reservar demostración", true),
    DIST("Hazte distribuidor Vicab", "Cuéntanos sobre tu negocio y el comercial de tu zona se pondrá en contacto contigo.", "Enviar solicitud", true),
    INFO("Solicita información o catálogos", "Te enviamos catálogos, tarifas o fichas técnicas de cualquiera de nuestras marcas.", "Solicitar información", true),
}

/** Pestañas visibles del formulario (INFO se abre desde enlaces, sin pestaña propia). */
val FORM_TABS = listOf(
    FormMode.CALL to "📞 Que me llamen",
    FormMode.DEMO to "Demo gratis",
    FormMode.DIST to "Ser distribuidor",
)

val BUSINESS_TYPES = listOf("Ferretería", "Droguería", "Almacén de madera", "Bricolaje", "Carpintería", "Otro")

data class Benefit(val icon: String, val title: String, val text: String)

val DISTRIBUTOR_BENEFITS = listOf(
    Benefit("users", "Formación técnica", "Jornadas en tu tienda para que tu equipo domine cada producto."),
    Benefit("truck", "Logística desde Barcelona", "Stock en Barberà del Vallès y unidades sueltas cuando las necesites."),
    Benefit("store", "Expositores y muestrarios", "Material de punto de venta para que el producto se venda solo."),
    Benefit("handshake", "Vicab Club y exclusividad", "Marcas en exclusiva y un programa anual que premia tu confianza."),
)

val PROVINCES = listOf(
    "A Coruña", "Álava", "Albacete", "Alicante", "Almería", "Asturias", "Ávila", "Badajoz", "Baleares", "Barcelona",
    "Burgos", "Cáceres", "Cádiz", "Cantabria", "Castellón", "Ceuta", "Ciudad Real", "Córdoba", "Cuenca", "Girona",
    "Granada", "Guadalajara", "Guipúzcoa", "Huelva", "Huesca", "Jaén", "La Rioja", "Las Palmas", "León", "Lleida",
    "Lugo", "Madrid", "Málaga", "Melilla", "Murcia", "Navarra", "Ourense", "Palencia", "Pontevedra", "Salamanca",
    "Santa Cruz de Tenerife", "Segovia", "Sevilla", "Soria", "Tarragona", "Teruel", "Toledo", "Valencia", "Valladolid",
    "Vizcaya", "Zamora", "Zaragoza", "Andorra", "Portugal",
)

/** Datos del formulario de contacto. */
data class ContactRequest(
    val mode: FormMode,
    val name: String = "",
    val phone: String = "",
    val company: String = "",
    val province: String = "",
    val businessType: String = "",
    val email: String = "",
    val message: String = "",
    val acceptsPrivacy: Boolean = false,
    val wantsNews: Boolean = false,
) {
    enum class Field { NAME, PHONE, PRIVACY }

    /** Campos obligatorios que faltan, en el orden en que aparecen. */
    fun missingFields(): List<Field> = buildList {
        if (name.isBlank()) add(Field.NAME)
        if (phone.isBlank()) add(Field.PHONE)
        if (!acceptsPrivacy) add(Field.PRIVACY)
    }

    val firstName: String get() = name.trim().substringBefore(' ')
}

// ---------------------------------------------------------------- Pie

val FOOTER_PRODUCTS = listOf("Interior", "Exterior", "Restauración", "Retoque", "Serie Oro")

val FOOTER_COMPANY = listOf(
    NavLink("Marcas", "#marcas"),
    NavLink("Catálogos", "#catalogos"),
    NavLink("Vídeos", "#videos"),
    NavLink("Distribuidores", "#contacto", FormMode.DIST),
    NavLink("Fichas de seguridad", "#fds"),
)

val CERTIFICATIONS = listOf("CATAS", "IBR", "EN71", "EAN")
