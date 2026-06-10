# Handoff: SplitApp — Estado completo del proyecto

## ✅ COMPLETADO: Sistema i18n EN/ES

Todos los archivos tienen `stringResource`. El mecanismo:
1. `MainActivity.attachBaseContext` aplica el locale guardado en SharedPreferences
2. Cambio de idioma → `LocaleManager.setLanguage()` + `activity.recreate()`
3. ViewModels y NavController sobreviven el `recreate()`

### Archivos i18n completados
| Archivo | Estado |
|---------|--------|
| `util/LocaleManager.kt` | ✅ LANG_EN/LANG_ES, SharedPreferences, applyLocale |
| `res/values/strings.xml` | ✅ inglés (default) |
| `res/values-es/strings.xml` | ✅ español |
| `MainActivity.kt` | ✅ attachBaseContext + onLanguageChange + recreate() |
| `ui/navigation/NavGraph.kt` | ✅ onLanguageChange propagado a Login y GroupList |
| `ui/auth/LoginScreen.kt` | ✅ toggle EN\|ES top-right en Box |
| `ui/auth/RegisterScreen.kt` | ✅ stringResource en todos los strings |
| `ui/group/GroupListScreen.kt` | ✅ onLanguageChange param + stringResource |
| `ui/group/components/ProfileDialog.kt` | ✅ onLanguageChange + SingleChoiceSegmentedButtonRow ES/EN |
| `ui/group/GroupDetailScreen.kt` | ✅ strings pre-resueltos para LaunchedEffect/lambdas |
| `ui/group/GroupActionConfirmDialog.kt` | ✅ |
| `ui/group/components/AddExpenseDialog.kt` | ✅ |
| `ui/group/components/BalancesSection.kt` | ✅ |
| `ui/group/components/ExpensesSection.kt` | ✅ |
| `ui/group/components/SettleDebtDialog.kt` | ✅ |
| `ui/group/components/AddMemberDialog.kt` | ✅ |
| `ui/group/components/CreateGroupDialog.kt` | ✅ |
| `ui/group/components/JoinGroupDialog.kt` | ✅ |
| `ui/group/components/GroupCard.kt` | ✅ |
| `ui/group/components/GroupDescriptionAccordion.kt` | ✅ |

### Notas i18n
- `@OptIn(ExperimentalMaterial3Api::class)` requerido en ProfileDialog y CreateGroupDialog (SegmentedButton)
- Strings usados en LaunchedEffect/lambdas deben pre-resolverse con `val x = stringResource(...)` antes del LaunchedEffect
- `%` literal en XML → `%%`

---

## ✅ COMPLETADO: Selector de moneda por grupo

Cada grupo tiene su propia moneda (EUR/USD/GBP). Se elige al crear el grupo.

### Archivos modificados
| Archivo | Cambio |
|---------|--------|
| `data/model/Group.kt` | Nuevo campo `val moneda: String = "EUR"` |
| `util/MoneyExtensions.kt` | Nueva función `Long.formatMoney(moneda: String)` — devuelve `3.50€`, `$3.50` o `£3.50` |
| `domain/repository/GroupRepository.kt` | `createGroup()` acepta `moneda: String = "EUR"` |
| `data/repository/GroupRepositoryImpl.kt` | Pasa `moneda` al crear el documento en Firestore |
| `domain/usecase/group/CreateGroupUseCase.kt` | Acepta y propaga `moneda` |
| `ui/group/GroupViewModel.kt` | `createGroup()` acepta `moneda` |
| `ui/group/components/CreateGroupDialog.kt` | `SingleChoiceSegmentedButtonRow` con `€ EUR / $ USD / £ GBP` + params `selectedMoneda`/`onMonedaChange` |
| `ui/group/GroupListScreen.kt` | Estado `groupMoneda`, pasado al dialog y al ViewModel |
| `ui/group/components/GroupCard.kt` | Usa `formatMoney(group.moneda)` |
| `ui/group/components/BalancesSection.kt` | Acepta `moneda`, usa `formatMoney()` |
| `ui/group/components/ExpensesSection.kt` | Acepta `moneda`, usa `formatMoney()` |
| `ui/group/components/SettleDebtDialog.kt` | Acepta `moneda`, usa `formatMoney()` |
| `ui/group/components/AddExpenseDialog.kt` | Acepta `moneda`, usa `formatMoney()` (reemplaza `€` hardcodeado) |
| `ui/group/GroupDetailScreen.kt` | Pasa `group.moneda` a todos los componentes anteriores |
| `res/values/strings.xml` | `create_group_currency_label = "Currency"` |
| `res/values-es/strings.xml` | `create_group_currency_label = "Moneda"` |

### Nota
El resumen global de GroupListScreen (`totalQueDebo`/`totalQueMeDeben`) sigue usando `formatEuros()` ya que agrega grupos de distintas monedas — es una limitación conocida pendiente de diseño.

---

## Estado del repositorio Git

El proyecto fue descargado como ZIP desde:
`https://github.com/JavierAguileraSanchez/SplitApp.git`

Para subir cambios desde cero (sin historial git local):
```powershell
cd "C:\Users\javoa\Desktop\SplitApp-main"
git init
git remote add origin https://github.com/JavierAguileraSanchez/SplitApp.git
git fetch origin
git branch -M main
git reset --mixed origin/main
git add .
git commit -m "descripción del cambio"
git push origin main
```
Si la rama es `master`, sustituir `main` por `master`.
