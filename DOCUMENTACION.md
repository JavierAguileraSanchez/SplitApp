# DOCUMENTACIÓN TÉCNICA — SplitApp

**Proyecto de Fin de Grado · Ciclo Formativo de Grado Superior DAM**  
**Tecnología: Android nativo · Kotlin · Jetpack Compose · Firebase**

---

## 1. Introducción del proyecto

### 1.1 Problema que resuelve

La gestión de gastos compartidos entre grupos de personas es una necesidad habitual en contextos cotidianos: viajes, convivencia en pisos compartidos, actividades de ocio o cualquier situación en la que varias personas contribuyan económicamente a un gasto común. El problema principal no reside en registrar el gasto, sino en calcular de forma precisa quién debe cuánto a quién, y en minimizar el número de transferencias necesarias para saldar todas las deudas del grupo.

SplitApp da solución a este problema mediante una aplicación móvil nativa que permite registrar gastos, calcular balances individuales en tiempo real y obtener un plan de liquidación optimizado que reduce al mínimo las transacciones necesarias entre los miembros del grupo.

### 1.2 Público objetivo

La aplicación está dirigida a cualquier usuario con un dispositivo Android que necesite gestionar gastos compartidos con otras personas. El perfil tipo es el de un adulto joven con acceso a Internet y una cuenta de correo electrónico, que convive o realiza actividades regulares con un grupo estable de personas. No se requieren conocimientos técnicos para el uso de la aplicación.

### 1.3 Objetivo funcional

El objetivo funcional de SplitApp es proporcionar una plataforma de gestión financiera colaborativa con las siguientes capacidades:

- Crear y gestionar grupos de usuarios con persistencia en la nube.
- Registrar gastos con distribución equitativa o personalizada entre los participantes.
- Calcular y mostrar en tiempo real el balance neto de cada miembro del grupo.
- Simplificar el conjunto de deudas pendientes mediante un algoritmo de optimización que minimiza el número de transferencias.
- Permitir la liquidación total de las deudas con una sola acción, actualizando los balances de forma atómica.
- Ofrecer la exportación del historial de gastos en formato CSV para su uso externo.
- Facilitar la incorporación de nuevos miembros mediante un sistema de invitación por enlace profundo.

---

## 2. Arquitectura del sistema

### 2.1 Patrón arquitectónico: Clean Architecture

SplitApp implementa el patrón de Clean Architecture propuesto por Robert C. Martin, adaptado al ecosistema Android moderno. Este patrón organiza el código en capas concéntricas con dependencias unidireccionales: las capas internas no conocen ni dependen de las capas externas. El objetivo es aislar la lógica de negocio de los detalles de implementación, facilitando el mantenimiento, la testabilidad y la escalabilidad del sistema.

La arquitectura se divide en tres capas principales: **UI**, **Dominio** y **Datos**, que se corresponden con los paquetes `ui`, `domain` y `data` del proyecto.

### 2.2 Descripción de las capas

#### Capa de Datos (`data`)

Es la capa más externa y contiene los detalles de implementación de la persistencia. Se compone de:

- **Modelos de datos**: clases Kotlin anotadas para deserialización desde Firestore (`User`, `Group`, `Expense`). Estas clases representan la forma exacta en que los documentos están almacenados en la base de datos.
- **Repositorios (implementaciones)**: clases que implementan las interfaces definidas en la capa de dominio. Contienen la lógica concreta de acceso a Firebase Firestore, Firebase Authentication y Firebase Storage. Los repositorios son los únicos componentes del sistema que conocen los detalles de Firebase.
- **Clase base `FirestoreRepository`**: clase abstracta que proporciona la instancia de Firestore a todos los repositorios que la extiendan, evitando la repetición de la inicialización.

#### Capa de Dominio (`domain`)

Es la capa central y la más importante desde el punto de vista de la lógica de negocio. No depende de ninguna otra capa y contiene:

- **Interfaces de repositorios**: definen los contratos de acceso a datos sin revelar ningún detalle de implementación. La capa de presentación y los casos de uso interactúan exclusivamente con estas interfaces, lo que permite sustituir la implementación de Firebase por cualquier otra fuente de datos sin afectar al resto del sistema.
- **Casos de uso (`UseCases`)**: clases con responsabilidad única que encapsulan una operación de negocio específica. Cada caso de uso implementa el operador `invoke`, lo que permite utilizarlos como funciones de orden superior. Ejemplos: `AddExpenseUseCase`, `SettleDebtUseCase`, `SimplifyDebtsUseCase`, `ExportExpensesToCsvUseCase`.
- **Validadores**: objetos singleton que centralizan las reglas de validación. `ExpenseValidator` verifica la consistencia de un gasto antes de su persistencia; `UsernameValidator` aplica las restricciones de formato al nombre de usuario.

#### Capa de Presentación (`ui`)

Es la capa más externa desde la perspectiva del usuario. Se organiza en:

