# SplitApp — Handoff

## Resumen del proyecto

SplitApp es una app Android (Jetpack Compose + Firebase) para dividir gastos entre grupos de personas. Cada grupo tiene miembros, gastos compartidos y balances en céntimos. La moneda se elige por grupo (EUR/USD/GBP). La app está en ES/EN con selector de idioma.

**Stack**: Kotlin, Jetpack Compose, Firebase Auth, Firestore. Arquitectura: ViewModel + UseCases + Repository.

**Funcionalidades implementadas**:
- Registro e inicio de sesión (Firebase Auth)
- Crear grupos, añadir gastos, ver balances por miembro
- Simplificación de deudas (algoritmo de transferencias mínimas)
- Sistema de invitaciones: enviar, aceptar, rechazar (con bloqueo tras 2 rechazos)
- Badge de invitaciones pendientes en la lista de grupos
- Selector de moneda por grupo
- i18n ES/EN

---

## Próxima funcionalidad: confirmación mutua para liquidar deudas

### Descripción

Actualmente el botón "Liquidar deudas" en `GroupDetailScreen` ejecuta la liquidación directamente. El objetivo es añadir un flujo de confirmación mutua entre los acreedores antes de que la liquidación tenga efecto.

### Comportamiento esperado

- El botón **solo es pulsable** para los miembros con balance positivo (les deben dinero). Los miembros con balance negativo ven el botón desactivado.
- Al pulsar, el usuario registra su confirmación en Firestore.
- El botón muestra el progreso: **`X/N`** donde N = total de acreedores del grupo, X = cuántos han confirmado ya.
- Un acreedor que ya ha confirmado puede **cancelar** su confirmación pulsando el mismo botón (que mostrará la acción de cancelar).
- Cuando **todos** los acreedores confirman (X == N), se ejecuta la liquidación: los balances de todos los implicados se resetean a 0.

### Datos en Firestore

Añadir un campo al documento del grupo:

```
grupos/{groupId}:
  liquidacionPendiente: {
    confirmaciones: [uid1, uid2, ...]   // acreedores que han confirmado
  }
```

Usar `FieldValue.arrayUnion(uid)` al confirmar y `FieldValue.arrayRemove(uid)` al cancelar.

### Archivos a modificar

- `Group.kt` — añadir campo `liquidacionPendiente` al modelo
- `GroupRepositoryImpl.kt` — métodos confirm/cancel con arrayUnion/arrayRemove, y lógica de liquidación cuando X == N
- `GroupViewModel.kt` — métodos `confirmSettlement()` / `cancelSettlement()`
- `GroupDetailScreen.kt` — UI del botón con contador X/N y estado de cancelar
- `GroupUiStates.kt` — añadir estado para el proceso si es necesario
- Firestore rules — permitir update de `liquidacionPendiente` a miembros activos
