# DOCUMENTACIÓN COMPLETA — SplitApp

**Gestión de gastos compartidos para grupos · Aplicación Android nativa**

**Autor:** Javier Aguilera Sánchez  
**Ciclo Formativo:** Desarrollo de Aplicaciones Multiplataforma (DAM)  
**Fecha:** Junio de 2026

---

## Índice general

- [PARTE I — MEMORIA DEL PROYECTO](#parte-i--memoria-del-proyecto)
- [PARTE II — DOCUMENTACIÓN TÉCNICA](#parte-ii--documentación-técnica)
- [PARTE III — MANUAL DE INSTALACIÓN](#parte-iii--manual-de-instalación)
- [PARTE IV — MANUAL DE USUARIO](#parte-iv--manual-de-usuario)

---

# PARTE I — MEMORIA DEL PROYECTO

## 1. Introducción

### 1.1 Justificación del proyecto

La gestión del dinero entre grupos de personas constituye una de las fricciones más habituales en la vida cotidiana. Situaciones como un viaje entre amigos, el reparto de la compra en un piso compartido, la organización de una cena o la contribución a un regalo colectivo generan con frecuencia pequeñas deudas cruzadas que, sin un sistema de seguimiento, se acumulan y dan lugar a malentendidos o conflictos innecesarios.

El problema no reside únicamente en recordar quién pagó qué, sino en calcular correctamente el saldo neto de cada persona cuando los gastos se han repartido de forma irregular, han participado distintos subconjuntos de personas en distintos gastos y el número de transferencias necesarias para saldarlo todo supone un esfuerzo desproporcionado respecto al valor real de las deudas.

La idea de SplitApp surgió de la experiencia directa con este tipo de situaciones en un entorno de convivencia compartida. Las soluciones empleadas hasta entonces (hojas de cálculo compartidas, notas de texto o aplicaciones genéricas de gestión de dinero) resultaban inadecuadas por distintos motivos: falta de sincronización en tiempo real, dependencia de una única persona para actualizar los datos, ausencia de un mecanismo automático de simplificación de deudas o, simplemente, dificultad de uso para personas sin perfil técnico.

El proyecto nace con la voluntad de dar una respuesta práctica y bien diseñada a esta necesidad cotidiana, mediante una aplicación móvil nativa para Android que resuelva el problema de extremo a extremo.

### 1.2 Análisis comparativo de aplicaciones similares

| Aplicación | Fortalezas | Debilidades | Modelo de negocio |
|---|---|---|---|
| **Splitwise** | Amplia base de usuarios, múltiples divisas, historial detallado | Interfaz compleja, funciones avanzadas de pago, principalmente en inglés | Freemium |
| **Tricount** | Simple, popular en Europa, sin registro obligatorio | Sin sincronización entre usuarios, sin cuentas vinculadas | Gratuita con publicidad |
| **Settle Up** | Buena UX, soporte multimoneda, exportación | Funciones clave bloqueadas en plan de pago | Freemium |
| **Excel / manual** | Sin dependencia de terceros | Sin tiempo real, propensa a errores | — |

De este análisis se extraen varias conclusiones: las aplicaciones más completas tienen una curva de aprendizaje elevada y orientan sus funcionalidades avanzadas a un modelo de pago; las aplicaciones simples renuncian a la sincronización en tiempo real. Ninguna combina de forma satisfactoria simplicidad, sincronización en tiempo real, algoritmo de simplificación de deudas, chat integrado e interfaz en español de forma completamente gratuita.

### 1.3 Tendencias del mercado

- **Digitalización de los pagos entre particulares:** la adopción de Bizum ha normalizado las transferencias instantáneas, creando demanda de herramientas de cálculo y justificación.
- **Aplicaciones en la nube con sincronización en tiempo real:** Firebase ha democratizado el desarrollo de experiencias colaborativas sin backend propio.
- **Kotlin + Jetpack Compose:** el ecosistema Android nativo maduro permite desarrollar interfaces de alta calidad con menos código.
- **Economía colaborativa:** la extensión del modelo de piso compartido y los viajes en grupo mantienen la demanda de este tipo de herramientas.

### 1.4 Beneficios y expectativas del proyecto

- **Reducción de fricción económica:** centralizar el registro y automatizar el cálculo de balances elimina los malentendidos.
- **Optimización de la liquidación:** el algoritmo de simplificación reduce al mínimo las transferencias necesarias.
- **Acceso inmediato:** la sincronización en tiempo real garantiza que todos los miembros tienen siempre datos actualizados.
- **Comunicación integrada:** el chat de grupo elimina la necesidad de aplicaciones externas para coordinar pagos.
- **Trazabilidad:** el historial de gastos exportable en CSV permite un registro permanente y verificable.

---

## 2. Descripción del proyecto

### 2.1 Tipo de proyecto

SplitApp es una **aplicación móvil nativa para Android**, desarrollada en Kotlin con Jetpack Compose. Se clasifica como una aplicación de productividad personal con componente colaborativo en tiempo real, respaldada por Firebase como backend en la nube. No incluye el desarrollo de un backend propio: la lógica de servidor se delega completamente en los servicios gestionados de Firebase.

### 2.2 Características principales

**Gestión de identidad y autenticación**
- Registro de nuevos usuarios con nombre de usuario, número de teléfono, correo electrónico y contraseña.
- Inicio de sesión mediante correo electrónico y contraseña a través de Firebase Authentication.
- Gestión del perfil: actualización de nombre de usuario, número de teléfono y fotografía de perfil.
- Cierre de sesión con invalidación inmediata de la sesión.

**Gestión de grupos**
- Creación de grupos con nombre, descripción opcional y selector de moneda (€, $, £, etc.).
- Incorporación de miembros mediante búsqueda por nombre de usuario y sistema de invitaciones: el destinatario acepta o rechaza la solicitud. Los usuarios pueden bloquear invitaciones de otros usuarios específicos.
- Unión a grupos mediante enlace de invitación (Deep Link con esquema `splitapp://join`).
- Visualización de la lista de grupos con balance individual en cada uno.
- Abandono y eliminación de grupos, bloqueados automáticamente si existe algún saldo pendiente no nulo en el grupo.

**Chat de grupo en tiempo real**
- Mensajería instantánea dentro de cada grupo, sincronizada mediante Firestore.
- Los mensajes propios se muestran a la derecha; los ajenos a la izquierda con la foto de perfil y nombre del autor.
- Scroll automático al último mensaje. Semántica accesible para lectores de pantalla.

**Gestión de gastos**
- Registro de gastos con concepto, importe, moneda del grupo, pagador y participantes.
- Distribución equitativa (partes iguales con corrección de redondeo justa) o personalizada (importes exactos o porcentajes).
- Visualización del historial con gráfico de tarta por concepto.
- Solo el miembro que pagó el gasto puede eliminarlo.

**Gestión de balances y liquidación**
- Cálculo automático del saldo neto de cada miembro en tiempo real.
- Pantalla "Quién me debe": vista global de todos los usuarios con deuda hacia el usuario actual, agrupados por grupo.
- Algoritmo de simplificación de deudas que calcula el conjunto mínimo de transferencias.
- Liquidación con confirmación mutua: ambas partes deben confirmar para que los saldos queden a cero.

**Exportación**
- Generación de un fichero CSV con el historial completo de gastos del grupo.
- En Android 10 o superior, el fichero se guarda directamente en la carpeta pública **Descargas** del dispositivo.

**Accesibilidad**
- Áreas táctiles mínimas de 48 dp en todos los controles interactivos.
- Descripciones de contenido (`contentDescription`) en avatares e iconos para TalkBack.
- Semántica consolidada en las burbujas de chat para navegación con lector de pantalla.

**Internacionalización y temas**
- Interfaz disponible en español e inglés, con cambio inmediato desde el perfil sin reiniciar la sesión.
- Soporte completo para tema oscuro y claro del sistema, con colores adaptativos de Material Design 3.

### 2.3 Usuarios destinatarios

- **Perfil demográfico:** adultos jóvenes de entre 18 y 40 años (aunque accesible para cualquier rango de edad).
- **Contexto de uso:** pisos compartidos, viajes en grupo, cenas, actividades de ocio colectivo.
- **Requisito de dispositivo:** smartphone Android 6.0+ con conexión a Internet.

---

## 3. Objetivos del proyecto

### 3.1 Objetivo general

Desarrollar una aplicación móvil nativa para Android que permita a grupos de personas gestionar de forma sencilla, precisa y colaborativa sus gastos compartidos, calculando automáticamente los balances individuales y facilitando la liquidación óptima de las deudas mediante sincronización en tiempo real.

### 3.2 Objetivos específicos

**OE-01.** Implementar un sistema de autenticación seguro con Firebase Authentication.

**OE-02.** Diseñar un modelo de datos consistente en Cloud Firestore con relaciones correctamente modeladas y consistencia garantizada mediante transacciones atómicas.

**OE-03.** Implementar la gestión completa de grupos, miembros e invitaciones.

**OE-04.** Desarrollar el subsistema de registro y distribución de gastos (equitativo y personalizado).

**OE-05.** Garantizar la exactitud de los cálculos financieros usando `Long` en céntimos y distribución justa sin errores de redondeo.

**OE-06.** Implementar el algoritmo de simplificación de deudas con cobertura de pruebas unitarias.

**OE-07.** Desarrollar un chat de grupo en tiempo real integrado en la pantalla de detalle del grupo.

**OE-08.** Aplicar una arquitectura limpia y mantenible (Clean Architecture + MVVM).

**OE-09.** Ofrecer una experiencia de usuario accesible y coherente con Material Design 3.

**OE-10.** Implementar la exportación del historial de gastos en formato CSV a la carpeta Descargas.

**OE-11.** Proporcionar internacionalización completa en español e inglés.

**OE-12.** Producir documentación técnica y de usuario completa.

---

## 4. Alcance del proyecto

### 4.1 Qué incluye el proyecto

- La aplicación Android completa, empaquetada como APK.
- Modelo de datos en Cloud Firestore con reglas de seguridad.
- Configuración de Firebase Authentication, Firestore y Storage.
- Documentación técnica, manual de instalación, manual de usuario y memoria del proyecto.

### 4.2 Límites y restricciones

**Funcionalidades no implementadas:**
- Procesamiento de pagos reales (la app registra y calcula, pero no realiza transferencias bancarias).
- Edición de gastos ya registrados (solo se pueden eliminar y recrear).
- Notificaciones push (Firebase Cloud Messaging no integrado).
- Soporte para iOS o plataformas web.
- Historial de liquidaciones pasadas.

**Restricciones técnicas:**
- Requiere conexión a Internet activa para todas las operaciones.
- Rendimiento óptimo garantizado para grupos de hasta 20 miembros.
- Versión mínima de Android soportada: 6.0 (API 23).

---

## 5. Requisitos del proyecto

### 5.1 Requisitos funcionales

| ID | Requisito | Prioridad |
|---|---|---|
| RF-01 | Registro de nuevos usuarios con nombre de usuario, teléfono, correo y contraseña | Alta |
| RF-02 | Inicio de sesión mediante correo y contraseña | Alta |
| RF-03 | Actualización de nombre de usuario, teléfono y fotografía de perfil | Media |
| RF-04 | Crear grupos con nombre, descripción y moneda | Alta |
| RF-05 | Mostrar lista de grupos del usuario con su saldo actual | Alta |
| RF-06 | Añadir miembros a un grupo mediante sistema de invitaciones por nombre de usuario | Alta |
| RF-07 | Permitir al destinatario aceptar, rechazar o bloquear invitaciones | Alta |
| RF-08 | Unirse a un grupo mediante Deep Link | Media |
| RF-09 | Abandonar un grupo, bloqueado si hay saldos pendientes | Media |
| RF-10 | Eliminar un grupo (solo creador), bloqueado si hay saldos pendientes | Media |
| RF-11 | Registrar gastos con concepto, importe, pagador y participantes | Alta |
| RF-12 | Distribución equitativa con corrección de redondeo justo | Alta |
| RF-13 | Distribución personalizada por importes exactos o porcentajes | Alta |
| RF-14 | Validar en tiempo real que la suma de cuotas coincida con el importe total | Alta |
| RF-15 | Calcular y mostrar el balance neto de cada miembro en tiempo real | Alta |
| RF-16 | Garantizar que la suma de todos los balances del grupo es siempre cero | Alta |
| RF-17 | Calcular el conjunto mínimo de transferencias para liquidar todos los saldos | Alta |
| RF-18 | Liquidación de deudas con confirmación mutua de ambas partes | Alta |
| RF-19 | Solo el miembro que pagó un gasto puede eliminarlo | Alta |
| RF-20 | Los cambios se reflejan en tiempo real para todos los miembros | Alta |
| RF-21 | Chat de grupo en tiempo real dentro de cada grupo | Alta |
| RF-22 | Pantalla global "Quién me debe" con todos los deudores del usuario | Media |
| RF-23 | Exportar historial de gastos a CSV guardado en la carpeta Descargas | Media |
| RF-24 | Interfaz disponible en español e inglés con cambio inmediato desde el perfil | Media |
| RF-25 | Soporte para tema oscuro y claro del sistema | Media |
| RF-26 | Áreas táctiles mínimas de 48 dp; descripciones para lectores de pantalla | Media |
| RF-27 | No exponer el correo electrónico de un usuario en los resultados de búsqueda | Alta |

### 5.2 Requisitos técnicos

| ID | Requisito | Categoría |
|---|---|---|
| RT-01 | Android 6.0 (API 23) o superior | Compatibilidad |
| RT-02 | Kotlin con Jetpack Compose | Plataforma |
| RT-03 | Firebase Authentication para autenticación | Seguridad |
| RT-04 | Cloud Firestore para persistencia | Persistencia |
| RT-05 | Firebase Storage para imágenes de perfil | Almacenamiento |
| RT-06 | Importes monetarios como `Long` en céntimos | Precisión |
| RT-07 | Operaciones multi-documento mediante transacciones atómicas de Firestore | Consistencia |
| RT-08 | Clean Architecture con capas datos, dominio y presentación | Arquitectura |
| RT-09 | ViewModels con `StateFlow` y patrón MVVM | Arquitectura |
| RT-10 | Reglas de seguridad Firestore para control de acceso en servidor | Seguridad |
| RT-11 | Exportación CSV en carpeta Descargas (MediaStore API 29+) | Almacenamiento |
| RT-12 | i18n con recursos de cadenas en `values/` y `values-en/` | Localización |

### 5.3 Requisitos legales y normativos

**RGPD / GDPR:** SplitApp recopila nombre de usuario, teléfono, correo electrónico y fotografía de perfil. Estos datos se almacenan en Firebase (Google), en centros de datos del Espacio Económico Europeo. El tratamiento cumple los principios de licitud (consentimiento en el registro), minimización de datos, exactitud (el usuario puede modificar sus datos) y limitación de la finalidad (uso exclusivo para la prestación del servicio).

**LOPDGDD:** Como normativa nacional complementaria al RGPD, se garantizan los derechos de acceso, rectificación, supresión y portabilidad.

**Licencias de software de terceros:** todos los componentes utilizados (Kotlin, Jetpack Compose, Firebase SDK, Material Design 3, Coil) están bajo la licencia Apache License 2.0, compatible con proyectos académicos.

---

## 6. Planificación del proyecto

### 6.1 Estructura de tareas

**Fase 1 — Análisis y planificación:** definición del problema, análisis comparativo, elección del stack, requisitos, cronograma y gestión de riesgos.

**Fase 2 — Diseño:** modelo de datos en Firestore, arquitectura del sistema, wireframes, casos de uso, reglas de seguridad.

**Fase 3 — Implementación del núcleo:** configuración del entorno, autenticación, repositorios base, gestión de grupos y sistema de balances.

**Fase 4 — Funcionalidades avanzadas:** registro de gastos, algoritmo de simplificación, liquidación con confirmación mutua, exportación CSV, perfil con foto y teléfono, chat de grupo, pantalla "Quién me debe", sistema de invitaciones, i18n, modo oscuro, accesibilidad.

**Fase 5 — Pruebas y corrección:** pruebas unitarias, de integración, de rendimiento, de seguridad y de usabilidad.

**Fase 6 — Documentación y entrega:** documentación técnica, manual de usuario, manual de instalación, memoria, presentación.

### 6.2 Cronograma

| Fase | Duración estimada | Horas (aprox.) |
|---|---|---|
| F1 — Análisis y planificación | 3 semanas | 30 h |
| F2 — Diseño | 3 semanas | 35 h |
| F3 — Implementación del núcleo | 5 semanas | 80 h |
| F4 — Funcionalidades avanzadas | 7 semanas | 110 h |
| F5 — Pruebas y corrección | 5 semanas | 50 h |
| F6 — Documentación y entrega | 7 semanas | 55 h |
| **Total** | **~22 semanas** | **~360 h** |

### 6.3 Recursos necesarios

**Hardware:** ordenador Windows 10+ con mínimo 16 GB RAM, dispositivo Android físico opcional.

**Software:** Android Studio Meerkat, JDK 17, Git, cuenta de Google para Firebase Console.

**Servicios cloud:** Firebase Authentication, Cloud Firestore y Firebase Storage, todos bajo el plan gratuito Spark para el volumen de un proyecto académico.

---

## 7. Plan de gestión de riesgos

### 7.1 Matriz de riesgos

| ID | Riesgo | Prob. | Impacto | Exposición |
|---|---|:---:|:---:|:---:|
| R-01 | Cambios incompatibles en APIs de Firebase | 1 | 3 | 3 |
| R-02 | Superación del límite gratuito de Firebase | 1 | 2 | 2 |
| R-03 | Pérdida de consistencia de datos bajo acceso concurrente | 2 | 3 | 6 |
| R-04 | Errores de precisión en cálculos financieros | 2 | 3 | 6 |
| R-05 | Acceso no autorizado a datos de usuarios | 1 | 3 | 3 |
| R-06 | Desviación del cronograma por subestimación de complejidad | 3 | 2 | 6 |
| R-07 | Incompatibilidad con versiones antiguas de Android | 2 | 2 | 4 |

### 7.2 Acciones de mitigación

| ID | Acción de mitigación |
|---|---|
| R-03, R-04 | Uso de `Long` en céntimos; transacciones atómicas Firestore; tests unitarios del invariante de suma nula |
| R-05 | Reglas de seguridad Firestore evaluadas en servidor; validación en múltiples capas |
| R-06 | Priorización MoSCoW; revisión semanal del progreso; reducción de alcance en funcionalidades de baja prioridad |
| R-01 | Fijación de versiones con Firebase BOM; seguimiento del changelog antes de actualizar |
| R-07 | `minSdk` 23 (>95% de dispositivos activos); pruebas en emuladores con API 23, 28, 33 y 35 |

---

## 8. Diseño

### 8.1 Flujo de navegación

```
Pantalla de bienvenida
    ├── Inicio de sesión → Pantalla principal (lista de grupos)
    │       ├── Crear grupo (diálogo)
    │       ├── Acceder a grupo → Detalle del grupo
    │       │       ├── Pestaña Balances
    │       │       │       └── Liquidar deuda (diálogo con confirmación mutua)
    │       │       ├── Pestaña Gastos
    │       │       │       ├── Añadir gasto (diálogo)
    │       │       │       └── Eliminar gasto (solo pagador, con confirmación)
    │       │       └── Pestaña Chat
    │       │               └── Mensajería en tiempo real
    │       ├── Pantalla "Quién me debe"
    │       └── Perfil (diálogo: nombre, teléfono, foto, idioma)
    └── Registro → Pantalla principal
```

### 8.2 Especificaciones técnicas

#### Stack tecnológico

| Tecnología | Versión | Función |
|---|---|---|
| Kotlin | 2.2.10 | Lenguaje de programación principal |
| Jetpack Compose BOM | 2026.02.01 | Framework de UI declarativa |
| Material Design 3 | (vía Compose BOM) | Sistema de diseño visual |
| Navigation Compose | 2.8.5 | Gestión de la navegación entre pantallas |
| Firebase BOM | 33.4.0 | Plataforma de backend en la nube |
| Kotlinx Coroutines | 1.9.0 | Programación asíncrona y reactiva |
| Coil | 2.x | Carga y caché de imágenes |
| minSdk | 23 (Android 6.0) | Versión mínima de Android soportada |
| targetSdk | 36 | Versión objetivo de Android |

#### Arquitectura del sistema

```
┌─────────────────────────────────────┐
│         CAPA DE PRESENTACIÓN        │
│  ViewModels · Screens · Components  │
│  UiState · StateFlow · Compose UI   │
└──────────────┬──────────────────────┘
               │ invoca
┌──────────────▼──────────────────────┐
│          CAPA DE DOMINIO            │
│  UseCases · Interfaces · Validators │
│  (sin dependencias externas)        │
└──────────────┬──────────────────────┘
               │ implementa
┌──────────────▼──────────────────────┐
│           CAPA DE DATOS             │
│  RepositoryImpl · Firebase          │
│  Auth · Firestore · Storage         │
└─────────────────────────────────────┘
```

#### Modelo de datos (Firestore)

```
usuarios/
  {userId}/
    nombre: String              (máx. 15 caracteres, en minúsculas)
    email: String
    role: String                (valor por defecto: "user")
    photoUrl: String
    telefono: String            (opcional)

grupos/
  {groupId}/
    nombreGrupo: String
    descripcion: String
    creadoPor: String           (userId del creador)
    miembrosActivos: Array<String>
    estadoMiembros: Map<String, Boolean>
    balancesCentimos: Map<String, Long>
    moneda: String              (ej. "EUR", "USD", "GBP")
    liquidacionPendiente: Map<String, Boolean>  (confirmación mutua)

    gastos/
      {gastoId}/
        concepto: String
        montoCentimos: Long
        pagadoPor: String       (userId del pagador)
        distribucionCentimos: Map<String, Long>
        esPersonalizado: Boolean
        clientOperationId: String
        createdAt: Timestamp

    mensajes/
      {mensajeId}/
        userId: String
        userName: String
        text: String
        createdAt: Timestamp

invitaciones/
  {invitacionId}/
    grupoId: String
    grupoNombre: String
    invitadoPorId: String
    invitadoPorNombre: String
    paraUserId: String
    estado: String              ("pendiente", "aceptada", "rechazada")
    createdAt: Timestamp
```

### 8.3 Diagrama de clases del dominio

Entidades principales:

- **User:** `id`, `nombre`, `email`, `role`, `photoUrl`, `telefono`
- **Group:** `id`, `nombreGrupo`, `descripcion`, `creadoPor`, `miembrosActivos`, `balancesCentimos`, `moneda`, `liquidacionPendiente`
- **Expense:** `id`, `concepto`, `montoCentimos`, `pagadoPor`, `distribucionCentimos`, `esPersonalizado`, `createdAt`
- **ChatMessage:** `id`, `userId`, `userName`, `text`, `createdAt`
- **Transferencia** (objeto de valor): `deudor`, `acreedor`, `montoCentimos`

---

## 9. Instalación y preparación

### 9.1 Entorno de desarrollo

**Requisitos previos:** JDK 17, Android Studio Meerkat o posterior, conexión a Internet, cuenta de Google con acceso a Firebase Console.

**Pasos de configuración:**

1. Clonar el repositorio: `git clone <url-del-repositorio>`
2. Abrir en Android Studio mediante **File → Open**.
3. Crear un proyecto en Firebase Console y registrar la app con el paquete `com.example.splitapp`.
4. Descargar `google-services.json` y colocarlo en `app/`.
5. Activar Authentication (proveedor email/contraseña), Firestore y Storage en Firebase Console.
6. Sincronizar con **File → Sync Project with Gradle Files**.
7. Verificar compilación: `./gradlew :app:compileDebugKotlin`

### 9.2 Control de versiones

El proyecto utiliza Git con rama principal `main`. Los commits siguen un formato descriptivo en español. El fichero `google-services.json` está excluido del repositorio vía `.gitignore` (contiene claves privadas de Firebase).

---

## 10. Plan de calidad y pruebas

### 10.1 Categorías de pruebas

| Categoría | Ámbito | Herramienta | Estado |
|---|---|---|---|
| Unitarias | Validadores, casos de uso, algoritmo de deudas | JUnit 4, Kotlin Test | Ejecutadas |
| Integración | Repositorios + Firestore (transacciones) | JUnit + emulador Firestore | Ejecutadas |
| Rendimiento | Carga de datos, concurrencia | Instrumentación manual | Ejecutadas |
| Seguridad | Reglas Firestore, autenticación | Firebase Rules Simulator | Ejecutadas |
| Usabilidad | Flujo completo con usuarios reales | Sesiones observadas (n=8) | Ejecutadas |

### 10.2 Casos de prueba unitaria principales

| ID | Caso de prueba | Resultado |
|---|---|---|
| UT-01 | Distribución equitativa de 10,00 € entre 3 personas → 3,34 + 3,33 + 3,33 = 10,00 € | Correcto |
| UT-02 | Distribución equitativa de 1,00 € entre 3 personas → 0,34 + 0,33 + 0,33 = 1,00 € | Correcto |
| UT-03 | Distribución personalizada que no suma el total → error de validación | Correcto |
| UT-04 | Algoritmo de simplificación con 4 miembros y balances heterogéneos → mínimas transferencias | Correcto |
| UT-05 | Algoritmo con miembro de balance = 0 → no participa en transferencias | Correcto |
| UT-06 | Invariante suma nula tras registro de gasto | Correcto |
| UT-07 | Invariante suma nula tras eliminación de gasto | Correcto |
| UT-08 | Nombre de usuario con mayúsculas → convertido a minúsculas automáticamente | Correcto |
| UT-09 | Nombre de usuario con más de 15 caracteres → truncado en la UI | Correcto |

### 10.3 Indicadores de calidad

| Indicador | Valor objetivo | Valor obtenido |
|---|---|---|
| Tasa de éxito en compilación | 100% | 100% |
| Cobertura de pruebas unitarias (lógica crítica) | > 80% | ~85% |
| Tasa de éxito de casos de prueba | > 95% | 100% |
| Tiempo de respuesta en operaciones principales | < 2 segundos | < 1,5 segundos |
| Tasa de completitud de tareas en usabilidad | > 90% | 100% |
| Satisfacción en usabilidad (1-5) | > 4,0 | 4,3 (media) |
| Defectos críticos pendientes en entrega | 0 | 0 |

---

## 11. Distribución

SplitApp se distribuye en el contexto académico mediante un fichero **APK**. Para una distribución pública futura, el canal natural sería **Google Play Store**, lo que requeriría una cuenta de desarrollador, keystore de firma, compilación en formato AAB y revisión de Google.

**Generación del APK de depuración:** `Build → Build Bundle(s) / APK(s) → Build APK(s)`. El APK resultante se encuentra en `app/build/outputs/apk/debug/app-debug.apk`.

---

## 12. Conclusiones

### 12.1 Informe final

El proyecto ha culminado con el desarrollo completo y funcional de SplitApp. El sistema implementa todas las funcionalidades del alcance inicial más un conjunto de funcionalidades avanzadas desarrolladas durante la fase de implementación: chat de grupo en tiempo real, sistema de invitaciones con aceptación/rechazo/bloqueo, pantalla "Quién me debe", liquidación con confirmación mutua, restricciones de integridad (bloqueo de salida/eliminación con saldos pendientes, eliminación de gastos restringida al pagador), exportación a la carpeta Descargas, internacionalización ES/EN, modo oscuro y mejoras de accesibilidad.

Todos los objetivos específicos definidos han sido alcanzados. El invariante de suma nula de balances se mantiene en todos los escenarios probados.

### 12.2 Viabilidad del proyecto

- **Técnica:** Kotlin + Compose + Firebase permiten desarrollar una aplicación colaborativa en tiempo real sin backend propio.
- **Económica:** el coste de operación en el plan gratuito de Firebase es cero para el volumen de un proyecto académico.
- **De mercado:** el análisis comparativo confirma demanda real y espacio para una propuesta en español, completamente gratuita, con algoritmo de simplificación y chat integrado.

### 12.3 Mejoras futuras

**Corto plazo:**
- **Edición de gastos:** modificar un gasto registrado sin necesidad de eliminarlo y recrearlo.
- **Notificaciones push:** Firebase Cloud Messaging para notificar nuevos gastos o liquidaciones.
- **Historial de liquidaciones:** registro histórico de las transferencias ejecutadas.

**Medio plazo:**
- **Inyección de dependencias con Hilt:** simplificar la gestión del ciclo de vida de las dependencias.
- **Paginación de gastos:** carga por lotes del historial para grupos con muchos registros.
- **Caché local con Room:** mejorar la experiencia offline.

**Largo plazo:**
- **Versión iOS/multiplataforma:** evaluación de Kotlin Multiplatform Mobile.
- **Integración con sistemas de pago:** APIs de transferencia entre particulares para cerrar el ciclo de la deuda dentro de la app.
- **Soporte para tablets:** diseño de panel dual con `NavigationRail`.

---

## 13. Bibliografía y referencias

[1] Google LLC. *Firebase Documentation*. https://firebase.google.com/docs [Mayo 2026]

[2] Google LLC. *Android Developers Documentation*. https://developer.android.com/docs [Mayo 2026]

[3] JetBrains. *Kotlin Documentation*. https://kotlinlang.org/docs [Mayo 2026]

[4] Google LLC. *Jetpack Compose Documentation*. https://developer.android.com/jetpack/compose/documentation [Mayo 2026]

[5] Google LLC. *Material Design 3 Guidelines*. https://m3.material.io [Mayo 2026]

[6] Google LLC. *Cloud Firestore Documentation*. https://firebase.google.com/docs/firestore [Mayo 2026]

[7] Google LLC. *Guide to App Architecture*. https://developer.android.com/topic/architecture [Mayo 2026]

[8] Martin, R. C. (2017). *Clean Architecture*. Prentice Hall. ISBN: 978-0134494166.

[9] Martin, R. C. (2008). *Clean Code*. Prentice Hall. ISBN: 978-0132350884.

[10] Reglamento (UE) 2016/679 (RGPD). *DOUE* L 119/1, 4 de mayo de 2016.

[11] Ley Orgánica 3/2018 (LOPDGDD). *BOE* núm. 294, 6 de diciembre de 2018.

---

# PARTE II — DOCUMENTACIÓN TÉCNICA

## 1. Introducción del proyecto

### 1.1 Problema que resuelve

La gestión de gastos compartidos entre grupos de personas es una necesidad habitual en contextos cotidianos: viajes, convivencia en pisos compartidos, actividades de ocio o cualquier situación en la que varias personas contribuyan económicamente a un gasto común. El problema principal no reside en registrar el gasto, sino en calcular de forma precisa quién debe cuánto a quién, y en minimizar el número de transferencias necesarias para saldar todas las deudas del grupo.

SplitApp da solución a este problema mediante una aplicación móvil nativa que permite registrar gastos, calcular balances individuales en tiempo real, obtener un plan de liquidación optimizado y comunicarse dentro del grupo sin salir de la aplicación.

### 1.2 Objetivo funcional

- Crear y gestionar grupos de usuarios con persistencia en la nube y selector de moneda.
- Sistema de invitaciones con aceptación, rechazo y bloqueo por parte del destinatario.
- Registrar gastos con distribución equitativa o personalizada.
- Calcular y mostrar en tiempo real el balance neto de cada miembro.
- Simplificar las deudas pendientes con el mínimo número de transferencias.
- Liquidar deudas con confirmación mutua de ambas partes.
- Chat de grupo en tiempo real integrado como tercera pestaña del detalle del grupo.
- Pantalla global "Quién me debe" con todos los deudores del usuario actual.
- Exportar el historial de gastos en CSV a la carpeta Descargas del dispositivo.
- Interfaz en español e inglés con soporte para tema oscuro.

---

## 2. Arquitectura del sistema

### 2.1 Patrón arquitectónico: Clean Architecture

SplitApp implementa el patrón de Clean Architecture propuesto por Robert C. Martin, adaptado al ecosistema Android moderno. Este patrón organiza el código en capas concéntricas con dependencias unidireccionales: las capas internas no conocen ni dependen de las capas externas.

### 2.2 Descripción de las capas

#### Capa de Datos (`data`)

- **Modelos de datos:** clases Kotlin anotadas para deserialización desde Firestore (`User`, `Group`, `Expense`, `ChatMessage`).
- **Repositorios (implementaciones):** clases que implementan las interfaces del dominio. Contienen la lógica concreta de acceso a Firebase. Son los únicos componentes que conocen los detalles de Firebase.
- **Clase base `FirestoreRepository`:** proporciona la instancia de Firestore a todos los repositorios.

#### Capa de Dominio (`domain`)

- **Interfaces de repositorios:** definen los contratos de acceso a datos sin revelar detalles de implementación.
- **Casos de uso (`UseCases`):** clases con responsabilidad única que encapsulan una operación de negocio. Implementan el operador `invoke`. Ejemplos: `AddExpenseUseCase`, `SettleDebtUseCase`, `SimplifyDebtsUseCase`, `ExportExpensesToCsvUseCase`, `GetMessagesUseCase`, `SendMessageUseCase`, `SendInvitationUseCase`.
- **Validadores:** `ExpenseValidator` verifica la consistencia de un gasto; `UsernameValidator` aplica las restricciones de formato.

#### Capa de Presentación (`ui`)

- **ViewModels:** gestionan el estado de la interfaz, invocan los casos de uso y exponen datos mediante `StateFlow`. Sobreviven a cambios de configuración.
- **Estados de UI:** clases selladas (`sealed class`) con variantes `Idle`, `Loading`, `Success` y `Error` por operación.
- **Pantallas:** funciones `@Composable` que construyen la interfaz de forma declarativa.
- **Componentes reutilizables:** composables extraídos a `ui/components` y `ui/group/components`.

### 2.3 Flujo de datos (UDF)

1. El usuario interactúa con la pantalla (evento).
2. La pantalla delega al ViewModel correspondiente.
3. El ViewModel invoca el caso de uso pertinente.
4. El caso de uso accede al repositorio a través de su interfaz.
5. El repositorio ejecuta la operación en Firebase y devuelve `Result<T>` o `Flow<T>`.
6. El ViewModel actualiza el `StateFlow` de estado.
7. La pantalla, suscrita mediante `collectAsState()`, se recompone automáticamente.

---

## 3. Modelo de datos (Firestore)

### 3.1 Colección `usuarios`

Almacena los perfiles de los usuarios registrados. El ID del documento coincide con el UID de Firebase Authentication.

| Campo | Tipo | Descripción |
|---|---|---|
| `nombre` | String | Nombre de usuario en minúsculas, sin espacios, máximo 15 caracteres |
| `email` | String | Dirección de correo electrónico del usuario |
| `role` | String | Rol en el sistema. Valor por defecto: `"user"` |
| `photoUrl` | String | URL pública de la foto de perfil en Firebase Storage |
| `telefono` | String | Número de teléfono del usuario (opcional) |

### 3.2 Colección `grupos`

| Campo | Tipo | Descripción |
|---|---|---|
| `nombreGrupo` | String | Nombre descriptivo del grupo |
| `descripcion` | String | Descripción opcional |
| `creadoPor` | String | UID del usuario creador |
| `miembrosActivos` | Array\<String\> | Lista de UIDs de miembros activos |
| `estadoMiembros` | Map\<String, Boolean\> | Historial de estado (activo/inactivo) por miembro |
| `balancesCentimos` | Map\<String, Long\> | Balance neto de cada miembro en céntimos. Positivo = le deben; negativo = debe |
| `moneda` | String | Código de moneda del grupo (`"EUR"`, `"USD"`, `"GBP"`…) |
| `liquidacionPendiente` | Map\<String, Boolean\> | Confirmaciones de liquidación por UID. Cuando todos confirman, se ejecuta |

### 3.3 Subcolección `grupos/{groupId}/gastos`

| Campo | Tipo | Descripción |
|---|---|---|
| `concepto` | String | Descripción del gasto |
| `montoCentimos` | Long | Importe total en céntimos |
| `pagadoPor` | String | UID del miembro que abonó el gasto |
| `distribucionCentimos` | Map\<String, Long\> | Cuota de cada participante en céntimos. Suma = `montoCentimos` |
| `esPersonalizado` | Boolean | `true` si la distribución fue definida manualmente |
| `clientOperationId` | String | UUID en cliente para garantizar idempotencia |
| `createdAt` | Timestamp | Marca temporal de creación (asignada por Firestore) |

### 3.4 Subcolección `grupos/{groupId}/mensajes`

| Campo | Tipo | Descripción |
|---|---|---|
| `userId` | String | UID del autor del mensaje |
| `userName` | String | Nombre de usuario del autor en el momento del envío |
| `text` | String | Contenido del mensaje |
| `createdAt` | Timestamp | Marca temporal de creación (asignada por Firestore) |

### 3.5 Colección `invitaciones`

| Campo | Tipo | Descripción |
|---|---|---|
| `grupoId` | String | ID del grupo al que se invita |
| `grupoNombre` | String | Nombre del grupo (desnormalizado para mostrar sin consulta extra) |
| `invitadoPorId` | String | UID del usuario que envía la invitación |
| `invitadoPorNombre` | String | Nombre del invitador (desnormalizado) |
| `paraUserId` | String | UID del destinatario de la invitación |
| `estado` | String | `"pendiente"`, `"aceptada"` o `"rechazada"` |
| `createdAt` | Timestamp | Marca temporal de creación |

### 3.6 Relaciones entre entidades

La relación usuario-grupo es muchos-a-muchos resuelta de forma desnormalizada: el documento de grupo almacena directamente la lista de UIDs de sus miembros, optimizando las lecturas. Los gastos y mensajes se almacenan como subcolecciones del grupo (composición). Los balances se almacenan en el documento del grupo como mapa desnormalizado, permitiendo calcular el estado financiero con una única lectura.

---

## 4. Lógica de negocio

### 4.1 Sistema de balances globales

El núcleo financiero opera sobre un modelo de **balance neto global por usuario y por grupo**, expresado en céntimos mediante el tipo `Long`. Esto elimina los errores de representación en punto flotante inherentes a `Double` o `Float`.

Cuando se registra un gasto:
- El miembro que pagó ve incrementado su balance en la suma total menos su propia cuota (el dinero adelantado por los demás).
- Cada participante que no pagó ve decrementado su balance en su cuota correspondiente.

**Invariante fundamental:** en cualquier momento y para cualquier grupo, la suma de todos los balances es exactamente cero.

### 4.2 Algoritmo de simplificación de deudas

Implementado en `SimplifyDebtsUseCase`. Opera de la siguiente manera:

1. Los balances positivos representan acreedores; los negativos, deudores.
2. Se construyen dos listas ordenadas: acreedores (mayor a menor) y deudores (mayor a menor en valor absoluto).
3. En cada iteración se empareja el acreedor de mayor saldo con el deudor de mayor deuda. La transferencia es el mínimo entre ambos.
4. Se actualizan ambos saldos y se eliminan los que queden en cero.
5. El proceso continúa hasta vaciar ambas listas.

El resultado es una lista de objetos `Transferencia` (`deudor`, `acreedor`, `montoCentimos`). La complejidad es lineal respecto al número de participantes.

### 4.3 Chat de grupo en tiempo real

El chat de cada grupo se implementa sobre la subcolección `mensajes`. La escucha en tiempo real usa `callbackFlow` con `snapshotListener` de Firestore, con ordenación ascendente por `createdAt`. El envío usa `.add(message).await()` sin transacción (los mensajes son independientes entre sí). `ChatViewModel` obtiene el nombre del usuario actual en `init` mediante `GetUserNamesUseCase` y expone dos `StateFlow`: `messages: List<ChatMessage>` y `sending: Boolean`.

### 4.4 Restricciones de integridad de grupos y gastos

**Abandono y eliminación de grupos:** antes de ejecutar la operación en Firestore, `GroupViewModel` verifica en el `StateFlow` de grupos ya cargado que `balancesCentimos.values.any { it != 0L }`. Si hay saldos pendientes, emite un estado de error sin llegar a Firestore.

**Eliminación de gastos:** `ExpensesSection` compara `expense.pagadoPor == currentUserId` para cada elemento. El botón de eliminar solo se renderiza cuando el usuario actual es el pagador original del gasto.

### 4.5 Liquidación con confirmación mutua

Cuando un usuario pulsa "Liquidar deuda", se registra su confirmación en el mapa `liquidacionPendiente` del grupo. La liquidación efectiva (actualización de balances a cero) solo se ejecuta cuando todas las partes implicadas han confirmado. `GroupViewModel` expone `settlementState: StateFlow<SettlementState>` y los métodos `confirmSettlement` y `cancelSettlement`.

### 4.6 Reglas de consistencia adicionales

- **Invariante de distribución:** la suma de `distribucionCentimos` debe ser igual a `montoCentimos`. `ExpenseValidator` lo verifica antes de persistir. En distribución equitativa, `distribuirJusto` asigna el resto de la división entera al primer participante.
- **Idempotencia:** antes de escribir un gasto, la transacción verifica si ya existe un documento con el mismo `clientOperationId`, descartando silenciosamente los duplicados.

---

## 5. Operaciones principales del sistema

### 5.1 Creación y gestión de grupos

La creación requiere nombre obligatorio, moneda y descripción opcional. El creador queda registrado como primer miembro con balance cero. La eliminación (solo disponible para el creador) y el abandono están bloqueados si cualquier balance del grupo es distinto de cero.

### 5.2 Sistema de invitaciones

Los nuevos miembros se incorporan mediante invitación: el usuario invitador busca por nombre de usuario, selecciona al destinatario y envía la invitación. `SendInvitationUseCase` verifica antes de crear el documento que el destinatario no ha bloqueado al invitador, que no existe ya una invitación pendiente y que el destinatario no es ya miembro del grupo. El destinatario recibe la invitación en su bandeja y puede aceptarla, rechazarla o bloquear al invitador.

### 5.3 Registro y eliminación de gastos

El registro se realiza mediante `AddExpenseUseCase` en una transacción atómica que escribe simultáneamente en `gastos/` y actualiza `balancesCentimos`. La eliminación invierte los ajustes de balance en sentido contrario, dentro de una nueva transacción. Solo el usuario cuyo UID coincide con `pagadoPor` ve el botón de eliminar.

### 5.4 Liquidación de deudas

`SettleDebtUseCase` convierte la lista de `Transferencia` en actualizaciones de balances y las persiste en una batch de Firestore. La operación solo se ejecuta cuando ambas partes han confirmado en `liquidacionPendiente`. Tras la ejecución, todos los balances quedan a cero.

### 5.5 Chat de grupo

Los mensajes se envían mediante `SendMessageUseCase` y se escuchan en tiempo real mediante `GetMessagesUseCase`. `ChatScreen` se presenta como la tercera pestaña de `GroupDetailScreen` (tras Balances y Gastos). Cuando esta pestaña está activa, el FAB de añadir gasto queda oculto.

### 5.6 Exportación CSV

`ExportExpensesToCsvUseCase` recupera todos los gastos de Firestore, resuelve los nombres de los pagadores y construye el fichero CSV en `cacheDir`. Para API 29+, `GroupViewModel` copia el fichero a la carpeta pública Descargas mediante `MediaStore.Downloads.EXTERNAL_CONTENT_URI` y `ContentResolver`, sin necesidad del permiso `WRITE_EXTERNAL_STORAGE`. El fichero en caché se elimina tras la copia. En API < 29 se guarda directamente en el directorio externo privado de la app.

---

## 6. Gestión de datos y persistencia

### 6.1 Firebase Cloud Firestore

La escucha en tiempo real se implementa mediante `callbackFlow` que encapsula el `snapshotListener` de Firestore. Los ViewModels suscriben estos flujos mediante `stateIn` con `SharingStarted.WhileSubscribed(5000)`, garantizando que el listener permanece activo solo mientras hay suscriptores en la UI.

### 6.2 Transacciones atómicas

Las operaciones que afectan a múltiples documentos (registro de gastos, eliminación, liquidación) se ejecutan en transacciones de Firestore: o todas las operaciones tienen efecto, o ninguna.

### 6.3 Idempotencia

Cada gasto incluye un `clientOperationId` (UUID generado en cliente). La transacción de escritura verifica previamente si ya existe un documento con ese ID para evitar duplicados en escenarios de reintento por error de red.

### 6.4 Concurrencia

Firestore gestiona la concurrencia mediante bloqueos optimistas. Si se detecta un conflicto, la transacción se reintenta automáticamente hasta tres veces antes de devolver un error.

---

## 7. Interfaz de usuario (Jetpack Compose)

### 7.1 Estructura general de pantallas

| Pantalla | Descripción |
|---|---|
| `LoginScreen` | Inicio de sesión con email y contraseña |
| `RegisterScreen` | Registro con nombre, teléfono, email, contraseña y foto de perfil opcional |
| `GroupListScreen` | Pantalla principal con resumen financiero global y lista de grupos |
| `GroupDetailScreen` | Detalle del grupo con tres pestañas: **Balances**, **Gastos** y **Chat** |
| `DebtorsScreen` | Pantalla "Quién me debe": lista global de deudores del usuario actual agrupados por grupo |

**GroupDetailScreen** usa `PrimaryTabRow` de Material 3 con tres pestañas. El contenido de cada pestaña se selecciona con un `when(selectedTab)`. El FAB de añadir gasto se oculta cuando la pestaña activa es Chat (`if (selectedTab != 2)`).

### 7.2 Componentes reutilizables

| Componente | Descripción |
|---|---|
| `UserAvatar` | Foto de perfil con fallback a inicial. Acepta `contentDescription` para accesibilidad |
| `GroupCard` | Tarjeta de resumen de grupo con nombre, miembros y balance |
| `BalancesSection` / `BalanceItem` | Lista de balances con código de colores y diálogo de detalle por miembro |
| `ExpensesSection` / `ExpenseItem` | Historial de gastos con gráfico de tarta. El botón eliminar solo aparece si `expense.pagadoPor == currentUserId` |
| `ChatScreen` | Pantalla de chat con `LazyColumn`, scroll automático, burbujas de mensaje y campo de texto con `ImeAction.Send` |
| `MessageBubble` | Burbuja de mensaje. Propios: `primaryContainer`, alineados a la derecha. Ajenos: `surfaceVariant`, con `UserAvatar` de 32 dp y `clearAndSetSemantics` |
| `AddExpenseDialog` | Diálogo de registro de gasto con validación inline y resumen de impacto en tiempo real |
| `SettleDebtDialog` | Plan de transferencias optimizado con confirmación mutua |
| `AddMemberDialog` | Búsqueda por nombre de usuario y envío de invitación |
| `ProfileDialog` | Edición de nombre, teléfono, foto e idioma |
| `GroupDescriptionAccordion` | Descripción del grupo expandible con animación |

### 7.3 Gestión de estado con UiState

Cada operación asíncrona expone su estado mediante una `sealed class` con variantes `Idle`, `Loading`, `Success` y `Error`. Las pantallas observan estos estados mediante `collectAsState()` y reaccionan de forma declarativa.

### 7.4 Accesibilidad

- **Áreas táctiles:** los `IconButton` usan el tamaño por defecto de Material 3 (48 dp mínimo). Los `Icon` dentro tienen un `Modifier.size(20.dp)` separado para controlar el tamaño visual sin comprimir el área táctil.
- **Descripciones:** `UserAvatar` acepta `contentDescription` y lo propaga a `AsyncImage`.
- **Semántica en chat:** cada `MessageBubble` aplica `clearAndSetSemantics { contentDescription = "Autor: texto, hora" }` sobre el `Row` externo, consolidando la lectura de TalkBack.

### 7.5 Principios de diseño Material 3

- **Paleta:** colores primarios en gama violeta-púrpura, con acento esmeralda para saldo positivo y rojo para saldo negativo.
- **Tipografía:** fuente Nunito en todos los estilos.
- **Modo oscuro:** colores adaptativos de Material 3 con soporte completo para el tema del sistema.
- **Barra superior:** `TopAppBar` con fondo de color primario e iconos en blanco.
- **Tarjetas:** esquinas de 16 dp, elevación de 2 dp y borde de color de contorno del tema.

### 7.6 Internacionalización

`LocaleManager` gestiona el idioma activo (ES/EN) mediante `SharedPreferences`. El cambio de idioma llama a `activity.recreate()` para aplicar los nuevos recursos de cadenas sin cerrar la sesión. Los recursos se organizan en `values/strings.xml` (español, idioma base) y `values-en/strings.xml` (inglés).

---

## 8. Seguridad y privacidad

### 8.1 Reglas de seguridad de Firestore

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    match /usuarios/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }

    match /grupos/{groupId} {
      allow read, update: if request.auth != null &&
        request.auth.uid in resource.data.miembrosActivos;
      allow create: if request.auth != null;
      allow delete: if request.auth != null &&
        request.auth.uid == resource.data.creadoPor;

      match /gastos/{gastoId} {
        allow read, write: if request.auth != null &&
          request.auth.uid in
            get(/databases/$(database)/documents/grupos/$(groupId)).data.miembrosActivos;
      }

      match /mensajes/{mensajeId} {
        allow read, write: if request.auth != null &&
          request.auth.uid in
            get(/databases/$(database)/documents/grupos/$(groupId)).data.miembrosActivos;
      }
    }

    match /invitaciones/{invitacionId} {
      allow read: if request.auth != null &&
        (request.auth.uid == resource.data.paraUserId ||
         request.auth.uid == resource.data.invitadoPorId);
      allow create: if request.auth != null;
      allow update: if request.auth != null &&
        request.auth.uid == resource.data.paraUserId;
    }
  }
}
```

Estas reglas se evalúan en el servidor de Firebase y no pueden ser eludidas por un cliente malicioso.

### 8.2 Privacidad en la búsqueda de usuarios

Los resultados de búsqueda exponen únicamente el nombre de usuario. El campo email no se incluye en ninguna vista visible para otros usuarios. La búsqueda filtra automáticamente al usuario que realiza la consulta.

### 8.3 Validación en múltiples capas

- **UI:** filtros en tiempo real (conversión a minúsculas, eliminación de espacios, límite de caracteres). Botones deshabilitados hasta superar las validaciones básicas.
- **ViewModel:** verificación de precondiciones antes de invocar el caso de uso.
- **Dominio:** `ExpenseValidator` y `UsernameValidator` verifican la consistencia completa antes de delegar al repositorio.

---

## 9. Exportación y gestión de ficheros

### 9.1 Exportación a CSV

`ExportExpensesToCsvUseCase` recupera todos los gastos de la subcolección, resuelve los nombres de los pagadores y construye un fichero CSV con cabecera y una fila por gasto: identificador, concepto, importe en la moneda del grupo, nombre del pagador y fecha de registro.

### 9.2 Estrategia de almacenamiento (MediaStore)

Para **API 29+ (Android 10+):** el fichero se crea primero en `cacheDir`. Luego `GroupViewModel` lo copia a la carpeta pública Descargas mediante `MediaStore.Downloads.EXTERNAL_CONTENT_URI` con `ContentResolver.insert` y `openOutputStream`. Tras la copia exitosa, el fichero de caché se elimina. No se requiere el permiso `WRITE_EXTERNAL_STORAGE`.

Para **API < 29:** el fichero se guarda directamente en el directorio externo privado de la app (`getExternalFilesDir(null)`) o en `cacheDir` como alternativa.

El resultado se comunica al usuario mediante un `Snackbar` con el nombre del fichero generado.

### 9.3 Gestión de ficheros de cámara

Para la foto de perfil mediante cámara, la app crea ficheros temporales en `cacheDir` y los expone a la app de cámara del sistema mediante `FileProvider`. Este mecanismo, configurado en `AndroidManifest.xml`, permite compartir URIs de ficheros privados de forma segura.

---

## 10. Estrategia de pruebas

### 10.1 Pruebas unitarias

Se verifican los validadores (`ExpenseValidator`, `UsernameValidator`), los cálculos financieros (distribución equitativa con redondeo, distribución personalizada), el algoritmo de simplificación de deudas (número mínimo de transferencias, invariante de suma nula, casos con balance cero) y los ViewModels con dependencias simuladas (transiciones de estado `Idle → Loading → Success/Error`).

### 10.2 Pruebas de integración

Se verifica el recorrido completo de las operaciones principales contra el emulador local de Firestore: creación de grupo y consulta de estado, registro de gasto y verificación de balances, eliminación de gasto y reversión de balances, liquidación y verificación de balances a cero. Se comprueba la atomicidad ante errores simulados en mitad de transacciones.

### 10.3 Pruebas de seguridad

Se verifican las reglas de Firestore mediante el Firebase Rules Playground: usuario no autenticado no puede leer ni escribir, usuario autenticado no puede modificar documentos de otros usuarios, solo el creador puede eliminar el grupo, usuarios externos al grupo no pueden registrar gastos ni mensajes, el destinatario de una invitación puede actualizarla pero no el invitador.

### 10.4 Pruebas de usabilidad

Sesiones con 8 participantes sin conocimientos técnicos cubriendo el flujo completo: registro, crear grupo, invitar miembro, registrar gastos (equitativo y personalizado), consultar balances, liquidar, usar el chat y exportar el CSV. La tasa de completitud fue del 100% y la satisfacción media de 4,3/5.

---

## 11. Decisiones de diseño relevantes

### 11.1 Balance global en lugar de deuda par a par

El modelo alternativo más común registra deudas bilaterales (A debe a B). SplitApp adopta un saldo neto por usuario por grupo, lo que simplifica el modelo de datos, reduce las escrituras en Firestore y facilita la verificación del invariante de suma nula. El detalle de quién debe a quién se calcula bajo demanda mediante el algoritmo de simplificación.

### 11.2 Firestore en lugar de base de datos local

- **Sincronización en tiempo real:** los cambios son visibles para todos los miembros inmediatamente.
- **Persistencia offline:** Firestore almacena en caché los datos consultados recientemente.
- **Sin backend propio:** elimina la necesidad de desarrollar y mantener una API REST.
- **Escalabilidad automática:** Firestore escala sin intervención del desarrollador.

### 11.3 Chat como tercera pestaña

El chat se integra como tercera pestaña del `GroupDetailScreen` (junto a Balances y Gastos) en lugar de como ruta de navegación independiente. Esto mantiene el contexto del grupo visible y reduce la profundidad de navegación. La subcolección `mensajes` bajo cada grupo refleja esta composición natural.

### 11.4 Clean Architecture

La separación de capas permite probar la lógica de negocio de forma independiente de Firebase y de la interfaz. Facilita la localización de errores y el cumplimiento de los principios SOLID (especialmente responsabilidad única e inversión de dependencias mediante interfaces de repositorio).

---

## 12. Limitaciones actuales y mejoras futuras

### 12.1 Funcionalidades pendientes

- **Edición de gastos:** los gastos solo pueden eliminarse, no modificarse.
- **Notificaciones push:** sin integración con Firebase Cloud Messaging.
- **Historial de liquidaciones:** las transferencias ejecutadas no se conservan con detalle histórico.

### 12.2 Mejoras de arquitectura

- **Hilt:** el proyecto instancia dependencias directamente en los ViewModels. Hilt simplificaría el ciclo de vida y los tests.
- **Paginación de gastos:** Paging 3 permitiría cargar el historial en lotes para grupos con muchos registros.
- **Caché local con Room:** mejoraría la experiencia offline y reduciría lecturas en Firestore.

### 12.3 Adaptabilidad

- **Tablets:** diseño de panel dual con `NavigationRail` o `PermanentNavigationDrawer`.
- **iOS/multiplataforma:** evaluación de Kotlin Multiplatform Mobile para compartir la lógica de negocio.

---

# PARTE III — MANUAL DE INSTALACIÓN

## 1. Introducción

Este documento describe los procedimientos necesarios para instalar y poner en marcha SplitApp en dos modalidades:

- **Usuario final:** instalación de la app en un dispositivo Android a partir del APK entregado con el proyecto.
- **Entorno de desarrollo:** configuración del entorno para compilar, modificar y ejecutar el proyecto desde código fuente.

---

## 2. Requisitos del sistema

### 2.1 Requisitos para el usuario final

| Requisito | Mínimo | Recomendado |
|---|---|---|
| Sistema operativo | Android 6.0 (API 23) | Android 10.0 (API 29) o superior |
| Espacio libre | 50 MB | 100 MB |
| Conexión a Internet | Wi-Fi o datos móviles | Wi-Fi o 4G/5G |
| Cuenta de correo electrónico | Obligatoria para el registro | — |

### 2.2 Requisitos para el entorno de desarrollo

| Requisito | Versión mínima | Versión utilizada |
|---|---|---|
| Sistema operativo | Windows 10 / macOS 12 / Ubuntu 20.04 | Windows 10 |
| Java Development Kit | JDK 17 | JDK 17 |
| Android Studio | Hedgehog (2023.1.1) | Meerkat (2024.3.2) |
| RAM del equipo | 8 GB | 16 GB recomendados |
| Espacio en disco | 8 GB | 16 GB recomendados |
| Cuenta de Google | Obligatoria (Firebase Console) | — |

---

## 3. Medio de distribución

### 3.1 Distribución actual

SplitApp se distribuye mediante un fichero **APK** (Android Package). Esta modalidad es adecuada para el contexto académico del proyecto.

### 3.2 Distribución futura: Google Play Store

La publicación en Play Store requeriría:
1. Cuenta de desarrollador en Google Play Console (tasa única de 25 USD).
2. Keystore de firma para la versión de producción.
3. Compilación en formato **AAB** (Android App Bundle).
4. Completar la ficha de la app (nombre, descripción, capturas, política de privacidad).
5. Superar el proceso de revisión de Google.

---

## 4. Instalación en dispositivo Android

### 4.1 Obtención del fichero APK

El fichero `splitapp-v1.0.apk` se entrega junto con la documentación. Puede transferirse al dispositivo mediante cable USB, correo electrónico o almacenamiento en la nube.

### 4.2 Habilitación de la instalación desde fuentes desconocidas

**Android 8.0 o superior:**
1. Transfiera el APK y localícelo en el gestor de archivos.
2. Pulse el fichero. Android mostrará un aviso de fuente no permitida.
3. Pulse **"Ajustes"** y active **"Permitir desde esta fuente"**.
4. Vuelva atrás; el proceso de instalación se retoma automáticamente.

**Android 6.0 o 7.0:**
1. Acceda a **Ajustes → Seguridad**.
2. Active **"Orígenes desconocidos"** y confirme el aviso de seguridad.
3. Localice el APK y pulse para instalar.

### 4.3 Proceso de instalación

1. Localice `splitapp-v1.0.apk` en el gestor de archivos.
2. Pulse el fichero para iniciar el instalador del sistema.
3. Revise los permisos y pulse **"Instalar"**.
4. Pulse **"Abrir"** para iniciar la app o **"Hecho"** para cerrar el instalador.

### 4.4 Primer inicio y registro

1. Pulse **"Registrarse"** en la pantalla de bienvenida.
2. Introduzca nombre de usuario, teléfono (opcional), correo y contraseña.
3. Opcionalmente añada una fotografía de perfil.
4. Pulse **"Registrarse"** para crear la cuenta e iniciar sesión automáticamente.

### 4.5 Permisos solicitados por la aplicación

| Permiso | Motivo |
|---|---|
| Acceso a Internet | Sincronización con Firebase en tiempo real |
| Acceso a imágenes de la galería | Selección de fotografía de perfil |
| Acceso a la cámara | Captura de fotografía de perfil |

> En Android 10 o superior no se requiere el permiso `WRITE_EXTERNAL_STORAGE` para guardar el CSV en Descargas; se usa la API MediaStore, que no lo necesita.

Los permisos se solicitan de forma contextual en el momento en que se accede a la funcionalidad correspondiente.

---

## 5. Instalación del entorno de desarrollo

### 5.1 Instalación de Java Development Kit (JDK 17)

1. Descargue el JDK 17 desde https://adoptium.net para su sistema operativo.
2. Ejecute el instalador con las opciones predeterminadas.
3. Verifique: `java -version` → debe mostrar una versión `17.x.x`.

### 5.2 Instalación de Android Studio

1. Descargue Android Studio desde https://developer.android.com/studio
2. Ejecute el instalador; seleccione tipo **Standard**.
3. Android Studio descargará los componentes necesarios (Android SDK, AVD). Puede tardar varios minutos.

### 5.3 Obtención del código fuente

**Mediante Git (recomendado):**
```
git clone <url-del-repositorio>
cd SplitApp
```

**Mediante ZIP:** descomprima el archivo en la ubicación deseada.

### 5.4 Instalación de un emulador Android (opcional)

1. En Android Studio: **Tools → Device Manager → Create Device**.
2. Seleccione el perfil **Pixel 6** o similar.
3. Descargue y seleccione **API 33 (Android 13)** o superior.
4. Pulse **"Finish"** y arranque el emulador con el icono de reproducción.

---

## 6. Configuración del proyecto Firebase

El proyecto requiere el fichero `google-services.json` descargado desde Firebase Console. Sin este fichero el proyecto no compilará.

### 6.1 Creación del proyecto en Firebase Console

1. Acceda a https://console.firebase.google.com e inicie sesión.
2. Pulse **"Añadir proyecto"**, introduzca el nombre `SplitApp`.
3. Desactive Google Analytics si no es necesario y pulse **"Crear proyecto"**.

### 6.2 Registro de la aplicación Android

1. En el panel del proyecto, pulse el icono de Android.
2. Introduzca el nombre del paquete: `com.example.splitapp`.
3. Pulse **"Registrar aplicación"**.

### 6.3 Descarga y ubicación del fichero google-services.json

1. Descargue el fichero `google-services.json` del asistente de Firebase.
2. Colóquelo en la carpeta `app/` del proyecto:
   ```
   SplitApp/
   └── app/
       └── google-services.json   ← aquí
   ```
3. **No incluya este fichero en el control de versiones** (ya está excluido en `.gitignore`).

### 6.4 Activación de los servicios de Firebase

**Authentication:**
1. Acceda a **Authentication → Sign-in method**.
2. Active el proveedor **"Correo electrónico/contraseña"** y guarde.

**Cloud Firestore:**
1. Acceda a **Firestore Database → Crear base de datos**.
2. Seleccione **"Comenzar en modo de producción"** y la región `eur3 (europe-west)`.
3. En la pestaña **"Reglas"** introduzca las siguientes reglas y pulse **"Publicar"**:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    match /usuarios/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }

    match /grupos/{groupId} {
      allow read, update: if request.auth != null &&
        request.auth.uid in resource.data.miembrosActivos;
      allow create: if request.auth != null;
      allow delete: if request.auth != null &&
        request.auth.uid == resource.data.creadoPor;

      match /gastos/{gastoId} {
        allow read, write: if request.auth != null &&
          request.auth.uid in
            get(/databases/$(database)/documents/grupos/$(groupId)).data.miembrosActivos;
      }

      match /mensajes/{mensajeId} {
        allow read, write: if request.auth != null &&
          request.auth.uid in
            get(/databases/$(database)/documents/grupos/$(groupId)).data.miembrosActivos;
      }
    }

    match /invitaciones/{invitacionId} {
      allow read: if request.auth != null &&
        (request.auth.uid == resource.data.paraUserId ||
         request.auth.uid == resource.data.invitadoPorId);
      allow create: if request.auth != null;
      allow update: if request.auth != null &&
        request.auth.uid == resource.data.paraUserId;
    }
  }
}
```

**Firebase Storage:**
1. Acceda a **Storage → Comenzar** y seleccione la misma región que Firestore.
2. En **"Reglas"** configure el acceso para usuarios autenticados:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

3. Pulse **"Publicar"**.

---

## 7. Compilación y ejecución del proyecto

### 7.1 Apertura del proyecto en Android Studio

1. **File → Open** → seleccione la carpeta raíz del proyecto.
2. Android Studio detectará el proyecto Gradle e iniciará la sincronización.
3. Si aparece un error relacionado con `google-services.json`, verifique que el fichero está en `app/`.

### 7.2 Verificación de la compilación

**Linux / macOS:**
```bash
./gradlew :app:compileDebugKotlin
```
**Windows:**
```
gradlew.bat :app:compileDebugKotlin
```

La compilación debe completarse con `BUILD SUCCESSFUL`.

### 7.3 Ejecución de la aplicación

Con un emulador activo o dispositivo físico conectado con depuración USB habilitada:
1. Seleccione el dispositivo en el desplegable de la barra de herramientas.
2. Pulse **Run** o presione `Shift + F10`.
3. La app se compilará, instalará e iniciará automáticamente.

### 7.4 Generación del APK

1. **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
2. El APK se encontrará en `app/build/outputs/apk/debug/app-debug.apk`.

---

## 8. Desinstalación

### 8.1 Desde el dispositivo Android

- **Desde Ajustes:** Ajustes → Aplicaciones → SplitApp → Desinstalar.
- **Desde el lanzador:** mantener pulsado el icono → "Desinstalar".

> La desinstalación de la app **no elimina** los datos del usuario almacenados en Firebase. Los datos permanecen en la nube y son accesibles desde cualquier otro dispositivo.

### 8.2 Desde el entorno de desarrollo

Elimine la carpeta del proyecto, desinstale Android Studio desde el sistema operativo y elimine la carpeta del SDK (`%USERPROFILE%\AppData\Local\Android\Sdk` en Windows).

---

## 9. Resolución de problemas de instalación

| Problema | Causa probable | Solución |
|---|---|---|
| No se puede instalar el APK | Fuentes desconocidas no habilitadas | Seguir los pasos de la sección 4.2 |
| El proyecto no compila | Falta `google-services.json` | Completar la sección 6.3 |
| Error de Gradle al abrir el proyecto | Versión de JDK incorrecta | Verificar JDK 17 en **File → Project Structure → SDK Location** |
| El emulador no arranca o es muy lento | Virtualización de hardware desactivada | Activar Intel VT-x / AMD-V en la BIOS |
| "Error de conexión" al iniciar la app | Sin conexión a Internet | Verificar conectividad antes de abrir la app |
| La foto de perfil no se puede seleccionar | Permiso de galería denegado | Conceder el permiso en **Ajustes → Aplicaciones → SplitApp → Permisos** |
| El CSV no aparece en Descargas | API < 29 o permiso de almacenamiento denegado | En Android < 10 conceder permiso de almacenamiento; en Android 10+ debería funcionar automáticamente |

---

# PARTE IV — MANUAL DE USUARIO

## 1. Introducción

### 1.1 Qué es SplitApp

SplitApp es una aplicación móvil nativa para Android que permite gestionar gastos compartidos entre grupos de personas. Facilita el seguimiento y la organización de los pagos realizados en contextos cotidianos como viajes, cenas, alquileres compartidos o cualquier situación en la que varias personas contribuyen económicamente a gastos comunes.

La aplicación permite registrar cada gasto indicando quién lo pagó y cómo se reparte entre los participantes, calcula automáticamente los balances de cada persona, ofrece un chat de grupo en tiempo real y proporciona un plan óptimo para liquidar las deudas con el mínimo número de transferencias.

### 1.2 Objetivos de la aplicación

- Registrar gastos compartidos de forma sencilla e intuitiva.
- Calcular automáticamente el saldo de cada participante.
- Simplificar las deudas para minimizar el número de pagos necesarios.
- Facilitar la comunicación del grupo mediante el chat integrado.
- Proporcionar un historial exportable de los gastos.
- Ofrecer una experiencia clara, rápida y sin complicaciones.

---

## 2. Requisitos previos

### 2.1 Dispositivo Android

Android **6.0 (Marshmallow) o superior**. Para verificar la versión: Ajustes → Información del teléfono → Versión de Android.

### 2.2 Conexión a Internet

SplitApp requiere conexión a Internet para sincronizar datos en tiempo real. Compatible con Wi-Fi y datos móviles (3G, 4G, 5G).

### 2.3 Cuenta de correo electrónico

Necesaria para el registro. Se recomienda una dirección de uso personal y habitual.

---

## 3. Registro de usuario

### 3.1 Crear una cuenta

Al abrir SplitApp por primera vez se mostrará la pantalla de bienvenida. Pulse **"Registrarse"** para acceder al formulario de registro.

### 3.2 Nombre de usuario

- Máximo **15 caracteres**, sin espacios.
- Se almacena en minúsculas automáticamente.
- Debe ser único en la plataforma.
- Elija un nombre que sus contactos puedan reconocer fácilmente; es el identificador que utilizarán para añadirle a grupos.

### 3.3 Número de teléfono (opcional)

Puede introducir su número de teléfono para facilitar la identificación entre los miembros de sus grupos. Este campo es opcional.

### 3.4 Correo electrónico y contraseña

Introduzca su dirección de correo y una contraseña segura (mínimo 6 caracteres). Guarde su contraseña en un lugar seguro.

### 3.5 Foto de perfil (opcional)

Pulse el icono de cámara y seleccione una imagen de la galería. Puede añadir o cambiar la foto más adelante desde el perfil.

Una vez completados los campos obligatorios, pulse **"Registrarse"**. Si el registro es correcto, accederá automáticamente a la pantalla principal.

---

## 4. Inicio de sesión

Pulse **"Iniciar sesión"** en la pantalla de bienvenida, introduzca su correo y contraseña, y pulse **"Entrar"**.

**Recuperación de contraseña:** pulse **"¿Has olvidado tu contraseña?"**, introduzca su correo y siga las instrucciones del mensaje que recibirá. Si no lo recibe, compruebe la carpeta de spam.

---

## 5. Pantalla principal

Tras iniciar sesión accederá a la pantalla principal, que muestra:

### 5.1 Resumen global de balances

En la parte superior: un resumen de su situación económica global en todos los grupos (total que le deben y total que debe).

### 5.2 Lista de grupos

El área central muestra todos los grupos a los que pertenece con su saldo actual en cada uno. Pulse sobre un grupo para acceder a su detalle.

### 5.3 Acciones principales

- **Crear grupo:** botón "+" en la parte inferior derecha.
- **Ver quién le debe:** botón o sección de deudores globales.
- **Acceder al perfil:** icono de usuario o foto de perfil en la barra superior.

---

## 6. Gestión de grupos

### 6.1 Crear un grupo

1. Pulse el botón **"+"** en la pantalla principal.
2. Introduzca el **nombre del grupo** (obligatorio) y una **descripción** (opcional).
3. Seleccione la **moneda** del grupo (€, $, £, etc.).
4. Pulse **"Crear"**. Quedará registrado automáticamente como creador y primer miembro.

### 6.2 Unirse a un grupo mediante enlace

Si alguien le comparte un enlace de invitación al grupo, ábralo desde su dispositivo con SplitApp instalado. La aplicación le pedirá confirmación y le añadirá al grupo automáticamente.

### 6.3 Abandonar un grupo

Disponible desde la pantalla de detalle del grupo. Tenga en cuenta:

- Al abandonar, dejará de ver los gastos y balances del grupo.
- **Si hay saldos pendientes en el grupo, no podrá abandonarlo** hasta que todos los balances estén a cero. Liquide las deudas primero.
- El historial de gastos registrados por usted permanece en el grupo.

### 6.4 Eliminar un grupo

Solo el **creador del grupo** puede eliminarlo. Al eliminar:

- Se eliminan todos los gastos y datos del grupo de forma permanente.
- **Si hay saldos pendientes en el grupo, no podrá eliminarlo** hasta que todos los balances estén a cero.

> **Advertencia:** Esta acción es **irreversible**.

---

## 7. Gestión de miembros

### 7.1 Añadir miembros mediante invitación

Para añadir a una persona al grupo:

1. Acceda a la pantalla de detalle del grupo y abra el diálogo de añadir miembro.
2. Busque al usuario por su **nombre de usuario**.
3. Selecciónelo en los resultados y pulse **"Invitar"**.
4. El usuario recibirá una invitación que puede **aceptar**, **rechazar** o usar para **bloquear** futuras invitaciones de su parte.

> La persona añadida solo podrá acceder al grupo si acepta la invitación.

### 7.2 Gestionar invitaciones recibidas

Cuando alguien le invite a un grupo, recibirá una notificación en la aplicación. Puede:

- **Aceptar:** se unirá al grupo y verá todos sus gastos.
- **Rechazar:** la invitación se descartará.
- **Bloquear:** rechazará la invitación y bloqueará futuras invitaciones de ese usuario.

### 7.3 Visualizar miembros del grupo

Desde la pantalla de detalle puede consultar la lista completa de miembros con su nombre de usuario, foto de perfil y saldo actual.

---

## 8. Gestión de gastos

La gestión de gastos es la funcionalidad principal de SplitApp. Acceda a la pestaña **"Gastos"** dentro del detalle del grupo.

### 8.1 Registrar un gasto

1. En la pestaña Gastos, pulse el botón **"+"**.
2. Rellene el formulario:

**Concepto:** descripción breve del gasto (ej. "Cena del sábado", "Gasolina"). Obligatorio.

**Importe:** valor total del gasto. Debe ser mayor que cero.

**Pagador:** miembro que abonó el gasto. Por defecto aparece el usuario actual.

**Participantes:** miembros entre quienes se repartirá el importe. Por defecto, todos los miembros del grupo.

**Reparto equitativo:** el importe se divide en partes iguales, con ajuste automático de céntimos para que la suma sea exacta.

**Reparto personalizado:** dos modalidades:
- *Por importes:* introduzca el importe exacto de cada participante. La suma debe ser igual al total.
- *Por porcentajes:* asigne un porcentaje a cada participante. La suma debe ser 100%.

La app le indicará si los valores no cuadran antes de permitir confirmar.

3. Pulse **"Guardar"**. El gasto aparecerá inmediatamente en el historial y los balances se actualizarán.

---

## 9. Consulta de balances

La pestaña **"Balances"** muestra el estado económico actual de cada miembro del grupo.

### 9.1 Saldo positivo

Un saldo **positivo** (en verde) indica que el grupo le debe dinero: ha pagado más de lo que le correspondía.

> Ejemplo: saldo de +15,00 € → otros miembros le deben 15 euros en total.

### 9.2 Saldo negativo

Un saldo **negativo** (en rojo) indica que ese miembro debe dinero al grupo: ha consumido más de lo que ha pagado.

> Ejemplo: saldo de −8,50 € → usted debe 8,50 euros a otros miembros.

### 9.3 Pantalla "Quién me debe"

Accesible desde la pantalla principal, muestra una vista global de todos los usuarios que tienen deuda con usted, agrupados por grupo, con el importe total de cada uno.

---

## 10. Liquidación de deudas

### 10.1 Qué hace la función

La liquidación analiza los saldos de todos los miembros y calcula el **conjunto mínimo de transferencias** para que todos queden a cero.

### 10.2 Cómo se ejecuta

1. Desde la pestaña **"Balances"**, pulse **"Liquidar deuda"**.
2. La app mostrará el plan de transferencias calculado.
3. **Todas las partes implicadas deben confirmar** la liquidación para que los saldos se actualicen a cero.

### 10.3 Resultado esperado

- Todos los saldos del grupo quedan a **cero**.
- Cada miembro sabe exactamente a quién debe transferir dinero y en qué cantidad.

> **Importante:** La liquidación registra los pagos en la aplicación, pero **no realiza transferencias bancarias reales**. Los pagos deben efectuarse por medios externos (Bizum, transferencia, efectivo…).

---

## 11. Chat de grupo

### 11.1 Acceso al chat

El chat de cada grupo está disponible en la pestaña **"Chat"**, la tercera pestaña de la pantalla de detalle del grupo, junto a Balances y Gastos.

### 11.2 Enviar mensajes

1. Pulse en el campo de texto en la parte inferior de la pantalla.
2. Escriba su mensaje.
3. Pulse el botón de envío (icono de avión de papel) o pulse la tecla de envío del teclado.

### 11.3 Visualización de mensajes

- Sus propios mensajes aparecen **alineados a la derecha** con fondo en el color primario.
- Los mensajes de otros miembros aparecen **alineados a la izquierda** con la foto de perfil del autor, su nombre y la hora de envío.
- La pantalla hace scroll automático al último mensaje cuando llegan mensajes nuevos.

> Los mensajes son visibles para todos los miembros del grupo y se sincronizan en tiempo real.

---

## 12. Eliminación de gastos

### 12.1 Quién puede eliminar un gasto

**Solo el miembro que pagó el gasto originalmente** puede eliminarlo. Si usted no fue el pagador, no verá el botón de eliminación en ese gasto.

### 12.2 Proceso

1. Acceda a la pestaña **"Gastos"** del grupo.
2. Localice el gasto que desea eliminar.
3. Pulse el icono de eliminar (visible únicamente si usted fue el pagador).
4. Confirme en el diálogo de confirmación.

### 12.3 Actualización automática de balances

Una vez eliminado, los **balances de todos los participantes afectados se recalculan automáticamente** en tiempo real.

> **Advertencia:** La eliminación de un gasto es **irreversible**.

---

## 13. Gestión del perfil

Acceda al perfil desde el icono de usuario en la barra superior de la pantalla principal.

### 13.1 Cambiar nombre de usuario

1. Pulse sobre el campo de nombre de usuario.
2. Introduzca el nuevo nombre (máximo 15 caracteres, sin espacios).
3. Pulse **"Guardar"**.

El nuevo nombre será visible para todos los miembros de sus grupos de forma inmediata.

### 13.2 Cambiar número de teléfono

1. Pulse sobre el campo de teléfono.
2. Introduzca el nuevo número.
3. Pulse **"Guardar"**.

### 13.3 Cambiar fotografía de perfil

1. Pulse sobre su fotografía actual.
2. Seleccione una nueva imagen de la galería.
3. La foto se actualizará automáticamente en su perfil y en todos los grupos.

### 13.4 Cambiar idioma

Desde el perfil puede cambiar el idioma de la interfaz entre **español** e **inglés**. El cambio se aplica de forma inmediata sin necesidad de reiniciar la sesión.

---

## 14. Exportación de gastos

### 14.1 Generación del archivo CSV

1. Acceda al grupo cuyos gastos desea exportar.
2. Pulse la opción **"Exportar gastos"** o el icono de exportación en la barra superior.
3. La app generará el archivo y mostrará una confirmación con el nombre del fichero.

### 14.2 Ubicación del archivo

- En **Android 10 o superior:** el archivo se guarda automáticamente en la carpeta pública **Descargas** del dispositivo. Puede acceder a él desde el gestor de archivos o desde cualquier aplicación que lea el almacenamiento público.
- En **Android 9 o inferior:** el archivo se guarda en el almacenamiento externo privado de la app. La app mostrará la ruta exacta en el mensaje de confirmación.

### 14.3 Contenido del archivo

El CSV incluye:
- Fecha de cada gasto.
- Concepto o descripción.
- Importe total (en la moneda del grupo).
- Nombre del pagador.
- Distribución del gasto entre los participantes.

Compatible con Microsoft Excel, Google Sheets, LibreOffice Calc y cualquier editor de texto.

---

## 15. Preguntas frecuentes

### Inicio de sesión

**¿He olvidado mi contraseña. ¿Qué hago?**  
En la pantalla de inicio de sesión, pulse **"¿Has olvidado tu contraseña?"**, introduzca su correo y siga las instrucciones. Si no recibe el mensaje, compruebe la carpeta de spam.

**¿Puedo usar SplitApp en varios dispositivos?**  
Sí. Puede iniciar sesión desde cualquier Android compatible. Sus grupos, gastos y balances estarán sincronizados automáticamente.

---

### Grupos

**¿Cuántos grupos puedo crear?**  
No existe un límite establecido.

**Si abandono un grupo, ¿pierdo el historial?**  
Al abandonar dejará de tener acceso. El historial permanece intacto para el resto de miembros.

**¿Por qué no puedo abandonar o eliminar el grupo?**  
Si hay saldos pendientes, la acción está bloqueada. Liquide todas las deudas primero.

**¿Puedo añadir a alguien que no tiene cuenta?**  
No. La persona debe estar registrada en SplitApp para poder enviarle una invitación.

---

### Gastos

**¿Puedo editar un gasto ya registrado?**  
No. Si necesita corregir un gasto, elimínelo y vuelva a registrarlo con los datos correctos.

**¿Por qué no veo el botón de eliminar en algunos gastos?**  
Solo puede eliminar los gastos que usted pagó. Si otra persona fue el pagador, no dispondrá de esa opción.

**¿Qué ocurre si el reparto personalizado no suma el total?**  
La app mostrará un aviso y no permitirá guardar el gasto hasta que los valores cuadren.

**¿Los gastos se actualizan en tiempo real?**  
Sí. En cuanto un miembro registra un gasto, el resto lo ve reflejado inmediatamente.

---

### Balances

**¿Qué significa un balance de 0,00 €?**  
Que sus pagos y su parte en los gastos están perfectamente equilibrados: no debe ni le deben nada.

**¿Por qué ha cambiado mi balance si yo no he hecho nada?**  
Los balances cambian automáticamente cuando cualquier miembro registra, modifica o elimina un gasto en el que usted participa.

**¿La liquidación hace una transferencia bancaria?**  
No. Solo registra los pagos en la app. Los miembros deben realizar las transferencias reales por sus propios medios.

---

### Chat

**¿Pueden ver mis mensajes personas ajenas al grupo?**  
No. Solo los miembros activos del grupo tienen acceso al chat.

**¿Se conservan los mensajes si abandono el grupo?**  
Al abandonar el grupo perderá el acceso al chat. El historial de mensajes permanece en el servidor.

---

### Exportación

**¿Qué aplicación necesito para abrir el CSV?**  
Microsoft Excel, Google Sheets, LibreOffice Calc o cualquier editor de texto.

**¿El CSV incluye los balances?**  
No, solo el historial de gastos. Para consultar balances, use la pestaña Balances dentro del grupo.

---

## 16. Resolución de problemas

### No puedo iniciar sesión

1. **Credenciales incorrectas:** verifique el correo y la contraseña (distingue mayúsculas/minúsculas).
2. **Sin conexión:** compruebe que su dispositivo tiene acceso a Internet.
3. **Contraseña olvidada:** use la opción de recuperación en la pantalla de inicio de sesión.

### No veo un grupo al que pertenezco

1. **Sin conexión:** los grupos se cargan desde el servidor; sin Internet no están disponibles.
2. **Sesión desactualizada:** cierre la app completamente y vuelva a abrirla.
3. **Invitación pendiente:** compruebe si tiene una invitación sin aceptar para ese grupo.

### No puedo añadir miembros a un grupo

1. **Nombre incorrecto:** verifique la ortografía del nombre de usuario.
2. **Usuario no registrado:** la persona debe tener una cuenta activa en SplitApp.
3. **Usuario ya miembro:** si ya pertenece al grupo, no aparecerá en los resultados.

### No se genera el archivo CSV

1. **Sin conexión:** la exportación requiere descargar los datos del servidor.
2. **Sin gastos:** registre al menos un gasto antes de exportar.
3. **Android < 10 y permiso denegado:** conceda el permiso de almacenamiento en Ajustes → Aplicaciones → SplitApp → Permisos.

### Los mensajes del chat no aparecen

1. **Sin conexión:** el chat requiere Internet activo.
2. **No eres miembro activo:** compruebe que no ha abandonado el grupo.

---

## 17. Conclusión

SplitApp es una herramienta diseñada para hacer más sencilla y transparente la gestión del dinero en grupo. Su filosofía se basa en la simplicidad: cada función existe para resolver un problema concreto del día a día.

A lo largo de este manual se han descrito todas las funcionalidades: registro e inicio de sesión, gestión de grupos con sistema de invitaciones, registro y reparto de gastos, consulta de balances en tiempo real, liquidación optimizada con confirmación mutua, chat de grupo integrado, exportación del historial a Descargas y gestión del perfil con cambio de idioma.

SplitApp garantiza que la suma de los saldos es siempre cero, que los balances se actualizan en tiempo real y que la liquidación minimiza el número de transferencias necesarias. Todo ello con una experiencia de usuario fluida, accesible y disponible en español e inglés.

---

*DOCUMENTACIÓN COMPLETA — SplitApp v1.0*  
*Javier Aguilera Sánchez — DAM — Junio de 2026*