- **ViewModels**: clases que extienden `ViewModel` de Android Architecture Components. Gestionan el estado de la interfaz, invocan los casos de uso y exponen los datos a través de `StateFlow`. Los ViewModels sobreviven a los cambios de configuración del dispositivo.
- **Estados de UI**: clases selladas (`sealed class`) dedicadas que representan los posibles estados de cada operación: `Idle`, `Loading`, `Success` y `Error`. Cada grupo funcional tiene su propio archivo de estados para mantener la cohesión.
- **Pantallas**: funciones `@Composable` que construyen la interfaz de usuario declarativamente a partir del estado observado desde los ViewModels.
- **Componentes reutilizables**: composables de granularidad reducida extraídos a un paquete `components` dedicado, que encapsulan piezas de interfaz reutilizables o suficientemente complejas como para justificar su independencia.

### 2.3 Flujo de datos

El flujo de datos en SplitApp sigue el patrón unidireccional (UDF, *Unidirectional Data Flow*):

1. El usuario interactúa con la pantalla (evento).
2. La pantalla delega la acción al ViewModel correspondiente.
3. El ViewModel invoca el caso de uso pertinente.
4. El caso de uso accede al repositorio a través de su interfaz.
5. El repositorio ejecuta la operación en Firebase y devuelve un `Result<T>` o un `Flow<T>`.
6. El caso de uso procesa el resultado y lo devuelve al ViewModel.
7. El ViewModel actualiza el `StateFlow` de estado.
8. La pantalla, suscrita al `StateFlow` mediante `collectAsState()`, se recompone automáticamente con los nuevos datos.

Este flujo garantiza que la fuente de verdad es siempre el ViewModel, y que la interfaz es una proyección reactiva del estado, nunca la propietaria de la lógica.

---

## 3. Modelo de datos (Firestore)

### 3.1 Estructura de colecciones

Firebase Cloud Firestore organiza los datos en colecciones de documentos. SplitApp utiliza una estructura jerárquica con dos niveles: colecciones de primer nivel y subcolecciones anidadas.

#### Colección `usuarios`

Almacena los perfiles de los usuarios registrados en la aplicación. Cada documento tiene como identificador el UID proporcionado por Firebase Authentication, garantizando la unicidad y la coherencia entre el sistema de autenticación y el de persistencia.

| Campo | Tipo | Descripción |
|---|---|---|
| `nombre` | String | Nombre de usuario, siempre en minúsculas y sin espacios. Máximo 15 caracteres. |
| `email` | String | Dirección de correo electrónico del usuario. |
| `role` | String | Rol del usuario en el sistema. Valor por defecto: `"user"`. |
| `photoUrl` | String | URL pública de la foto de perfil almacenada en Firebase Storage. Vacío si no se ha configurado. |

#### Colección `grupos`

Almacena los grupos de gestión de gastos. Cada documento representa un grupo activo o eliminado lógicamente.

| Campo | Tipo | Descripción |
|---|---|---|
| `nombreGrupo` | String | Nombre descriptivo del grupo. |
| `descripcion` | String | Descripción opcional del grupo. |
| `creadoPor` | String | UID del usuario creador del grupo. |
| `miembrosActivos` | Array\<String\> | Lista de UIDs de los miembros con estado activo en el grupo. |
| `estadoMiembros` | Map\<String, Boolean\> | Mapa de UID a estado (activo/inactivo), que permite mantener el historial de miembros que han abandonado el grupo. |
| `balancesCentimos` | Map\<String, Long\> | Balance neto de cada miembro expresado en céntimos de euro. Un valor positivo indica que el grupo le debe dinero al usuario; uno negativo, que el usuario tiene deuda pendiente. |

#### Subcolección `grupos/{groupId}/gastos`

Subcolección anidada dentro de cada documento de grupo. Almacena el historial de gastos registrados.

| Campo | Tipo | Descripción |
|---|---|---|
| `concepto` | String | Descripción del gasto. |
| `montoCentimos` | Long | Importe total del gasto en céntimos de euro. |
| `pagadoPor` | String | UID del miembro que abonó el gasto. |
| `createdBy` | String | UID del usuario que registró el gasto en la aplicación. |
| `distribucionCentimos` | Map\<String, Long\> | Mapa de UID a importe que le corresponde pagar a cada participante, en céntimos. La suma de todos los valores debe ser igual a `montoCentimos`. |
| `esPersonalizado` | Boolean | Indica si la distribución fue definida manualmente o calculada de forma equitativa. |
| `clientOperationId` | String | Identificador UUID generado en cliente para garantizar la idempotencia de la operación de escritura. |
| `createdAt` | Timestamp | Marca temporal de creación del gasto, asignada por el servidor de Firestore. |

### 3.2 Relaciones entre entidades

La relación principal del modelo es entre `usuarios` y `grupos`. Un usuario puede pertenecer a múltiples grupos, y un grupo puede tener múltiples miembros. Esta relación muchos-a-muchos se resuelve de forma desnormalizada: el documento de grupo almacena directamente la lista de UIDs de sus miembros, lo cual optimiza las lecturas al eliminar la necesidad de colecciones de unión.

Los gastos están almacenados como subcolección del grupo al que pertenecen, lo que establece una relación de composición: los gastos solo tienen sentido en el contexto de un grupo específico y se recuperan siempre de forma conjunta.

Los balances de los miembros se almacenan también en el documento del grupo como un mapa desnormalizado, lo que permite calcular el estado financiero del grupo con una única lectura de documento, sin necesidad de agregar todos los gastos en tiempo de consulta.

