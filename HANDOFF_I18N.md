# SplitApp — Handoff

## Resumen del proyecto

SplitApp es una app Android (Jetpack Compose + Firebase) para dividir gastos entre grupos de personas. Cada grupo tiene miembros, gastos compartidos y balances en céntimos. La moneda se elige por grupo (EUR/USD/GBP). La app está en ES/EN con selector de idioma.

**Stack**: Kotlin, Jetpack Compose, Firebase Auth, Firestore. Arquitectura: ViewModel + UseCases + Repository.

**Funcionalidades implementadas**:
- Registro e inicio de sesión (Firebase Auth), con foto de perfil (Firebase Storage)
- Crear grupos, añadir gastos, ver balances por miembro
- Simplificación de deudas (algoritmo de transferencias mínimas)
- Sistema de invitaciones: enviar, aceptar, rechazar (con bloqueo tras 2 rechazos)
- Badge de invitaciones pendientes en la lista de grupos
- Selector de moneda por grupo
- Confirmación mutua para liquidar deudas (todos los acreedores deben confirmar)
- i18n ES/EN

---

## Próxima funcionalidad: vista "Quién me debe"

### Descripción

Una nueva pantalla accesible desde `GroupListScreen` que muestra, para el usuario actual, un resumen consolidado de **todas las personas que le deben dinero**, agrupadas por deudor.

### Comportamiento esperado

- Lista de deudores (usuarios con balance negativo hacia el usuario actual en cualquier grupo).
- Por cada deudor: su nombre, los grupos en los que coincide con el usuario actual y le debe dinero, y el importe que le debe en cada grupo.
- Al final de cada deudor: el **total consolidado** que esa persona le debe sumando todos los grupos.
- Solo se muestran deudores con saldo > 0 hacia el usuario. Si no hay nadie, mostrar estado vacío.
- La moneda se muestra por grupo (cada grupo tiene la suya).

### Datos disponibles

- `groups: List<Group>` ya disponible en `GroupViewModel` — contiene `balancesCentimos: Map<userId, Long>` y `moneda`.
- Un balance positivo de `userId` en `balancesCentimos` significa que ese usuario **le debe** al usuario actual.
- Los nombres de usuario se obtienen con `groupRepository.getUserNames(userIds)`.

### Archivos a crear / modificar

- `DebtorsScreen.kt` — nueva pantalla con la lista de deudores.
- `GroupViewModel.kt` — añadir un StateFlow que calcule y exponga la lista de deudores a partir de `groups`.
- `GroupUiStates.kt` — añadir el modelo de datos `DebtorSummary` (deudorId, nombre, lista de (grupo, importe, moneda), total).
- `NavGraph.kt` + `Screen.kt` — añadir la nueva ruta.
- `GroupListScreen.kt` — añadir acceso a la nueva pantalla (botón o icono en la TopAppBar).
- `strings.xml` + `strings-es.xml` — textos de la nueva pantalla en EN/ES.
