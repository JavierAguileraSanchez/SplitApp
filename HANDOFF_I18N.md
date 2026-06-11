# SplitApp — Handoff

## Resumen del proyecto

SplitApp es una app Android (Jetpack Compose + Firebase) para dividir gastos entre grupos de personas. Cada grupo tiene miembros, gastos compartidos y balances en céntimos. La moneda se elige por grupo (EUR/USD/GBP).

**Stack**: Kotlin, Jetpack Compose, Firebase Auth, Firestore, Firebase Storage. Arquitectura: ViewModel + UseCases + Repository.

**Funcionalidades implementadas**:
- Registro e inicio de sesión (Firebase Auth), con foto de perfil (Firebase Storage)
- Nombres de usuario únicos (case-insensitive via campo `nombreLower` en Firestore), con mayúsculas permitidas
- Crear grupos, añadir gastos, ver balances por miembro
- Simplificación de deudas (algoritmo de transferencias mínimas)
- Sistema de invitaciones: enviar, aceptar, rechazar (con bloqueo tras 2 rechazos)
- Badge de invitaciones pendientes en la lista de grupos
- Selector de moneda por grupo
- Confirmación mutua para liquidar deudas (todos los acreedores deben confirmar)
- i18n ES/EN con selector en login/register (top-left) y en perfil
- Selector modo claro/oscuro con persistencia por usuario (SharedPreferences)
- Pantalla "Quién me debe" con vista por persona y por grupo

---

## ✅ Teléfono de usuario y dialog de miembro — IMPLEMENTADO

### Archivos modificados/creados
- `data/model/User.kt` — campo `telefono: String = ""`
- `domain/repository/AuthRepository.kt` + `AuthRepositoryImpl.kt` — método `updatePhone`
- `domain/repository/GroupRepository.kt` + `GroupRepositoryImpl.kt` — método `getMemberProfiles`
- `ui/profile/ProfileUiStates.kt` — `UpdatePhoneState`
- `ui/profile/ProfileViewModel.kt` — `phoneInput`, `savePhone()`, `resetUpdatePhoneState()`
- `ui/group/components/ProfileDialog.kt` — campo teléfono opcional
- `ui/group/GroupListScreen.kt` — cablea el campo teléfono
- `ui/group/GroupViewModel.kt` — `memberProfiles`, `loadMemberProfiles()`
- `ui/group/GroupDetailScreen.kt` — carga perfiles, muestra `MemberDetailDialog` al pulsar miembro
- `ui/group/components/BalancesSection.kt` — items clicables + foto real en avatar
- `ui/group/components/MemberDetailDialog.kt` — **nuevo** dialog con foto ampliada, nombre y teléfono

### Funcionalidades
1. El usuario puede guardar su teléfono en Perfil (opcional, junto con nombre)
2. En la pestaña Balances de un grupo, cada miembro es clicable
3. Al pulsar aparece un dialog con la foto ampliada (100dp), el username y el teléfono (o "Sin número")
4. Los avatares en la lista de balances muestran la foto real del usuario si la tiene

---

## Próxima funcionalidad: integración de pago con Bizum

### Objetivo

Permitir al usuario lanzar un pago Bizum directamente desde la app cuando quiera saldar una deuda, sin tener que salir manualmente a otra app.

### Consideraciones técnicas importantes

**Bizum no tiene una API pública oficial para terceros (P2P)**. Las opciones reales son:

1. **Deep link a la app de Bizum** (opción más viable sin acuerdo comercial):
   - URI scheme: `bizum://` — no está documentado oficialmente pero existe
   - Alternativa: Intent hacia la app bancaria con los datos pre-rellenados
   - Limitación: no hay confirmación de pago de vuelta a SplitApp

2. **Bizum for Business / Bizum eCommerce** (requiere ser empresa registrada):
   - Pasarela a través de Redsys o PaynoPain
   - Requiere credenciales de comercio, certificados TLS y backend propio
   - Documentación: redsys.es / paynopain.com

3. **API de un banco concreto** (BBVA, Santander, CaixaBank Open API):
   - Requieren OAuth2 + registro como desarrollador en cada banco
   - Solo funciona para clientes de ese banco

### Comportamiento deseado en la app

- En la pantalla de balances o en "Quién me debe", un botón "Pagar con Bizum" junto a cada deuda
- El botón pre-rellena el importe y el destinatario (teléfono o número Bizum del acreedor)
- Si se usa deep link: lanza la app/banco con los datos; al volver a SplitApp se puede marcar manualmente como pagado
- Si se usa API real: confirmar el pago y liquidar automáticamente la deuda en Firestore

### Datos disponibles en la app

- `group.balancesCentimos: Map<userId, Long>` — balances por usuario
- `SimplifyDebtsUseCase` — devuelve `List<Transferencia(deudor, acreedor, montoCentimos)`
- `groupRepository.getUserNames(userIds)` — nombres de usuario
- El teléfono/número Bizum del acreedor **no está almacenado actualmente** en el modelo `User` — habría que añadir un campo opcional `telefonoBizum: String = ""`

### Archivos a crear / modificar

- `data/model/User.kt` — añadir campo `telefonoBizum: String = ""`
- `domain/repository/AuthRepository.kt` + `AuthRepositoryImpl.kt` — método para guardar/leer teléfono Bizum
- `ui/group/components/ProfileDialog.kt` — campo para que el usuario introduzca su número Bizum
- `ui/group/components/BalancesSection.kt` — botón "Pagar con Bizum" en cada fila de deuda
- `ui/group/components/SettleDebtDialog.kt` — integrar opción Bizum en el flujo de liquidación
- `util/BizumHelper.kt` (nuevo) — lógica para construir el deep link / Intent y lanzarlo
- `res/values/strings.xml` + `res/values-es/strings.xml` — strings de la funcionalidad
- Si se implementa API real: nueva capa `data/remote/BizumApiService.kt` + repositorio

### Flujo recomendado para empezar (MVP con deep link)

1. Añadir `telefonoBizum` al modelo `User` y al profile dialog (campo opcional)
2. Crear `BizumHelper.kt` que construya un `Intent` hacia Bizum con importe y destinatario
3. Añadir botón en `SettleDebtDialog` que lance el Intent
4. Mostrar diálogo de confirmación: "¿Has completado el pago? Marcar deuda como pagada"
5. Si el usuario confirma → liquidar la deuda normalmente vía `confirmSettlement`
