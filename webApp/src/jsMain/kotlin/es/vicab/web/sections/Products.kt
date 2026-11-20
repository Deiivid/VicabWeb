package es.vicab.web.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import es.vicab.web.AppState
import es.vicab.web.BEFORE_AFTER
import es.vicab.web.Blockquote
import es.vicab.web.Company
import es.vicab.web.Dom
import es.vicab.web.FigCaption
import es.vicab.web.Figure
import es.vicab.web.FormMode
import es.vicab.web.Icon
import es.vicab.web.Link
import es.vicab.web.OFFER_FEATURES
import es.vicab.web.PRODUCT_LINES
import es.vicab.web.ProductLine
import es.vicab.web.STEPS
import es.vicab.web.Stars
import es.vicab.web.catalog
import es.vicab.web.reveal
import es.vicab.web.t
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.max
import org.jetbrains.compose.web.attributes.min
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.flexWrap
import org.jetbrains.compose.web.css.gap
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.marginTop
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.css.paddingTop
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Article
import org.jetbrains.compose.web.dom.B
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun ProductLines() {
    Section({ classes("sec"); id("lineas"); style { paddingTop(72.px) } }) {
        Div({ classes("wrap") }) {
            Div({
                classes("head")
                reveal()
                style {
                    display(DisplayStyle.Flex)
                    justifyContent(JustifyContent.SpaceBetween)
                    alignItems(AlignItems.FlexEnd)
                    gap(24.px)
                    property("max-width", "none")
                    flexWrap(FlexWrap.Wrap)
                }
            }) {
                Div({ style { maxWidth(640.px) } }) {
                    Span({ classes("eyebrow") }) { Text(t("Productos Borma Wachs")) }
                    H2({ classes("h2") }) { Text(t("Todo para el tratamiento de la madera")) }
                    P { Text(t("Cinco líneas profesionales que cubren cualquier trabajo: del mantenimiento de un parquet a la restauración de una antigüedad.")) }
                }
                Link("#catalogos", attrs = { classes("btn", "btn-line") }) { Icon("book"); Text(t("Ver todos los catálogos")) }
            }
            Div({ classes("pgrid") }) {
                PRODUCT_LINES.forEachIndexed { i, line -> ProductCard(line, i) }
            }
        }
    }
}

@Composable
private fun ProductCard(line: ProductLine, index: Int) {
    Article({ classes("pcard", line.css, "d$index"); reveal() }) {
        Img(line.photo, t(line.name)) {
            style { property("object-position", line.photoPosition) }
            attr("loading", "lazy")
        }
        Div({ classes("pc-body") }) {
            Span({ classes("pc-tag") }) {
                I({ style { backgroundColor(Color(line.color)) } }) { Icon(line.icon) }
                Text(t(line.name))
            }
            H3 { Text(t(line.headline)) }
            Div({ classes("pc-more") }) {
                P { Text(t(line.description)) }
                Div({ classes("pc-chips") }) { line.tags.forEach { Span { Text(t(it)) } } }
            }
            Div({ classes("pc-acts") }) {
                CatalogButton(line.catalogKey, attrs = { classes("btn", "btn-white", "btn-sm") }) {
                    Icon("book"); Text(t("Ver catálogo"))
                }
                Link("#contacto", mode = FormMode.DEMO, attrs = { classes("pc-link") }) {
                    Text(t("Pedir demo") + " "); Icon("arrow-r")
                }
            }
        }
    }
}

/** Botón que abre un catálogo en el visor de PDF. */
@Composable
fun CatalogButton(
    key: String,
    attrs: org.jetbrains.compose.web.dom.AttrBuilderContext<org.w3c.dom.HTMLButtonElement>? = null,
    content: org.jetbrains.compose.web.dom.ContentBuilder<org.w3c.dom.HTMLButtonElement>,
) {
    Button({
        onClick { e ->
            e.preventDefault()
            catalog(key)?.let { AppState.viewer.open(it) }
        }
        attrs?.invoke(this)
    }, content)
}

