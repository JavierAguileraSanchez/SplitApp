# Handoff: Implementación i18n (EN/ES) — SplitApp

## Estado actual (sesión anterior)

Se está implementando soporte bilingüe (inglés/español) en la app Android SplitApp.
- Idioma por defecto: **inglés**
- Selector en login (top-right, toggle EN|ES)
- Selector completo en ProfileDialog (segmented button)
- El cambio de idioma llama `activity.recreate()` para aplicar el nuevo locale

## Archivos YA COMPLETADOS ✅

1. `app/src/main/java/com/example/splitapp/util/LocaleManager.kt` — CREADO
   - `getLanguage(context)` / `setLanguage(context, lang)` / `applyLocale(context)`
   - Usa SharedPreferences (key: `"language"`, default `"en"`)

2. `app/src/main/res/values/strings.xml` — REESCRITO (inglés, recurso default)
3. `app/src/main/res/values-es/strings.xml` — CREADO (español)

## Archivos PENDIENTES ❌ (en orden de dependencia)

### 4. `MainActivity.kt`
Añadir:
- `override fun attachBaseContext(newBase: Context) { super.attachBaseContext(LocaleManager.applyLocale(newBase)) }`
- `SplitAppNavigation(onLanguageChange = { lang -> LocaleManager.setLanguage(this, lang); recreate() })`
- `SplitAppNavigation` recibe `onLanguageChange: (String) -> Unit` como parámetro

### 5. `ui/navigation/NavGraph.kt`
- Añadir `onLanguageChange: (String) -> Unit` al composable `NavGraph`
- Pasarlo a `LoginScreen(... onLanguageChange = onLanguageChange)`
- Pasarlo a `GroupListScreen(... onLanguageChange = onLanguageChange)`

### 6. `ui/auth/LoginScreen.kt`
- Añadir `onLanguageChange: (String) -> Unit` y `currentLanguage: String = LocaleManager.getLanguage(LocalContext.current)` al composable (o leer dentro con `val context = LocalContext.current; val currentLanguage = LocaleManager.getLanguage(context)`)
- Envolver TODO en `Box(Modifier.fillMaxSize())` y añadir el toggle EN|ES en `Modifier.align(Alignment.TopEnd).padding(8.dp)` usando dos `TextButton` con peso bold para el seleccionado
- Reemplazar todos los strings hardcoded con `stringResource(R.string.xxx)`

Strings clave LoginScreen:
```
"Gestiona tus gastos compartidos" → stringResource(R.string.login_subtitle)
"Formato de email inválido"        → stringResource(R.string.login_email_error)
"Contraseña"                       → stringResource(R.string.login_password_label)
"Iniciar Sesión"                   → stringResource(R.string.login_button)
"Reintentar"                       → stringResource(R.string.retry)
"¿No tienes cuenta?..."           → stringResource(R.string.login_register_link)
"Logo SplitApp"                    → stringResource(R.string.login_logo_cd)
```

### 7. `ui/auth/RegisterScreen.kt`
- Añadir `onLanguageChange` NO ES NECESARIO aquí (el usuario no necesita toggle en register)
- Solo reemplazar strings hardcoded con stringResource

```
"Crear Cuenta"               → stringResource(R.string.register_title)
"Añadir foto"                → stringResource(R.string.register_add_photo_cd)
"Nombre de usuario"          → stringResource(R.string.register_name_label)
"Sin espacios · ${nombre.length}/15 caracteres" → stringResource(R.string.register_name_hint, nombre.length)
"Formato de email inválido"  → stringResource(R.string.login_email_error)  // same key
"Contraseña"                 → stringResource(R.string.login_password_label)  // same key
"Registrarse"                → stringResource(R.string.register_button)
"Reintentar"                 → stringResource(R.string.retry)
"¿Ya tienes cuenta?..."      → stringResource(R.string.register_login_link)
"Foto de perfil"             → stringResource(R.string.register_photo_dialog_title)
"Elegir de la galería"       → stringResource(R.string.choose_from_gallery)
"Tomar foto"                 → stringResource(R.string.take_photo)
"Cancelar"                   → stringResource(R.string.cancel)
```

### 8. `ui/group/GroupListScreen.kt`
- Añadir `onLanguageChange: (String) -> Unit` al composable
- Pasarlo a `ProfileDialog(... onLanguageChange = onLanguageChange)`
- Reemplazar strings

```
"Mis Grupos"                     → stringResource(R.string.groups_title)
"Unirse con enlace"               → stringResource(R.string.groups_join_link_cd)
"Perfil"                          → stringResource(R.string.groups_profile_cd)
"Crear Grupo"                     → stringResource(R.string.groups_create_cd)
"Total que debo"                  → stringResource(R.string.groups_total_owed)
"Total que me deben"              → stringResource(R.string.groups_total_owed_to_me)
"No tienes grupos"                → stringResource(R.string.groups_empty_title)
"Presiona el botón + para crear..." → stringResource(R.string.groups_empty_hint)
```