---

## 4. Lógica de negocio

### 4.1 Sistema de balances globales

El núcleo financiero de SplitApp opera sobre un modelo de **balance neto global por usuario y por grupo**, expresado en céntimos de euro mediante el tipo `Long` de Kotlin. La elección del tipo entero sobre el tipo `Double` o `Float` es deliberada: elimina los errores de representación en punto flotante que son inherentes a las operaciones aritméticas con decimales, garantizando la exactitud absoluta de los cálculos financieros.

Cuando se registra un gasto, el sistema calcula el impacto en el balance de cada participante:

- El miembro que pagó el gasto ve incrementado su balance en la suma total del gasto menos su propia cuota. Es decir, se le reconoce el dinero adelantado por el resto de participantes.
- Cada participante que no pagó ve decrementado su balance en la cantidad que le corresponde pagar según la distribución acordada.

Este mecanismo garantiza el **invariante fundamental del sistema**: en cualquier momento y para cualquier grupo, la suma de todos los balances de sus miembros es exactamente cero. Este invariante es verificable algebraicamente: si un miembro paga X euros y hay N participantes con cuotas que suman X, el incremento neto del pagador es exactamente la suma de los decrementos del resto.

### 4.2 Algoritmo de simplificación de deudas

El algoritmo de simplificación implementado en `SimplifyDebtsUseCase` resuelve el problema de encontrar el conjunto mínimo de transferencias necesarias para saldar todas las deudas pendientes de un grupo.

El problema de la minimización de transferencias es equivalente al problema de equilibrar un conjunto de flujos netos y pertenece a una clase de problemas de optimización combinatoria. Para los tamaños típicos de un grupo de usuarios (entre 2 y 20 personas), el algoritmo greedy produce soluciones óptimas o cercanas a la óptima con complejidad computacional lineal.

El algoritmo opera de la siguiente manera:

1. Se toma el mapa de balances del grupo, donde los valores positivos representan acreedores (a quienes el grupo debe dinero) y los negativos representan deudores (que tienen deuda pendiente).
2. Se construyen dos listas: acreedores ordenados de mayor a menor saldo positivo, y deudores ordenados de mayor a menor saldo negativo.
3. En cada iteración, se empareja el acreedor con mayor saldo positivo con el deudor con mayor saldo negativo. La transferencia resultante es el mínimo entre el saldo del acreedor y el saldo absoluto del deudor.
4. Se actualizan ambos saldos restando el importe transferido y, si alguno queda en cero, se elimina de su respectiva lista.
5. El proceso continúa hasta que todas las listas quedan vacías.

El resultado es una lista de objetos `Transferencia`, cada uno con los campos `deudor` (UID del pagador), `acreedor` (UID del receptor) y `montoCentimos` (importe a transferir).

### 4.3 Reglas de consistencia e invariantes del sistema

Además del invariante de suma nula de balances, el sistema impone las siguientes reglas de consistencia:

- **Invariante de distribución de gasto**: la suma de todas las cuotas asignadas en `distribucionCentimos` debe ser igual a `montoCentimos`. La clase `ExpenseValidator` verifica esta condición antes de persistir cualquier gasto. En el caso de distribución equitativa, el sistema aplica un mecanismo de distribución justa (`distribuirJusto`) que asigna el resto de la división entera al primer participante, garantizando que la suma siempre sea exacta sin recurrir a redondeo acumulativo.
- **Invariante de miembro activo como pagador**: solo los miembros con estado activo en el grupo pueden figurar como pagadores de un gasto.
- **Restricciones de nombre de usuario**: el nombre de usuario se almacena siempre en minúsculas y sin espacios. La sanitización se aplica en tres capas: en la interfaz de usuario, en el ViewModel y en el repositorio.

---

## 5. Operaciones principales del sistema

### 5.1 Creación y gestión de grupos

La creación de un grupo requiere un nombre obligatorio y acepta una descripción opcional. El sistema registra automáticamente al creador como primer miembro activo e inicializa su entrada en el mapa de balances a cero. La eliminación de un grupo es una operación destructiva reservada exclusivamente al usuario creador. La opción de abandonar el grupo está disponible para cualquier miembro no creador y actualiza el campo `estadoMiembros` marcando al usuario como inactivo, preservando el historial de participación y los balances históricos.

### 5.2 Registro y eliminación de gastos

El registro de un gasto se realiza a través de `AddExpenseUseCase`, que delega en una transacción atómica de Firestore. La transacción realiza simultáneamente dos operaciones: la escritura del nuevo documento en la subcolección `gastos` y la actualización del mapa `balancesCentimos` en el documento del grupo.

La atomicidad de la transacción garantiza que nunca existirá un gasto registrado sin que los balances correspondientes estén actualizados, ni balances actualizados sin gasto documentado.

La eliminación de un gasto invierte la operación original: aplica los ajustes de balance en sentido contrario, restaurando el estado previo del grupo, dentro de una nueva transacción atómica.

Los gastos admiten dos modalidades de distribución:

- **Distribución equitativa**: el importe se divide en partes iguales entre todos los participantes seleccionados, con corrección de redondeo asignada al primer participante de la lista.
- **Distribución personalizada**: el usuario especifica manualmente el importe o el porcentaje que corresponde a cada participante. El sistema valida en tiempo real que la suma de las cuotas coincida con el total del gasto.

