package es.vicab.web.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import es.vicab.web.AppState
import es.vicab.web.BUSINESS_TYPES
import es.vicab.web.Company
import es.vicab.web.ContactRequest
import es.vicab.web.ContactRequest.Field
import es.vicab.web.DISTRIBUTOR_BENEFITS
import es.vicab.web.FORM_TABS
import es.vicab.web.FormMode
import es.vicab.web.Icon
import es.vicab.web.PROVINCES
import es.vicab.web.PhoneLink
import es.vicab.web.Link
import es.vicab.web.reveal
import es.vicab.web.t
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.autoComplete
import org.jetbrains.compose.web.attributes.AutoComplete
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.rows
import org.jetbrains.compose.web.attributes.selected
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.attributes.value
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Aside
import org.jetbrains.compose.web.dom.B
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.CheckboxInput
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Form
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.H4
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.Iframe
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Option
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.RadioInput
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Select
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextArea
import org.w3c.dom.HTMLElement

@Composable
fun Contact() {
    val distMode = AppState.formMode == FormMode.DIST
    Section({ classes("sec"); id("contacto") }) {
        Div({ classes("wrap") }) {
            Div({ classes("head", "center"); reveal() }) {
                Span({ classes("eyebrow") }) { Text(t("Contacto")) }
                H2({ classes("h2") }) { Text(t("Hablemos de tu negocio")) }
                P { Text(t("Elige cómo prefieres que te atendamos. Respondemos en horario comercial.")) }
            }
            Div({ classes("contact2"); if (distMode) classes("mode-dist"); reveal() }) {
                ContactPanel(distMode)
                Div({ classes("c-form") }) { ContactForm() }
            }
        }
    }
}