### 9. `ui/group/GroupDetailScreen.kt`
Strings a reemplazar:
```kotlin
// Pre-resolve estos antes de lambdas/LaunchedEffect:
val linkCopiedMsg = stringResource(R.string.group_link_copied)
val invLinkLabel = stringResource(R.string.group_invitation_link_label)
val exportSuccessTemplate = stringResource(R.string.group_export_success)
val invalidAmountMsg = stringResource(R.string.add_expense_invalid_amount)
val noParticipantsMsg = stringResource(R.string.add_expense_no_participants)
val fillSplitMsg = stringResource(R.string.add_expense_fill_split_values)
val amountsMismatchMsg = stringResource(R.string.add_expense_amounts_mismatch)
val pctMismatchMsg = stringResource(R.string.add_expense_percentages_mismatch)

// Usos en LaunchedEffect y lambdas:
"Enlace de invitación copiado" → linkCopiedMsg
"Historial exportado con éxito en ${event.filePath}" → String.format(exportSuccessTemplate, event.filePath)

// En customSplitValidationMessage:
"Ingresa un monto total válido" → invalidAmountMsg
"Selecciona al menos un participante" → noParticipantsMsg
etc.

// En UI:
"Detalle del Grupo" → stringResource(R.string.group_detail_fallback)
"Cargando grupo..." → stringResource(R.string.group_loading)
"Grupo no encontrado" → stringResource(R.string.group_not_found)
"Atrás" → stringResource(R.string.group_back_cd)
"Compartir enlace..." → stringResource(R.string.group_share_cd)
"Agregar miembro" → stringResource(R.string.group_add_member_cd)
"Más opciones" → stringResource(R.string.group_more_options_cd)
"Exportar gastos" → stringResource(R.string.group_export_expenses)
"Eliminar grupo" / "Salirse del grupo" → stringResource(R.string.group_delete/group_leave)
"Añadir gasto" → stringResource(R.string.group_add_expense_cd)
"Balances" → stringResource(R.string.group_tab_balances)
"Gastos" → stringResource(R.string.group_tab_expenses)
```

### 10. `ui/group/GroupActionConfirmDialog.kt`
```
"¿Quieres eliminar este grupo?..." → stringResource(R.string.group_action_delete_confirm)
"¿Quieres salirte de este grupo?" → stringResource(R.string.group_action_leave_confirm)
"Eliminar grupo"                   → stringResource(R.string.group_action_delete_button)
"Salirse del grupo"                → stringResource(R.string.group_action_leave_button)
"Cancelar"                         → stringResource(R.string.cancel)
```

### 11. `ui/group/components/ProfileDialog.kt`
- Añadir `onLanguageChange: (String) -> Unit` al composable
- Leer `val currentLanguage = remember { LocaleManager.getLanguage(context) }` (context ya existe)
- Añadir sección de idioma con `SingleChoiceSegmentedButtonRow` (Material3 experimental) tras el campo nombre
- Strings a reemplazar:
```
"Cambiar foto de perfil" → stringResource(R.string.profile_change_photo_title)
"Elegir de la galería"   → stringResource(R.string.choose_from_gallery)
"Tomar foto"             → stringResource(R.string.take_photo)
"Cancelar"               → stringResource(R.string.cancel)
"Perfil"                 → stringResource(R.string.profile_title)
"Cambiar foto"           → stringResource(R.string.profile_change_photo)
"Nombre"                 → stringResource(R.string.profile_name_label)
"Sin espacios · ${x.length}/15 caracteres" → stringResource(R.string.profile_name_hint, x.length)
"Email: ${user.email}"   → stringResource(R.string.profile_email_label, user.email)
"Rol: Administrador/Usuario" → stringResource(R.string.profile_role_admin/user)
"Perfil actualizado correctamente" → stringResource(R.string.profile_updated)
"No se pudo cargar el perfil" → stringResource(R.string.profile_load_error)
"Guardar cambios"        → stringResource(R.string.profile_save)
"Cerrar"                 → stringResource(R.string.profile_close)
"Cerrar Sesión"          → stringResource(R.string.profile_logout)
"Idioma"                 → stringResource(R.string.language)
"Español"/"English"      → stringResource(R.string.lang_spanish/lang_english)
```

