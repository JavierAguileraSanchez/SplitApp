# SplitApp — Handoff

## Resumen del proyecto

SplitApp es una app Android (Jetpack Compose + Firebase) para dividir gastos entre grupos de personas.

**Stack**: Kotlin, Jetpack Compose, Firebase Auth, Firestore, Firebase Storage. Arquitectura MVVM: ViewModel + UseCases + Repository.

**Funcionalidades implementadas**:
- Registro e inicio de sesión (Firebase Auth) con foto de perfil (Firebase Storage)
- Nombres de usuario únicos (case-insensitive), teléfono opcional en perfil
- Crear grupos, añadir gastos, ver balances por miembro
- División personalizada de gastos (por importe o porcentaje)
- Simplificación de deudas (algoritmo de transferencias mínimas)
- Sistema de invitaciones: enviar, aceptar, rechazar (bloqueo tras 2 rechazos)
- Badge de invitaciones pendientes
- Selector de moneda por grupo (EUR/USD/GBP)
- Confirmación mutua para liquidar deudas (todos los acreedores deben confirmar)
- Pantalla "Quién me debe" con vista por persona y por grupo
- Al pulsar un miembro en la pantalla de balances, se muestra un dialog con su foto ampliada, nombre y teléfono
- i18n ES/EN con selector en perfil y en login/register
- Modo claro/oscuro con persistencia por usuario (SharedPreferences)

---

## Próxima funcionalidad: chat de grupo

Añadir un chat en tiempo real dentro de cada grupo para que los miembros puedan comunicarse sin salir de la app.