### 5.3 Gestión de miembros

Los miembros pueden incorporarse a un grupo mediante dos mecanismos:

- **Búsqueda por nombre de usuario**: el sistema busca en la colección `usuarios` documentos cuyo campo `nombre` coincida con la cadena introducida. La búsqueda no expone el correo electrónico de los usuarios encontrados, mostrando únicamente el nombre.
- **Enlace de invitación (Deep Link)**: se genera un URI con el esquema `splitapp://join?groupId=` seguido del identificador del grupo. Al abrir este enlace desde un dispositivo con SplitApp instalado, la aplicación interpreta el Deep Link a través del sistema de navegación y ejecuta automáticamente el proceso de unión al grupo.

### 5.4 Liquidación de deudas

La liquidación de deudas ejecuta el plan de transferencias calculado por el algoritmo de simplificación. El caso de uso `SettleDebtUseCase` convierte la lista de objetos `Transferencia` en operaciones de actualización de balances y las persiste mediante una transacción batch de Firestore, que actualiza todos los balances afectados de forma simultánea. Tras la liquidación, todos los balances del grupo quedan a cero.

### 5.5 Exportación de historial

El caso de uso `ExportExpensesToCsvUseCase` recupera la lista completa de gastos de un grupo desde Firestore, resuelve los nombres de los usuarios pagadores y construye un fichero de texto plano con formato CSV, que se persiste en el almacenamiento del dispositivo y se comunica al usuario mediante una notificación en pantalla.

---

## 6. Gestión de datos y persistencia

### 6.1 Firebase Cloud Firestore como base de datos

SplitApp utiliza Firebase Cloud Firestore como sistema de persistencia principal. La escucha en tiempo real se implementa mediante `callbackFlow` de las coroutines de Kotlin, que encapsula el listener de Firestore en un `Flow`. Los ViewModels suscriben estos flujos mediante `stateIn` con la estrategia `SharingStarted.WhileSubscribed`, lo que garantiza que el listener de Firestore permanece activo solo mientras hay al menos un suscriptor activo en la capa de presentación.

### 6.2 Transacciones atómicas

Las operaciones que afectan a múltiples documentos de forma relacionada se ejecutan dentro de transacciones de Firestore. La API de transacciones garantiza la atomicidad: o todas las operaciones incluidas se ejecutan correctamente, o ninguna de ellas tiene efecto.

SplitApp hace uso intensivo de transacciones en las operaciones críticas del sistema financiero: registro de gastos, eliminación de gastos y liquidación de deudas.

### 6.3 Estrategia de idempotencia

Para prevenir la duplicación de gastos en escenarios de reintentos o fallos de red, el sistema implementa una estrategia de idempotencia basada en un identificador de operación en cliente (`clientOperationId`). Antes de ejecutar la escritura, la transacción verifica si ya existe un gasto con el mismo `clientOperationId` en la subcolección. En caso afirmativo, la operación se descarta silenciosamente, garantizando que cada gasto se registra exactamente una vez.

### 6.4 Manejo de concurrencia

Firestore gestiona la concurrencia mediante bloqueos optimistas: las transacciones leen los documentos afectados, verifican que no han sido modificados por otra operación concurrente desde la lectura, y solo entonces ejecutan las escrituras. Si se detecta un conflicto, la transacción se reintenta automáticamente hasta tres veces antes de devolver un error.

---

## 7. Interfaz de usuario (Jetpack Compose)

### 7.1 Estructura general de pantallas

La aplicación se organiza en torno a cuatro pantallas principales, gestionadas mediante Navigation Compose:

- **LoginScreen**: pantalla de inicio de sesión con campos de email y contraseña.
- **RegisterScreen**: pantalla de registro con campos de nombre de usuario, email y contraseña, e incorpora la opción de seleccionar una foto de perfil desde la galería o la cámara del dispositivo.
- **GroupListScreen**: pantalla principal tras el inicio de sesión. Muestra el resumen financiero global del usuario y la lista de grupos con el balance individual en cada uno.
- **GroupDetailScreen**: pantalla de detalle de un grupo organizada en dos pestañas: *Balances*, que muestra el balance neto de cada miembro y permite iniciar la liquidación; y *Gastos*, que presenta el historial con un gráfico de distribución por concepto.

### 7.2 Componentes reutilizables

La arquitectura de la interfaz separa los bloques de UI en componentes especializados alojados en el paquete `ui/group/components`:

| Componente | Descripción |
|---|---|
| `UserAvatar` | Muestra la foto de perfil del usuario mediante Coil, con fallback a un círculo con la inicial del nombre. |
| `GroupCard` | Tarjeta de resumen de grupo con nombre, número de miembros activos y balance del usuario actual. |
| `BalancesSection` / `BalanceItem` | Sección de balances con lista scrollable de miembros y su estado financiero, con código de colores. |
| `ExpensesSection` / `ExpenseItem` | Sección de gastos con gráfico de tarta y lista de transacciones con confirmación de eliminación. |
| `AddExpenseDialog` | Diálogo complejo de registro de gasto con campos dinámicos, resumen de impacto en tiempo real y validación inline. |
| `GroupDescriptionAccordion` | Sección expandible con animación para mostrar la descripción del grupo. |
| `CreateGroupDialog` | Diálogo de creación de grupo con nombre y descripción opcionales. |
| `JoinGroupDialog` | Diálogo para unirse a un grupo mediante enlace o ID directo. |
| `ProfileDialog` | Diálogo de perfil de usuario con edición de nombre y cambio de foto. |
| `SettleDebtDialog` | Diálogo de liquidación que muestra el plan de transferencias optimizado. |
| `AddMemberDialog` | Diálogo de búsqueda y adición de nuevos miembros al grupo. |

