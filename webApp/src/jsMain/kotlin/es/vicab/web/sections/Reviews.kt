package es.vicab.web.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import es.vicab.web.Company
import es.vicab.web.Details
import es.vicab.web.FAQS
import es.vicab.web.Figure
import es.vicab.web.GoogleLogo
import es.vicab.web.REVIEWS
import es.vicab.web.Review
import es.vicab.web.Stars
import es.vicab.web.Summary
import es.vicab.web.reveal
import es.vicab.web.t
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.B
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.TouchEvent
import org.w3c.dom.get

private const val AUTO_ADVANCE_MS = 6000L

/** Una diapositiva por reseña más la tarjeta final que lleva a Google. */
private val SLIDES = REVIEWS.size + 1

@Composable
fun ReviewsAndFaq() {
    Section({ classes("sec", "soft"); id("opiniones") }) {
        Span({ id("fds"); classes("anchor") })
        Div({ classes("wrap", "qa") }) {
            Div({ classes("qa-l"); reveal() }) {
                Span({ classes("eyebrow") }) { Text(t("Opiniones")) }
                H2({ classes("h2"); style { margin(14.px, 0.px, 22.px) } }) { Text(t("Lo que dicen nuestros clientes")) }
                Div({ classes("g-score") }) {
                    GoogleLogo()
                    Div {
                        B { Text(t("${Company.GOOGLE_RATING} en Google")) }
                        Stars()
                        Span { Text(t("${Company.GOOGLE_REVIEW_COUNT} reseñas de clientes")) }
                    }
                    GoogleReviewsLink({ classes("btn", "btn-line", "btn-sm") }, "Ver todas en Google")
                }
                ReviewSlider()
            }
            Div({ classes("qa-r"); reveal(1) }) {
                Span({ classes("eyebrow") }) { Text(t("Preguntas frecuentes")) }
                H2({ classes("h2"); style { margin(14.px, 0.px, 22.px) } }) { Text(t("Resolvemos tus dudas")) }
                Div({ classes("faq") }) {
                    FAQS.forEachIndexed { i, faq ->
                        Details({ if (i == 0) attr("open", "") }) {
                            Summary { Text(t(faq.question)); I() }
                            P { Text(t(faq.answer)) }
                        }
                    }
                }
                P({ classes("qa-more") }) {
                    Text(t("¿No encuentras tu respuesta? Llámanos y te atendemos al momento.") + " ")
                    A("tel:${Company.PHONE_TEL}") { Text(Company.PHONE_DISPLAY) }
                    Text(" · ")
                    A("mailto:${Company.EMAIL}?subject=Solicitud%20de%20fichas%20de%20seguridad") { Text(t("Solicitar FDS")) }
                }
            }
        }
    }
}

@Composable
private fun GoogleReviewsLink(attrs: org.jetbrains.compose.web.dom.AttrBuilderContext<org.w3c.dom.HTMLAnchorElement>, label: String) {
    A(Company.GOOGLE_REVIEWS_URL, {
        attrs.invoke(this)
        target(ATarget.Blank)
        attr("rel", "noopener")
    }) { Text(t(label)) }
}

/** Carrusel de reseñas: una cada vez, avanza solo, se pausa al pasar el ratón y admite deslizar. */
@Composable
private fun ReviewSlider() {
    var current by remember { mutableStateOf(0) }
    var paused by remember { mutableStateOf(false) }
    // Cada vez que cambia la reseña (sola o a mano) se reinicia la cuenta atrás
    LaunchedEffect(current, paused) {
        if (paused) return@LaunchedEffect
        delay(AUTO_ADVANCE_MS)
        current = (current + 1) % SLIDES
    }
    fun show(i: Int) { current = (i + SLIDES) % SLIDES }

    Div({
        classes("rev-slider")
        id("revSlider")
        onMouseEnter { paused = true }
        onMouseLeave { paused = false }
        ref { el ->
            var startX: Double? = null
            val onStart: (org.w3c.dom.events.Event) -> Unit = { e ->
                startX = e.unsafeCast<TouchEvent>().touches[0]?.clientX?.toDouble()
            }
            val onEnd: (org.w3c.dom.events.Event) -> Unit = { e ->
                val x0 = startX
                val x1 = e.unsafeCast<TouchEvent>().changedTouches[0]?.clientX?.toDouble()
                if (x0 != null && x1 != null && kotlin.math.abs(x1 - x0) > 40) show(current + if (x1 < x0) 1 else -1)
                startX = null
            }
            el.addEventListener("touchstart", onStart)
            el.addEventListener("touchend", onEnd)
            onDispose {
                el.removeEventListener("touchstart", onStart)
                el.removeEventListener("touchend", onEnd)
            }
        }
    }) {
        Div({ classes("rev-track"); style { property("transform", "translateX(${-current * 100}%)") } }) {
            REVIEWS.forEachIndexed { i, review -> ReviewCard(review, active = i == current) }
            MoreReviewsCard(active = current == REVIEWS.size)
        }
    }
    Div({ classes("rev-nav") }) {
        Div({ classes("rev-dots"); id("revDots") }) {
            (0 until SLIDES).forEach { i ->
                Button({
                    attr("aria-label", "${i + 1}")
                    if (i == current) classes("on")
                    onClick { show(i) }
                })
            }
        }
        GoogleReviewsLink({ classes("link") }, "Leer las ${Company.GOOGLE_REVIEW_COUNT} reseñas en Google Maps")
    }
}

@Composable
private fun ReviewCard(review: Review, active: Boolean) {
    Figure({ classes("rev"); if (active) classes("on") }) {
        Div({ classes("rev-top") }) {
            Span({ classes("av"); style { backgroundColor(Color(review.color)) } }) { Text(review.initial) }
            Span({ classes("who") }) { B { Text(review.author) }; Span { Text(t(review.meta)) } }
            GoogleLogo()
        }
        Stars()
        // Las reseñas se muestran tal como las escribieron en Google
        P { Text(review.text) }
    }
}

/** Última diapositiva: invita a leer todas las reseñas en Google. */
@Composable
private fun MoreReviewsCard(active: Boolean) {
    A(Company.GOOGLE_REVIEWS_URL, {
        classes("rev", "rev-more")
        if (active) classes("on")
        target(ATarget.Blank)
        attr("rel", "noopener")
    }) {
        GoogleLogo()
        B { Text(t("${Company.GOOGLE_RATING} en Google")) }
        Stars()
        Span { Text(t("Leer las ${Company.GOOGLE_REVIEW_COUNT} reseñas en Google Maps") + " →") }
    }
}