/** Panel oscuro: acciones directas, datos de contacto y mapa (o ventajas en modo distribuidor). */
@Composable
private fun ContactPanel(distMode: Boolean) {
    Aside({ classes("c-panel") }) {
        Div({ classes("cp-head") }) {
            Span({ classes("cp-live") }) { Span({ classes("dot") }); Text(t("Respuesta en horario comercial")) }
            H3 { Text(t("¿Prefieres hablar ahora?")) }
        }
        Div({ classes("cp-actions") }) {
            PhoneLink({ classes("cp-btn", "cp-call") }) {
                Span({ classes("cp-btn-ic") }) { Icon("phone") }
                Span { Small { Text(t("Llamar ahora")) }; B { Text(Company.PHONE_DISPLAY) } }
            }
            Link(whatsapp = Company.WA_INFO, attrs = { classes("cp-btn", "cp-wa") }) {
                Span({ classes("cp-btn-ic") }) { Icon("wa") }
                Span { Small { Text("WhatsApp") }; B { Text(t("Escribir por WhatsApp")) } }
            }
        }
        Div({ classes("cp-rows") }) {
            InfoRow("mail", "Email") { A("mailto:${Company.EMAIL}") { Text(Company.EMAIL) } }
            InfoRow("clock", "Horario") {
                P { Text(t("Lunes a viernes")); Br(); Text("8:00 – 13:00 · 15:30 – 19:00") }
            }
            InfoRow("pin", "Dirección") {
                P {
                    Text("C. Can Salvatella, nave 22 · Edificio Euromotor"); Br()
                    Text("08210 Barberà del Vallès (Barcelona)")
                }
            }
        }
        if (distMode) {
            Div({ classes("c-dist") }) {
                H4 { Text(t("Ventajas de ser distribuidor")) }
                DISTRIBUTOR_BENEFITS.forEach { b ->
                    Div({ classes("ben") }) {
                        Span({ classes("ben-ic") }) { Icon(b.icon) }
                        Div { B { Text(t(b.title)) }; P { Text(t(b.text)) } }
                    }
                }
            }
        } else {
            Div({ classes("cp-map") }) {
                Iframe({
                    attr("title", t("Mapa de Vicab"))
                    attr("loading", "lazy")
                    attr("referrerpolicy", "no-referrer-when-downgrade")
                    attr("src", Company.MAP_EMBED_URL)
                })
                A(Company.DIRECTIONS_URL, {
                    classes("cp-route")
                    target(ATarget.Blank)
                    attr("rel", "noopener")
                }) { Icon("pin"); Text(t("Cómo llegar")) }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: String, title: String, content: @Composable () -> Unit) {
    Div({ classes("cp-row") }) {
        I { Icon(icon) }
        Div { H4 { Text(t(title)) }; content() }
    }
}

@Composable
private fun ContactForm() {
    val mode = AppState.formMode
    var request by remember { mutableStateOf(ContactRequest(mode)) }
    var showErrors by remember { mutableStateOf(false) }
    var sentTo by remember { mutableStateOf<String?>(null) }
    val missing = if (showErrors) request.missingFields() else emptyList()
    val focusTargets = remember { mutableMapOf<Field, HTMLElement>() }

    Form(attrs = {
        classes("form")
        id("form")
        if (mode == FormMode.DIST) classes("mode-dist")
        attr("novalidate", "")
        onSubmit { e ->
            e.preventDefault()
            val bad = request.copy(mode = mode).missingFields()
            if (bad.isNotEmpty()) {
                showErrors = true
                focusTargets[bad.first()]?.focus()
                return@onSubmit
            }
            // ⚑ CONFIGURAR: aquí se enviaría la solicitud (Formspree, Netlify Forms, email, CRM…)
            sentTo = request.firstName
        }
    }) {
        val name = sentTo
        if (name != null) {
            FormSent(name)
            return@Form
        }

        Div({ classes("seg"); attr("role", "tablist"); attr("aria-label", t("Tipo de consulta")) }) {
            FORM_TABS.forEach { (tab, label) ->
                Button({
                    type(ButtonType.Button)
                    if (tab == mode) classes("on")
                    onClick { AppState.formMode = tab }
                }) {
                    Icon(TAB_ICONS.getValue(tab))
                    Text(t(label).removePrefix("📞").trim())
                }
            }
        }
        Div({ classes("f-head") }) {
            H3({ id("fTitle") }) { Text(t(mode.title)) }
            P({ classes("sub"); id("fSub") }) { Text(t(mode.subtitle)) }
        }
        Div({ classes("row") }) {
            Label(attrs = { classes("field") }) {
                Text(t("Nombre *"))
                Input(InputType.Text) {
                    name("nombre"); attr("required", ""); autoComplete(AutoComplete.name)
                    placeholder(t("Nombre y apellidos"))
                    value(request.name)
                    onInput { request = request.copy(name = it.value) }
                    if (Field.NAME in missing) style { property("border-color", "var(--magenta)") }
                    ref { focusTargets[Field.NAME] = it; onDispose { focusTargets.remove(Field.NAME) } }
                }
            }
            Label(attrs = { classes("field") }) {
                Text(t("Teléfono *"))
                Input(InputType.Tel) {
                    name("tel"); attr("required", ""); autoComplete(AutoComplete.tel)
                    placeholder("600 000 000")
                    value(request.phone)
                    onInput { request = request.copy(phone = it.value) }
                    if (Field.PHONE in missing) style { property("border-color", "var(--magenta)") }
                    ref { focusTargets[Field.PHONE] = it; onDispose { focusTargets.remove(Field.PHONE) } }
                }
            }
        }
        Div({ classes("row", "xtra"); hideUnless(mode.fullForm) }) {
            Label(attrs = { classes("field") }) {
                Text(t("Empresa"))
                Input(InputType.Text) {
                    name("empresa"); autoComplete(AutoComplete.organization)
                    placeholder(t("Nombre de tu negocio"))
                    value(request.company)
                    onInput { request = request.copy(company = it.value) }
                }
            }
            Label(attrs = { classes("field") }) {
                Text(t("Provincia"))
                Select({
                    name("provincia"); id("fProv")
                    onChange { request = request.copy(province = it.value ?: "") }
                }) {
                    Option("", { if (request.province.isEmpty()) selected() }) { Text(t("Selecciona…")) }
                    PROVINCES.forEach { p -> Option(p, { if (request.province == p) selected() }) { Text(p) } }
                }
            }
        }
        Div({ classes("field", "dist-only") }) {
            Text(t("Tipo de negocio"))
            Div({ classes("tchips") }) {
                BUSINESS_TYPES.forEach { type ->
                    Label(attrs = { classes("tchip") }) {
                        RadioInput(checked = request.businessType == type) {
                            name("tipo"); value(type)
                            onInput { request = request.copy(businessType = type) }
                        }
                        Span { Text(t(type)) }
                    }
                }
            }
        }
        Label(attrs = { classes("field", "xtra"); hideUnless(mode.fullForm) }) {
            Text(t("Email"))
            Input(InputType.Email) {
                name("email"); autoComplete(AutoComplete.email)
                placeholder("tu@empresa.com")
                value(request.email)
                onInput { request = request.copy(email = it.value) }
            }
        }
        Label(attrs = { classes("field", "xtra"); hideUnless(mode.fullForm) }) {
            Text(t("Mensaje"))
            TextArea(request.message) {
                name("msg"); rows(4)
                placeholder(t("Cuéntanos qué necesitas"))
                onInput { request = request.copy(message = it.value) }
            }
        }
        Label(attrs = { classes("check") }) {
            CheckboxInput(request.acceptsPrivacy) {
                attr("required", "")
                onInput { request = request.copy(acceptsPrivacy = it.value) }
                ref { focusTargets[Field.PRIVACY] = it; onDispose { focusTargets.remove(Field.PRIVACY) } }
            }
            Text(" ")
            Span {
                Text(t("He leído y acepto la") + " ")
                A("#") { Text(t("política de privacidad")) }
                Text(". *")
            }
        }
        Label(attrs = { classes("check") }) {
            CheckboxInput(request.wantsNews) { onInput { request = request.copy(wantsNews = it.value) } }
            Text(" ")
            Span { Text(t("Quiero recibir novedades y promociones de Vicab (opcional).")) }
        }
        Div({ classes("submit") }) {
            Button({ classes("btn", "btn-primary", "btn-lg"); type(ButtonType.Submit); id("fBtn") }) {
                Text(t(mode.button) + " "); Icon("arrow-r")
            }
            Small {
                Icon("lock") { attr("width", "15"); attr("height", "15") }
                Text(t("Tus datos están seguros"))
            }
        }
    }
}

@Composable
private fun FormSent(firstName: String) {
    Div({ classes("ok") }) {
        Div({ classes("ic") }) { Icon("check") }
        H3 { Text("${t("¡Gracias")}, $firstName!") }
        P({ style { property("color", "var(--muted)"); property("margin-top", "10px") } }) {
            Text(t("Hemos recibido tu solicitud. Te contactaremos en horario comercial."))
            Br()
            Text(t("¿Prefieres no esperar? Llámanos al") + " ")
            PhoneLink({ style { property("color", "var(--blue)"); property("font-weight", "700") } }) { Text(Company.PHONE_DISPLAY) }
            Text(".")
        }
    }
}

private val TAB_ICONS = mapOf(FormMode.CALL to "phone", FormMode.DEMO to "calendar", FormMode.DIST to "store")

private fun org.jetbrains.compose.web.attributes.AttrsScope<*>.hideUnless(visible: Boolean) {
    if (!visible) style { display(DisplayStyle.None) }
}