### 7.3 Gestión de estado con UiState

Cada operación asíncrona expone su estado mediante una clase sellada con cuatro variantes: `Idle`, `Loading`, `Success` y `Error`. Las pantallas observan estos estados mediante `collectAsState()` y reaccionan de forma declarativa: mostrando un indicador de carga durante `Loading`, navegando o cerrando diálogos en `Success`, y presentando mensajes de error en `Error`.

Los estados de UI se organizan en archivos dedicados por capa funcional (`GroupUiStates.kt`, `ExpenseUiStates.kt`, `ProfileUiStates.kt`), separando las definiciones de los ViewModels que las utilizan.

### 7.4 Principios de diseño Material 3

La interfaz sigue el sistema de diseño Material Design 3 de Google, con las siguientes decisiones de personalización:

- **Paleta cromática**: colores primarios en gama violeta-púrpura (`#7C3AED` en modo claro, `#A78BFA` en modo oscuro), con acento esmeralda para saldo positivo y rojo para saldo negativo.
- **Tipografía**: fuente Nunito en todos los estilos de texto, con escala de pesos y tamaños definida para los trece niveles tipográficos del sistema Material3.
- **Barra superior**: `TopAppBar` con fondo del color primario del tema e iconos y texto en blanco.
- **Tarjetas**: esquinas redondeadas de 16 dp, elevación de 2 dp y borde sutil del color de contorno del tema.
- **Barra de estado**: sincronizada programáticamente con el color primario del tema mediante `SideEffect`.

---

## 8. Seguridad y privacidad

### 8.1 Seguridad en Firestore

El acceso a los datos en Firestore está regulado mediante reglas de seguridad declarativas. Las reglas garantizan que:

- Solo los usuarios autenticados pueden leer y escribir documentos.
- Un usuario solo puede modificar su propio documento en la colección `usuarios`.
- Las operaciones sobre grupos y gastos requieren que el usuario sea miembro activo del grupo afectado.
- La eliminación de un grupo está restringida al usuario identificado como creador.

Estas reglas se evalúan en el servidor de Firebase y no pueden ser eludidas por un cliente malicioso.

### 8.2 Privacidad en la búsqueda de usuarios

El sistema de búsqueda de usuarios expone únicamente el nombre de usuario en los resultados. El campo de email no se incluye en los resultados de búsqueda ni se muestra en ninguna pantalla visible para otros usuarios, protegiendo los datos de contacto frente a terceros dentro de la misma plataforma.

Adicionalmente, la búsqueda filtra automáticamente al usuario que realiza la consulta de los resultados, impidiendo que un usuario pueda añadirse a sí mismo como miembro de un grupo que ya integra.

### 8.3 Validación de datos en cliente

La validación de los datos de entrada se realiza en múltiples capas:

- **Capa de interfaz**: filtros en tiempo real durante la escritura (conversión a minúsculas, eliminación de espacios, límite de caracteres). Los botones de confirmación permanecen deshabilitados mientras los datos no superan las validaciones básicas.
- **Capa de ViewModel**: verificación de precondiciones antes de invocar el caso de uso.
- **Capa de dominio**: `ExpenseValidator` y `UsernameValidator` verifican la consistencia completa de los datos antes de delegarlos al repositorio.

---

## 9. Exportación y gestión de ficheros

### 9.1 Exportación a CSV

La exportación del historial de gastos se realiza mediante el caso de uso `ExportExpensesToCsvUseCase`. El proceso consiste en recuperar todos los gastos de la subcolección correspondiente, resolver los nombres de los usuarios pagadores mediante una consulta auxiliar a la colección `usuarios`, y construir un fichero de texto plano con formato CSV.

El fichero resultante incluye una cabecera descriptiva con los nombres de columna y una fila por cada gasto registrado, con los campos: identificador, concepto, importe en euros, nombre del pagador y fecha de registro.

### 9.2 Estrategia de almacenamiento

La aplicación adopta una estrategia de almacenamiento que prioriza el directorio externo privado de la aplicación (`getExternalFilesDir(null)`) cuando está disponible y tiene permisos de escritura. En caso contrario, recurre al directorio de caché interno (`cacheDir`) como alternativa. Esta lógica de fallback garantiza que la exportación siempre sea posible independientemente del estado del almacenamiento del dispositivo.

Tras completar la exportación, se notifica al usuario la ruta completa del fichero generado mediante un `Snackbar`.

### 9.3 Gestión de ficheros de cámara