@Composable
fun Offer() {
    Section({ classes("sec-sm", "offer-sec") }) {
        Div({ classes("wrap") }) {
            Div({ classes("offer2"); reveal() }) {
                Div({ classes("o-media") }) {
                    Img("img/fotos/f-brocha-borma.jpg", t("Brocha Borma Wachs aplicando acabado sobre madera")) {
                        style { property("object-position", "52% 45%") }
                    }
                    Div({ classes("o-badge") }) { B { Text("0 €") }; Span { Text(t("Sin coste ni compromiso")) } }
                }
                Div({ classes("o-txt") }) {
                    Span({ classes("eyebrow") }) { Text(t("Para tiendas y profesionales")) }
                    H2 { Text(t("Vamos a tu tienda y te enseñamos cómo funciona. Gratis.")) }
                    P { Text(t("Un técnico de Vicab visita tu punto de venta, prueba los productos delante de tu equipo y te ayuda a elegir el surtido que más se va a vender en tu zona.")) }
                    Div({ classes("o-feats") }) {
                        OFFER_FEATURES.forEach { f ->
                            Div({ classes("o-feat") }) { Span { Icon(f.icon) }; Text(t(f.label)) }
                        }
                    }
                    Div({ classes("o-acts") }) {
                        Link("#contacto", mode = FormMode.DEMO, attrs = { classes("btn", "btn-primary", "btn-lg") }) {
                            Text(t("Reservar mi demostración") + " "); Icon("arrow-r")
                        }
                        Link(whatsapp = Company.WA_DEMO, attrs = { classes("btn", "btn-line", "btn-lg") }) {
                            Icon("wa") { style { color(Color("#25d366")) } }
                            Text(" " + t("Por WhatsApp"))
                        }
                    }
                    Figure({ classes("o-quote") }) {
                        Stars()
                        Blockquote { Text("“" + t("Gracias a Vicab por el asesoramiento y ayuda que nos ha proporcionado.") + "”") }
                        FigCaption { Text("Lara Pino · Google") }
                    }
                }
            }
        }
    }
}

@Composable
fun HowWeWork() {
    var stepsIn by remember { mutableStateOf(false) }
    Section({ classes("sec"); id("como") }) {
        Div({ classes("wrap") }) {
            Div({ classes("head", "center"); reveal() }) {
                Span({ classes("eyebrow") }) { Text(t("Cómo trabajamos")) }
                H2({ classes("h2") }) { Text(t("Empezar con Vicab es así de fácil")) }
            }
        }
        Div({ classes("wrap", "how") }) {
            Div({ classes("how-l"); reveal() }) {
                Div({
                    classes("steps", "vsteps")
                    id("steps")
                    if (stepsIn) classes("in")
                    ref { el ->
                        Dom.observeOnce(el, threshold = 0.45) { stepsIn = true }
                        onDispose { }
                    }
                }) {
                    Div({ classes("steps-line"); attr("aria-hidden", "true") }) {
                        Span({ classes("fill") }); Span({ classes("shine") })
                    }
                    STEPS.forEachIndexed { i, step ->
                        Div({ classes("step"); reveal(i + 1) }) {
                            Div({ classes("num") }) { Icon(step.icon) }
                            H3 { Text(t(step.title)) }
                            P { Text(t(step.text)) }
                        }
                    }
                }
                Link("#contacto", mode = FormMode.DEMO, attrs = { classes("btn", "btn-primary"); style { marginTop(34.px) } }) {
                    Text(t("Pide tu demo gratis") + " "); Icon("arrow-r")
                }
            }
            Div({ classes("how-r"); reveal(2) }) {
                BeforeAfter()
                Div({ classes("ba-cap") }) {
                    Span({ classes("eyebrow") }) { Text(t("Resultados reales")) }
                    B { Text(t("Desliza y compruébalo tú mismo")) }
                    P { Text(t("Una madera apagada y castigada recupera el color, el brillo y la protección con un solo tratamiento. Es exactamente lo que enseñamos en cada demostración.")) }
                    if (BEFORE_AFTER.simulated) {
                        Span({ classes("note") }) { Text(t("⚑ Prueba piloto: sustituir por fotos reales de un trabajo antes/después")) }
                    }
                }
            }
        }
    }
}

/** Comparador antes/después con un deslizador. */
@Composable
private fun BeforeAfter() {
    var position by remember { mutableStateOf(50) }
    val photos = BEFORE_AFTER
    Div({
        classes("ba")
        if (photos.simulated) classes("simulated")
        id("ba")
        style { property("--p", "$position%") }
    }) {
        Img(photos.after, t("Después del tratamiento"))
        Div({ classes("before"); style { property("position", "absolute"); property("inset", "0") } }) {
            Img(photos.before, t("Antes del tratamiento"))
            if (photos.simulated) {
                Icon("scratch", viewBox = "0 0 400 300") { classes("scratch"); attr("preserveAspectRatio", "none") }
            }
        }
        Span({ classes("lab", "l") }) { Text(t("Antes")) }
        Span({ classes("lab", "r") }) { Text(t("Después")) }
        Div({ classes("bar") })
        Div({ classes("knob") }) { Icon("swap") }
        Input(InputType.Range) {
            min("0"); max("100")
            value(position)
            attr("aria-label", t("Comparar antes y después"))
            onInput { position = (it.value ?: 50).toInt() }
        }
    }
}
