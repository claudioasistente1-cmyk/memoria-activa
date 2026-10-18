# Memoria Activa

Aplicacion Android nativa, liviana y offline de juegos de memoria, atencion y logica,
pensada para adultos mayores. Sin login, sin publicidad, sin backend propio y sin Google
Play Store: se distribuye por APK y se actualiza sola desde GitHub Releases.

## Que incluye

- **5 juegos completos**: Encontrar parejas, Recordar secuencias, Memoria visual,
  Calculo mental y Patrones y logica.
- **Desafio diario**: 3 actividades elegidas automaticamente cada dia, con dificultad
  adaptada al rendimiento y sin penalizaciones por dias sin jugar.
- **Progreso local**: historial, estadisticas por juego y dias activos (Room).
- **Recordatorios locales** configurables (WorkManager + NotificationManager), sin backend.
- **Actualizaciones automaticas** desde GitHub Releases: manifiesto `update.json`,
  comparacion de `versionCode`, descarga por HTTPS, verificacion SHA-256 e instalador del
  sistema.
- **Contenido remoto** (preguntas, series, simbolos, colores) con validacion y fallback
  local. Nunca se ejecuta codigo remoto.
- **Privacidad total**: todo se guarda en el telefono. No hay analytics ni SDK de
  terceros.

Stack: Kotlin, Jetpack Compose, Material 3, MVVM, Room, DataStore, WorkManager, OkHttp y
Moshi. `minSdk 28` (Android 9), `targetSdk 34`.

---

## Requisitos

Para desarrollar:

- Android Studio (Ladybug o superior) o JDK 17 + Android SDK 34.
- No hace falta tener Gradle instalado: el proyecto incluye el wrapper.

Para publicar:

- Repositorio en GitHub (cuenta elegida al inicio: `claudioasistente1-cmyk`).
- Un keystore de firma guardado **fuera** del repositorio.

---

## Estructura

```
app/src/main/java/com/claudio/memoria/
  data/           Room, DataStore, contenido remoto, red y repositorios
  domain/         modelos, motores puros de cada juego y generador del desafio diario
  ui/             tema, navegacion, pantallas, ViewModels y las 5 interfaces de juego
  notifications/  canales y construccion de notificaciones
  update/         manifiesto, descarga, verificacion SHA-256 e instalador
  work/           recordatorio diario, chequeo de actualizaciones y reinicio
app/src/main/assets/content.json     contenido local inicial (fallback offline)
docs/                                lo que se publica en GitHub Pages
.github/workflows/build-release.yml  compila, prueba, firma y publica
scripts/                             generacion de keystore y publicacion
```

---

## Compilar localmente

```powershell
.\gradlew assembleDebug
```