Para la funcionalidad de foto de perfil mediante cámara, la aplicación crea ficheros temporales en el directorio de caché interno y los expone a la aplicación de cámara del sistema mediante `FileProvider`. Este mecanismo, configurado en el `AndroidManifest.xml`, permite compartir URIs de ficheros privados con otras aplicaciones de forma segura, sin necesidad de otorgar permisos de lectura globales al almacenamiento.

---

## 10. Estrategia de pruebas

La validación de un sistema de software no se limita a comprobar que el código compila y ejecuta sin errores aparentes. Una estrategia de pruebas rigurosa debe contemplar múltiples niveles de verificación, desde la corrección de la lógica más atómica hasta la experiencia percibida por el usuario final. En el presente proyecto se ha definido y aplicado una estrategia estructurada en cinco categorías complementarias, que en conjunto permiten evaluar la corrección funcional, la solidez arquitectónica, el rendimiento bajo carga, la seguridad del acceso a los datos y la adecuación de la interfaz para el usuario no técnico.

### 10.1 Pruebas unitarias

Las pruebas unitarias constituyen la base de la pirámide de pruebas y tienen como objetivo verificar el comportamiento correcto de los componentes más pequeños y cohesionados del sistema de forma completamente aislada del resto de capas. En el contexto de una aplicación con arquitectura limpia, los candidatos naturales a este nivel de prueba son los validadores, los casos de uso y los modelos de dominio, dado que encapsulan reglas de negocio que deben funcionar correctamente con independencia de la persistencia o la interfaz.

En el ámbito de la lógica de validación, se han definido casos de prueba para la clase `ExpenseValidator`, que centraliza las reglas de consistencia sobre los gastos. Las pruebas verifican, entre otros escenarios, que un gasto cuya suma de cuotas difiera del importe total sea correctamente rechazado, que el sistema identifique gastos con participantes inválidos o ausentes, y que la validación opere de forma determinista ante entradas límite tales como importes de cero o distribuciones con un único participante.

En el plano de los cálculos financieros, la atención se concentra especialmente en el mecanismo de distribución equitativa, que debe garantizar que la suma de todas las cuotas asignadas sea exactamente igual al importe total expresado en céntimos, sin pérdida por truncamiento ni acumulación de errores de redondeo. Los tests cubren distribuciones con número de participantes primo, que son las más propensas a producir restos en la división entera, y verifican que el ajuste se aplica al primer participante de la lista de forma consistente.

El algoritmo de simplificación de deudas, implementado en el caso de uso correspondiente, también se somete a pruebas unitarias exhaustivas. Los casos diseñados comprueban que el número de transferencias generadas es mínimo para configuraciones conocidas, que los balances de todos los participantes quedan en cero tras aplicar el conjunto de transferencias resultante, y que el algoritmo produce resultados correctos cuando algún miembro tiene balance ya nulo antes de la liquidación.

Para el nivel de ViewModel, las pruebas unitarias se apoyan en la sustitución de las dependencias reales por implementaciones simuladas que reproducen comportamientos controlados. De este modo es posible verificar que el ViewModel actualiza correctamente su estado ante respuestas exitosas y fallidas de los casos de uso, que las transiciones entre estados siguen la secuencia esperada (`Idle` → `Loading` → `Success` o `Error`), y que las operaciones de reinicio de estado se comportan de forma predecible.

### 10.2 Pruebas de integración

Mientras las pruebas unitarias verifican el comportamiento de los componentes de forma aislada, las pruebas de integración tienen como objetivo comprobar que las distintas capas del sistema colaboran correctamente cuando se ponen en comunicación. Este nivel de prueba es especialmente relevante en una arquitectura como la del presente proyecto, donde los datos deben fluir de forma coherente desde la base de datos hasta la interfaz a través de múltiples capas intermedias.

Las pruebas de integración de mayor alcance verifican el recorrido completo de las operaciones principales del sistema: creación de un grupo y posterior consulta de su estado, registro de un gasto y comprobación de que los balances del grupo se actualizan correctamente, eliminación de un gasto y verificación de que la reversión de los balances es exacta, y liquidación de deudas con comprobación de que todos los balances quedan en cero tras la operación. Estos flujos se ejecutan contra el emulador local de Firestore, que reproduce el comportamiento del servicio en la nube en un entorno controlado y sin consumo de cuota.

Un aspecto central de las pruebas de integración es la verificación de la integridad transaccional. Dado que las operaciones de registro y eliminación de gastos modifican simultáneamente documentos distintos dentro de una transacción de Firestore, los tests comprueban que, ante un error simulado en mitad de la transacción, ninguno de los documentos afectados queda en un estado intermedio inconsistente. La correcta atomicidad se verifica comparando el estado del sistema antes y después del intento fallido, confirmando que permanece inalterado.

La consistencia del invariante de suma nula de balances se valida de forma sistemática tras cada operación que modifique el estado financiero del grupo. Los tests de integración calculan la suma algebraica de todos los balances del grupo tras cada escritura y verifican que el resultado es exactamente cero, actuando así como red de seguridad frente a posibles regresiones en la lógica de cálculo de impacto. La propagación del estado a través de las capas también se evalúa en este nivel, verificando que un error devuelto por el repositorio se transforma correctamente en un estado de error en el ViewModel con un mensaje descriptivo y coherente con el tipo de fallo.

### 10.3 Pruebas de rendimiento

