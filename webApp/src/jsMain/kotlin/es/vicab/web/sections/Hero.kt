package es.vicab.web.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import es.vicab.web.BRANDS
import es.vicab.web.Company
import es.vicab.web.Dom
import es.vicab.web.FigCaption
import es.vicab.web.Figure
import es.vicab.web.FormMode
import es.vicab.web.GOLDEN_RULES
import es.vicab.web.HERO_CHIPS
import es.vicab.web.HERO_PHOTOS
import es.vicab.web.Icon
import es.vicab.web.Link
import es.vicab.web.PhoneLink
import es.vicab.web.STATS
import es.vicab.web.Stat
import es.vicab.web.reveal
import es.vicab.web.t
import kotlinx.browser.window
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.dom.B
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Em
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Ol
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Span
import es.vicab.web.Strong
import org.jetbrains.compose.web.dom.Text
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun Hero() {
    Section({ classes("hero") }) {
        Img("img/logo.png", "") { classes("brush"); attr("aria-hidden", "true") }
        Div({ classes("wrap", "hero-grid") }) {
            Div {
                Span({ classes("pill"); reveal() }) {
                    B { Text(t("Exclusiva")) }
                    Text(" " + t("Distribuidor oficial de Borma Wachs"))
                }
                H1({ reveal(1) }) {
                    Text(t("Profesionales del tratamiento de la madera"))
                    Span({ classes("hl") }) { Text(".") }
                    Span({ classes("script") }) { Text(t("Pasión por la madera")) }
                }
                P({ classes("lead"); reveal(2) }) {
                    Text(t("Productos para proteger, reparar y decorar la madera, solo para tiendas y profesionales. Te visitamos y hacemos una") + " ")
                    Strong({ style { color(Color.white) } }) { Text(t("demostración gratuita en tu tienda")) }
                    Text(".")
                }
                Div({ classes("cta"); reveal(3) }) {
                    Link("#contacto", mode = FormMode.DEMO, attrs = { classes("btn", "btn-primary", "btn-lg") }) {
                        Text(t("Pide tu demo gratis") + " "); Icon("arrow-r")
                    }
                    PhoneLink({ classes("btn", "btn-ghost", "btn-lg") }) {
                        Icon("phone"); Text(" " + Company.PHONE_DISPLAY)
                    }
                }
                Div({ classes("hero-proof"); reveal(4) }) {
                    Div({ classes("chips") }) {
                        HERO_CHIPS.forEach { chip ->
                            Span({ classes("chip") }) { Icon(chip.icon); Text(t(chip.label)) }
                        }
                    }
                }
            }

            Div({ classes("bento"); reveal(2) }) {
                HERO_PHOTOS.forEach { photo ->
                    Figure({ classes(photo.css) }) {
                        Img(photo.src, t(photo.alt)) {
                            photo.position?.let { pos -> style { property("object-position", pos) } }
                        }
                        FigCaption { Text(t(photo.caption)) }
                    }
                }
                FloatCard("fc-1", "calendar", "Demostración gratuita", "En tu tienda, sin compromiso")
                FloatCard("fc-2", "award", "+15 años", "en el sector de la madera")
            }
        }
        Div({ classes("wrap", "hero-brands"); reveal(3) }) {
            Span({ classes("lbl") }) { Text(t("Distribuimos en exclusiva")) }
            Div({ classes("marquee") }) {
                Div({ classes("track"); id("track") }) {
                    // Los logos van dos veces para que el carrusel sea un bucle continuo
                    repeat(2) { copy ->
                        BRANDS.forEach { brand ->
                            Link("#marcas", attrs = {
                                classes("brand")
                                if (copy == 1) { attr("aria-hidden", "true"); attr("tabindex", "-1") }
                            }) { Img(brand.logo, brand.name) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatCard(css: String, icon: String, title: String, subtitle: String) {
    Div({ classes("float-card", css) }) {
        Span({ classes("ic") }) { Icon(icon) }
        Span { B { Text(t(title)) }; Span { Text(t(subtitle)) } }
    }
}

@Composable
fun Stats() {
    Div({ classes("wrap") }) {
        Div({ classes("stats"); reveal() }) {
            STATS.forEach { StatItem(it) }
        }
    }
}

/** Cifra que cuenta desde 0 hasta su valor la primera vez que se ve. */
@Composable
private fun StatItem(stat: Stat) {
    var shown by remember { mutableStateOf(stat.value) }
    Div({ classes("stat") }) {
        B {
            if (stat.prefix.isNotEmpty()) Text(stat.prefix)
            Span({
                ref { el ->
                    if (stat.value > 0) {
                        shown = 0
                        Dom.observeOnce(el, threshold = 0.6) { animateCount(stat.value) { shown = it } }
                    }
                    onDispose { }
                }
            }) { Text(shown.toString()) }
            if (stat.suffix.isNotEmpty()) Em { Text(stat.suffix) }
        }
        Span { Text(t(stat.label)) }
    }
}

private fun animateCount(to: Int, durationMs: Double = 1400.0, onFrame: (Int) -> Unit) {
    var start = -1.0
    fun tick(time: Double) {
        if (start < 0) start = time
        val p = ((time - start) / durationMs).coerceAtMost(1.0)
        val eased = 1 - (1 - p).pow(3)
        onFrame((to * eased).roundToInt())
        if (p < 1) window.requestAnimationFrame(::tick)
    }
    window.requestAnimationFrame(::tick)
}

@Composable
fun GoldenRules() {
    Section({ classes("normas-top"); id("normas") }) {
        Div({ classes("wrap") }) {
            Div({ classes("gold-rules"); reveal() }) {
                Div({ classes("gr-intro") }) {
                    Div({ classes("gr-seal"); attr("aria-hidden", "true") }) {
                        B { Text(GOLDEN_RULES.size.toString()) }
                        Icon("crown")
                    }
                    Span({ classes("eyebrow", "gold") }) { Text(t("Nuestro compromiso")) }
                    H2 { Text(t("Las 8 normas de oro de Vicab")) }
                    P { Text(t("Lo que garantizamos a cada tienda que trabaja con nosotros, sin letra pequeña.")) }
                }
                Ol({ classes("gr-list") }) {
                    GOLDEN_RULES.forEachIndexed { i, rule ->
                        Li({ classes("gr-item"); reveal(i % 4 + 1) }) {
                            Div({ classes("gr-top") }) {
                                Span({ classes("gr-ic") }) { Icon(rule.icon) }
                                Span({ classes("gr-n") }) { Text((i + 1).toString().padStart(2, '0')) }
                            }
                            B { Text(t(rule.title)) }
                            Small { Text(t(rule.detail)) }
                        }
                    }
                }
            }
        }
    }
}
