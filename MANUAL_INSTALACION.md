# Manual de Instalación — SplitApp

**Versión 1.0**  
**Proyecto de Fin de Grado — Desarrollo de Aplicaciones Multiplataforma**  
**Autor: Javier Aguilera Sánchez**

---

## Índice

1. [Introducción](#1-introducción)
2. [Requisitos del sistema](#2-requisitos-del-sistema)
3. [Medio de distribución](#3-medio-de-distribución)
4. [Instalación en dispositivo Android](#4-instalación-en-dispositivo-android)
5. [Instalación del entorno de desarrollo](#5-instalación-del-entorno-de-desarrollo)
6. [Configuración del proyecto Firebase](#6-configuración-del-proyecto-firebase)
7. [Compilación y ejecución del proyecto](#7-compilación-y-ejecución-del-proyecto)
8. [Desinstalación](#8-desinstalación)
9. [Resolución de problemas de instalación](#9-resolución-de-problemas-de-instalación)

---

## 1. Introducción

Este documento describe los procedimientos necesarios para instalar y poner en marcha SplitApp en sus dos modalidades:

- **Instalación para el usuario final:** cómo instalar la aplicación en un dispositivo Android a partir del fichero APK entregado con el proyecto.
- **Instalación del entorno de desarrollo:** cómo configurar el entorno completo para compilar, modificar y ejecutar el proyecto desde su código fuente.

---

## 2. Requisitos del sistema

### 2.1 Requisitos para el usuario final

| Requisito | Mínimo | Recomendado |
|---|---|---|
| Sistema operativo | Android 6.0 (API 23) | Android 10.0 (API 29) o superior |
| Espacio libre en almacenamiento | 50 MB | 100 MB |
| Conexión a Internet | Wi-Fi o datos móviles (3G/4G/5G) | Wi-Fi o 4G/5G |
| Cuenta de correo electrónico | Obligatoria para el registro | — |

### 2.2 Requisitos para el entorno de desarrollo

| Requisito | Versión mínima | Versión utilizada en el proyecto |
|---|---|---|
| Sistema operativo (PC) | Windows 10 / macOS 12 / Ubuntu 20.04 | Windows 10 |
| Java Development Kit (JDK) | 17 | 17 |
| Android Studio | Hedgehog (2023.1.1) | Meerkat (2024.3.2) |
| RAM del equipo | 8 GB | 16 GB recomendados |
| Espacio libre en disco | 8 GB | 16 GB recomendados |
| Cuenta de Google | Obligatoria (Firebase Console) | — |

---

## 3. Medio de distribución

### 3.1 Distribución actual

En la versión actual del proyecto, SplitApp se distribuye mediante un **fichero APK** (Android Package), que es el formato nativo de instalación de aplicaciones Android. El APK se entrega directamente junto con la documentación del proyecto para su evaluación e instalación manual en el dispositivo.

Esta modalidad es adecuada para el contexto académico en el que se enmarca el proyecto, ya que permite instalar y probar la aplicación sin necesidad de cuentas de tienda ni procesos de revisión externos.

### 3.2 Medio de distribución previsto a futuro: Google Play Store

Como paso natural hacia la distribución pública de la aplicación, el canal previsto para una versión futura de SplitApp es **Google Play Store**, la tienda oficial de aplicaciones para Android.

Google Play Store ofrece las siguientes ventajas respecto a la distribución directa por APK:

- **Mayor alcance:** disponible para cualquier usuario Android sin necesidad de configuraciones adicionales.
- **Actualizaciones automáticas:** los usuarios reciben nuevas versiones de forma transparente.
- **Confianza del usuario:** la presencia en la tienda oficial otorga credibilidad a la aplicación.
- **Revisión de seguridad:** Google analiza las aplicaciones publicadas antes de hacerlas disponibles.

La publicación en Play Store requeriría:
1. Crear una cuenta de desarrollador en Google Play Console (con una tasa única de 25 USD).
2. Generar un keystore de firma para la versión de producción de la aplicación.
3. Compilar la aplicación en formato **Android App Bundle (.aab)**, el formato requerido por Play Store.
4. Completar la ficha de la aplicación (nombre, descripción, capturas de pantalla, política de privacidad).
5. Superar el proceso de revisión de Google (habitualmente entre horas y 3 días hábiles).

La publicación en Play Store se contempla como una **mejora futura** del proyecto, una vez superada la fase académica.

---

## 4. Instalación en dispositivo Android

### 4.1 Obtención del fichero APK

El fichero de instalación de SplitApp se denomina `splitapp-v1.0.apk` y se entrega junto con la documentación del proyecto. Puede transferirse al dispositivo Android mediante:

- Cable USB (copiando el fichero directamente al almacenamiento del dispositivo).
- Correo electrónico (enviándolo como adjunto y abriéndolo desde el dispositivo).
- Servicio de almacenamiento en la nube (Google Drive, Dropbox, etc.).

### 4.2 Habilitación de la instalación desde fuentes desconocidas

Android bloquea por defecto la instalación de aplicaciones que no provienen de Google Play Store. Para instalar el APK de SplitApp es necesario habilitar esta opción. El procedimiento varía según la versión de Android instalada en el dispositivo:

**Android 8.0 o superior (procedimiento habitual en dispositivos actuales):**

1. Transfiera el fichero APK al dispositivo y localícelo en el gestor de archivos.
2. Pulse sobre el fichero. Android mostrará un aviso indicando que la instalación desde esta fuente no está permitida.
3. Pulse **"Ajustes"** en el mensaje de advertencia.
4. Active la opción **"Permitir desde esta fuente"** para la aplicación desde la que está intentando instalar (normalmente el gestor de archivos).
5. Vuelva atrás. Android retomará el proceso de instalación automáticamente.

**Android 6.0 o 7.0:**

1. Acceda a **Ajustes → Seguridad**.
2. Active la opción **"Orígenes desconocidos"** o **"Fuentes desconocidas"**.
3. Confirme el aviso de seguridad que mostrará el sistema.
4. Localice el fichero APK y pulse sobre él para iniciar la instalación.

[Figura 1: Activación del permiso de instalación desde fuentes desconocidas en Android 10]

### 4.3 Proceso de instalación

1. Localice el fichero `splitapp-v1.0.apk` en el gestor de archivos del dispositivo.
2. Pulse sobre el fichero para iniciar el instalador del sistema.
3. Se mostrará la pantalla de confirmación de instalación con el nombre de la aplicación y los permisos que requiere.
4. Revise la información y pulse **"Instalar"**.
5. El sistema instalará la aplicación en unos segundos.
6. Pulse **"Abrir"** para iniciar SplitApp directamente o **"Hecho"** para cerrar el instalador.

[Figura 2: Pantalla de confirmación de instalación del APK de SplitApp]

### 4.4 Primer inicio y registro

Al abrir la aplicación por primera vez se mostrará la pantalla de bienvenida. Para comenzar a utilizarla:

1. Pulse **"Registrarse"** si no dispone de cuenta.
2. Introduzca su nombre de usuario, correo electrónico y contraseña.
3. Opcionalmente, añada una fotografía de perfil.
4. Pulse **"Registrarse"** para crear la cuenta e iniciar la sesión automáticamente.

Si ya dispone de una cuenta, pulse **"Iniciar sesión"** e introduzca sus credenciales.

Para una descripción completa del uso de la aplicación, consulte el **Manual de Usuario** (`MANUAL_USUARIO.md`).

### 4.5 Permisos solicitados por la aplicación

| Permiso | Motivo |
|---|---|
| Acceso a Internet | Sincronización de datos con Firebase en tiempo real |
| Acceso a imágenes de la galería | Selección de fotografía de perfil |
| Escritura en almacenamiento | Guardado del fichero CSV exportado |
| Acceso a la cámara | Captura de fotografía de perfil con la cámara del dispositivo |

Los permisos se solicitan de forma contextual en el momento en que el usuario accede a la funcionalidad que los requiere, no durante la instalación.

---

## 5. Instalación del entorno de desarrollo

Esta sección está dirigida a desarrolladores o evaluadores técnicos que necesiten compilar o modificar el proyecto desde su código fuente.

### 5.1 Instalación de Java Development Kit (JDK 17)

1. Acceda a https://adoptium.net y descargue el **JDK 17** para su sistema operativo.
2. Ejecute el instalador y siga el asistente con las opciones predeterminadas.
3. Verifique la instalación abriendo una terminal y ejecutando:
   ```
   java -version
   ```
   La salida debe mostrar una versión `17.x.x`.

### 5.2 Instalación de Android Studio

1. Descargue Android Studio desde https://developer.android.com/studio
2. Ejecute el instalador. Durante el asistente de configuración inicial:
   - Seleccione el tipo de instalación **Standard**.
   - Acepte los componentes sugeridos (Android SDK, Android Virtual Device).
3. Android Studio descargará e instalará los componentes necesarios automáticamente. Este proceso puede tardar varios minutos.
4. Una vez completada la instalación, inicie Android Studio.

[Figura 3: Pantalla de bienvenida de Android Studio tras la instalación]

### 5.3 Obtención del código fuente

**Mediante Git (recomendado):**
```
git clone <url-del-repositorio>
cd SplitApp2
```

**Mediante fichero ZIP:**  
Si dispone del código fuente en formato comprimido, descomprima el fichero ZIP en la ubicación deseada del equipo.

### 5.4 Instalación de un emulador Android (opcional)

Si no dispone de dispositivo físico Android para las pruebas:

1. En Android Studio, acceda a **Tools → Device Manager**.
2. Pulse **"Create Device"**.
3. Seleccione un perfil de dispositivo; se recomienda **Pixel 6** o similar.
4. En la pantalla de selección de imagen del sistema, descargue y seleccione **API 33 (Android 13)** o superior.
5. Pulse **"Finish"**. El dispositivo virtual aparecerá en la lista del Device Manager.
6. Pulse el icono de reproducción para arrancar el emulador.

---

## 6. Configuración del proyecto Firebase

El proyecto requiere el fichero de configuración `google-services.json`, descargado desde Firebase Console. Sin este fichero el proyecto no compilará.

### 6.1 Creación del proyecto en Firebase Console

1. Acceda a https://console.firebase.google.com e inicie sesión con una cuenta de Google.
2. Pulse **"Añadir proyecto"** e introduzca el nombre `SplitApp`.
3. Desactive Google Analytics si no es necesario y pulse **"Crear proyecto"**.

### 6.2 Registro de la aplicación Android

1. En el panel del proyecto, pulse el icono de Android.
2. Introduzca el nombre del paquete: `com.example.splitapp`.
3. Pulse **"Registrar aplicación"**.

### 6.3 Descarga y ubicación del fichero google-services.json

1. Descargue el fichero `google-services.json` que ofrece el asistente de Firebase.
2. Colóquelo en la carpeta `app/` del proyecto:
   ```
   SplitApp2/
   └── app/
       └── google-services.json   ← aquí
   ```
3. **No incluya este fichero en el control de versiones** (está excluido en el `.gitignore` ya que contiene claves de API privadas).

### 6.4 Activación de los servicios de Firebase

**Authentication:**
1. Acceda a **Authentication → Sign-in method** en Firebase Console.
2. Active el proveedor **"Correo electrónico/contraseña"** y guarde los cambios.

[Figura 4: Firebase Console — activación del proveedor de autenticación]

**Cloud Firestore:**
1. Acceda a **Firestore Database → Crear base de datos**.
2. Seleccione **"Comenzar en modo de producción"** y elija la región `eur3 (europe-west)`.
3. Una vez creada, acceda a la pestaña **"Reglas"** e introduzca las siguientes reglas de seguridad:

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
          request.auth.uid in get(/databases/$(database)/documents/grupos/$(groupId)).data.miembrosActivos;
      }
    }
  }
}
```

4. Pulse **"Publicar"** para activar las reglas.

**Firebase Storage:**
1. Acceda a **Storage → Comenzar** y seleccione la misma región que Firestore.
2. En la pestaña **"Reglas"**, configure el acceso para usuarios autenticados:

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

[Figura 5: Firebase Console — panel con los tres servicios activados]

---

## 7. Compilación y ejecución del proyecto

### 7.1 Apertura del proyecto en Android Studio

1. Inicie Android Studio y acceda a **File → Open**.
2. Seleccione la carpeta raíz del proyecto (`SplitApp2/`).
3. Android Studio detectará el proyecto Gradle e iniciará la sincronización automáticamente.
4. Espere a que finalice la sincronización. Si aparece un error relacionado con `google-services.json`, verifique que el fichero está en `app/` (sección 6.3).

### 7.2 Verificación de la compilación

Compruebe que el proyecto compila sin errores mediante el siguiente comando desde la terminal en la raíz del proyecto:

**Linux / macOS:**
```bash
./gradlew :app:compileDebugKotlin
```

**Windows:**
```
gradlew.bat :app:compileDebugKotlin
```

La compilación debe completarse con el mensaje `BUILD SUCCESSFUL`. Las advertencias (*warnings*) son normales y no impiden la ejecución.

### 7.3 Ejecución de la aplicación

Con un emulador activo o un dispositivo físico conectado por USB con depuración USB habilitada:

1. Seleccione el dispositivo en el desplegable de la barra de herramientas de Android Studio.
2. Pulse el botón **Run** o presione `Shift + F10`.
3. Android Studio compilará e instalará la aplicación en el dispositivo seleccionado.
4. La aplicación se iniciará automáticamente mostrando la pantalla de bienvenida.

### 7.4 Generación del APK para distribución

1. En Android Studio, acceda a **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
2. Espere a que finalice el proceso de compilación.
3. Android Studio mostrará una notificación en la esquina inferior derecha con un enlace **"locate"** para abrir la carpeta con el fichero generado.
4. El APK se encontrará en: `app/build/outputs/apk/debug/app-debug.apk`.

---

## 8. Desinstalación

### 8.1 Desinstalación desde el dispositivo Android

**Desde los ajustes del sistema:**
1. Acceda a **Ajustes → Aplicaciones** (o **Gestión de aplicaciones**).
2. Localice **SplitApp** en la lista.
3. Pulse **"Desinstalar"** y confirme.

**Desde el lanzador de aplicaciones:**
1. Mantenga pulsado el icono de SplitApp.
2. Seleccione la opción **"Desinstalar"** del menú contextual o arrastre el icono al área de desinstalación.

> **Nota:** La desinstalación de la aplicación del dispositivo **no elimina** los datos del usuario almacenados en Firebase (grupos, gastos y balances). Estos datos permanecen en la nube y son accesibles iniciando sesión desde cualquier otro dispositivo con la misma cuenta.

### 8.2 Eliminación del entorno de desarrollo

Para limpiar el entorno de desarrollo del equipo:

1. Elimine la carpeta del proyecto.
2. Desinstale Android Studio mediante el desinstalador del sistema operativo.
3. Elimine la carpeta del SDK de Android ubicada en `%USERPROFILE%\AppData\Local\Android\Sdk` (Windows) o `~/Library/Android/sdk` (macOS).

---

## 9. Resolución de problemas de instalación

| Problema | Causa probable | Solución |
|---|---|---|
| No se puede instalar el APK | Fuentes desconocidas no habilitadas | Seguir los pasos de la sección 4.2 |
| El proyecto no compila | Falta `google-services.json` | Completar la sección 6.3 |
| Error de Gradle al abrir el proyecto | Versión de JDK incorrecta | Verificar JDK 17 en **File → Project Structure → SDK Location** |
| El emulador no arranca o es muy lento | Virtualización de hardware desactivada | Activar Intel VT-x / AMD-V en la BIOS del equipo |
| "Error de conexión" al iniciar la app | Sin conexión a Internet | Verificar conectividad del dispositivo antes de abrir la app |
| La foto de perfil no se puede seleccionar | Permiso de galería denegado | Conceder el permiso en **Ajustes → Aplicaciones → SplitApp → Permisos** |
| El CSV no se genera | Permiso de almacenamiento denegado | Conceder el permiso de almacenamiento desde los ajustes de la app |

---

*Manual de Instalación — SplitApp v1.0*  
*Javier Aguilera Sánchez — DAM — Mayo 2026*
