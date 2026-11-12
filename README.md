# ♟️ Ajedrez Maestro: Entrenador Táctico y Analizador Avanzado

[![Android](https://img.shields.io/badge/Platform-Android%207.0%2B-brightgreen.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blueviolet.svg)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Design-Material%203-gold.svg)](https://m3.material.io/)
[![Firebase](https://img.shields.io/badge/Backend-Firebase%20Firestore%20%26%20Auth-orange.svg)](https://firebase.google.com/)
[![Room Database](https://img.shields.io/badge/Local%20DB-Room%20SQLite-green.svg)](https://developer.android.com/training/data-storage/room)

---

## 📖 1. Introducción

**Ajedrez Maestro** es una aplicación móvil nativa para Android diseñada para entusiastas, jugadores de club y ajedrecistas avanzados que desean perfeccionar su cálculo táctico, dominar repertorios de aperturas y analizar partidas con un motor de evaluación posicional en tiempo real. 

La aplicación combina la calidez visual de un tablero clásico de maderas nobles (nogal y arce) con un diseño moderno bajo estándares **Material Design 3**, respuesta háptica inmersiva, base de datos local SQLite mediante **Room** para entrenamiento 100% offline, y sincronización en la nube mediante **Firebase Cloud Firestore** y **Google Sign-In**.

---

## 🔍 2. Descripción

El entrenamiento del ajedrez moderno exige retroalimentación inmediata, comprensión de patrones y constancia diaria. **Ajedrez Maestro** proporciona un entorno de aprendizaje estructurado donde cada ejercicio táctico evalúa la precisión del usuario, recalcula su puntuación Elo mediante el sistema de calificación oficial y almacena el progreso de forma persistente.

Gracias a su arquitectura híbrida:
- **Sin conexión:** Puedes resolver tácticas, practicar aperturas y analizar partidas en cualquier lugar sin depender de conexión a Internet.
- **Con conexión:** Al iniciar sesión con tu cuenta de Google mediante **Jetpack Credential Manager**, tus estadísticas, nivel Elo, récords y partidas guardadas se respaldan automáticamente en **Cloud Firestore**.

---

## ⚡ 3. Capacidades y Características Clave

### 🎯 3.1. Escalera de Retos Progresiva (1000 a 2400 Elo)
- **6 Niveles de Maestría:**
  1. *Nivel 1 (1000 - 1200 Elo) - Iniciación:* Mates de pasillo y bifurcaciones elementales.
  2. *Nivel 2 (1200 - 1400 Elo) - Jugador de Club:* Clavadas absolutas de alfil y tenedores centrales.
  3. *Nivel 3 (1400 - 1650 Elo) - Táctico Avanzado:* Ataques al descubierto y dobles amenazas dinámicas.
  4. *Nivel 4 (1650 - 1850 Elo) - Candidato a Maestro:* Sacrificios posicionales, atracciones y regalo griego (*Axh7+*).
  5. *Nivel 5 (1850 - 2100 Elo) - Maestro Nacional:* Redes complejas de mate, finales técnicos de torres (*Puente de Lucena*).
  6. *Nivel 6 (2100 - 2400 Elo) - Gran Maestro:* Maniobras de élite, sacrificios de dama y cálculo profundo.
- Medallas de superación, seguimiento de porcentaje de avance por nivel y selección directa de problemas.

### 🧩 3.2. Entrenador Táctico por Temas
- Biblioteca extensa de ejercicios clasificados por motivos estratégicos: *Tenedores, Clavadas, Ataque Descubierto, Redes de Mate, Sacrificios, Finales y Desviaciones*.
- Pistas dinámicas (*Hint*) con flechas doradas en el tablero.
- Solución guiada con visualización de la secuencia ganadora.
- Actualización automática del Elo táctico (+/- puntos) tras cada intento.

### 📚 3.3. Repertorio de Aperturas Magistrales
- Guía paso a paso con árboles de variantes y flechas de dirección:
  - **Defensa Siciliana (Variante Najdorf)**
  - **Apertura Española (Ruy López)**
  - **Gambito de Dama Declinado**
  - **Defensa Francesa**
  - **Defensa Caro-Kann**
  - **Defensa India de Rey**
- Explicación didáctica de los planes estratégicos para las piezas **Blancas** y **Negras**.

### 💻 3.4. Tablero de Análisis con Motor Integrado
- **Evaluación en tiempo real:** Barra gráfica de ventaja y puntuación en centipeones (`+1.4`, `-0.8`) o mates forzados (`+M2`).
- **Mejores variantes (PV):** Sugerencia de la jugada óptima calculada mediante algoritmo Minimax con poda Alfa-Beta y tablas pieza-casilla (PST).
- **Clasificación de jugadas:** Cada movimiento recibe una insignia de calidad (*Brillante*, *La mejor*, *Excelente*, *Buena*, *Imprecisión*, *Error*, *Grave error*).
- **Herramientas de análisis:** Volteo de tablero (*Flip*), flechas tácticas vectoriales en Canvas, carga de posiciones personalizadas mediante cadenas **FEN** y guardado de partidas anotadas con PGN.

### ☁️ 3.5. Autenticación y Sincronización en la Nube
- **Google Sign-In:** Inicio de sesión rápido y seguro con un toque mediante la API de **Credential Manager**.
- **Perfil de Jugador:** Avatar, nombre de usuario, correo electrónico y control de cierre de sesión.
- **Sincronización Híbrida:** Los datos se guardan localmente en Room y se replican en Cloud Firestore con reglas de seguridad de confianza cero (`request.auth.uid == userId`).

---

## 📥 4. Cómo Descargar desde GitHub

Para descargar el código fuente en tu equipo local mediante Git, abre una terminal y ejecuta:

```bash
# Clonar el repositorio
git clone https://github.com/tu-usuario/ajedrez-maestro.git

# Entrar al directorio del proyecto
cd ajedrez-maestro
```

Alternativamente, puedes descargar el archivo ZIP completo desde el botón **Code > Download ZIP** en la página del repositorio en GitHub y descomprimirlo en tu carpeta de preferencia.

---

## 🛠️ 5. Requerimientos del Sistema

### Para Desarrolladores / Compilación
| Componente | Requisito Mínimo | Requisito Recomendado |
| :--- | :--- | :--- |
| **Sistema Operativo** | Windows 10/11, macOS o Linux | macOS / Linux / Windows 11 |
| **Android Studio** | Android Studio Iguana o Ladybug (2024+) | Última versión estable |
| **Java Development Kit (JDK)** | OpenJDK 11 | OpenJDK 17 o 21 |
| **Android SDK Build-Tools** | SDK Platform 34 | SDK Platform 36 |
| **Gradle** | Versión 8.9+ (Gestionado vía Gradle Wrapper) | Incluido en el proyecto |

### Para Dispositivos Móviles (Usuarios Finales)
- **Sistema Operativo:** Android 7.0 (API nivel 24 - Nougat) o superior.
- **Servicios:** Google Play Services instalado (para inicio de sesión con Google Sign-In).
- **Almacenamiento:** ~40 MB de espacio disponible.

---

## ⚙️ 6. Configuración del Proyecto

### 6.1. Configuración de Firebase (Opcional para sincronización en la nube)
El proyecto incluye la integración de Firebase preconfigurada. Si deseas compilar con tu propio proyecto de Firebase:
1. Dirígete a la [Consola de Firebase](https://console.firebase.google.com/).
2. Crea un proyecto y agrega una aplicación Android con el Package Name / Application ID:
   ```text
   com.aistudio.chessmaster.vqkzyt
   ```
3. Registra tu huella digital SHA-1 de desarrollo. Puedes obtenerla ejecutando:
   ```bash
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```
4. Descarga el archivo `google-services.json` y colócalo en la carpeta `/app/google-services.json`.
5. Habilita **Cloud Firestore** y **Authentication > Google Sign-In** en la consola de Firebase.

### 6.2. Variables de Entorno
El archivo `.env.example` en la raíz contiene las referencias de configuración del entorno. Si utilizas el Secrets Gradle Plugin, crea un archivo `.env` basado en él si es necesario.

---

## 🚀 7. Cómo Compilar e Instalar

### Opción A: Desde Android Studio (Recomendado)
1. Abre **Android Studio**.
2. Selecciona **File > Open...** y elige la carpeta raíz del proyecto.
3. Espera a que Gradle sincronice las dependencias (`Sync Project with Gradle Files`).
4. Conecta tu teléfono Android mediante depuración USB o inicia un emulador de Android.
5. Haz clic en el botón verde **Run ('app')** (`Shift + F10`) para compilar e instalar la app directamente.

### Opción B: Desde la Línea de Comandos (Terminal)
Si tienes el SDK de Android configurado en tus variables de entorno:

```bash
# Compilar la aplicación en modo Debug
./gradlew assembleDebug

# El archivo APK generado se encontrará en:
# app/build/outputs/apk/debug/app-debug.apk

# Instalar directamente en un dispositivo o emulador conectado vía ADB
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📱 8. Breve Manual de Uso

### 🏠 Pestaña 1: Inicio (Dashboard)
- **Tarjeta de Nivel Elo:** Muestra tu Elo actual, rango alcanzado (*Aficionado, Club, Experto, Maestro*), récord máximo y racha de días activos.
- **Estado de la Nube:** Un banner superior indica si estás conectado con tu cuenta de Google (*Nube Activa*) o en *Modo Local*. Puedes tocarlo para acceder al inicio de sesión.
- **Accesos Rápidos:** Tarjetas directas hacia la *Escalera de Retos*, *Táctica por Temas*, *Repertorio de Aperturas* y el *Tablero de Análisis*.

### ⭐ Pestaña 2: Retos (Escalera Elo 1000-2400)
- Selecciona cualquiera de los **6 niveles de dificultad** en el carrusel superior.
- Cada nivel contiene retos con su correspondiente evaluación y motivo táctico.
- Los problemas superados muestran un distintivo verde **Superado**. Toca cualquier reto para jugarlo en el tablero interactivo.

### 🎯 Pestaña 3: Táctica por Temas
- Filtra por categorías tácticas (*Clavadas, Tenedores, Red de Mate, Sacrificios, etc.*).
- Mueve las piezas tocando la casilla de origen y la casilla de destino.
- Si necesitas ayuda, pulsa **Pista** para iluminar la maniobra con una flecha ámbar.
- Pulsa **Solución** si deseas ver la secuencia de jugadas ganadora.

### 🔍 Pestaña 4: Análisis con Motor
- **Modo libre:** Juega movimientos para blancas o negras en un tablero abierto.
- **Barra de evaluación:** Observa cómo oscila la ventaja en centipeones con cada jugada.
- **Jugar contra el motor:** Pulsa el botón de reproducción ▶️ para que el motor responda con la mejor jugada calculada.
- **Cargar FEN:** Pulsa el botón de carga para pegar cualquier posición FEN oficial.
- **Guardar partida:** Guarda posiciones o partidas completas con título y notas personales en la base de datos.

### 📊 Pestaña 5: Progreso y Perfil
- **Perfil de Usuario:** Muestra la cuenta de Google vinculada y permite cerrar sesión de forma segura.
- **Gráfica de efectividad:** Visualiza tu porcentaje de acierto desglosado por motivo táctico (*Tenedores, Clavadas, Mates, etc.*).
- **Partidas Guardadas:** Listado de todas tus partidas analizadas con opción de consulta o eliminación.
- **Historial de Sesiones:** Registro cronológico de entrenamientos con fecha, duración y variación de Elo (+/-).

---

## 🏛️ 9. Estructura del Proyecto

```text
├── app/
│   ├── src/main/
│   │   ├── java/com/example/
│   │   │   ├── MainActivity.kt               # Actividad principal y navegación Compose
│   │   │   ├── auth/
│   │   │   │   └── AuthManager.kt            # Autenticación Google Sign-In con CredentialManager
│   │   │   ├── chess/
│   │   │   │   ├── engine/                   # Motor de reglas, jugadas legales y evaluación Minimax
│   │   │   │   │   ├── ChessGame.kt
│   │   │   │   │   └── ChessEvaluator.kt
│   │   │   │   ├── model/                    # Modelos de dominio (Piece, Square, Move, EvalResult)
│   │   │   │   │   └── ChessModels.kt
│   │   │   │   ├── data/
│   │   │   │   │   ├── AppDatabase.kt        # Base de datos Room SQLite
│   │   │   │   │   ├── ChessRepository.kt    # Repositorio unificado local + nube
│   │   │   │   │   ├── dao/ChessDao.kt       # Consultas reactivas con Flow
│   │   │   │   │   ├── entity/               # Entidades de Room
│   │   │   │   │   ├── firebase/             # Servicio de sincronización Cloud Firestore
│   │   │   │   │   │   └── FirestoreSyncService.kt
│   │   │   │   │   └── dataset/              # Catálogo de problemas (1000-2400) y aperturas
│   │   │   │   │       ├── TacticsDataset.kt
│   │   │   │   │       └── OpeningsDataset.kt
│   │   │   │   └── ui/
│   │   │   │       ├── components/           # Tablero de madera, piezas Staunton, barra de eval
│   │   │   │       │   ├── ChessBoardView.kt
│   │   │   │       │   ├── ChessPieceDrawable.kt
│   │   │   │       │   └── EvaluationBarView.kt
│   │   │   │       ├── screens/              # Pantallas de la aplicación
│   │   │   │       │   ├── DashboardScreen.kt
│   │   │   │       │   ├── ChallengesScreen.kt
│   │   │   │       │   ├── TacticsScreen.kt
│   │   │   │       │   ├── OpeningsScreen.kt
│   │   │   │       │   ├── AnalysisScreen.kt
│   │   │   │       │   ├── StatisticsScreen.kt
│   │   │   │       │   └── AuthScreen.kt
│   │   │   │       └── viewmodel/            # ViewModel con StateFlow reactivo
│   │   │   │           └── ChessViewModel.kt
│   │   │   └── ui/theme/                     # Sistema de diseño, fuentes Cinzel/Outfit y colores M3
│   │   ├── res/                              # Recursos gráficos, fuentes (.ttf), iconos y strings
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts                      # Dependencias del módulo app
├── firestore.rules                           # Reglas de seguridad de Cloud Firestore
├── firebase-blueprint.json                   # Esquema de datos de colecciones Firestore
├── settings.gradle.kts
└── README.md                                 # Documentación del proyecto
```

---

## 📄 10. Licencia

Este proyecto ha sido desarrollado como una herramienta educativa y deportiva de código abierto. Siéntete libre de adaptarlo, mejorarlo y contribuir al desarrollo de mejores herramientas de ajedrez para la comunidad de habla hispana.