La APK queda en `app/build/outputs/apk/debug/`. Para una release firmada localmente,
defini antes las variables de entorno `KEYSTORE_FILE`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`
y `KEY_PASSWORD` y ejecuta:

```powershell
.\gradlew assembleRelease
```

> Este equipo no tiene JDK/Android SDK instalado. El camino recomendado es dejar que
> GitHub Actions compile (ver mas abajo); la seccion local sirve para tu maquina de
> desarrollo o para Android Studio.

---

## Instalar la APK sin Play Store

1. Descarga la APK desde la seccion **Releases** del repositorio en el telefono.
2. Al abrirla, Android va a pedir permiso para instalar apps de esa fuente
   ("Instalar apps desconocidas"). Activalo solo para el navegador o la app de archivos.
3. Toca **Instalar**. Si ya tenias una version anterior, se actualiza conservando todo el
   progreso (mismo `applicationId` y misma firma).
4. Abri **Memoria Activa** y acepta el permiso de notificaciones para recibir el
   recordatorio diario.

Para actualizaciones futuras no hace falta descargar nada a mano: la app avisa sola
cuando hay una version nueva.

---

## Firma: keystore y secrets

La firma es lo que permite actualizar la app sin desinstalarla. **Si cambia la firma,
Android no permite la actualizacion.** Generala una sola vez y guardala.

En Windows, con un JDK instalado:

```powershell
cd scripts
.\generate-keystore.ps1 -Alias memoria
```

El script crea `release.jks` y `release.jks.base64`. Despues carga estos secrets en
GitHub (`Settings > Secrets and variables > Actions > New repository secret`):

| Secret | Valor |
| --- | --- |
| `SIGNING_KEYSTORE_BASE64` | contenido de `release.jks.base64` |
| `KEYSTORE_PASSWORD` | contrasena elegida al generar el keystore |
| `KEY_ALIAS` | alias elegido (por defecto `memoria`) |
| `KEY_PASSWORD` | misma contrasena del keystore |

Guarda `release.jks` y la contrasena en un lugar privado. No se suben al repositorio
(estan en `.gitignore`).

---

## Activar GitHub Pages

El workflow publica `docs/` en la rama `gh-pages` e intenta habilitar Pages solo. Si no
lo logra, hace falta una vez:

1. **Settings > Pages**.
2. Source: **Deploy from a branch**.
3. Branch: `gh-pages`, carpeta `/ (root)`. Guardar.

Quedan disponibles:

- `https://<usuario>.github.io/memoria-activa/update.json`
- `https://<usuario>.github.io/memoria-activa/content.json`

La app apunta a esas URLs. El workflow las calcula a partir del repositorio real al
compilar, asi que no hay que editar nada si el repo tiene otro nombre.

---

## Publicar una nueva version

1. Actualiza `version.properties` o deja que el script lo haga.
2. Opcional: escribi las novedades en `RELEASE_NOTES.md` (se usan en el release y en
   `update.json`).
3. Ejecuta:

```powershell
cd scripts
.\publish.ps1 -Version 1.0.1
```

El script actualiza `versionCode`, commitea, crea el tag `v1.0.1` y lo pushea. GitHub
Actions entonces:

1. Compila y corre los tests.
2. Firma la APK con el keystore de los secrets.
3. Crea el release `v1.0.1` y sube `memoria-activa-1.0.1.apk`.
4. Genera `docs/update.json` con `versionCode`, `versionName`, URL, SHA-256 y tamano.
5. Publica `docs/` en GitHub Pages.

`versionCode` se calcula como `mayor*10000 + menor*100 + parche`. Siempre tiene que
crecer. No reutilices tags ni bajes la version.

Tambien podes disparar el workflow a mano desde **Actions > Build and Release APK >
Run workflow** (sin publicar release, solo para probar el build).

---

## Como funciona la actualizacion

- La app consulta `update.json` al iniciar y una vez por dia (WorkManager, solo con red).
- Si `versionCode` remoto > `versionCode` instalado, muestra una notificacion
  **"Hay una nueva actualizacion disponible"** (una sola vez por version).
- Al tocarla abre una pantalla con version, novedades, **Actualizar** y **Mas tarde**.
- **Actualizar** descarga la APK por HTTPS, verifica el SHA-256 y, solo si coincide, abre
  el instalador del sistema. Nunca instala en silencio.
- Si falta el permiso "instalar apps desconocidas", la pantalla explica como darlo.
- Si no hay conexion, el chequeo falla en silencio y se reintenta mas tarde.

### Manifiesto `update.json`

```json
{
  "versionCode": 10001,
  "versionName": "1.0.1",
  "apkUrl": "https://github.com/<usuario>/memoria-activa/releases/download/v1.0.1/memoria-activa-1.0.1.apk",
  "sha256": "64 caracteres hexadecimales",
  "releaseNotes": "Novedades de la version",
  "apkSizeBytes": 12345678,
  "minSdk": 28,
  "publishedAt": "2026-10-18T12:00:00Z"
}
```

---

## Contenido remoto