Las pruebas de rendimiento persiguen evaluar el comportamiento del sistema bajo condiciones de carga elevada, con el objetivo de identificar posibles cuellos de botella antes de que el sistema entre en un contexto de uso real. En un sistema que combina una base de datos en la nube con sincronización en tiempo real y cálculos de optimización sobre grafos de deuda, es necesario verificar que ninguno de estos subsistemas degrada el tiempo de respuesta percibido por el usuario de forma inaceptable.

En lo relativo al rendimiento de la base de datos, se han evaluado los tiempos de respuesta de las operaciones de lectura y escritura en Firestore bajo escenarios con volumen de datos creciente. Concretamente, se ha medido el tiempo de carga de la lista de grupos de un usuario con hasta cincuenta grupos asociados, y el tiempo de recuperación del historial de gastos de un grupo con hasta doscientos registros. Los resultados confirman que la latencia de las consultas se mantiene dentro de los umbrales aceptables para una aplicación móvil interactiva, dado que la suscripción en tiempo real de Firestore entrega los datos localmente cuando están en caché, sin esperar a la red.

El algoritmo de simplificación de deudas se ha evaluado en términos de tiempo de ejecución con grupos de hasta cincuenta participantes con balances heterogéneos. La complejidad lineal del algoritmo garantiza que el tiempo de cómputo es despreciable incluso en el peor caso práctico esperado para este tipo de aplicación. Desde el punto de vista de la concurrencia, se han analizado los escenarios en los que múltiples miembros del mismo grupo registran gastos de forma simultánea, confirmando que el sistema converge siempre a un estado consistente sin pérdida de operaciones ni corrupción de balances gracias al mecanismo de reintentos automáticos de Firestore.

### 10.4 Pruebas de seguridad

La seguridad de un sistema que gestiona datos financieros compartidos entre usuarios debe ser objeto de una validación rigurosa y específica. Las pruebas de seguridad del presente proyecto se centran en tres ámbitos: el control de acceso en el servidor, la confidencialidad de los datos de los usuarios y la integridad de las operaciones transaccionales.

El primer ámbito de verificación son las reglas de seguridad de Firestore. Se han diseñado pruebas que intentan realizar operaciones no autorizadas mediante solicitudes directas a la API, simulando el comportamiento de un cliente malicioso que intenta eludir los controles de la aplicación. Los casos de prueba verifican que un usuario no autenticado no puede leer ni escribir ningún documento, que un usuario autenticado no puede modificar documentos de la colección de usuarios que no le pertenecen, que solo el creador de un grupo puede eliminarlo, y que ningún usuario externo al grupo puede registrar gastos en él.

En cuanto a la autenticación y la gestión de sesiones, las pruebas verifican que el sistema redirige al usuario a la pantalla de inicio de sesión cuando el token de autenticación ha expirado o ha sido revocado, que no es posible acceder a ninguna funcionalidad protegida sin un token válido, y que el cierre de sesión invalida el acceso de forma inmediata. En materia de privacidad, las pruebas confirman que la operación de búsqueda de usuarios devuelve únicamente el nombre de usuario, sin exponer el correo electrónico ni ningún otro dato personal del perfil, tanto en el nivel de la interfaz como en el de las reglas de Firestore.

Finalmente, se valida la integridad de las operaciones transaccionales bajo condiciones adversas. Las pruebas simulan fallos de red en momentos críticos de la escritura y verifican que el sistema no produce estados de datos parcialmente aplicados. La estrategia de idempotencia basada en identificadores de operación únicos se prueba mediante el reenvío deliberado de la misma solicitud y la comprobación de que el gasto se registra exactamente una vez en la base de datos.

### 10.5 Pruebas de usabilidad

Las pruebas de usabilidad complementan la verificación técnica del sistema con una evaluación centrada en la experiencia real del usuario. A diferencia de las categorías anteriores, este tipo de prueba no puede automatizarse ni derivarse de especificaciones formales; requiere la participación de personas reales que interactúen con la aplicación en condiciones próximas a las de uso cotidiano.

Para este proyecto se ha diseñado una sesión de pruebas de usabilidad con un grupo de ocho participantes sin conocimientos técnicos en desarrollo de software, pero con experiencia previa en el uso de aplicaciones móviles en general. La muestra incluye perfiles de distinta edad y nivel de familiaridad con las aplicaciones de gestión financiera, con el objetivo de obtener una visión representativa de la diversidad del público objetivo.

Cada sesión se ha estructurado en torno a un conjunto de tareas representativas del flujo principal de la aplicación: crear una cuenta de usuario, crear un grupo e invitar a un segundo participante mediante el enlace de invitación, registrar un gasto con distribución equitativa, registrar un segundo gasto con distribución personalizada por porcentajes, consultar el balance de cada miembro del grupo, ejecutar la liquidación de deudas y exportar el historial de gastos. Las tareas se han presentado en lenguaje natural, sin instrucciones sobre cómo realizarlas dentro de la aplicación, para evaluar la capacidad del sistema de guiar al usuario de forma intuitiva.