Código del selector de idioma a insertar en ProfileDialog (dentro del when(profileState) is Success, después del campo nombre):
```kotlin
Spacer(modifier = Modifier.height(12.dp))
Text(text = stringResource(R.string.language), style = MaterialTheme.typography.labelMedium)
Spacer(modifier = Modifier.height(8.dp))
SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
    listOf(
        LocaleManager.LANG_ES to stringResource(R.string.lang_spanish),
        LocaleManager.LANG_EN to stringResource(R.string.lang_english)
    ).forEachIndexed { index, (code, label) ->
        SegmentedButton(
            selected = currentLanguage == code,
            onClick = { onLanguageChange(code) },
            shape = SegmentedButtonDefaults.itemShape(index = index, count = 2),
            label = { Text(label) }
        )
    }
}
```
Imports necesarios:
```kotlin
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import com.example.splitapp.util.LocaleManager
```

### 12. `ui/group/components/AddExpenseDialog.kt`
```
splitHint → val splitAmountLabel = stringResource(R.string.add_expense_split_amount)
            val splitPctLabel = stringResource(R.string.add_expense_split_percentage)
            val splitHint = if(customSplitMode == SplitMode.Percentages) splitPctLabel else splitAmountLabel

"Añadir nuevo gasto"          → stringResource(R.string.add_expense_title)
"Título"                      → stringResource(R.string.add_expense_title_label)
"Monto"                       → stringResource(R.string.add_expense_amount_label)
"Pagado por"                  → stringResource(R.string.add_expense_paid_by)
"División personalizada"      → stringResource(R.string.add_expense_custom_split)
"Modo de división"            → stringResource(R.string.add_expense_split_mode)
"Introduce $splitHint por participante" → stringResource(R.string.add_expense_enter_value, splitHint)
"¿Quiénes participan?"        → stringResource(R.string.add_expense_who_participates)
"Resumen de Impacto"          → stringResource(R.string.add_expense_impact_summary)
"Confirmar"                   → stringResource(R.string.confirm)
"Cancelar"                    → stringResource(R.string.cancel)

roundingMessage:
val missingTemplate = stringResource(R.string.add_expense_missing_amount)
val overTemplate = stringResource(R.string.add_expense_over_amount)
roundingDifference > 0 → String.format(missingTemplate, "${String.format("%.2f", roundingDifference)}€")
else                    → String.format(overTemplate, "${String.format("%.2f", -roundingDifference)}€")
```

### 13. `ui/group/components/BalancesSection.kt`
```
"No hay miembros registrados" → stringResource(R.string.balances_no_members)
"Balances actuales"           → stringResource(R.string.balances_current)
"Liquidar deuda"              → stringResource(R.string.balances_settle_debt)
"Le deben: ${balance.formatEuros()}" → stringResource(R.string.balance_owed_to_them, balance.formatEuros())
"Debe: ${(-balance).formatEuros()}"  → stringResource(R.string.balance_owes, (-balance).formatEuros())
"Saldo neutro"                → stringResource(R.string.balance_neutral)
"inactivo"                    → stringResource(R.string.balance_inactive)
```

### 14. `ui/group/components/ExpensesSection.kt`
```
"Aún no hay gastos..."        → stringResource(R.string.expenses_empty_title)
"Registra tu primer gasto..." → stringResource(R.string.expenses_empty_hint)
"Total: ${...}"               → stringResource(R.string.expense_total, expense.montoCentimos.formatEuros())
"Pagado por: $payerName"      → stringResource(R.string.expense_paid_by_label, payerName)
"Fecha: ${...}"               → stringResource(R.string.expense_date_label, expense.createdAt?.toDate()?.formatToDisplay() ?: "—")
"Eliminar gasto" (title)      → stringResource(R.string.expense_delete_title)
"¿Eliminar \"${expense.concepto}\"?..." → stringResource(R.string.expense_delete_confirm, expense.concepto)
"Eliminar" (button)           → stringResource(R.string.expense_delete_button)
"Cancelar"                    → stringResource(R.string.cancel)
"Eliminar gasto" (cd)         → stringResource(R.string.expense_delete_cd)
```

### 15. `ui/group/components/SettleDebtDialog.kt`
```
"Liquidar deuda"              → stringResource(R.string.settle_title)
"Para ponerse al día:"        → stringResource(R.string.settle_description)
"No hay deudas pendientes."   → stringResource(R.string.settle_no_debts)
"• $deudor debe pagar $monto a $acreedor" → stringResource(R.string.settle_transfer, deudorName, transaction.montoCentimos.formatEuros(), acreedorName)
"Confirmar Pago"              → stringResource(R.string.settle_confirm)
"Cancelar"                    → stringResource(R.string.cancel)
```

