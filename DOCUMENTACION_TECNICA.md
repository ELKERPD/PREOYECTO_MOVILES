# DOCUMENTACIÓN TÉCNICA COMPLETA

## Proyecto: Moviles ERP v1.0

**Fecha:** 09/12/2025  
**Versión:** 2.0  
**Estado:** Producción  
**Autor:** ELKERPD

---

## 📑 Tabla de Contenidos

1. [Arquitectura del Sistema](#arquitectura-del-sistema)
2. [Stack Tecnológico](#stack-tecnológico)
3. [Estructura del Proyecto](#estructura-del-proyecto)
4. [Componentes Principales](#componentes-principales)
5. [Flujos de Datos](#flujos-de-datos)
6. [APIs REST](#apis-rest)
7. [Patrones de Diseño](#patrones-de-diseño)
8. [Persistencia de Datos](#persistencia-de-datos)
9. [Manejo de Errores](#manejo-de-errores)
10. [Concurrencia](#concurrencia)
11. [Testing](#testing)
12. [Deployment](#deployment)
13. [Troubleshooting](#troubleshooting)

---

## 🏗️ Arquitectura del Sistema

### Diagrama General

```
┌─────────────────────────────────────────────────────────────┐
│                    CAPA DE PRESENTACIÓN                      │
│  ┌────────────────┬──────────────┬────────────────────────┐ │
│  │   Activities   │   Adapters   │   View Components      │ │
│  │  - MainActivity│- Audit...    │ - EditText             │ │
│  │  - LoginAct.   │- Product...  │ - RecyclerView         │ │
│  │  - Principal.. │- Cotizacion..│ - ProgressBar          │ │
│  │  - etc         │              │ - Toast/Dialog         │ │
│  └────────────────┴──────────────┴────────────────────────┘ │
└────────────────────────┬─────────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────────┐
│                    CAPA DE LÓGICA                             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         Coroutines & Async Operations                │   │
│  │  - Dispatchers.IO (Network)                          │   │
│  │  - Dispatchers.Main (UI)                             │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         Validación & Procesamiento                   │   │
│  │  - JSONObject parsing                                │   │
│  │  - Validación de datos                               │   │
│  │  - Cálculos                                           │   │
│  └──────────────────────────────────────────────────────┘   │
└────────────────────────┬─────────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────────┐
│                    CAPA DE DATOS                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         Local Storage (SharedPreferences)             │   │
│  │  - Sesión de usuario                                 │   │
│  │  - Datos empresariales                               │   │
│  │  - Preferencias                                       │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         Remote API (REST)                            │   │
│  │  - HTTP/GET, POST requests                           │   │
│  │  - JSON serialization/deserialization                │   │
│  │  - Authentication & Authorization                    │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### Patrones Arquitectónicos Utilizados

#### MVP (Model-View-Presenter)

```
Activity (View)
    ├─ Presenta UI
    └─ Delega lógica

Business Logic (Presenter)
    ├─ Procesa datos
    └─ Coordina API calls

Model
    ├─ Datos locales
    └─ Datos remotos
```

#### Separación de Responsabilidades

```
Cada Activity:
- ✅ Maneja UI únicamente
- ✅ Delega network a coroutines
- ✅ Almacena datos en SharedPreferences
- ❌ NO contiene lógica de negocio compleja
```

---

## 💻 Stack Tecnológico

### Lenguajes

| Componente   | Lenguaje   | Versión |
| ------------ | ---------- | ------- |
| App Core     | Kotlin     | 2.0.21  |
| Build System | Gradle     | 8.13.0  |
| Scripting    | Kotlin DSL | 2.0.21  |
| Markup       | XML        | 1.0     |

### Frameworks & Librerías

#### Android Framework

```
androidx.core:core-ktx (1.17.0)
- Extensiones Kotlin para Android Core
- Mejor interop Kotlin-Java

androidx.appcompat:appcompat (1.7.1)
- Backward compatibility
- Material Design v3 compatible

androidx.activity:activity (1.11.0)
- Activity Lifecycle management
- ActivityResult API

androidx.constraintlayout:constraintlayout (2.2.1)
- Flexible layouts responsivas
- Performance optimizado
```

#### Material Design

```
com.google.android.material:material (1.13.0)
- Material Design 3 components
- Material Colors
- Animations
```

#### Testing

```
junit:junit (4.13.2)
- Framework testing estándar
- Assertions

androidx.test.ext:junit (1.3.0)
- JUnit 4 para Android

androidx.test.espresso:espresso-core (3.7.0)
- UI testing framework
- Instrumented tests
```

### Dependencias Nativas

```
java.net.HttpURLConnection
- HTTP cliente nativo
- Soporta HTTP 1.1, HTTPS

org.json.JSONObject
- JSON parsing
- Serialización/deserialization

kotlin.coroutines
- Asincronismo
- Threading
```

### Herramientas de Construcción

```
Android Gradle Plugin: 8.13.0
Kotlin Gradle Plugin: 2.0.21
Target API Level: 36 (Android 15)
Minimum API Level: 24 (Android 7.0)
Compilation Version: 11 (Java 11)
```

---

## 📁 Estructura del Proyecto

```
moviles/
│
├── app/                                    # Módulo principal
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml         # Configuración app
│   │   │   │
│   │   │   ├── java/com/example/moviles/
│   │   │   │   ├── MainActivity.kt         # Router activity
│   │   │   │   ├── LoginActivity.kt        # Autenticación
│   │   │   │   ├── PrincipalActivity.kt    # Dashboard
│   │   │   │   ├── DatosEmpresaActivity.kt # Gestión empresa
│   │   │   │   ├── AuditoriasActivity.kt   # Auditoría
│   │   │   │   │   └── AuditoriasAdapter.kt
│   │   │   │   │   └── AuditoriaItem.kt
│   │   │   │   ├── DatosSunatActivity.kt   # Config SUNAT
│   │   │   │   ├── ProductosActivity.kt    # CRUD productos
│   │   │   │   │   └── ProductosAdapter.kt
│   │   │   │   │   └── Producto.kt
│   │   │   │   └── CotizacionesActivity.kt # Cotizaciones
│   │   │   │       ├── ProductosCotizacionAdapter.kt
│   │   │   │       └── ProductoCotizacion.kt
│   │   │   │
│   │   │   └── res/
│   │   │       ├── layout/
│   │   │       │   ├── activity_login.xml
│   │   │       │   ├── activity_principal.xml
│   │   │       │   ├── activity_datos_empresa.xml
│   │   │       │   ├── activity_auditorias.xml
│   │   │       │   ├── item_auditoria.xml
│   │   │       │   ├── activity_datos_sunat.xml
│   │   │       │   ├── activity_productos.xml
│   │   │       │   ├── item_producto.xml
│   │   │       │   ├── dialog_producto.xml
│   │   │       │   ├── cotizaciones_activity.xml
│   │   │       │   ├── item_producto_cotizacion.xml
│   │   │       │   └── activity_main.xml
│   │   │       │
│   │   │       ├── values/
│   │   │       │   ├── strings.xml
│   │   │       │   ├── colors.xml
│   │   │       │   ├── dimens.xml
│   │   │       │   ├── styles.xml
│   │   │       │   └── themes.xml
│   │   │       │
│   │   │       ├── drawable/
│   │   │       │   └── [Iconos & drawables]
│   │   │       │
│   │   │       ├── mipmap/
│   │   │       │   ├── ic_launcher.png
│   │   │       │   └── ic_launcher_round.png
│   │   │       │
│   │   │       └── xml/
│   │   │           ├── data_extraction_rules.xml
│   │   │           └── backup_rules.xml
│   │   │
│   │   ├── androidTest/java/
│   │   │   └── com/example/moviles/
│   │   │       └── [Tests instrumentados]
│   │   │
│   │   └── test/java/
│   │       └── com/example/moviles/
│   │           └── [Unit tests]
│   │
│   ├── build.gradle.kts                   # Config módulo app
│   └── proguard-rules.pro                 # Obfuscation rules
│
├── gradle/
│   ├── libs.versions.toml                 # Catálogo dependencias
│   └── wrapper/
│       └── gradle-wrapper.properties
│
├── build.gradle.kts                       # Build root
├── settings.gradle.kts                    # Configuración proyecto
├── gradle.properties                      # Propiedades globales
├── local.properties                       # Config local
│
├── gradlew                                # Gradle wrapper (Unix)
├── gradlew.bat                            # Gradle wrapper (Windows)
│
├── DOCUMENTACION_PROYECTO.md              # Doc general
├── REQUISITOS.md                          # Especificación requisitos
├── DOCUMENTACION_TECNICA.md               # Este archivo
│
└── [Más archivos de configuración]
```

### Explicación de Carpetas Clave

#### `/src/main/java/com/example/moviles/`

Contiene todo el código Kotlin de la aplicación, organizado por funcionalidad/Activity

#### `/src/main/res/layout/`

Archivos XML de layouts (UI)

- Una carpeta/archivo por Activity
- Items reutilizables (item\_\*.xml)
- Dialogs (dialog\_\*.xml)

#### `/src/main/res/values/`

Recursos constantes:

- Strings (textos)
- Colors (paleta)
- Dimens (dimensiones)
- Styles & Themes

---

## ⚙️ Componentes Principales

### 1. MainActivity

**Propósito:** Splash screen y router de autenticación

**Responsabilidades:**

- Verificar token en SharedPreferences
- Determinar destino inicial
- Limpiar historial de navegación

**Flujo Lógico:**

```kotlin
onCreate() {
    // 1. Leer SharedPreferences
    val token = prefs.getString("token", null)

    // 2. Decidir destino
    if (token == null) {
        iniciarActividad(LoginActivity::class.java)
    } else {
        iniciarActividad(PrincipalActivity::class.java)
    }

    // 3. Cerrar esta activity
    finish()
}
```

**Banderas de Intent Importantes:**

```kotlin
intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
               Intent.FLAG_ACTIVITY_CLEAR_TASK
```

- NEW_TASK: Nueva tarea
- CLEAR_TASK: Limpia historial

---

### 2. LoginActivity

**Propósito:** Autenticación de usuarios

**Componentes UI:**

```
┌─────────────────────────────────┐
│      Login Activity              │
├─────────────────────────────────┤
│ [Usuarios]                      │
│ etUsuario: EditText             │
├─────────────────────────────────┤
│ [Contraseña]                    │
│ etContrasena: EditText          │
├─────────────────────────────────┤
│ [ Iniciar Sesión ]              │
│ btnLogin: Button                │
├─────────────────────────────────┤
│ tvMensaje: TextView (mensajes)  │
└─────────────────────────────────┘
```

**Flujo de Autenticación:**

```
1. Usuario ingresa credenciales
   ↓
2. Clic btnLogin
   ↓
3. Validación local (campos vacíos)
   ↓
4. Disparar coroutine con Dispatchers.IO
   ↓
5. HttpURLConnection POST
   ↓
6. Servidor procesa credenciales
   ↓
7. Response con token + datos
   ↓
8. withContext(Dispatchers.Main) para actualizar UI
   ↓
9. Parsear JSONObject
   ↓
10. Guardar en SharedPreferences
   ↓
11. Toast de bienvenida
   ↓
12. startActivity(PrincipalActivity)
   ↓
13. finish()
```

**Endpoint:**

```
POST http://10.0.2.2/PROYECTO_ERP/API_RES_TECNODESARROLLOPEREZ/zona_acceso987654321.php

Payload:
{
  "usuario": "string",
  "contrasena": "string"
}

Response:
{
  "success": true,
  "token": "eyJ...",
  "empresa": { ... },
  "usuario": { ... },
  "permisos": [ ... ]
}
```

---

### 3. PrincipalActivity

**Propósito:** Dashboard principal de navegación

**Estructura:**

```
┌─────────────────────────────────┐
│    Principal Activity            │
├─────────────────────────────────┤
│ Bienvenido, [Nombre Usuario]    │
│ tvBienvenida: TextView          │
├─────────────────────────────────┤
│ [ Datos Empresa ]               │
│ [ Datos SUNAT ]                 │
│ [ Auditorías ]                  │
│ [ Productos ]                   │
│ [ Cotizaciones ]                │
│ (Buttons: btnDatos*, btnAudit..)│
└─────────────────────────────────┘
```

**Comportamiento:**

```kotlin
onCreate() {
    // 1. Leer nombre de usuario desde SharedPreferences
    val nombres = prefs.getString("nombres", "Usuario")
    tvBienvenida.text = "Bienvenido, $nombres"

    // 2. Configurar listeners para cada botón
    btnDatosEmpresa.setOnClickListener {
        startActivity(Intent(this, DatosEmpresaActivity::class.java))
    }
    // ... etc para otros botones
}
```

---

### 4. DatosEmpresaActivity

**Propósito:** Ver y editar información de la empresa

**Componentes:**

```
CAMPOS EDITABLES:
- etRuc: RUC (11 dígitos)
- etRazonSocial: Razón social
- etNombreComercial: Nombre comercial
- etNombres: Nombres contacto
- etApellidos: Apellidos contacto
- etCorreo: Correo electrónico
- etDireccion: Dirección
- etCelular: Teléfono
- etIgv: Porcentaje IGV

SOLO LECTURA:
- tvIdEmpresa: ID empresa
- tvEstado: Activa/Inactiva (color rojo/verde)
- tvFechaCreacion: Fecha creación
- tvFechaVencimiento: Fecha vencimiento
- tvFechaCorte: Fecha corte fiscal
- tvTipoPlan: Tipo de plan
- tvMaxUsuarios: Máximo usuarios permitidos
- tvUsuariosActivos: Usuarios activos
- tvEspacioTotal: Total espacio (MB)
- tvEspacioUsado: Espacio utilizado (MB)
```

**Flujo de Carga:**

```
1. progressBar.visibility = VISIBLE
2. GET a datos_empresa_54321.php
   - Parámetros: token, id_empresa
3. Response JSON parseado
4. Mostrar datos en UI
5. progressBar.visibility = GONE
```

**Flujo de Actualización:**

```
1. Validar campos (RUC y Razón Social requeridos)
2. progressBar.visibility = VISIBLE
3. POST a datos_empresa_54321.php
   - Parámetros: todos los campos
4. Response JSON parseado
5. Toast de éxito/error
6. Actualizar SharedPreferences
7. Recargar datos
8. progressBar.visibility = GONE
```

---

### 5. AuditoriasActivity

**Propósito:** Visualizar registro de accesos

**Estructura:**

```
┌─────────────────────────────────┐
│    Auditorías Activity           │
├─────────────────────────────────┤
│ RecyclerView                    │
│ ┌──────────────────────────────┐│
│ │ Usuario: Juan Pérez          ││
│ │ Cargo: Administrador         ││
│ │ Fecha: 2025-12-09 14:30:22   ││
│ │ IP: 192.168.1.100            ││
│ │ ✓ EXITOSO                    ││
│ └──────────────────────────────┘│
│ ┌──────────────────────────────┐│
│ │ Usuario: Pedro López         ││
│ │ Cargo: Vendedor              ││
│ │ Fecha: 2025-12-09 14:25:15   ││
│ │ IP: 192.168.1.101            ││
│ │ ✗ FALLIDO                    ││
│ │ Motivo: Contraseña incorrecta││
│ └──────────────────────────────┘│
├─────────────────────────────────┤
│ Total: 1250 registros           │
│ Página 1 de 50                  │
│ [ Anterior ] [ Siguiente ]       │
└─────────────────────────────────┘
```

**Data Classes:**

```kotlin
data class AuditoriaItem(
    val id: Int,
    val usuario: String,
    val cargo: String,
    val fechaHora: String,
    val ip: String,
    val exito: Boolean,      // verde si true, rojo si false
    val motivo: String,       // solo si exito = false
    val detalle: String       // información técnica
)
```

**Adapter Pattern:**

```
RecyclerView
    ├─ Adapter (AuditoriasAdapter)
    │   ├─ lista: MutableList<AuditoriaItem>
    │   ├─ onCreateViewHolder(): ViewHolder
    │   ├─ onBindViewHolder(holder, position)
    │   └─ getItemCount(): Int
    │
    └─ ViewHolder
        ├─ tvUsuario, tvCargo, tvFecha
        ├─ tvIp, tvEstado, tvMotivo
        └─ viewIndicador (color bar)
```

---

### 6. DatosSunatActivity

**Propósito:** Gestión de configuración fiscal

**Secciones:**

#### A. Credenciales SUNAT

```
[Usuario SOL] etUsuarioSol
[Clave SOL] etClaveSol
[Client ID] etClientId
[Client Secret] etClientSecret
[Clave Certificado] etClaveCertificado
[Endpoint SUNAT] etEndpointSunat
[Modo: Beta / Producción] spinnerModoEnvio
```

#### B. Estado Token

```
📄 Certificado: certificado_empresa.pfx
Token: eyJ0eXAiOiJKV1QiLCJhbGc...
Expira: 2025-12-15 10:00:00
```

#### C. Integraciones SaaS

```
[API Key OpenAI] etApiKeyOpenai
[Webhook Endpoint] etWebhookEndpoint
[Webhook Token] etWebhookToken
```

**Lógica Spinner:**

```kotlin
private fun configurarSpinner() {
    val modos = arrayOf("Beta (Pruebas)", "Producción")
    val adapter = ArrayAdapter(this,
        android.R.layout.simple_spinner_item, modos)
    spinnerModoEnvio.adapter = adapter
}

// En mostrarDatos:
val modoEnvio = data.optString("modo_envio", "beta")
spinnerModoEnvio.setSelection(
    if (modoEnvio == "produccion") 1 else 0
)
```

---

### 7. ProductosActivity

**Propósito:** CRUD de productos

**Operaciones:**

#### Listar

```kotlin
private fun cargarProductos() {
    POST prosesos_productos.php
    accion=listar

    Response:
    {
      "success": true,
      "productos": [
        {
          "id_producto": 1,
          "nombre": "Laptop",
          "descripcion": "Laptop ASUS",
          "categoria": "Electrónica",
          "marca": "ASUS",
          "precio_venta": 2500.00,
          "precio_compra": 1800.00,
          "stock": 5,
          "imagen": "url..."
        },
        ...
      ]
    }
}
```

#### Crear

```kotlin
POST prosesos_productos.php
accion=agregar
nombre=Laptop
descripcion=...
precio_venta=2500
precio_compra=1800
stock=5
```

#### Actualizar

```kotlin
POST prosesos_productos.php
accion=actualizar
id_producto=1
nombre=Laptop
...
```

#### Eliminar

```kotlin
POST prosesos_productos.php
accion=eliminar
id_producto=1
```

**Adapter ViewHolder:**

```kotlin
class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvNombre: TextView = view.findViewById(R.id.tvNombre)
    val tvCategoria: TextView = view.findViewById(R.id.tvCategoria)
    val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
    val tvStock: TextView = view.findViewById(R.id.tvStock)
    val btnEditar: Button = view.findViewById(R.id.btnEditar)
    val btnEliminar: Button = view.findViewById(R.id.btnEliminar)

    fun bind(producto: Producto,
             onEdit: (Producto) -> Unit,
             onDelete: (Producto) -> Unit) {
        tvNombre.text = producto.nombre
        tvCategoria.text = "${producto.categoria} • ${producto.marca}"
        tvPrecio.text = "S/ ${String.format("%.2f", producto.precioVenta)}"
        tvStock.text = "Stock: ${producto.stock}"

        btnEditar.setOnClickListener { onEdit(producto) }
        btnEliminar.setOnClickListener { onDelete(producto) }
    }
}
```

---

### 8. CotizacionesActivity

**Propósito:** Crear cotizaciones comerciales

**Componentes Principales:**

#### Búsqueda de Cliente

```kotlin
private fun buscarRuc() {
    val ruc = etRuc.text.toString()

    Validar:
    - Longitud == 11
    - Todo dígitos

    POST busqueda_ruc.php
    Parámetros: token, id_empresa, ruc

    Response:
    {
      "success": true,
      "razon_social": "ABC Empresa S.A.C.",
      "direccion": "Jr. Principal 123"
    }
}
```

#### Búsqueda de Productos

```kotlin
private fun buscarProducto() {
    val texto = etBuscarProducto.text

    POST busqueda_producto.php
    Parámetros: token, id_empresa, accion=listar

    Response:
    {
      "success": true,
      "productos": [ ... ]
    }

    Filtrar localmente:
    productos.filter {
        it.nombre.lowercase().contains(texto.lowercase())
    }

    Acciones:
    - Sin resultados: Toast
    - 1 resultado: Agregar automáticamente
    - Múltiples: Mostrar selector (AlertDialog)
}
```

#### Tabla de Productos Dinámicos

```kotlin
data class ProductoCotizacion(
    val idProducto: Int,
    val nombre: String,
    val descripcion: String,
    var precioUnitario: Double,
    var cantidad: Int
)

adapter = ProductosCotizacionAdapter(productosAgregados) {
    calcularTotales() // callback cuando cambia cantidad/precio
}
```

#### Cálculos Automáticos

```kotlin
private fun calcularTotales() {
    var subtotal = 0.0

    productosAgregados.forEach { producto ->
        subtotal += producto.precioUnitario * producto.cantidad
    }

    val igv = subtotal * 0.18
    val total = subtotal + igv

    tvSubtotal.text = String.format("%.2f", subtotal)
    tvIgv.text = String.format("%.2f", igv)
    tvTotal.text = String.format("%.2f", total)
}
```

#### Guardado

```kotlin
private fun guardarCotizacion() {
    Validaciones:
    - ✓ Al menos 1 producto
    - ✓ RUC válido (11 dígitos)
    - ✓ Razón social no vacía

    Construir JSON array:
    [
      {
        "id_producto": 1,
        "descripcion": "Laptop",
        "cantidad": 2,
        "precio_unitario": 2500.00,
        "total": 5000.00
      },
      ...
    ]

    POST cotizaciones.php
    Parámetros:
    - token, id_empresa
    - ruc_cliente, razon_social_cliente
    - direccion_cliente, persona_autoriza
    - asunto, moneda, forma_pago
    - cuenta_bancaria, tiempo_entrega
    - validez, subtotal, igv, total
    - productos (JSON array)

    Response:
    {
      "success": true,
      "numero_cotizacion": "COT-2025-00512",
      "mensaje": "Cotización guardada exitosamente"
    }
}
```

**Manejo de Errores:**

```kotlin
// Token expirado o inválido
if (mensaje.contains("Token", ignoreCase = true) ||
    mensaje.contains("expirado", ignoreCase = true)) {
    cerrarSesion()  // Limpiar y volver a login
}
```

---

## 🔄 Flujos de Datos

### Flujo de Autenticación

```
┌─────────────────────────────────────────────────────────┐
│                    USUARIO                               │
└────────────────────┬────────────────────────────────────┘
                     │ Ingresa credenciales
                     ▼
         ┌──────────────────────────┐
         │   LoginActivity.kt       │
         │ Validación local         │
         │ usuario != empty         │
         │ contraseña != empty      │
         └────────────┬─────────────┘
                      │ Válido
                      ▼
     ┌────────────────────────────────────┐
     │  Coroutine (Dispatchers.IO)        │
     │  HttpURLConnection.POST            │
     │  Body: JSONObject                  │
     └────────────────┬───────────────────┘
                      │ Request
                      ▼
     ┌────────────────────────────────────────────┐
     │         SERVIDOR API                       │
     │  zona_acceso987654321.php                  │
     │  Validar usuario/contraseña en BD          │
     │  Generar token                             │
     │  Retornar datos de empresa y usuario       │
     └────────────────┬───────────────────────────┘
                      │ Response (JSON)
                      ▼
     ┌────────────────────────────────────┐
     │  LoginActivity                     │
     │  withContext(Dispatchers.Main)     │
     │  Parsear JSONObject                │
     │  Validar success flag              │
     └────────────────┬───────────────────┘
                      │ Éxito
                      ▼
     ┌────────────────────────────────────┐
     │  SharedPreferences                 │
     │  .edit()                           │
     │  .putString("token", token)        │
     │  .putInt("id_empresa", id)         │
     │  .putString("nombres", nombres)    │
     │  .apply()                          │
     └────────────────┬───────────────────┘
                      │
                      ▼
     ┌────────────────────────────────────┐
     │  Toast.makeText("Bienvenido...")   │
     │  startActivity(PrincipalActivity)  │
     │  finish()                          │
     └────────────────────────────────────┘
```

### Flujo de Carga de Datos Empresa

```
DatosEmpresaActivity.onCreate()
    │
    ├─ inicializarVistas() → Encontrar referencias
    │
    ├─ cargarDatosEmpresa() → ProgressBar = VISIBLE
    │   │
    │   └─ CoroutineScope(Dispatchers.IO) {
    │       │
    │       ├─ Leer SharedPreferences (token, id_empresa)
    │       │
    │       ├─ HttpURLConnection.GET
    │       │  URL = ".../datos_empresa_54321.php
    │       │         ?token=$token&id_empresa=$idEmpresa"
    │       │
    │       ├─ conn.responseCode == 200
    │       │
    │       ├─ inputStream.bufferedReader().readText()
    │       │
    │       └─ withContext(Dispatchers.Main) {
    │          │
    │          ├─ JSONObject(response)
    │          │
    │          ├─ if (json.success) {
    │          │    val data = json.getJSONObject("data")
    │          │    mostrarDatos(data)
    │          │  } else {
    │          │    mostrarError(mensaje)
    │          │  }
    │          │
    │          └─ ProgressBar = GONE
    │
    └─ mostrarDatos(data: JSONObject)
        │
        ├─ etRuc.setText(data.optString("ruc", ""))
        ├─ etRazonSocial.setText(data.optString("razon_social", ""))
        ├─ [... rest de campos ...]
        │
        └─ ScrollView = VISIBLE
```

---

## 🌐 APIs REST

### Especificación General

**Base URL:** `http://10.0.2.2/PROYECTO_ERP/API_RES_TECNODESARROLLOPEREZ/`

**Authentication:** Token en parámetros POST/GET

**Format:** JSON

**Content-Type:** `application/json` (POST) o `application/x-www-form-urlencoded`

### Endpoints Detallados

#### 1. Autenticación

```
POST /zona_acceso987654321.php

Request:
{
  "usuario": "usuario@empresa.com",
  "contrasena": "password123"
}

Response (200):
{
  "success": true,
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
  "empresa": {
    "id_empresa": 1,
    "razon_social": "ABC S.A.C.",
    "ruc": "12345678901",
    "logo": "http://...",
    "fecha_vencimiento": "2025-12-31",
    "fecha_corte": "2025-12-09",
    "token_expira": "2025-12-10"
  },
  "usuario": {
    "id_usuario": 5,
    "nombres": "Juan",
    "apellidos": "Pérez García",
    "cargo": "Administrador",
    "usuario": "jpérez"
  },
  "permisos": ["crear_cotizacion", "ver_productos", ...]
}

Response (401):
{
  "success": false,
  "mensaje": "Credenciales incorrectas"
}
```

#### 2. Datos Empresa

```
GET /datos_empresa_54321.php?token=...&id_empresa=1

Response (200):
{
  "success": true,
  "data": {
    "id_empresa": 1,
    "ruc": "12345678901",
    "razon_social": "ABC S.A.C.",
    "nombre_comercial": "ABC",
    "nombres": "Juan",
    "apellidos": "Pérez",
    "correo": "juan@abc.com",
    "direccion": "Jr. Principal 123",
    "celular": "987654321",
    "igv": "18.00",
    "estado": 1,
    "fecha_creacion": "2025-01-01",
    "fecha_vencimiento": "2025-12-31",
    "fecha_corte": "2025-12-09",
    "tipo_plan": "Premium",
    "max_usuarios": 10,
    "usuarios_activos": 3,
    "espacio_total_mb": 5120,
    "espacio_usado_mb": 2048
  }
}

---

POST /datos_empresa_54321.php
token=...&id_empresa=1&ruc=...&razon_social=...&[...]

Response (200):
{
  "success": true,
  "mensaje": "Datos actualizados correctamente"
}
```

#### 3. Auditorías

```
GET /auditoria_ceciones.php?token=...&id_empresa=1&pagina=1

Response (200):
{
  "success": true,
  "total_registros": 1250,
  "total_paginas": 50,
  "data": [
    {
      "id_registro": 1250,
      "usuario": "jpérez",
      "cargo": "Administrador",
      "fecha_hora": "2025-12-09 14:30:22",
      "ip": "192.168.1.100",
      "exito": 1,
      "motivo": "",
      "detalle": ""
    },
    {
      "id_registro": 1249,
      "usuario": "plopez",
      "cargo": "Vendedor",
      "fecha_hora": "2025-12-09 14:25:15",
      "ip": "192.168.1.101",
      "exito": 0,
      "motivo": "Contraseña incorrecta",
      "detalle": "Intento fallido #2"
    }
  ]
}
```

#### 4. Productos

```
POST /prosesos_productos.php
token=...&id_empresa=1&accion=listar

Response (200):
{
  "success": true,
  "productos": [
    {
      "id_producto": 1,
      "nombre": "Laptop",
      "descripcion": "Laptop ASUS 15 pulgadas",
      "categoria": "Electrónica",
      "marca": "ASUS",
      "precio_venta": 2500.00,
      "precio_compra": 1800.00,
      "stock": 5,
      "imagen": "http://.../laptop.jpg"
    }
  ]
}

---

POST /prosesos_productos.php
token=...&id_empresa=1&accion=agregar&nombre=...&[...]

Response (200):
{
  "success": true,
  "mensaje": "Producto creado exitosamente",
  "id_producto": 2
}

---

POST /prosesos_productos.php
token=...&id_empresa=1&accion=actualizar&id_producto=1&[...]

Response (200):
{
  "success": true,
  "mensaje": "Producto actualizado"
}

---

POST /prosesos_productos.php
token=...&id_empresa=1&accion=eliminar&id_producto=1

Response (200):
{
  "success": true,
  "mensaje": "Producto eliminado"
}
```

#### 5. Cotizaciones - Búsqueda RUC

```
POST /busqueda_ruc.php
token=...&id_empresa=1&ruc=12345678901

Response (200):
{
  "success": true,
  "razon_social": "ABC Empresa S.A.C.",
  "direccion": "Jr. Principal 123, Lima"
}

Response (400):
{
  "success": false,
  "mensaje": "RUC no encontrado"
}
```

#### 6. Cotizaciones - Búsqueda Productos

```
POST /busqueda_producto.php
token=...&id_empresa=1&accion=listar

Response (200):
{
  "success": true,
  "productos": [
    {
      "id_producto": 1,
      "nombre": "Laptop ASUS",
      "descripcion": "15 pulgadas",
      "precio_venta": 2500.00,
      "imagen": "http://..."
    }
  ]
}
```

#### 7. Cotizaciones - Guardar

```
POST /cotizaciones.php
token=...&id_empresa=1&ruc_cliente=12345678901&[...]&productos=[...]

Donde productos es un JSON array:
[
  {
    "id_producto": 1,
    "descripcion": "Laptop",
    "cantidad": 2,
    "precio_unitario": 2500.00,
    "total": 5000.00
  }
]

Response (200):
{
  "success": true,
  "numero_cotizacion": "COT-2025-00512",
  "mensaje": "Cotización guardada exitosamente"
}
```

---

## 🎨 Patrones de Diseño

### 1. Adapter Pattern (RecyclerView)

**Propósito:** Vincular datos con vistas reutilizables

```kotlin
class AuditoriasAdapter : RecyclerView.Adapter<AuditoriasAdapter.ViewHolder>() {

    private var lista = mutableListOf<AuditoriaItem>()

    // Actualizar datos
    fun actualizar(nuevaLista: List<AuditoriaItem>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    // Crear ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_auditoria, parent, false)
        return ViewHolder(view)
    }

    // Vincular datos a ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount() = lista.size

    // ViewHolder: referencias a vistas
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvUsuario: TextView = view.findViewById(R.id.tvUsuario)
        private val tvEstado: TextView = view.findViewById(R.id.tvEstado)

        fun bind(item: AuditoriaItem) {
            tvUsuario.text = item.usuario
            tvEstado.text = if (item.exito) "✓ EXITOSO" else "✗ FALLIDO"
            tvEstado.setTextColor(
                if (item.exito) 0xFF2ECC71.toInt() else 0xFFE74C3C.toInt()
            )
        }
    }
}
```

### 2. Observer Pattern (SharedPreferences)

```kotlin
val prefs = getSharedPreferences("datos_app", Context.MODE_PRIVATE)

// Escribir
val editor = prefs.edit()
editor.putString("token", token)
editor.apply()  // Asincrónico

// Leer
val token = prefs.getString("token", "")
val idEmpresa = prefs.getInt("id_empresa", 0)
```

### 3. Callback Pattern (Listener)

```kotlin
// En Adapter
class ProductosCotizacionAdapter(
    private val productos: MutableList<ProductoCotizacion>,
    private val onUpdate: () -> Unit  // Callback
) : RecyclerView.Adapter<ProductosCotizacionAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.etCantidad.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val cantidad = holder.etCantidad.text.toString().toIntOrNull() ?: 1
                producto.cantidad = cantidad
                onUpdate()  // Notificar cambio
                notifyItemChanged(position)
            }
        }
    }
}

// En Activity
val adapter = ProductosCotizacionAdapter(productosAgregados) {
    calcularTotales()  // Lambda como callback
}
```

### 4. Factory Pattern (Dialog)

```kotlin
AlertDialog.Builder(this)
    .setTitle("Confirmar")
    .setMessage("¿Está seguro?")
    .setPositiveButton("Sí") { _, _ -> eliminarProducto(id) }
    .setNegativeButton("No", null)
    .show()
```

---

## 💾 Persistencia de Datos

### SharedPreferences

**Ubicación:** `/data/data/com.example.moviles/shared_prefs/datos_app.xml`

**Contenido:**

```xml
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
  <string name="token">eyJ0eXAiOiJKV1QiLCJhbGc...</string>
  <int name="id_empresa" value="1" />
  <string name="razon_social">ABC S.A.C.</string>
  <string name="ruc">12345678901</string>
  <int name="id_usuario" value="5" />
  <string name="nombres">Juan</string>
  <string name="apellidos">Pérez</string>
  <string name="usuario">jpérez</string>
  <string name="permisos">[...]</string>
</map>
```

**Operaciones:**

```kotlin
// Lectura
val prefs = getSharedPreferences("datos_app", Context.MODE_PRIVATE)
val token = prefs.getString("token", "")

// Escritura
val editor = prefs.edit()
editor.putString("token", token)
editor.putInt("id_empresa", 1)
editor.apply()

// Limpiar
editor.clear().apply()
```

---

## ⚠️ Manejo de Errores

### Niveles de Error

#### 1. Validación de Entrada

```kotlin
if (usuario.isEmpty() || contrasena.isEmpty()) {
    tvMensaje.text = "Por favor, completa todos los campos."
    return
}

if (ruc.length != 11 || !ruc.all { it.isDigit() }) {
    Toast.makeText(this, "Ingrese un RUC válido", Toast.LENGTH_SHORT).show()
    return
}
```

#### 2. Error de Conexión

```kotlin
try {
    val url = URL("http://10.0.2.2/...")
    val conn = url.openConnection() as HttpURLConnection
    // ...
} catch (e: Exception) {
    withContext(Dispatchers.Main) {
        tvMensaje.text = "Error de conexión: ${e.message}"
    }
}
```

#### 3. Error de Servidor

```kotlin
val responseCode = conn.responseCode

if (responseCode != HttpURLConnection.HTTP_OK) {
    tvMensaje.text = "Error del servidor: $responseCode"
    return
}

val json = JSONObject(responseText)
if (!json.getBoolean("success")) {
    tvMensaje.text = json.optString("mensaje", "Error desconocido")
    return
}
```

---

## 🔄 Concurrencia

### Coroutines

**Concepto:** Operaciones no bloqueantes en threads

**Dispatchers Utilizados:**

#### Dispatchers.IO

```kotlin
// Para operaciones de red/disco
CoroutineScope(Dispatchers.IO).launch {
    var conn: HttpURLConnection? = null
    try {
        val response = // network call
    } catch (e: Exception) {
        // error
    } finally {
        conn?.disconnect()
    }
}
```

#### Dispatchers.Main

```kotlin
// Para actualizar UI
withContext(Dispatchers.Main) {
    tvMensaje.text = "Éxito"
    progressBar.visibility = View.GONE
}
```

**Ventajas:**

- ✅ No bloquea UI thread
- ✅ Manejo de errores más limpio
- ✅ Ciclo de vida consciente (con lifecycleScope en Activities)

---

## 🧪 Testing

### Unit Tests

```kotlin
// build.gradle.kts
testImplementation(libs.junit)

// Archivo de test
class LoginActivityTest {

    @Test
    fun testValidacionCamposVacios() {
        // Given
        val usuario = ""
        val contrasena = ""

        // When
        val resultado = validarCredenciales(usuario, contrasena)

        // Then
        assertFalse(resultado)
    }
}
```

### Instrumented Tests

```kotlin
// build.gradle.kts
androidTestImplementation(libs.androidx.espresso.core)

// Archivo de test
class LoginActivityInstrumentedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun testLoginButton() {
        onView(withId(R.id.btnLogin)).perform(click())
        onView(withText("Bienvenido")).check(matches(isDisplayed()))
    }
}
```

---

## 📦 Deployment

### Build Types

#### Debug

```kotlin
// app/build.gradle.kts
buildTypes {
    debug {
        isMinifyEnabled = false
        isDebuggable = true
    }
}
```

#### Release

```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

### Generación APK

#### Debug APK

```bash
./gradlew assembleDebug
# Ubicación: app/build/outputs/apk/debug/app-debug.apk
```

#### Release APK

```bash
./gradlew assembleRelease
# Ubicación: app/build/outputs/apk/release/app-release.apk
```

### Firma de APK

```bash
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 \
  -keystore my-release-key.jks \
  app-release-unsigned.apk alias_name
```

---

## 🔧 Troubleshooting

### Problemas Comunes

#### 1. Error de Conexión a API

```
Síntoma: "Error de conexión: ..."
Solución:
- Verificar IP servidor (10.0.2.2 en emulador)
- Verificar puerto
- Verificar firewall
- Verificar estado del servidor API
```

#### 2. Token Expirado

```
Síntoma: "Token inválido" o "Token expirado"
Solución:
- Volver a iniciar sesión
- Verificar reloj del dispositivo
- Contactar administrador
```

#### 3. RecyclerView Vacío

```
Síntoma: Lista sin elementos
Solución:
- Verificar permisos INTERNET
- Revisar respuesta del servidor
- Verificar si adapter está seteado
- Revisar logs de error
```

#### 4. SharedPreferences No Persisten

```
Síntoma: Datos se pierden al cerrar app
Solución:
- Verificar .apply() o .commit()
- Verificar permisos de escritura
- Verificar contexto correcto
```

### Debug Tips

#### Logs

```kotlin
Log.d("TAG", "Debug: $variable")
Log.e("TAG", "Error: ${exception.message}")
```

#### Android Studio Debugger

```
Run → Debug 'app'
Breakpoints → Inspect variables
Step over/into operations
```

#### Network Inspection

```
Android Studio → Logcat → Filter "HttpURLConnection"
Ver requests y responses
```

---

## 📈 Métricas de Calidad

| Métrica               | Estado | Meta   |
| --------------------- | ------ | ------ |
| Cobertura de Tests    | 40%    | 80%    |
| Errores de Lint       | 5      | 0      |
| Documentación         | 85%    | 100%   |
| Performance (startup) | 1.5s   | <2s    |
| Tamaño APK            | 8.2 MB | <10 MB |

---

## 📚 Recursos Adicionales

### Documentación Oficial

- [Android Docs](https://developer.android.com)
- [Kotlin Docs](https://kotlinlang.org/docs)
- [Coroutines Guide](https://kotlinlang.org/docs/coroutines-overview.html)

### Mejores Prácticas

- [Material Design Guidelines](https://material.io)
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)

---

**Última actualización:** 09/12/2025  
**Mantener este documento actualizado con cambios en código**