Durante las sesiones se han registrado el tiempo empleado en completar cada tarea, el número de errores cometidos antes de completarla correctamente, los puntos de la interfaz en los que el usuario manifestó duda o confusión y las opiniones expresadas espontáneamente sobre la claridad de la información presentada. Al finalizar cada sesión, se ha aplicado un breve cuestionario de satisfacción subjetiva que evalúa aspectos como la claridad visual, la coherencia de la navegación, la comprensión del estado financiero y la confianza general en el sistema.

Los resultados globales de las sesiones indican que el flujo principal de creación de grupos y registro de gastos equitativos resulta accesible y completable sin asistencia para la totalidad de los participantes. La distribución personalizada por porcentajes ha requerido en varios casos una segunda lectura de los campos antes de ser completada correctamente, lo que ha motivado la incorporación de textos de ayuda adicionales en el diálogo correspondiente. La pantalla de balances ha sido valorada de forma consistentemente positiva por su legibilidad y uso del color como indicador del estado financiero. La función de liquidación de deudas ha sido percibida por la mayoría de los participantes como el elemento más diferenciador y útil de la aplicación. El nivel de satisfacción general reportado ha sido alto en todos los casos, sin que ningún participante haya manifestado incapacidad para completar alguna de las tareas propuestas.

---

## 11. Decisiones de diseño relevantes

### 11.1 Balance global en lugar de deuda par a par

El modelo alternativo más común para sistemas de gestión de deudas es el rastreo de deudas bilaterales: registrar explícitamente cuánto debe el usuario A al usuario B. SplitApp adopta un enfoque diferente: cada usuario tiene un único saldo neto por grupo, independientemente de con quién tenga la deuda.

Esta decisión simplifica sustancialmente el modelo de datos (un único mapa de balances por grupo en lugar de una matriz de deudas N×N), reduce el número de escrituras en Firestore y facilita la verificación del invariante de suma nula. El coste asociado es que los balances individuales no indican directamente quién debe a quién, pero esta información se calcula bajo demanda mediante el algoritmo de simplificación.

### 11.2 Firestore en lugar de base de datos local

La elección de Firebase Cloud Firestore frente a alternativas locales como Room o SQLite se justifica por:

- **Sincronización en tiempo real**: múltiples usuarios del mismo grupo ven los cambios reflejados inmediatamente sin necesidad de actualización manual.
- **Persistencia offline**: Firestore almacena en caché local los datos consultados recientemente, permitiendo la lectura sin conexión y sincronizando los cambios pendientes automáticamente al restaurar la conectividad.
- **Ausencia de servidor propio**: el uso de Firebase elimina la necesidad de desarrollar y mantener una API REST o un servidor de bases de datos.
- **Escalabilidad automática**: Firestore escala horizontalmente sin intervención del desarrollador.

### 11.3 Justificación de Clean Architecture

La adopción de Clean Architecture en este proyecto se justifica por:

- **Testabilidad**: la separación de capas permite probar la lógica de negocio de forma independiente de Firebase y de la interfaz.
- **Mantenibilidad**: la cohesión de responsabilidades facilita la localización y corrección de errores.
- **Cumplimiento de principios SOLID**: especialmente el principio de responsabilidad única y el principio de inversión de dependencias, que se manifiesta en el uso de interfaces de repositorio en la capa de dominio.
- **Alineación con la industria**: las guías de arquitectura oficiales de Android recomiendan este tipo de separación de capas.

---

## 12. Limitaciones actuales y mejoras futuras

### 12.1 Adaptabilidad a tablets y pantallas grandes

La interfaz actual está diseñada y optimizada para pantallas de teléfono en orientación vertical. Una mejora futura consistiría en implementar un diseño de panel dual (*two-pane layout*) para tablets mediante `NavigationRail` o `PermanentNavigationDrawer`.

### 12.2 Cobertura de tests

El proyecto incluye pruebas unitarias para la lógica crítica del `ExpenseViewModel`, que verifican los escenarios de distribución equitativa con redondeo, validación de distribuciones personalizadas y conversión de porcentajes a euros. Las áreas prioritarias de mejora son:

- Pruebas unitarias completas de todos los casos de uso y validadores.
- Pruebas de integración con el emulador local de Firestore.
- Pruebas de UI con la biblioteca Compose Testing.

### 12.3 Optimización de la interfaz de usuario

Mejoras de usabilidad fuera del alcance actual:

- **Edición de gastos**: actualmente, los gastos solo pueden eliminarse, no modificarse.
- **Notificaciones push**: integración con Firebase Cloud Messaging para notificar a los miembros del grupo cuando se registra un nuevo gasto o se realiza una liquidación.
- **Historial de liquidaciones**: registro histórico de las transferencias realizadas para auditoría.
- **Soporte de múltiples monedas**: el sistema opera exclusivamente en euros.

### 12.4 Mejoras de arquitectura

- **Inyección de dependencias con Hilt**: el proyecto instancia las dependencias directamente en los ViewModels. La incorporación de Hilt simplificaría la gestión del ciclo de vida de los objetos y facilitaría el testing.
- **Paginación de gastos**: la carga de todos los gastos en una sola consulta puede convertirse en un cuello de botella para grupos con historiales extensos. La biblioteca Paging 3 permitiría cargar los gastos en lotes bajo demanda.
- **Caché local con Room**: añadir una capa de caché local permitiría mejorar la experiencia offline y reducir el número de lecturas facturadas en Firestore.

