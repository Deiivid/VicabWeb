# Vicab · Web (Kotlin Multiplatform + Compose HTML)

Web corporativa de Vicab hecha en Kotlin. Usa **Compose HTML**, que genera HTML y CSS reales en el navegador
(no un canvas), para que Google pueda leer el contenido y la página cargue rápido.

## Estructura

- **`shared/`**: contenido y lógica sin interfaz, en Kotlin común.
  - `Content.kt`: productos, catálogos, marcas, vídeos, reseñas, FAQ, provincias y datos de contacto.
  - `I18n.kt` y `Translations.kt`: los idiomas (es, ca/va, eu, gl, en). El texto en español es la clave.
  - `PdfPaging.kt`: la paginación del visor de catálogos.
  - Tests en `commonTest`.
- **`webApp/`**: la web con Compose HTML (solo para el target JS).
  - `App.kt`: punto de entrada, orden de las secciones y eventos globales.
  - `AppState.kt`: estado global (idioma, modo del formulario, pestañas, modales).
  - `sections/`: una función `@Composable` por cada bloque de la página.
  - `CatalogViewer.kt`: el visor de PDF (usa PDF.js, que se carga desde el CDN).
  - `resources/`: `index.html` (SEO, iconos SVG), `styles.css`, imágenes y PDFs.

## Comandos

| Qué | Comando |
| --- | --- |
| Desarrollo con recarga | `./gradlew :webApp:jsBrowserDevelopmentRun --continuous` |
| Versión para publicar | `./gradlew :webApp:jsBrowserDistribution` |
| Tests | `./gradlew :shared:jsNodeTest` |

La versión para publicar queda en `webApp/build/dist/js/productionExecutable/`. Esa carpeta se sube tal cual a
cualquier hosting estático (Netlify, Cloudflare Pages, Hostinger…).

## Despliegue

Cada push a `main` lanza `.github/workflows/deploy.yml`: pasa los tests, compila la versión de producción y la publica en
GitHub Pages: **https://deiivid.github.io/VicabWeb/**

## Pendiente (⚑ en el código)

- Revisión de las traducciones por hablantes nativos.
- Envío real del formulario: Formspree, Netlify Forms o similar (en `sections/Contact.kt`).
- Fotos reales de antes y después: se cambian en `BEFORE_AFTER` (`shared/.../Content.kt`).
- Confirmar las descripciones de Pizzi, Whale Spray y Ducasse.
- Textos legales: aviso legal, privacidad y cookies.
- Las fotos de la portada vienen del collage antiguo y tienen poca resolución.