### 16. `ui/group/components/AddMemberDialog.kt`
```
"Añadir miembro al grupo"     → stringResource(R.string.add_member_title)
"Buscar por nombre de usuario"→ stringResource(R.string.add_member_search_label)
"No se encontraron usuarios"  → stringResource(R.string.add_member_no_results)
"Confirmar"                   → stringResource(R.string.confirm)
"Cancelar"                    → stringResource(R.string.cancel)
```

### 17. `ui/group/components/CreateGroupDialog.kt`
```
"Crear Nuevo Grupo"           → stringResource(R.string.create_group_title)
"Nombre del grupo"            → stringResource(R.string.create_group_name_label)
"Descripción (opcional)"      → stringResource(R.string.create_group_description_label)
"Crear"                       → stringResource(R.string.create_group_confirm)
"Cancelar"                    → stringResource(R.string.cancel)
```

### 18. `ui/group/components/JoinGroupDialog.kt`
```
"Unirse con enlace"           → stringResource(R.string.join_group_title)
"Enlace o ID del grupo"       → stringResource(R.string.join_group_input_label)
"splitapp://join?groupId=... o solo el ID" → stringResource(R.string.join_group_input_placeholder)
"Unirse"                      → stringResource(R.string.join_group_confirm)
"Cancelar"                    → stringResource(R.string.cancel)
```

### 19. `ui/group/components/GroupCard.kt`
```
"Miembros activos: ${group.miembrosActivos.size}" → stringResource(R.string.group_active_members, group.miembrosActivos.size)
"Me deben: ${userBalance.formatEuros()}"  → stringResource(R.string.group_card_owed_to_me, userBalance.formatEuros())
"Les debo: ${(-userBalance).formatEuros()}" → stringResource(R.string.group_card_i_owe, (-userBalance).formatEuros())
"Saldo: ${userBalance.formatEuros()}"    → stringResource(R.string.group_card_balance, userBalance.formatEuros())
```

### 20. `ui/group/components/GroupDescriptionAccordion.kt`
```
"Descripción del grupo"       → stringResource(R.string.group_description_header)
"Contraer"                    → stringResource(R.string.group_description_collapse)
"Expandir"                    → stringResource(R.string.group_description_expand)
```

## Lógica del toggle de idioma en LoginScreen

```kotlin
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToGroupList: () -> Unit,
    onLanguageChange: (String) -> Unit  // NUEVO
) {
    val context = LocalContext.current
    val currentLanguage = remember { LocaleManager.getLanguage(context) }
    // ... resto del estado existente ...

    Box(modifier = Modifier.fillMaxSize()) {
        // Contenido original (Column con verticalScroll)
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) { /* ... todo el contenido original con stringResource ... */ }

        // Toggle idioma top-right
        Row(
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { if (currentLanguage != LocaleManager.LANG_ES) onLanguageChange(LocaleManager.LANG_ES) },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.lang_es),
                    fontWeight = if (currentLanguage == LocaleManager.LANG_ES) FontWeight.Bold else FontWeight.Normal,
                    color = if (currentLanguage == LocaleManager.LANG_ES) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            Text(text = "|", style = MaterialTheme.typography.labelSmall,
                 color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
            TextButton(
                onClick = { if (currentLanguage != LocaleManager.LANG_EN) onLanguageChange(LocaleManager.LANG_EN) },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.lang_en),
                    fontWeight = if (currentLanguage == LocaleManager.LANG_EN) FontWeight.Bold else FontWeight.Normal,
                    color = if (currentLanguage == LocaleManager.LANG_EN) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

// Imports nuevos para LoginScreen:
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import com.example.splitapp.util.LocaleManager
```

## Mecanismo de funcionamiento

1. `MainActivity.attachBaseContext` lee el idioma guardado en SharedPreferences y envuelve el Context con ese locale.
2. Al cambiar idioma → `LocaleManager.setLanguage()` + `activity.recreate()`.
3. `recreate()` llama `attachBaseContext` de nuevo → contexto con nuevo locale.
4. `stringResource()` usa el contexto correcto → carga strings del qualifier adecuado.
5. NavController state se preserva (usa rememberSaveable internamente) → usuario sigue en la misma pantalla.
6. ViewModels sobreviven `recreate()` → estado de datos se preserva.

## Notas importantes

- `SingleChoiceSegmentedButtonRow` / `SegmentedButton` requieren `@OptIn(ExperimentalMaterial3Api::class)`.
- Los mensajes de error provenientes de repositorios/Firebase quedan en español por ahora (fuera del scope).
- En `GroupDetailScreen.kt`, pre-resolver strings antes de LaunchedEffect/lambdas ya que `stringResource()` no puede usarse en coroutines.
- La `%` literal en strings XML debe escribirse `%%` (ej: `100%%`).
