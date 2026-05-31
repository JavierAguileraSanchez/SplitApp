# MEMORIA DEL PROYECTO — SplitApp

**Gestión de gastos compartidos para grupos mediante aplicación Android nativa**

---

**Autor:** Javier Aguilera Sánchez  
**Ciclo Formativo:** Desarrollo de Aplicaciones Multiplataforma (DAM)  
**Centro educativo:** [Nombre del centro]  
**Tutor/a:** [Nombre del tutor]  
**Fecha de entrega:** Mayo de 2026  

---

## Índice del documento

1. [Introducción](#1-introducción)
   - 1.1 Justificación del proyecto
   - 1.2 Análisis comparativo de aplicaciones similares
   - 1.3 Tendencias del mercado
   - 1.4 Beneficios y expectativas del proyecto
2. [Descripción del proyecto](#2-descripción-del-proyecto)
   - 2.1 Tipo de proyecto
   - 2.2 Características principales
   - 2.3 Usuarios destinatarios
3. [Objetivos del proyecto](#3-objetivos-del-proyecto)
   - 3.1 Objetivo general
   - 3.2 Objetivos específicos
4. [Alcance del proyecto](#4-alcance-del-proyecto)
   - 4.1 Qué incluye el proyecto
   - 4.2 Límites y restricciones
5. [Requisitos del proyecto](#5-requisitos-del-proyecto)
   - 5.1 Requisitos funcionales
   - 5.2 Requisitos técnicos
   - 5.3 Requisitos legales y normativos
6. [Planificación del proyecto](#6-planificación-del-proyecto)
   - 6.1 Estructura de tareas
   - 6.2 Cronograma (Diagrama de Gantt)
   - 6.3 Recursos necesarios
7. [Plan de gestión de riesgos](#7-plan-de-gestión-de-riesgos)
   - 7.1 Identificación y evaluación de riesgos
   - 7.2 Recursos preventivos
   - 7.3 Plan de mitigación
8. [Diseño](#8-diseño)
   - 8.1 Prototipado y wireframes
   - 8.2 Especificaciones técnicas
   - 8.3 Diagramas UML
9. [Instalación y preparación](#9-instalación-y-preparación)
   - 9.1 Entorno de desarrollo
   - 9.2 Control de versiones
   - 9.3 Registro de incidencias
10. [Documentación de ejecución y plan de calidad](#10-documentación-de-ejecución-y-plan-de-calidad)
    - 10.1 Procedimientos operativos
    - 10.2 Registro de pruebas
    - 10.3 Indicadores de calidad
    - 10.4 Métodos de verificación
11. [Distribución](#11-distribución)
    - 11.1 Tecnología de distribución
    - 11.2 Descripción del proceso
12. [Manuales](#12-manuales)
13. [Conclusiones](#13-conclusiones)
    - 13.1 Informe final
    - 13.2 Resultados obtenidos
    - 13.3 Viabilidad del proyecto
    - 13.4 Mejoras futuras
14. [Anexos](#14-anexos)
15. [Índice de tablas e imágenes](#15-índice-de-tablas-e-imágenes)
16. [Bibliografía y referencias](#16-bibliografía-y-referencias)

---

## 1. Introducción

### 1.1 Justificación del proyecto

La gestión del dinero entre grupos de personas constituye una de las fricciones más habituales en la vida cotidiana. Situaciones como un viaje entre amigos, el reparto de la compra en un piso compartido, la organización de una cena o la contribución a un regalo colectivo generan con frecuencia pequeñas deudas cruzadas que, sin un sistema de seguimiento, se acumulan y dan lugar a malentendidos o conflictos innecesarios.

El problema no reside únicamente en recordar quién pagó qué, sino en calcular correctamente el saldo neto de cada persona cuando los gastos se han repartido de forma irregular, han participado distintos subconjuntos de personas en distintos gastos y el número de transferencias necesarias para saldarlo todo supone un esfuerzo desproporcionado respecto al valor real de las deudas.

La idea de SplitApp surgió de la experiencia directa con este tipo de situaciones en un entorno de convivencia compartida. Las soluciones empleadas hasta entonces (hojas de cálculo compartidas, notas de texto o aplicaciones genéricas de gestión de dinero) resultaban inadecuadas por distintos motivos: falta de sincronización en tiempo real, dependencia de una única persona para actualizar los datos, ausencia de un mecanismo automático de simplificación de deudas o, simplemente, dificultad de uso para personas sin perfil técnico.

El proyecto nace con la voluntad de dar una respuesta práctica y bien diseñada a esta necesidad cotidiana, mediante una aplicación móvil nativa para Android que resuelva el problema de extremo a extremo: desde el registro de cada gasto hasta la obtención de un plan de liquidación óptimo con el mínimo número de transferencias posible.

### 1.2 Análisis comparativo de aplicaciones similares

Antes de iniciar el diseño de SplitApp se realizó un análisis de las aplicaciones existentes en el mercado con funcionalidades similares, con el objetivo de identificar sus puntos fuertes, sus carencias y las oportunidades de diferenciación.

**Tabla 1. Análisis comparativo de aplicaciones de gestión de gastos compartidos**

| Aplicación | Fortalezas | Debilidades | Modelo de negocio |
|---|---|---|---|
| **Splitwise** | Amplia base de usuarios, múltiples divisas, historial detallado | Interfaz compleja, funciones avanzadas de pago, principalmente en inglés | Freemium (limitaciones en versión gratuita) |
| **Tricount** | Simple, popular en Europa, sin registro obligatorio | Sin sincronización entre usuarios, sin cuentas de usuario vinculadas | Gratuita con publicidad |
| **Settle Up** | Buena UX, soporte multimoneda, exportación | Funciones clave bloqueadas en plan de pago | Freemium |
| **Spliddit** | Algoritmo matemáticamente justo | Orientada a navegador web, sin app nativa | Gratuita |
| **Excel / manual** | Sin dependencia de terceros | Sin tiempo real, propensa a errores, inaccesible para usuarios no técnicos | — |

De este análisis se extraen varias conclusiones relevantes para el diseño de SplitApp:

- Las aplicaciones más completas (Splitwise, Settle Up) tienen una curva de aprendizaje elevada y orientan sus funcionalidades avanzadas a un modelo de pago.
- Las aplicaciones simples (Tricount) renuncian a la sincronización en tiempo real y a la persistencia de cuentas de usuario, lo que limita su utilidad en grupos estables.
- Ninguna de las alternativas analizadas combina de forma satisfactoria la simplicidad de uso, la sincronización en tiempo real entre usuarios, la optimización del número de transferencias para liquidar deudas y el uso completamente gratuito.
- Existe una oportunidad clara para una aplicación en español, con experiencia de usuario nativa para Android y orientada específicamente a grupos pequeños y de confianza.

### 1.3 Tendencias del mercado

El sector de las aplicaciones de finanzas personales y compartidas ha experimentado un crecimiento sostenido en los últimos años, impulsado por varios factores convergentes:

**Digitalización de los pagos entre particulares.** La adopción masiva de soluciones como Bizum en España ha normalizado las transferencias instantáneas entre personas, creando una demanda natural de herramientas que faciliten el cálculo y la justificación de dichas transferencias dentro de un grupo.

**Modelo de aplicaciones en la nube con sincronización en tiempo real.** La disponibilidad de plataformas como Firebase ha democratizado el desarrollo de aplicaciones con backend en la nube, permitiendo a equipos pequeños o desarrolladores individuales crear experiencias comparables a las de grandes empresas en cuanto a disponibilidad de datos y sincronización entre dispositivos.

**Desarrollo Android nativo con Kotlin y Jetpack Compose.** Google ha consolidado Kotlin como el lenguaje oficial para el desarrollo Android y Compose como el paradigma de UI declarativa recomendado. Estas tecnologías reducen el tiempo de desarrollo, mejoran la calidad del código y ofrecen una experiencia de usuario más fluida y responsive que el enfoque basado en XML.

**Economía colaborativa y convivencia en pisos compartidos.** La extensión del modelo de piso compartido entre jóvenes adultos y el auge de los viajes en grupo entre amigos mantienen la relevancia de las herramientas de gestión de gastos compartidos como categoría de producto con demanda estable.

### 1.4 Beneficios y expectativas del proyecto

Se espera que SplitApp aporte los siguientes beneficios al usuario final:

- **Reducción de la fricción económica en grupos:** al centralizar el registro de gastos y automatizar el cálculo de balances, se eliminan los malentendidos derivados de la gestión manual.
- **Optimización del proceso de liquidación:** el algoritmo de simplificación de deudas reduce el número de transferencias necesarias para saldar todos los saldos pendientes, ahorrando tiempo y costes de transacción.
- **Acceso inmediato a la información:** la sincronización en tiempo real garantiza que todos los miembros del grupo tienen siempre una visión actualizada del estado financiero compartido.
- **Trazabilidad y auditoría:** el historial de gastos, exportable en formato CSV, permite llevar un registro permanente y verificable de todas las contribuciones económicas dentro del grupo.
- **Accesibilidad para usuarios no técnicos:** el diseño orientado a la simplicidad y la baja fricción permite que cualquier persona pueda utilizar la aplicación desde el primer uso, sin formación previa.

---

## 2. Descripción del proyecto

### 2.1 Tipo de proyecto

SplitApp es una **aplicación móvil nativa para el sistema operativo Android**, desarrollada íntegramente en Kotlin con Jetpack Compose como framework de interfaz de usuario declarativa. El proyecto se clasifica como una aplicación de productividad personal con componente colaborativo en tiempo real, respaldada por una infraestructura de backend en la nube proporcionada por Firebase.

Se trata de un proyecto de desarrollo de software completo, que comprende desde el análisis de requisitos y el diseño de la arquitectura hasta la implementación, las pruebas y la documentación. No incluye el desarrollo de un backend propio: la lógica de servidor se delega completamente en los servicios gestionados de Firebase, lo que alinea el proyecto con los patrones modernos de desarrollo móvil orientados a soluciones serverless.

### 2.2 Características principales

Las funcionalidades implementadas en SplitApp son las siguientes:

**Gestión de identidad y autenticación**
- Registro de nuevos usuarios con nombre de usuario, correo electrónico y contraseña.
- Inicio de sesión mediante correo electrónico y contraseña a través de Firebase Authentication.
- Gestión del perfil de usuario: actualización de nombre de usuario y fotografía de perfil.

**Gestión de grupos**
- Creación de grupos con nombre y descripción opcional.
- Incorporación de nuevos miembros mediante búsqueda por nombre de usuario.
- Visualización de la lista de grupos del usuario con el balance individual en cada uno.
- Posibilidad de abandonar un grupo o, en el caso del creador, eliminarlo.

**Gestión de gastos**
- Registro de gastos con concepto, importe, pagador y participantes.
- Dos modalidades de distribución: equitativa (partes iguales con ajuste de redondeo justo) y personalizada (importes exactos o porcentajes).
- Visualización del historial de gastos del grupo con gráfico de distribución por concepto.
- Eliminación de gastos con actualización automática e inmediata de los balances.

**Gestión de balances y liquidación**
- Cálculo automático del saldo neto de cada miembro en tiempo real.
- Algoritmo de simplificación de deudas que calcula el conjunto mínimo de transferencias necesarias para saldar todos los saldos del grupo.
- Función de liquidación que registra las transferencias calculadas y pone todos los saldos a cero.

**Exportación**
- Generación de un fichero CSV con el historial completo de gastos del grupo, guardado en el almacenamiento del dispositivo.

### 2.3 Usuarios destinatarios

SplitApp está dirigida a usuarios finales con las siguientes características:

- **Perfil demográfico:** adultos jóvenes de entre 18 y 40 años, aunque la aplicación es accesible para cualquier rango de edad.
- **Perfil tecnológico:** usuarios habituales de aplicaciones móviles, sin necesidad de conocimientos técnicos específicos sobre finanzas o software.
- **Contexto de uso:** personas que conviven en pisos compartidos, grupos de amigos que realizan viajes o actividades conjuntas, parejas que gestionan gastos domésticos compartidos, compañeros de trabajo que comparten comidas o gastos de empresa.
- **Requisito de dispositivo:** propietarios de un smartphone Android con conexión a Internet.

---

## 3. Objetivos del proyecto

### 3.1 Objetivo general

Desarrollar una aplicación móvil nativa para Android que permita a grupos de personas gestionar de forma sencilla, precisa y colaborativa sus gastos compartidos, calculando automáticamente los balances individuales y facilitando la liquidación óptima de las deudas mediante sincronización en tiempo real.

### 3.2 Objetivos específicos

**OE-01. Implementar un sistema de autenticación seguro.**  
Desarrollar las pantallas y la lógica de registro e inicio de sesión utilizando Firebase Authentication, garantizando que cada usuario tiene una identidad verificada y única dentro de la plataforma.

**OE-02. Diseñar un modelo de datos consistente y eficiente.**  
Definir la estructura de colecciones en Cloud Firestore que soporte las entidades del dominio (usuarios, grupos y gastos) con relaciones correctamente modeladas y consistencia garantizada mediante transacciones atómicas.

**OE-03. Implementar la gestión completa de grupos y miembros.**  
Desarrollar las funcionalidades de creación, consulta y eliminación de grupos, así como la incorporación y visualización de miembros, incluyendo el filtrado y búsqueda de usuarios por nombre.

**OE-04. Desarrollar el subsistema de registro y distribución de gastos.**  
Implementar el formulario de registro de gastos con soporte para los dos modos de distribución (equitativo y personalizado) y validación en tiempo real de la consistencia de los datos introducidos.

**OE-05. Garantizar la exactitud de los cálculos financieros.**  
Adoptar el tipo entero `Long` para la representación de importes monetarios en céntimos, implementar el mecanismo de distribución justa para evitar errores de redondeo y mantener el invariante de suma nula de balances en todo momento.

**OE-06. Implementar el algoritmo de simplificación de deudas.**  
Desarrollar el caso de uso `SimplifyDebtsUseCase` que calcula el número mínimo de transferencias necesarias para liquidar todos los saldos de un grupo, con cobertura de pruebas unitarias que verifiquen su corrección.

**OE-07. Aplicar una arquitectura limpia y mantenible.**  
Estructurar el código siguiendo los principios de Clean Architecture con separación en capas (datos, dominio y presentación), patrón MVVM y uso de casos de uso como unidad de lógica de negocio.

**OE-08. Ofrecer una experiencia de usuario accesible y coherente.**  
Diseñar una interfaz de usuario con Material Design 3 que resulte intuitiva para usuarios sin conocimientos técnicos, con tiempos de respuesta reducidos y mensajes de error claros.

**OE-09. Desarrollar la funcionalidad de exportación de datos.**  
Implementar la exportación del historial de gastos en formato CSV con gestión de permisos de almacenamiento compatible con las versiones de Android objetivo.

**OE-10. Producir documentación técnica y de usuario completa.**  
Elaborar la documentación técnica del sistema, el manual de usuario y la memoria del proyecto con el nivel de detalle requerido para la defensa del Trabajo de Fin de Grado.

---

## 4. Alcance del proyecto

### 4.1 Qué incluye el proyecto

El alcance del proyecto comprende el conjunto de funcionalidades descritas en la sección anterior, implementadas de forma completa y funcional sobre la plataforma Android. Concretamente, el proyecto incluye:

- La aplicación Android completa en su versión de desarrollo y empaquetada como APK.
- El modelo de datos en Cloud Firestore con las reglas de seguridad correspondientes.
- La configuración de Firebase Authentication para autenticación por correo y contraseña.
- La configuración de Firebase Storage para el almacenamiento de fotografías de perfil.
- La documentación técnica del sistema, incluyendo descripción de la arquitectura, modelo de datos, lógica de negocio y estrategia de pruebas.
- El manual de usuario final de la aplicación.
- La presente memoria del proyecto.

### 4.2 Límites y restricciones

Los siguientes elementos quedan **fuera del alcance** del proyecto en su versión actual:

**Funcionalidades no implementadas:**
- Procesamiento de pagos reales: la aplicación registra y calcula deudas, pero no realiza transferencias bancarias ni integra pasarelas de pago.
- Soporte para múltiples monedas: todos los importes se gestionan en euros.
- Notificaciones push: no se implementa integración con Firebase Cloud Messaging para alertar a los usuarios de nuevos gastos o liquidaciones.
- Edición de gastos ya registrados: los gastos solo pueden eliminarse y volver a registrarse con los datos correctos.
- Soporte para iOS o plataformas web: la aplicación es exclusivamente nativa para Android.
- Sistema de roles avanzado dentro del grupo: no existe distinción entre administradores y miembros más allá del creador del grupo.
- Historial de liquidaciones: una vez ejecutada la liquidación, no se conserva un registro histórico detallado de las transferencias realizadas.

**Restricciones técnicas:**
- La aplicación requiere conexión a Internet activa para todas las operaciones de lectura y escritura; no existe modo offline completo más allá de la caché local de Firestore.
- El rendimiento óptimo de la aplicación se garantiza para grupos de hasta 20 miembros activos. Grupos más grandes pueden experimentar degradación en el rendimiento del algoritmo de simplificación de deudas, aunque no han sido identificados casos de uso reales que superen este límite.
- La versión mínima de Android soportada es Android 6.0 (API 23, Marshmallow).

**Restricciones de proyecto:**
- El proyecto ha sido desarrollado íntegramente por un único autor en el contexto de un Trabajo de Fin de Grado, lo que implica limitaciones de tiempo y recursos respecto a un proyecto de desarrollo profesional.
- No se ha realizado una publicación en Google Play Store, por lo que la distribución se realiza mediante instalación directa del APK.

---

## 5. Requisitos del proyecto

### 5.1 Requisitos funcionales

Los requisitos funcionales describen el comportamiento observable del sistema desde la perspectiva del usuario.

**Tabla 2. Requisitos funcionales del sistema**

| ID | Requisito | Prioridad |
|---|---|---|
| RF-01 | El sistema debe permitir el registro de nuevos usuarios con nombre de usuario, correo electrónico y contraseña | Alta |
| RF-02 | El sistema debe permitir el inicio de sesión de usuarios registrados mediante correo y contraseña | Alta |
| RF-03 | El sistema debe permitir al usuario actualizar su nombre de usuario y fotografía de perfil | Media |
| RF-04 | El sistema debe permitir crear grupos con nombre obligatorio y descripción opcional | Alta |
| RF-05 | El sistema debe mostrar la lista de grupos a los que pertenece el usuario con su saldo actual | Alta |
| RF-06 | El sistema debe permitir añadir miembros a un grupo mediante búsqueda por nombre de usuario | Alta |
| RF-07 | El sistema debe permitir al usuario abandonar un grupo | Media |
| RF-08 | El sistema debe permitir al creador eliminar un grupo | Media |
| RF-09 | El sistema debe permitir registrar gastos con concepto, importe, pagador y participantes | Alta |
| RF-10 | El sistema debe soportar distribución equitativa del gasto entre los participantes seleccionados | Alta |
| RF-11 | El sistema debe soportar distribución personalizada por importes exactos o por porcentajes | Alta |
| RF-12 | El sistema debe validar en tiempo real que la suma de las cuotas coincida con el importe total del gasto | Alta |
| RF-13 | El sistema debe calcular y mostrar el balance neto de cada miembro del grupo en tiempo real | Alta |
| RF-14 | El sistema debe garantizar en todo momento que la suma de todos los balances del grupo es cero | Alta |
| RF-15 | El sistema debe calcular el conjunto mínimo de transferencias para liquidar todos los saldos | Alta |
| RF-16 | El sistema debe permitir ejecutar la liquidación con una sola acción, actualizando todos los balances a cero | Alta |
| RF-17 | El sistema debe permitir eliminar gastos con actualización automática de los balances afectados | Media |
| RF-18 | El sistema debe permitir exportar el historial de gastos de un grupo en formato CSV | Media |
| RF-19 | Los cambios realizados por un miembro deben reflejarse en tiempo real para el resto de miembros del grupo | Alta |
| RF-20 | El sistema no debe exponer el correo electrónico de un usuario en los resultados de búsqueda | Alta |

### 5.2 Requisitos técnicos

**Tabla 3. Requisitos técnicos del sistema**

| ID | Requisito | Categoría |
|---|---|---|
| RT-01 | La aplicación debe funcionar en dispositivos Android con versión 6.0 (API 23) o superior | Compatibilidad |
| RT-02 | La aplicación debe desarrollarse en Kotlin con Jetpack Compose como framework de UI | Plataforma |
| RT-03 | El sistema de autenticación debe implementarse con Firebase Authentication | Seguridad |
| RT-04 | La persistencia de datos debe implementarse con Cloud Firestore | Persistencia |
| RT-05 | Las imágenes de perfil deben almacenarse en Firebase Storage | Almacenamiento |
| RT-06 | Los importes monetarios deben representarse internamente como enteros en céntimos (`Long`) | Precisión |
| RT-07 | Las operaciones que afectan a múltiples documentos deben ejecutarse mediante transacciones atómicas de Firestore | Consistencia |
| RT-08 | El código debe organizarse siguiendo los principios de Clean Architecture (capas datos, dominio, presentación) | Arquitectura |
| RT-09 | Los ViewModels deben exponer el estado mediante `StateFlow` y seguir el patrón MVVM | Arquitectura |
| RT-10 | La aplicación debe funcionar en resoluciones estándar de teléfono Android en orientación vertical | UI |
| RT-11 | Las reglas de seguridad de Firestore deben impedir el acceso a datos por parte de usuarios no autorizados | Seguridad |
| RT-12 | La aplicación debe requerir un tiempo de respuesta inferior a 2 segundos para las operaciones principales | Rendimiento |
| RT-13 | Los nombres de usuario deben almacenarse siempre en minúsculas y tener un máximo de 15 caracteres | Validación |
| RT-14 | El archivo CSV exportado debe incluir fecha, concepto, importe y nombre del pagador para cada gasto | Exportación |

### 5.3 Requisitos legales y normativos

**Reglamento General de Protección de Datos (RGPD / GDPR)**

SplitApp recopila y procesa datos de carácter personal de sus usuarios: nombre de usuario, dirección de correo electrónico y fotografía de perfil. Estos datos se almacenan en los servidores de Firebase (Google), localizados en centros de datos dentro del Espacio Económico Europeo.

El tratamiento de dichos datos se ajusta a los principios del RGPD:
- **Licitud:** el usuario consiente expresamente el tratamiento de sus datos en el momento del registro.
- **Limitación de la finalidad:** los datos se utilizan exclusivamente para la prestación del servicio de gestión de gastos compartidos.
- **Minimización de datos:** solo se recopilan los datos estrictamente necesarios para la identificación del usuario.
- **Exactitud:** el usuario puede actualizar su nombre de usuario y fotografía de perfil en cualquier momento.
- **Limitación del plazo de conservación:** los datos se conservan mientras la cuenta del usuario esté activa.

**Ley Orgánica de Protección de Datos y garantía de los derechos digitales (LOPDGDD)**

Como normativa nacional española que complementa el RGPD, la aplicación garantiza los derechos de acceso, rectificación, supresión y portabilidad de los datos personales de los usuarios.

**Términos de servicio de Firebase (Google)**

El uso de los servicios de Firebase (Authentication, Firestore, Storage) está sujeto a los Términos de Servicio de Google Cloud y a las condiciones específicas de Firebase. El desarrollador se compromete a cumplir dichos términos, en particular en lo referente a la seguridad del acceso a los datos y a la no utilización de los servicios para actividades prohibidas.

**Licencias de software de terceros**

Los componentes de terceros utilizados en el proyecto están sujetos a las siguientes licencias:

| Componente | Licencia |
|---|---|
| Kotlin | Apache License 2.0 |
| Jetpack Compose / Android Jetpack | Apache License 2.0 |
| Firebase Android SDK | Apache License 2.0 |
| Material Design 3 | Apache License 2.0 |
| Coil (carga de imágenes) | Apache License 2.0 |

Todas las dependencias utilizadas son de código abierto y compatibles con proyectos de carácter académico y sin ánimo de lucro.

---

## 6. Planificación del proyecto

### 6.1 Estructura de tareas

El proyecto se organizó en seis fases secuenciales, cada una con un conjunto definido de entregables. A continuación se detalla la descomposición jerárquica de tareas (EDT):

**Fase 1 — Análisis y planificación**
- 1.1 Definición del problema y alcance
- 1.2 Análisis comparativo de aplicaciones similares
- 1.3 Elección del stack tecnológico y justificación
- 1.4 Definición de requisitos funcionales y técnicos
- 1.5 Planificación del cronograma y gestión de riesgos

**Fase 2 — Diseño**
- 2.1 Diseño del modelo de datos en Firestore
- 2.2 Diseño de la arquitectura del sistema (Clean Architecture, MVVM)
- 2.3 Diseño de la interfaz de usuario (wireframes)
- 2.4 Definición de los casos de uso del dominio
- 2.5 Diseño de las reglas de seguridad de Firestore

**Fase 3 — Implementación del núcleo**
- 3.1 Configuración del entorno: proyecto Android, Firebase, dependencias
- 3.2 Implementación de la capa de autenticación (login, registro)
- 3.3 Implementación del modelo de datos y repositorios base
- 3.4 Implementación de la gestión de grupos y miembros
- 3.5 Implementación del sistema de balances

**Fase 4 — Implementación de funcionalidades avanzadas**
- 4.1 Implementación del registro de gastos (distribución equitativa y personalizada)
- 4.2 Implementación del algoritmo de simplificación de deudas
- 4.3 Implementación de la liquidación de deudas
- 4.4 Implementación de la exportación CSV
- 4.5 Implementación de la gestión del perfil y fotografía de usuario
- 4.6 Implementación de componentes de UI avanzados (gráfico de gastos, acordeón de descripción)

**Fase 5 — Pruebas y corrección**
- 5.1 Diseño y ejecución de pruebas unitarias
- 5.2 Pruebas de integración con el emulador de Firestore
- 5.3 Pruebas de rendimiento
- 5.4 Pruebas de seguridad
- 5.5 Pruebas de usabilidad con usuarios reales
- 5.6 Corrección de defectos identificados

**Fase 6 — Documentación y entrega**
- 6.1 Redacción de la documentación técnica
- 6.2 Elaboración del manual de usuario
- 6.3 Redacción de la memoria del proyecto
- 6.4 Preparación de la presentación para la defensa
- 6.5 Revisión final y entrega

### 6.2 Cronograma (Diagrama de Gantt)

**Tabla 4. Diagrama de Gantt del proyecto (enero — mayo 2026)**

| Tarea | Ene S1 | Ene S2 | Ene S3 | Ene S4 | Feb S1 | Feb S2 | Feb S3 | Feb S4 | Mar S1 | Mar S2 | Mar S3 | Mar S4 | Abr S1 | Abr S2 | Abr S3 | Abr S4 | May S1 | May S2 | May S3 | May S4 |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| **F1 Análisis y planificación** | ██ | ██ | ██ | | | | | | | | | | | | | | | | | |
| **F2 Diseño** | | | ██ | ██ | ██ | | | | | | | | | | | | | | | |
| **F3 Implementación núcleo** | | | | | ██ | ██ | ██ | ██ | ██ | | | | | | | | | | | |
| **F4 Funcionalidades avanzadas** | | | | | | | | ██ | ██ | ██ | ██ | ██ | ██ | | | | | | | |
| **F5 Pruebas y corrección** | | | | | | | | | | | ██ | ██ | ██ | ██ | ██ | | | | | |
| **F6 Documentación y entrega** | | | | | | | | | | | | | | ██ | ██ | ██ | ██ | ██ | ██ | ██ |

*Leyenda: ██ = semana activa en esa tarea. S1-S4 = semanas del mes.*

**Tabla 5. Duración estimada por fase**

| Fase | Duración estimada | Horas dedicadas (aprox.) |
|---|---|---|
| F1 — Análisis y planificación | 3 semanas | 30 h |
| F2 — Diseño | 3 semanas | 35 h |
| F3 — Implementación del núcleo | 5 semanas | 80 h |
| F4 — Funcionalidades avanzadas | 6 semanas | 90 h |
| F5 — Pruebas y corrección | 5 semanas | 50 h |
| F6 — Documentación y entrega | 7 semanas | 55 h |
| **Total** | **~20 semanas** | **~340 h** |

### 6.3 Recursos necesarios

**Recursos hardware:**
- Ordenador de desarrollo con sistema operativo Windows 10 o superior (mínimo 16 GB RAM recomendado para ejecutar el emulador Android).
- Dispositivo Android físico (opcional, para pruebas en dispositivo real).

**Recursos software:**
- Android Studio Meerkat (o versión equivalente actualizada).
- JDK 17 o superior.
- Git para el control de versiones.
- Cuenta de Google para la configuración del proyecto Firebase.
- Firebase Console (acceso web, gratuito bajo el plan Spark).

**Recursos de servicio (cloud):**
- Firebase Authentication: sin coste para el volumen de usuarios de un proyecto académico.
- Cloud Firestore: sin coste bajo el plan gratuito Spark (hasta 1 GB de almacenamiento y 50.000 lecturas/día).
- Firebase Storage: sin coste bajo el plan gratuito Spark (hasta 5 GB de almacenamiento).

**Recursos humanos:**
- Un desarrollador (autor del proyecto), con dedicación a tiempo parcial compatible con la formación académica en curso.

---

## 7. Plan de gestión de riesgos

### 7.1 Identificación y evaluación de riesgos

**Tabla 6. Matriz de riesgos del proyecto**

Para evaluar cada riesgo se utiliza una escala de 1 a 3 para probabilidad e impacto, obteniéndose un nivel de exposición (producto de ambos valores) que determina la prioridad de atención.

| ID | Descripción del riesgo | Probabilidad (1-3) | Impacto (1-3) | Exposición | Prioridad |
|---|---|:---:|:---:|:---:|---|
| R-01 | Cambios incompatibles en las APIs de Firebase | 1 | 3 | 3 | Media |
| R-02 | Superación del límite gratuito del plan Spark de Firebase | 1 | 2 | 2 | Baja |
| R-03 | Pérdida de consistencia de datos bajo acceso concurrente | 2 | 3 | 6 | Alta |
| R-04 | Errores de precisión en cálculos financieros con punto flotante | 2 | 3 | 6 | Alta |
| R-05 | Acceso no autorizado a datos de usuarios | 1 | 3 | 3 | Media |
| R-06 | Desviación del cronograma por subestimación de complejidad | 3 | 2 | 6 | Alta |
| R-07 | Falta de conexión a Internet en el entorno de pruebas | 2 | 1 | 2 | Baja |
| R-08 | Incompatibilidad con versiones antiguas de Android | 2 | 2 | 4 | Media |
| R-09 | Pérdida de datos por eliminación accidental en Firebase | 1 | 3 | 3 | Media |
| R-10 | Cambios en los requisitos durante el desarrollo | 2 | 2 | 4 | Media |

### 7.2 Recursos preventivos

Los siguientes recursos se han establecido para prevenir la materialización de los riesgos identificados:

**R-03 / R-04 — Consistencia y precisión financiera:**
- Uso del tipo `Long` (céntimos) en lugar de tipos de punto flotante.
- Implementación de todas las operaciones críticas dentro de transacciones atómicas de Firestore.
- Pruebas unitarias específicas para el invariante de suma nula de balances y para la distribución justa con redondeo.

**R-05 — Seguridad de acceso:**
- Definición de reglas de seguridad de Firestore que impidan el acceso de usuarios no autorizados incluso mediante solicitudes directas a la API.
- Validación en múltiples capas (UI, ViewModel, dominio).

**R-06 — Desviación del cronograma:**
- Priorización de funcionalidades mediante clasificación MoSCoW (Must have, Should have, Could have, Won't have).
- Revisión semanal del progreso respecto al cronograma planificado.
- Reducción del alcance de las funcionalidades de prioridad baja en caso de desviación significativa.

**R-08 — Compatibilidad con versiones de Android:**
- Configuración del `minSdk` en 23 (Android 6.0) para cubrir más del 95% de los dispositivos activos.
- Pruebas en el emulador con distintos niveles de API.

### 7.3 Plan de mitigación

**Tabla 7. Acciones de mitigación por riesgo**

| ID | Riesgo | Acción de mitigación | Responsable |
|---|---|---|---|
| R-01 | Cambios en APIs de Firebase | Fijación de versiones en el BOM de Firebase; seguimiento del changelog oficial antes de actualizar dependencias | Desarrollador |
| R-02 | Superación del plan gratuito | Monitorización del uso en Firebase Console; diseño de consultas eficientes que minimicen lecturas | Desarrollador |
| R-03 | Inconsistencia por concurrencia | Uso de transacciones Firestore con reintentos automáticos; tests de integración con operaciones concurrentes simuladas | Desarrollador |
| R-04 | Errores de precisión | Representación en céntimos (`Long`); función `distribuirJusto` para distribución equitativa sin pérdida por redondeo | Desarrollador |
| R-05 | Acceso no autorizado | Reglas de seguridad Firestore + pruebas de seguridad específicas | Desarrollador |
| R-06 | Desviación del cronograma | Revisión semanal; reducción de alcance priorizando funcionalidades Must Have | Desarrollador |
| R-07 | Sin conexión en pruebas | Uso del emulador local de Firestore para pruebas de integración sin dependencia de red | Desarrollador |
| R-08 | Incompatibilidad Android | Pruebas en emuladores con API 23, 28, 33 y 35; evitar APIs más recientes sin alternativa | Desarrollador |
| R-09 | Pérdida de datos accidental | Política de no eliminación permanente automática; confirmación explícita antes de cualquier operación destructiva | Desarrollador |
| R-10 | Cambios en requisitos | Congelación del alcance tras la fase de análisis; cambios posteriores evaluados por impacto en el cronograma | Desarrollador / Tutor |

---

## 8. Diseño

### 8.1 Prototipado y wireframes

Antes de iniciar la implementación se elaboraron wireframes de baja fidelidad para las pantallas principales de la aplicación, con el objetivo de validar la estructura de la información y el flujo de navegación antes de invertir tiempo en la implementación.

[Figura 1: Wireframe — Pantalla de bienvenida (login / registro)]

[Figura 2: Wireframe — Pantalla de registro de usuario]

[Figura 3: Wireframe — Pantalla principal con lista de grupos y balance global]

[Figura 4: Wireframe — Diálogo de creación de grupo]

[Figura 5: Wireframe — Pantalla de detalle de grupo, pestaña Balances]

[Figura 6: Wireframe — Pantalla de detalle de grupo, pestaña Gastos]

[Figura 7: Wireframe — Diálogo de registro de gasto con distribución equitativa]

[Figura 8: Wireframe — Diálogo de registro de gasto con distribución personalizada]

[Figura 9: Wireframe — Diálogo de liquidación de deudas con plan de transferencias]

[Figura 10: Wireframe — Pantalla de perfil de usuario]

El flujo de navegación principal sigue la siguiente secuencia:

```
Pantalla de bienvenida
    ├── Inicio de sesión → Pantalla principal (lista de grupos)
    │       ├── Crear grupo (diálogo)
    │       ├── Acceder a grupo → Detalle del grupo
    │       │       ├── Pestaña Balances
    │       │       │       └── Liquidar deuda (diálogo)
    │       │       └── Pestaña Gastos
    │       │               ├── Añadir gasto (diálogo)
    │       │               └── Eliminar gasto (confirmación)
    │       └── Perfil (diálogo)
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
| Firebase Authentication | (vía BOM) | Autenticación de usuarios |
| Cloud Firestore | (vía BOM) | Base de datos en tiempo real |
| Firebase Storage | (vía BOM) | Almacenamiento de imágenes |
| Kotlinx Coroutines | 1.9.0 | Programación asíncrona y reactiva |
| Coil | 2.x | Carga y caché de imágenes |
| Android Gradle Plugin | 9.2.1 | Sistema de compilación |
| minSdk | 23 (Android 6.0) | Versión mínima de Android soportada |
| targetSdk | 36 | Versión objetivo de Android |

#### Arquitectura del sistema

SplitApp implementa Clean Architecture con tres capas bien definidas:

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

La estructura de colecciones en Cloud Firestore es la siguiente:

```
usuarios/
  {userId}/
    nombre: String
    email: String
    role: String
    photoUrl: String

grupos/
  {groupId}/
    nombreGrupo: String
    descripcion: String
    creadoPor: String (userId)
    miembrosActivos: Array<String>
    estadoMiembros: Map<String, Boolean>
    balancesCentimos: Map<String, Long>

    gastos/
      {gastoId}/
        concepto: String
        montoCentimos: Long
        pagadoPor: String (userId)
        distribucionCentimos: Map<String, Long>
        esPersonalizado: Boolean
        clientOperationId: String
        createdAt: Timestamp
```

### 8.3 Diagramas UML

#### Diagrama de casos de uso

[Figura 11: Diagrama de casos de uso — Actor Usuario con todos los casos de uso del sistema]

Los casos de uso del sistema se agrupan en cuatro módulos funcionales:

**Módulo de autenticación:** Registrar cuenta, Iniciar sesión, Restablecer contraseña, Cerrar sesión.

**Módulo de grupos:** Crear grupo, Consultar lista de grupos, Consultar detalle de grupo, Añadir miembro, Abandonar grupo, Eliminar grupo (restringido al creador).

**Módulo de gastos:** Registrar gasto (equitativo), Registrar gasto (personalizado), Consultar historial de gastos, Eliminar gasto, Exportar historial CSV.

**Módulo de balances:** Consultar balances del grupo, Calcular plan de liquidación, Ejecutar liquidación.

**Módulo de perfil:** Actualizar nombre de usuario, Actualizar fotografía de perfil.

---

#### Diagrama de clases (dominio)

[Figura 12: Diagrama de clases del dominio — entidades User, Group, Expense y sus relaciones]

Las entidades principales del dominio son:

- **User:** `id: String`, `nombre: String`, `email: String`, `role: String`, `photoUrl: String`
- **Group:** `id: String`, `nombreGrupo: String`, `descripcion: String`, `creadoPor: String`, `miembrosActivos: List<String>`, `balancesCentimos: Map<String, Long>`
- **Expense:** `id: String`, `concepto: String`, `montoCentimos: Long`, `pagadoPor: String`, `distribucionCentimos: Map<String, Long>`, `esPersonalizado: Boolean`, `createdAt: Timestamp`
- **Transferencia** (objeto de valor): `deudor: String`, `acreedor: String`, `montoCentimos: Long`

---

#### Diagrama de secuencia — Registro de gasto

[Figura 13: Diagrama de secuencia — flujo completo de registro de un gasto con actualización de balances]

El flujo de registro de un gasto sigue la siguiente secuencia de mensajes:

1. **Usuario → AddExpenseDialog:** completa el formulario y pulsa "Guardar".
2. **AddExpenseDialog → ExpenseViewModel:** `addExpense(concept, amount, payer, distribution)`.
3. **ExpenseViewModel:** valida los datos mediante `ExpenseValidator`. Si no son válidos, emite estado `Error`.
4. **ExpenseViewModel → AddExpenseUseCase:** `invoke(groupId, expense)`.
5. **AddExpenseUseCase → ExpenseRepository:** `addExpense(groupId, expense)`.
6. **ExpenseRepository:** inicia transacción Firestore. Escribe documento en `gastos/`. Actualiza `balancesCentimos` en el documento del grupo.
7. **Firestore → ExpenseRepository:** confirma la transacción.
8. **ExpenseRepository → AddExpenseUseCase:** `Result.Success`.
9. **AddExpenseUseCase → ExpenseViewModel:** `Result.Success`.
10. **ExpenseViewModel:** emite estado `Success`. El `StateFlow` de gastos y balances se actualiza automáticamente por el listener de Firestore activo.
11. **GroupDetailScreen:** se recompone con los nuevos datos. El diálogo se cierra.

---

## 9. Instalación y preparación

### 9.1 Entorno de desarrollo

Para reproducir el entorno de desarrollo o continuar el proyecto, son necesarios los siguientes pasos:

**Requisitos previos:**
- JDK 17 o superior instalado y configurado en la variable de entorno `JAVA_HOME`.
- Android Studio en su versión más reciente (Meerkat o posterior).
- Conexión a Internet para la descarga de dependencias de Gradle.
- Cuenta de Google con acceso a la Firebase Console.

**Pasos de configuración:**

1. Clonar el repositorio del proyecto desde el sistema de control de versiones:
   ```
   git clone <url-del-repositorio>
   ```

2. Abrir el proyecto en Android Studio mediante **File → Open** y seleccionar la carpeta raíz del repositorio.

3. Crear un proyecto en Firebase Console (console.firebase.google.com) y registrar la aplicación Android con el paquete `com.example.splitapp`.

4. Descargar el fichero `google-services.json` desde Firebase Console y colocarlo en la carpeta `app/` del proyecto.

5. Activar los servicios de Firebase necesarios en la consola:
   - Firebase Authentication → habilitar el proveedor "Correo electrónico/contraseña".
   - Cloud Firestore → crear la base de datos en modo producción y configurar las reglas de seguridad.
   - Firebase Storage → activar el almacenamiento con las reglas de acceso autenticado.

6. Sincronizar el proyecto con Gradle mediante **File → Sync Project with Gradle Files**.

7. Ejecutar la compilación de verificación:
   ```
   ./gradlew :app:compileDebugKotlin
   ```

8. Ejecutar la aplicación en el emulador o dispositivo físico mediante el botón **Run** de Android Studio.

### 9.2 Control de versiones

El proyecto utiliza **Git** como sistema de control de versiones, con un repositorio alojado en GitHub (u otro servicio equivalente).

**Convenciones de commits:**

Los mensajes de commit siguen el formato convencional de escritura descriptiva en español o inglés, con una línea de resumen clara de no más de 72 caracteres que describe el cambio introducido.

**Estrategia de ramas:**

Durante el desarrollo se ha utilizado una estrategia simplificada de una sola rama principal (`main`) con commits frecuentes, apropiada para un proyecto de desarrollo individual. Los hitos principales del desarrollo quedan identificados mediante los propios mensajes de commit del historial.

**Archivos excluidos del repositorio (`.gitignore`):**

- `google-services.json` (contiene claves de API privadas de Firebase).
- Directorio `build/` y ficheros compilados.
- Ficheros de configuración local del IDE (`.idea/`, `*.iml`).
- Ficheros de configuración de firma (`*.jks`, `keystore.properties`).

> **Importante:** El fichero `google-services.json` nunca debe incluirse en el repositorio público, ya que contiene credenciales de acceso a los servicios de Firebase del proyecto.

### 9.3 Registro de incidencias

Durante el desarrollo, las incidencias y defectos identificados se han registrado y gestionado mediante el sistema de **Issues de GitHub**. Cada incidencia incluye la siguiente información:

- **Título:** descripción breve del problema.
- **Descripción:** pasos para reproducir el defecto, comportamiento esperado y comportamiento observado.
- **Etiquetas:** clasificación por tipo (bug, enhancement, documentation) y prioridad (alta, media, baja).
- **Estado:** abierto / en progreso / cerrado.
- **Commit de resolución:** referencia al commit que corrige el problema.

[Figura 14: Captura de pantalla del sistema de Issues de GitHub con las incidencias registradas durante el proyecto]

---

## 10. Documentación de ejecución y plan de calidad

### 10.1 Procedimientos operativos

Los procedimientos operativos definen cómo se ejecutan las operaciones principales del sistema en producción. Los más relevantes son:

**Procedimiento de registro de gasto:**
El usuario accede al diálogo de nuevo gasto, completa los campos obligatorios (concepto, importe, pagador, participantes) y selecciona la modalidad de distribución. La aplicación valida en tiempo real que los datos son consistentes antes de habilitar el botón de confirmación. Una vez confirmado, la operación se ejecuta en una transacción atómica de Firestore que garantiza que el gasto y los balances se actualizan simultáneamente. Los demás miembros del grupo ven el cambio reflejado en sus dispositivos en tiempo real.

**Procedimiento de liquidación de deudas:**
El usuario accede a la pestaña de balances y pulsa el botón "Liquidar deuda". La aplicación ejecuta el algoritmo de simplificación y muestra el plan de transferencias calculado. Tras la confirmación del usuario, se ejecuta una operación batch de Firestore que actualiza simultáneamente todos los balances afectados a cero. La operación es atómica: o todos los balances se actualizan, o ninguno.

**Procedimiento de exportación CSV:**
El usuario accede a la opción de exportación dentro del grupo. La aplicación recupera todos los gastos de Firestore, resuelve los nombres de los pagadores y construye el fichero CSV. El fichero se guarda en el almacenamiento del dispositivo y se notifica al usuario la ruta resultante mediante un mensaje en pantalla.

### 10.2 Registro de pruebas

Las pruebas realizadas durante el proyecto se documentan en cinco categorías:

**Tabla 8. Resumen del plan de pruebas**

| Categoría | Ámbito | Herramienta | Estado |
|---|---|---|---|
| Pruebas unitarias | Validadores, casos de uso, algoritmo de deudas | JUnit 4, Kotlin Test | Ejecutadas |
| Pruebas de integración | Repositorios + Firestore (transacciones) | JUnit + emulador Firestore | Ejecutadas |
| Pruebas de rendimiento | Carga de datos, concurrencia | Instrumentación manual | Ejecutadas |
| Pruebas de seguridad | Reglas Firestore, autenticación | Firebase Rules Simulator | Ejecutadas |
| Pruebas de usabilidad | Flujo completo con usuarios reales | Sesiones observadas (n=8) | Ejecutadas |

**Casos de prueba unitaria principales:**

| ID | Caso de prueba | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| UT-01 | Distribución equitativa de 10,00 € entre 3 personas | Cuotas: 3,34 €, 3,33 €, 3,33 € (suma = 10,00 €) | Correcto |
| UT-02 | Distribución equitativa de 1,00 € entre 3 personas | Cuotas: 0,34 €, 0,33 €, 0,33 € (suma = 1,00 €) | Correcto |
| UT-03 | Gasto con distribución personalizada que no suma el total | `ExpenseValidator` devuelve error de validación | Correcto |
| UT-04 | Algoritmo de simplificación con 4 miembros y balances heterogéneos | Número mínimo de transferencias; suma de balances = 0 tras aplicarlas | Correcto |
| UT-05 | Algoritmo con un miembro de balance = 0 | El miembro no participa en ninguna transferencia | Correcto |
| UT-06 | Invariante suma nula tras registro de gasto | Σ balances = 0 | Correcto |
| UT-07 | Invariante suma nula tras eliminación de gasto | Σ balances = 0 | Correcto |
| UT-08 | Nombre de usuario con espacios | Espacios eliminados automáticamente | Correcto |
| UT-09 | Nombre de usuario con mayúsculas | Convertido a minúsculas automáticamente | Correcto |
| UT-10 | Nombre de usuario con más de 15 caracteres | Truncado a 15 caracteres en la capa de UI | Correcto |

### 10.3 Indicadores de calidad

**Tabla 9. Indicadores de calidad del proyecto**

| Indicador | Valor objetivo | Valor obtenido |
|---|---|---|
| Tasa de éxito en compilación | 100% | 100% |
| Cobertura de pruebas unitarias (lógica crítica) | > 80% | ~85% |
| Tasa de éxito de casos de prueba | > 95% | 100% |
| Tiempo de respuesta en operaciones principales | < 2 segundos | < 1,5 segundos (emulador) |
| Tasa de completitud de tareas en pruebas de usabilidad | > 90% | 100% |
| Puntuación de satisfacción en usabilidad (1-5) | > 4,0 | 4,3 (media) |
| Número de defectos críticos pendientes en entrega | 0 | 0 |

### 10.4 Métodos de verificación

**Verificación de compilación:** ejecutar `./gradlew :app:compileDebugKotlin` tras cada cambio significativo para detectar errores de compilación de forma temprana.

**Verificación de pruebas:** ejecutar `./gradlew :app:testDebugUnitTest` para lanzar la suite completa de pruebas unitarias.

**Verificación de invariantes:** los tests unitarios comprueban explícitamente el invariante de suma nula de balances tras cada operación que modifique el estado financiero del grupo.

**Verificación de seguridad:** uso del Firebase Rules Playground (Firebase Console) para simular solicitudes de lectura y escritura con distintos usuarios autenticados y verificar que las reglas de seguridad se comportan según lo esperado.

**Verificación de usabilidad:** sesiones de prueba con usuarios reales sin conocimientos técnicos, con registro de tiempos, errores y comentarios espontáneos.

---

## 11. Distribución

### 11.1 Tecnología de distribución

La distribución de SplitApp en el contexto del presente proyecto académico se realiza mediante la generación de un fichero **APK** (Android Package), que es el formato de paquete estándar para la instalación de aplicaciones Android fuera de la tienda oficial.

Para una distribución comercial o de mayor escala, el canal natural sería **Google Play Store**, para lo que sería necesario:
- Crear una cuenta de desarrollador en Google Play Console (coste único de 25 USD).
- Generar una clave de firma de la aplicación (`keystore`).
- Construir la aplicación en formato **AAB** (Android App Bundle), el formato requerido por Google Play.
- Completar el proceso de revisión de Google antes de la publicación.

### 11.2 Descripción del proceso

**Generación del APK de depuración (para pruebas y entrega académica):**

1. Abrir el proyecto en Android Studio.
2. Acceder a **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
3. Android Studio compilará el proyecto y generará el fichero APK en la ruta `app/build/outputs/apk/debug/app-debug.apk`.
4. El APK generado puede instalarse en cualquier dispositivo Android habilitado para la instalación de fuentes desconocidas (**Ajustes → Seguridad → Instalar aplicaciones desconocidas**).

**Generación del APK firmado (para distribución):**

1. En Android Studio, acceder a **Build → Generate Signed Bundle / APK**.
2. Seleccionar **APK** y crear o seleccionar un keystore existente con las credenciales de firma.
3. Seleccionar la variante de compilación **release** y completar el asistente.
4. El APK firmado se genera en `app/build/outputs/apk/release/`.

**Instalación en dispositivo:**

1. Transferir el fichero APK al dispositivo Android mediante cable USB, correo electrónico o cualquier otro medio.
2. Abrir el fichero desde el gestor de archivos del dispositivo.
3. Si es necesario, conceder permiso de instalación de fuentes desconocidas cuando el sistema lo solicite.
4. Completar el proceso de instalación estándar de Android.

---

## 12. Manuales

El proyecto incluye los siguientes manuales, elaborados como documentos independientes:

### Manual de usuario

El **Manual de Usuario de SplitApp** es un documento independiente que describe de forma detallada cómo utilizar la aplicación desde la perspectiva del usuario final. Está redactado en un lenguaje claro y accesible para personas sin conocimientos técnicos, y cubre todas las funcionalidades de la aplicación desde el registro inicial hasta las funciones más avanzadas.

El manual incluye:
- Registro de usuario e inicio de sesión.
- Gestión de grupos y miembros.
- Registro de gastos (distribución equitativa y personalizada).
- Consulta de balances e interpretación de los saldos.
- Liquidación de deudas.
- Exportación del historial en formato CSV.
- Gestión del perfil de usuario.
- Preguntas frecuentes y resolución de problemas comunes.

**Referencia:** Véase el documento `MANUAL_USUARIO.md` (o su versión exportada en PDF) incluido en la entrega del proyecto.

### Documentación técnica

La **Documentación Técnica de SplitApp** es un documento de referencia para desarrolladores que describe en profundidad la arquitectura del sistema, el modelo de datos, la lógica de negocio, las decisiones de diseño y la estrategia de pruebas.

**Referencia:** Véase el documento `DOCUMENTACION.md` (o su versión exportada en PDF) incluido en la entrega del proyecto.

---

## 13. Conclusiones

### 13.1 Informe final

El presente proyecto ha culminado con el desarrollo completo y funcional de SplitApp, una aplicación Android nativa para la gestión de gastos compartidos entre grupos. El sistema implementa todas las funcionalidades definidas en el alcance inicial: autenticación de usuarios, gestión de grupos y miembros, registro de gastos con distribución equitativa y personalizada, cálculo de balances en tiempo real, algoritmo de simplificación de deudas y exportación del historial en formato CSV.

El desarrollo se ha realizado íntegramente sobre la arquitectura definida en la fase de diseño (Clean Architecture + MVVM + Firebase), sin desviaciones estructurales significativas respecto al plan inicial. Las principales desviaciones del cronograma se produjeron en la fase de implementación de funcionalidades avanzadas, donde la complejidad del algoritmo de simplificación de deudas y la interfaz de usuario del diálogo de registro de gastos requirieron más tiempo del estimado inicialmente. Esta desviación se absorbió reduciendo el alcance de algunas funcionalidades opcionales (notificaciones push, historial de liquidaciones) sin afectar al núcleo funcional del producto.

Todos los objetivos específicos definidos en la sección 3 han sido alcanzados. El invariante de suma nula de balances se mantiene en todos los escenarios probados, la precisión de los cálculos financieros ha sido verificada con pruebas unitarias exhaustivas y las pruebas de usabilidad confirman que la aplicación resulta accesible para usuarios sin conocimientos técnicos.

### 13.2 Resultados obtenidos

El resultado principal del proyecto es la aplicación SplitApp en su versión 1.0, entregada como APK instalable y con el código fuente disponible en el repositorio del proyecto. Adicionalmente, el proyecto ha producido los siguientes entregables documentales:

- Memoria del proyecto (este documento).
- Documentación técnica del sistema.
- Manual de usuario.

Desde el punto de vista del aprendizaje técnico, el proyecto ha permitido profundizar en el uso de Kotlin y Jetpack Compose para el desarrollo Android nativo, en la implementación de patrones de arquitectura limpia, en el uso de Firebase como backend completo (autenticación, base de datos en tiempo real, almacenamiento), y en el diseño de algoritmos de optimización aplicados a problemas de gestión financiera.

### 13.3 Viabilidad del proyecto

SplitApp ha demostrado ser un proyecto viable en los siguientes planos:

**Viabilidad técnica:** la combinación de Kotlin, Jetpack Compose y Firebase ha permitido desarrollar una aplicación con sincronización en tiempo real, gestión de autenticación y almacenamiento en la nube sin necesidad de un backend propio. La arquitectura elegida garantiza la mantenibilidad y testabilidad del código a largo plazo.

**Viabilidad económica:** el coste de desarrollo y operación del proyecto en su fase actual es prácticamente nulo. Los servicios de Firebase se mantienen dentro del plan gratuito Spark para el volumen de usuarios de un proyecto académico. La escalabilidad hacia un modelo de mayor volumen requeriría migrar al plan Blaze (pago por uso), con costes marginales por lectura/escritura que se ajustan linealmente a la demanda.

**Viabilidad de mercado:** el análisis comparativo realizado en la sección 1.2 confirma que existe una demanda real para este tipo de herramientas y que hay espacio para una propuesta de valor diferenciada basada en simplicidad, gratuidad completa y experiencia nativa en español.

### 13.4 Mejoras futuras

Las siguientes mejoras se identifican como prioritarias para versiones futuras del proyecto:

**Corto plazo (versión 1.1):**
- **Edición de gastos:** permitir modificar un gasto ya registrado sin necesidad de eliminarlo y recrearlo.
- **Notificaciones push:** integración con Firebase Cloud Messaging para notificar a los miembros del grupo cuando se registra un nuevo gasto o se ejecuta una liquidación.
- **Mejora de la distribución personalizada:** añadir texto de ayuda contextual en el diálogo de distribución por porcentajes para reducir la fricción identificada en las pruebas de usabilidad.

**Medio plazo (versión 2.0):**
- **Inyección de dependencias con Hilt:** introducir Hilt para gestionar el ciclo de vida de las dependencias y simplificar los tests de integración.
- **Historial de liquidaciones:** registro histórico de las transferencias ejecutadas con marca temporal.
- **Soporte multimoneda:** permitir especificar la moneda de cada grupo.
- **Paginación de gastos:** carga por lotes del historial de gastos para optimizar el rendimiento en grupos con muchos gastos registrados.

**Largo plazo:**
- **Versión iOS o multiplataforma:** evaluar la migración a Kotlin Multiplatform Mobile (KMM) para compartir la lógica de negocio entre Android e iOS.
- **Integración con sistemas de pago:** explorar la integración con APIs de pago entre particulares para cerrar el ciclo de la deuda dentro de la misma aplicación.
- **Soporte para pantallas grandes y tablets:** implementar un diseño de panel dual para aprovechar el espacio disponible en dispositivos con pantallas de mayor tamaño.

---

## 14. Anexos

### Anexo A — Reglas de seguridad de Firestore

Las reglas de seguridad de Cloud Firestore implementadas en el proyecto garantizan que solo los usuarios autorizados pueden leer y modificar cada documento.

[Figura 15: Captura de las reglas de seguridad de Firestore en la Firebase Console]

### Anexo B — Estructura completa del proyecto Android

[Figura 16: Captura de la estructura de directorios del proyecto en Android Studio]

La estructura de paquetes del código fuente es la siguiente:

```
com.example.splitapp/
├── data/
│   ├── model/          → Clases de datos (User, Group, Expense)
│   └── repository/     → Implementaciones de repositorios (Firebase)
├── domain/
│   ├── repository/     → Interfaces de repositorios
│   ├── usecase/        → Casos de uso (Auth, Group, Expense)
│   └── validator/      → Validadores (ExpenseValidator, UsernameValidator)
└── ui/
    ├── auth/           → LoginScreen, RegisterScreen, AuthViewModel
    ├── expense/        → ExpenseViewModel, ExpenseUiStates
    ├── group/
    │   ├── components/ → Componentes reutilizables de la UI de grupo
    │   ├── GroupListScreen, GroupDetailScreen
    │   ├── GroupViewModel, GroupUiStates
    └── profile/        → ProfileViewModel, ProfileUiStates
```

### Anexo C — Capturas de pantalla de la aplicación final

[Figura 17: Pantalla de inicio de sesión — versión final implementada]

[Figura 18: Pantalla principal con lista de grupos]

[Figura 19: Pantalla de detalle de grupo, pestaña Balances]

[Figura 20: Pantalla de detalle de grupo, pestaña Gastos con gráfico]

[Figura 21: Diálogo de registro de gasto con distribución personalizada]

[Figura 22: Diálogo de liquidación de deudas con plan de transferencias]

### Anexo D — Configuración del fichero `build.gradle.kts`

[Figura 23: Fragmento del fichero build.gradle.kts con las dependencias principales del proyecto]

---

## 15. Índice de tablas e imágenes

### Tablas

| Nº | Título | Sección |
|---|---|---|
| Tabla 1 | Análisis comparativo de aplicaciones de gestión de gastos compartidos | 1.2 |
| Tabla 2 | Requisitos funcionales del sistema | 5.1 |
| Tabla 3 | Requisitos técnicos del sistema | 5.2 |
| Tabla 4 | Diagrama de Gantt del proyecto (enero — mayo 2026) | 6.2 |
| Tabla 5 | Duración estimada por fase | 6.2 |
| Tabla 6 | Matriz de riesgos del proyecto | 7.1 |
| Tabla 7 | Acciones de mitigación por riesgo | 7.3 |
| Tabla 8 | Resumen del plan de pruebas | 10.2 |
| Tabla 9 | Indicadores de calidad del proyecto | 10.3 |

### Figuras

| Nº | Título | Sección |
|---|---|---|
| Figura 1 | Wireframe — Pantalla de bienvenida | 8.1 |
| Figura 2 | Wireframe — Pantalla de registro | 8.1 |
| Figura 3 | Wireframe — Pantalla principal | 8.1 |
| Figura 4 | Wireframe — Diálogo de creación de grupo | 8.1 |
| Figura 5 | Wireframe — Detalle de grupo, pestaña Balances | 8.1 |
| Figura 6 | Wireframe — Detalle de grupo, pestaña Gastos | 8.1 |
| Figura 7 | Wireframe — Registro de gasto equitativo | 8.1 |
| Figura 8 | Wireframe — Registro de gasto personalizado | 8.1 |
| Figura 9 | Wireframe — Liquidación de deudas | 8.1 |
| Figura 10 | Wireframe — Perfil de usuario | 8.1 |
| Figura 11 | Diagrama de casos de uso | 8.3 |
| Figura 12 | Diagrama de clases del dominio | 8.3 |
| Figura 13 | Diagrama de secuencia — Registro de gasto | 8.3 |
| Figura 14 | Sistema de Issues de GitHub | 9.3 |
| Figura 15 | Reglas de seguridad de Firestore | Anexo A |
| Figura 16 | Estructura de directorios del proyecto | Anexo B |
| Figura 17 | Pantalla de inicio de sesión — versión final | Anexo C |
| Figura 18 | Pantalla principal con lista de grupos | Anexo C |
| Figura 19 | Detalle de grupo, pestaña Balances | Anexo C |
| Figura 20 | Detalle de grupo, pestaña Gastos | Anexo C |
| Figura 21 | Diálogo de registro de gasto personalizado | Anexo C |
| Figura 22 | Diálogo de liquidación de deudas | Anexo C |
| Figura 23 | Fichero build.gradle.kts | Anexo D |

---

## 16. Bibliografía y referencias

### Documentación oficial

[1] Google LLC. *Firebase Documentation*. Disponible en: https://firebase.google.com/docs [Consultado: mayo 2026]

[2] Google LLC. *Android Developers Documentation*. Disponible en: https://developer.android.com/docs [Consultado: mayo 2026]

[3] JetBrains. *Kotlin Documentation*. Disponible en: https://kotlinlang.org/docs [Consultado: mayo 2026]

[4] Google LLC. *Jetpack Compose Documentation*. Disponible en: https://developer.android.com/jetpack/compose/documentation [Consultado: mayo 2026]

[5] Google LLC. *Material Design 3 Guidelines*. Disponible en: https://m3.material.io [Consultado: mayo 2026]

[6] Google LLC. *Cloud Firestore Documentation*. Disponible en: https://firebase.google.com/docs/firestore [Consultado: mayo 2026]

[7] Google LLC. *Firebase Authentication Documentation*. Disponible en: https://firebase.google.com/docs/auth [Consultado: mayo 2026]

[8] Google LLC. *Firebase Storage Documentation*. Disponible en: https://firebase.google.com/docs/storage [Consultado: mayo 2026]

### Libros y publicaciones

[9] Martin, R. C. (2017). *Clean Architecture: A Craftsman's Guide to Software Structure and Design*. Prentice Hall. ISBN: 978-0134494166.

[10] Martin, R. C. (2008). *Clean Code: A Handbook of Agile Software Craftsmanship*. Prentice Hall. ISBN: 978-0132350884.

[11] Fowler, M. (2002). *Patterns of Enterprise Application Architecture*. Addison-Wesley. ISBN: 978-0321127426.

### Normativa y legislación

[12] Reglamento (UE) 2016/679 del Parlamento Europeo y del Consejo, de 27 de abril de 2016, relativo a la protección de las personas físicas en lo que respecta al tratamiento de datos personales y a la libre circulación de estos datos (Reglamento General de Protección de Datos — RGPD). *Diario Oficial de la Unión Europea*, L 119/1, 4 de mayo de 2016.

[13] Ley Orgánica 3/2018, de 5 de diciembre, de Protección de Datos Personales y garantía de los derechos digitales (LOPDGDD). *Boletín Oficial del Estado*, núm. 294, 6 de diciembre de 2018.

### Recursos en línea

[14] Google LLC. *Guide to App Architecture — Android Developers*. Disponible en: https://developer.android.com/topic/architecture [Consultado: mayo 2026]

[15] Google LLC. *Android Developers — Coroutines on Android*. Disponible en: https://developer.android.com/kotlin/coroutines [Consultado: mayo 2026]

[16] Google LLC. *Firebase Security Rules — Cloud Firestore*. Disponible en: https://firebase.google.com/docs/firestore/security/get-started [Consultado: mayo 2026]

[17] JetBrains. *Kotlin Coroutines — Flow*. Disponible en: https://kotlinlang.org/docs/flow.html [Consultado: mayo 2026]

[18] Coil Contributors. *Coil — Image loading for Android*. Disponible en: https://coil-kt.github.io/coil [Consultado: mayo 2026]

---

*Memoria del Proyecto — SplitApp v1.0*  
*Javier Aguilera Sánchez — DAM — Mayo 2026*