El contenido editable vive en JSON (preguntas, operaciones, series, simbolos, colores,
dificultades). El archivo local `app/src/main/assets/content.json` es el fallback y se
copia a `docs/content.json` en cada release.

Flujo:

1. La app carga el contenido local.
2. Si existe una copia cacheada valida y mas nueva, la usa.
3. Con internet intenta descargar `content.json`.
4. Valida `schemaVersion`, `minAppVersionCode` y campos minimos.
5. Si es valido y tiene `contentVersion` mayor, lo guarda y lo aplica.
6. Si falla, sigue con el contenido anterior. Nunca borra progreso ni ejecuta codigo.

Para publicar contenido nuevo sin subir una APK: edita `app/src/main/assets/content.json`,
subi `contentVersion` y corre el workflow manualmente o publica una version. El workflow
copia el archivo a Pages.

### Esquema resumido

```json
{
  "schemaVersion": 1,
  "contentVersion": 2,
  "minAppVersionCode": 1,
  "pairs": { "symbols": ["🍎", "🍌"] },
  "visualMemory": { "symbols": ["🍎", "⭐"] },
  "sequence": { "colors": ["#E05C5C", "#4C8DD9"], "paletteSize": 4 },
  "math": [
    { "level": 1, "questions": [ { "text": "2 + 2", "answer": 4, "options": [3, 4, 5, 6] } ] }
  ],
  "series": [
    { "level": 1, "items": [ { "sequence": [2, 4, 6, 8], "answer": 10 } ] }
  ],
  "texts": {
    "homeGreeting": "Hola",
    "dailyIntro": "Tus ejercicios de hoy",
    "encouragement": "Cada dia entrenamos juntos."
  }
}
```

Reglas:

- `schemaVersion` debe ser `1`; si no, se descarta.
- `minAppVersionCode` no puede superar el `versionCode` instalado.
- Los textos y datos son contenido, no codigo.
- Un juego nuevo con logica distinta requiere una APK nueva.

---

## Tests

```powershell
.\gradlew test
```

Cubren:

- Invariantes de los motores de los 5 juegos.
- Generacion deterministica del desafio diario y dificultad adaptativa.
- Validacion del manifiesto de actualizacion.
- Parseo del contenido remoto con valores por defecto.
- Calculo del proximo recordatorio.

---

## Privacidad

- No hay cuentas, analytics, publicidad ni rastreadores.
- Progreso, historial y configuracion viven en Room/DataStore en el telefono.
- Las unicas conexiones de red son: descargar contenido, comprobar actualizaciones y
  bajar la APK, siempre por HTTPS y a URLs fijas de GitHub.

---

## Decisiones y limites del MVP

- Sin backend propio: GitHub Releases + Pages son suficientes y gratuitos.
- Sin Hilt/Koin: inyeccion manual simple (`AppContainer`) para mantener el APK chico.
- Sin WebView ni motor de juegos: todo Compose.
- La dificultad se adapta con la precision de la ultima partida (sube con >= 75%,
  baja con < 45%).
- Los recordatorios usan WorkManager: pueden demorarse algunos minutos y no piden
  permisos de alarma exacta.
- Los juegos nuevos o cambios de logica requieren APK; el contenido remoto solo cambia
  datos y configuraciones.

---

## Orden recomendado para dejarla andando

1. Crear el repositorio `memoria-activa` en la cuenta elegida.
2. Generar el keystore y cargar los 4 secrets.
3. Push del proyecto.
4. Ejecutar **Actions > Build and Release APK > Run workflow** para validar el build.
5. Instalar el APK del artifact en el telefono.
6. Publicar la primera version con `scripts/publish.ps1 -Version 1.0.0`.
7. Activar GitHub Pages si el workflow no pudo.
8. Instalar en los celulares de tus padres y configurar el horario del recordatorio.

> Memoria Activa es un entretenimiento y un habito de estimulacion. No es un tratamiento
> medico ni promete prevenir enfermedades cognitivas.
