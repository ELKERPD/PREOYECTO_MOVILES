# 📱 Moviles ERP - Aplicación de Gestión Empresarial Mobile

<div align="center">

![Version](https://img.shields.io/badge/version-1.0-blue.svg?style=for-the-badge)
![Status](https://img.shields.io/badge/status-Production-success.svg?style=for-the-badge)
![Android](https://img.shields.io/badge/android-7.0%2B-green.svg?style=for-the-badge)
![License](https://img.shields.io/badge/license-Private-red.svg?style=for-the-badge)
![Kotlin](https://img.shields.io/badge/kotlin-2.0.21-purple.svg?style=for-the-badge)

**Solución móvil integral para gestión comercial y operativa empresarial**

[Características](#-características-principales) • [Instalación](#-instalación) • [Uso](#-guía-de-uso) • [Documentación](#-documentación-completa) • [Soporte](#-soporte)

</div>

---

## 🎯 Descripción General

**Moviles ERP** es una aplicación Android nativa desarrollada en Kotlin que proporciona herramientas completas para la gestión integral de operaciones empresariales. Diseñada específicamente para profesionales comerciales y administrativos en Perú, integra autenticación segura, gestión de datos empresariales, configuración fiscal con SUNAT, auditoría de accesos y generación de cotizaciones comerciales.

### 🎬 Visión

Transformar la forma en que los equipos comerciales y administrativos operan, proporcionando acceso instantáneo a información crítica del negocio desde cualquier ubicación, mejorando la productividad y la toma de decisiones.

### 🏆 Misión

Entregar una plataforma móvil segura, rápida e intuitiva que automatice procesos comerciales y facilite la gestión operativa con integración nativa a sistemas fiscales locales.

---

## ✨ Características Principales

### 🔐 Autenticación y Seguridad
- ✅ **Login seguro** con usuario y contraseña
- ✅ **Token-based authentication** para gestión de sesiones
- ✅ **Validación en cliente y servidor**
- ✅ **Historial de auditoría** completo de accesos
- ✅ **Control de acceso** granular por usuario

### 📊 Gestión Empresarial
- ✅ **Dashboard informativo** con datos de la empresa
- ✅ **Edición de información** empresarial en tiempo real
- ✅ **Monitoreo de suscripción** y plan activo
- ✅ **Seguimiento de uso** de espacio en disco
- ✅ **Visualización de licencia** y vencimientos

### 🇵🇪 Integración Fiscal SUNAT
- ✅ **Gestión de credenciales SOL**
- ✅ **Configuración de certificados digitales**
- ✅ **Soporte para ambiente Beta/Producción**
- ✅ **Gestión de tokens SUNAT**
- ✅ **Integración con webhooks** para notificaciones
- ✅ **API Key OpenAI** para inteligencia artificial

### 📋 Auditoría y Monitoreo
- ✅ **Registro completo de accesos** de usuarios
- ✅ **Información detallada** de intentos fallidos
- ✅ **Rastreo de IP** de conexión
- ✅ **Paginación inteligente** de registros
- ✅ **Visualización por cargo** y rol de usuario

### 📦 Gestión de Productos
- ✅ **CRUD completo** de catálogo
- ✅ **Control de stock** en tiempo real
- ✅ **Gestión de precios** (compra/venta)
- ✅ **Categorización y marcas** de productos
- ✅ **Búsqueda y filtrado** rápido
- ✅ **Edición masiva** de información

### 💰 Generación de Cotizaciones
- ✅ **Búsqueda instantánea** de clientes por RUC
- ✅ **Autocomplete de datos** del cliente
- ✅ **Selección dinámica** de productos
- ✅ **Cálculo automático** de totales (IGV 18%)
- ✅ **Edición inline** de cantidades y precios
- ✅ **Generación de números** secuenciales
- ✅ **Guardado en servidor** con respaldo

### 🚀 Rendimiento y UX
- ✅ **Carga rápida** (<2 segundos)
- ✅ **Interfaz intuitiva** basada en Material Design
- ✅ **Navegación fluida** entre módulos
- ✅ **Feedback visual** con ProgressBars
- ✅ **Mensajes claros** de error y éxito
- ✅ **Validación en tiempo real**

---

## 📋 Requisitos

### Hardware Mínimo
```
Dispositivo:      Smartphone Android
Pantalla:         4.5" a 6.5" (todos los tamaños)
RAM:              2GB mínimo (3GB recomendado)
Almacenamiento:   50MB libres
```

### Software Requerido
```
Android:          7.0 (API 24) o superior
Conexión:         WiFi o datos móviles 4G/5G
Servidor API:     Disponible y accesible en red
```

### Para Desarrollo
```
Android Studio:   2024.1 o superior
JDK:              Java 11+
Gradle:           8.13.0+
Kotlin:           2.0.21
Git:              2.25+
```

---

## 🚀 Instalación

### Opción 1: Para Usuarios Finales

#### Paso 1: Obtener el APK
```bash
# Solicitar archivo app-release.apk al administrador
# o descargar desde repositorio de la empresa
```

#### Paso 2: Transferir al Dispositivo
- Conectar dispositivo Android por USB
- Copiar archivo `.apk` a la carpeta de descargas
- O transferir mediante Bluetooth/Email

#### Paso 3: Instalar
1. Abrir **Administrador de Archivos**
2. Navegar a **Descargas**
3. Tocar el archivo **app-release.apk**
4. Permitir **instalación desde fuentes desconocidas** (si se solicita)
5. Esperar a que termine la instalación
6. Tocar **Abrir** para ejecutar

#### Paso 4: Primer Inicio
1. Se abrirá la pantalla de **Login**
2. Ingresar **usuario** y **contraseña**
3. Tocar **Iniciar Sesión**
4. Esperar a que se cargue el **Dashboard**

### Opción 2: Para Desarrolladores

#### Paso 1: Clonar Repositorio
```bash
git clone https://github.com/ELKERPD/PREOYECTO_MOVILES.git
cd PREOYECTO_MOVILES
```

#### Paso 2: Abrir en Android Studio
```bash
# Opción A: Desde línea de comandos
studio .

# Opción B: Manualmente
# 1. Android Studio → File → Open
# 2. Seleccionar carpeta del proyecto
# 3. Esperar a sincronización
```

#### Paso 3: Sincronizar Gradle
```bash
./gradlew sync
# o desde Android Studio: Build → Sync Now
```

#### Paso 4: Ejecutar en Emulador
```bash
# Crear emulador (si no existe)
Android Studio → AVD Manager → Create Virtual Device
Seleccionar: Android 7.0 o superior

# Ejecutar proyecto
./gradlew installDebug
# o: Android Studio → Run → Run 'app'
```

#### Paso 5: Ejecutar en Dispositivo Real
```bash
# En el dispositivo:
Settings → About Phone → Build Number (tocar 7 veces)
Settings → Developer Options → USB Debugging (activar)

# Conectar por USB
adb devices  # Verificar conexión

# Ejecutar
./gradlew installDebug
# o: Android Studio → Run → Run 'app'
```

---

## 📖 Guía de Uso

### 🎬 Flujo Principal de la Aplicación

```
┌─────────────────┐
│   APP INICIA    │
└────────┬────────┘
         │
    ┌────▼──────────┐
    │ ¿Hay sesión?  │
    └───┬────────┬──┘
        │        │
       SÍ       NO
        │        │
        ▼        ▼
    ┌────────┐  ┌──────────────┐
    │DASHBOARD│  │LOGIN ACTIVITY │
    └────────┘  └──────┬───────┘
        │              │
        │              ▼
        │         ┌─────────────┐
        │         │INGRESAR CRED│
        │         │USUARIO/PASS │
        │         └──────┬──────┘
        │                │
        │                ▼
        │         ┌──────────────┐
        │         │VALIDAR SERVER│
        │         └──────┬───────┘
        │                │
        │         ┌──────▼──────┐
        │         │¿Válido?     │
        │         └──┬───────┬──┘
        │           SÍ       NO
        │            │       └─→ ERROR
        │            │
        │            ▼
        │         ┌──────────┐
        │         │GUARDAR   │
        │         │PREFS     │
        │         └────┬─────┘
        │              │
        └──────────────┼──────┐
                       │      │
                       ▼      ▼
           ┌─────────────────────────────┐
           │   PRINCIPAL ACTIVITY (MENU) │
           └─────────────────────────────┘
                       │
         ┌─────────────┼─────────────┬──────────┐
         │             │             │          │
         ▼             ▼             ▼          ▼
      ┌──────┐   ┌──────────┐  ┌─────────┐ ┌──────────┐
      │DATOS │   │AUDITORÍA │  │PRODUCTOS│ │COTIZAC...│
      │EMPRE.│   │          │  │         │ │          │
      └──────┘   └──────────┘  └─────────┘ └──────────┘
```

### 1️⃣ Login - Iniciar Sesión

**Ubicación:** Pantalla inicial si no hay sesión activa

**Pasos:**
1. Abrir aplicación → Se abre pantalla de Login
2. Campo **Usuario**: Ingresar correo o usuario
3. Campo **Contraseña**: Ingresar contraseña
4. Botón **Iniciar Sesión**: Tocar para validar
5. Esperar 2-3 segundos a respuesta del servidor
6. Si es correcto → Ir a Dashboard
7. Si es incorrecto → Ver mensaje de error

**Validaciones:**
- ✓ Usuario no puede estar vacío
- ✓ Contraseña no puede estar vacía
- ✓ Debe haber conexión de red activa

**Errores Comunes:**
```
"Credenciales incorrectas"
→ Verificar usuario y contraseña (mayúsculas/minúsculas)

"Error de conexión"
→ Verificar WiFi/datos móviles

"Token expirado"
→ Volver a iniciar sesión
```

---

### 2️⃣ Dashboard - Panel Principal

**Ubicación:** Pantalla después de login exitoso

**Elementos:**
```
┌─────────────────────────────────┐
│ Bienvenido, Juan Pérez          │
│ Empresa: ABC S.A.C.             │
├─────────────────────────────────┤
│ [ Datos de Empresa ]            │
│ [ Configuración SUNAT ]         │
│ [ Auditorías ]                  │
│ [ Productos ]                   │
│ [ Cotizaciones ]                │
├─────────────────────────────────┤
│ © 2025 Moviles ERP v1.0         │
└─────────────────────────────────┘
```

**Opciones:**
- **Datos de Empresa:** Editar información de la empresa
- **SUNAT:** Configurar integraciones fiscales
- **Auditorías:** Ver registro de accesos
- **Productos:** Gestionar catálogo
- **Cotizaciones:** Crear cotizaciones comerciales

---

### 3️⃣ Datos de Empresa

**Objetivo:** Ver y editar información de tu empresa

**Cómo acceder:**
Dashboard → Clic en **"Datos Empresa"**

**Información Disponible:**

**Campos Editables:**
| Campo | Tipo | Ejemplo |
|-------|------|---------|
| RUC | Texto | 12345678901 |
| Razón Social | Texto | ABC Empresa S.A.C. |
| Nombre Comercial | Texto | ABC |
| Nombres | Texto | Juan |
| Apellidos | Texto | Pérez García |
| Correo | Email | juan@abc.com |
| Dirección | Texto | Jr. Principal 123 |
| Celular | Teléfono | 987654321 |
| IGV | Porcentaje | 18.00 |

**Campos de Solo Lectura:**
- Estado (Activa ✓ / Inactiva ✗)
- Fecha Creación
- Fecha Vencimiento
- Fecha Corte Fiscal
- Tipo de Plan
- Máximo Usuarios
- Usuarios Activos
- Espacio Total (MB)
- Espacio Usado (MB)

**Cómo Editar:**
1. Modificar campos que desees
2. Clic en botón **"Actualizar"**
3. Esperar confirmación (ProgressBar)
4. Ver mensaje de éxito o error

**Validaciones:**
- RUC y Razón Social son obligatorios
- Email debe ser válido (formato correcto)
- Todos los datos se validan en servidor

---

### 4️⃣ Configuración SUNAT

**Objetivo:** Gestionar integraciones fiscales

**Cómo acceder:**
Dashboard → Clic en **"Datos SUNAT"**

**Secciones:**

#### A. Credenciales SOL
```
[Usuario SOL]        → Tu usuario de SUNAT Online
[Clave SOL]          → Tu contraseña de SUNAT Online
[Client ID]          → Identificador OAuth
[Client Secret]      → Secreto OAuth
[Clave Certificado]  → Contraseña del certificado digital
[Endpoint SUNAT]     → URL del servidor SUNAT
```

#### B. Modo de Envío
```
Selector: [ Beta | Producción ]

Beta:         Para pruebas y desarrollo
Producción:   Para envíos reales a SUNAT
```

#### C. Estado del Token
```
Certificado Cargado:  Muestra archivo actual
Token SUNAT:          Token de autenticación
Expira:               Fecha expiración del token
```

#### D. Integraciones Adicionales
```
[API Key OpenAI]       → Para inteligencia artificial
[Webhook Endpoint]     → URL para notificaciones
[Webhook Token]        → Token de seguridad
```

**Cómo Configurar:**
1. Completar campos de Credenciales SOL
2. Seleccionar modo (Beta o Producción)
3. Opcional: Agregar integraciones
4. Clic en **"Actualizar"**
5. Esperar confirmación

**⚠️ Importante:**
- Guardar credenciales de forma segura
- No compartir clave SOL con otros
- Verificar modo antes de guardar

---

### 5️⃣ Auditorías - Historial de Accesos

**Objetivo:** Ver registro de todos los accesos de usuarios

**Cómo acceder:**
Dashboard → Clic en **"Auditorías"**

**Información Mostrada:**

**Por cada registro:**
```
Usuario:        Quién accedió
Cargo:          Su rol en la empresa
Fecha/Hora:     Cuándo accedió
IP:             De dónde accedió
Estado:         ✓ Exitoso o ✗ Fallido
Motivo (si falló): Por qué falló
```

**Paginación:**
- Muestra X registros por página
- Botón **"Anterior"** → Página anterior
- Botón **"Siguiente"** → Página siguiente
- Indicador: "Página 1 de 50"

**Indicadores Visuales:**
- ✅ Verde = Acceso exitoso
- ❌ Rojo = Acceso fallido

**Ejemplo:**
```
┌──────────────────────────────────┐
│ Usuario: Juan Pérez              │
│ Cargo: Administrador             │
│ Fecha: 2025-12-09 14:30:22       │
│ IP: 192.168.1.100                │
│ ✓ EXITOSO                        │
└──────────────────────────────────┘

┌──────────────────────────────────┐
│ Usuario: Pedro López             │
│ Cargo: Vendedor                  │
│ Fecha: 2025-12-09 14:25:15       │
│ IP: 192.168.1.101                │
│ ✗ FALLIDO                        │
│ Motivo: Contraseña incorrecta    │
└──────────────────────────────────┘
```

---

### 6️⃣ Productos - Gestión de Catálogo

**Objetivo:** Crear, editar y eliminar productos

**Cómo acceder:**
Dashboard → Clic en **"Productos"**

#### Listar Productos
Automático al entrar, muestra todos los productos en RecyclerView

#### Agregar Producto
1. Clic en botón **"Nuevo Producto"**
2. Se abre diálogo con formulario
3. Llenar campos:
   - **Nombre** (obligatorio)
   - **Descripción**
   - **Precio Venta**
   - **Precio Compra**
   - **Stock**
4. Clic en **"Guardar"**
5. Esperar confirmación

#### Editar Producto
1. En lista, clic en botón **"Editar"** del producto
2. Se abre diálogo con datos cargados
3. Modificar campos deseados
4. Clic en **"Guardar"**

#### Eliminar Producto
1. En lista, clic en botón **"Eliminar"**
2. Se abre diálogo de confirmación
3. Clic en **"Eliminar"** para confirmar
4. Producto se elimina de la lista

**Campos Disponibles:**
| Campo | Tipo | Requerido |
|-------|------|-----------|
| Nombre | Texto | ✓ Sí |
| Descripción | Texto | Opcional |
| Precio Venta | Número | ✓ Sí |
| Precio Compra | Número | ✓ Sí |
| Stock | Número | ✓ Sí |

**Validaciones:**
- Nombre no puede estar vacío
- Precios deben ser números positivos
- Stock no puede ser negativo

---

### 7️⃣ Cotizaciones - Crear Presupuestos

**Objetivo:** Generar cotizaciones comerciales para clientes

**Cómo acceder:**
Dashboard → Clic en **"Cotizaciones"**

#### Paso 1: Buscar Cliente
```
1. Ingresa RUC del cliente (11 dígitos)
2. Clic en "Buscar RUC"
3. Sistema busca en base de datos
4. Autocompleta:
   - Razón Social
   - Dirección
```

**Validaciones:**
- RUC debe tener exactamente 11 dígitos
- Deben ser números solamente
- Cliente debe existir en sistema

#### Paso 2: Datos Adicionales del Cliente
```
- Persona que Autoriza (contacto)
- Asunto de la Cotización
```

Rellenar datos complementarios del cliente.

#### Paso 3: Agregar Productos
```
1. Ingresa nombre/código del producto
2. Clic en "Buscar Producto"
3. Sistema busca coincidencias

Si encuentra:
- 1 resultado  → Agrega automáticamente
- Múltiples   → Muestra lista para seleccionar
- Ninguno     → Muestra "No encontrado"

Repetir para agregar más productos
```

#### Paso 4: Editar Cantidades y Precios
```
Para cada producto:
1. Tocar cantidad → Editar → Enter
   Subtotal fila se recalcula

2. Tocar precio → Editar → Enter
   Subtotal fila se recalcula

Sistema calcula automáticamente:
- Subtotal por fila = precio × cantidad
- Subtotal total = suma de filas
- IGV = subtotal × 18%
- TOTAL = subtotal + IGV
```

#### Paso 5: Revisar Totales
```
Visualizar:
- Subtotal (suma de todos productos)
- IGV 18% (impuesto automático)
- TOTAL (subtotal + IGV)
```

#### Paso 6: Guardar Cotización
```
1. Verificar todos los datos
2. Clic en "Guardar"
3. Sistema valida:
   ✓ Al menos 1 producto
   ✓ RUC válido
   ✓ Razón Social completada
4. Si todo OK:
   → Mensaje: "Cotización guardada: COT-2025-00512"
   → Asigna número secuencial
   → Limpia formulario
5. Si hay error:
   → Muestra mensaje específico
```

**Ejemplo Cotización Completa:**
```
╔═══════════════════════════════════════╗
║    COTIZACIÓN COT-2025-00512          ║
╠═══════════════════════════════════════╣
║ CLIENTE:                              ║
║ RUC: 12345678901                      ║
║ Razón Social: ABC Empresa S.A.C.      ║
║ Dirección: Jr. Principal 123          ║
║ Contacto: Juan Pérez                  ║
╠═══════════════════════════════════════╣
║ PRODUCTOS:                            ║
║ 1. Laptop ASUS 15"                    ║
║    Cantidad: 2 × S/ 2,500.00 = 5,000  ║
║                                       ║
║ 2. Mouse Inalámbrico                  ║
║    Cantidad: 3 × S/ 50.00 = 150       ║
╠═══════════════════════════════════════╣
║ SUBTOTAL:          S/ 5,150.00        ║
║ IGV (18%):         S/ 927.00          ║
║ ─────────────────────────────────────  ║
║ TOTAL:             S/ 6,077.00        ║
╚═══════════════════════════════════════╝
```

---

## 🔒 Seguridad

### 🛡️ Buenas Prácticas

#### Protección de Credenciales
- 🔐 Nunca compartir usuario o contraseña
- 🔐 No escribir contraseña en notas visibles
- 🔐 Cambiar contraseña regularmente (cada 90 días)
- 🔐 Cerrar sesión al terminar de trabajar
- 🔐 Usar contraseñas fuertes (mínimo 8 caracteres)

#### Datos Sensibles
- 🔐 No capturar pantallazos con datos confidenciales
- 🔐 No permitir que otros usen tu cuenta
- 🔐 No compartir datos de cliente
- 🔐 Limpiar cache si se comparte dispositivo
- 🔐 No acceder desde redes públicas inseguras

#### Conexión de Red
- 🔐 Usar WiFi empresarial o confiable
- 🔐 Evitar redes públicas abiertas
- 🔐 Verificar HTTPS cuando esté disponible
- 🔐 No usar proxies no autorizados

#### Gestión de Sesión
- 🔐 Token se guarda de forma segura en SharedPreferences
- 🔐 Token expira automáticamente
- 🔐 Requiere re-login si token caduca
- 🔐 Cierre automático después de inactividad

---

## 📞 Soporte y Troubleshooting

### ❌ Problemas Comunes y Soluciones

#### 1. No puedo iniciar sesión
```
❌ Síntoma: "Credenciales incorrectas"

✅ Solución:
   1. Verificar usuario (sin espacios)
   2. Verificar contraseña (mayúsculas/minúsculas)
   3. Restablecer contraseña si la olvidaste
   4. Contactar administrador
```

#### 2. Error de conexión
```
❌ Síntoma: "Error de conexión: ..."

✅ Solución:
   1. Verificar WiFi/datos móviles activos
   2. Ir a Settings → WiFi → Conectarse
   3. Reiniciar dispositivo
   4. Verificar firewall corporativo
   5. Contactar administrador IT
```

#### 3. Aplicación se congela
```
❌ Síntoma: App responde lentamente

✅ Solución:
   1. Esperar 5-10 segundos
   2. Cerrar app: Settings → Apps → Moviles
   3. Limpiar cache: Storage → Clear Cache
   4. Reiniciar dispositivo
   5. Desinstalar y reinstalar
```

#### 4. Token expirado
```
❌ Síntoma: "Token inválido" o "Token expirado"

✅ Solución:
   1. Cerrar sesión
   2. Iniciar sesión nuevamente
   3. Verificar reloj del dispositivo
   4. Contactar soporte
```

#### 5. RecyclerView/Lista vacía
```
❌ Síntoma: No aparecen datos

✅ Solución:
   1. Verificar conexión activa
   2. Esperar 2-3 segundos a carga
   3. Deslizar hacia abajo para refrescar
   4. Verificar que tienes permisos
   5. Contactar soporte
```

#### 6. Botones no responden
```
❌ Síntoma: Tocar botón pero no pasa nada

✅ Solución:
   1. Esperar a que termine operación actual
   2. Verificar ProgressBar cargando
   3. Cerrar y reabrir app
   4. Reiniciar dispositivo
```

### 📞 Contacto de Soporte

| Soporte | Contacto | Respuesta |
|---------|----------|-----------|
| 📧 Email | support@empresa.com | <2 horas |
| 📞 Teléfono | +51 1 XXX-XXXX | <1 hora |
| 💬 Chat | [WhatsApp](tel:) | Inmediato |
| 🕐 Horario | Lun-Vie 9AM-5PM (Zona Perú) | En horario |

---

## 📚 Documentación Completa

La documentación detallada está organizada en varios archivos:

### 📘 [README_USUARIO.md](./README_USUARIO.md)
- Guía completa de uso por módulo
- Troubleshooting detallado
- Configuración avanzada

**Leer si:** Eres usuario final y necesitas ayuda

**Tiempo:** 30-45 minutos

---

### 📗 [DOCUMENTACION_PROYECTO.md](./DOCUMENTACION_PROYECTO.md)
- Visión general del proyecto
- Descripción de todos los componentes
- Flujos de datos
- Información general

**Leer si:** Quieres entender la arquitectura general

**Tiempo:** 45-60 minutos

---

### 📙 [DOCUMENTACION_TECNICA.md](./DOCUMENTACION_TECNICA.md)
- Especificaciones técnicas detalladas
- Código Kotlin documentado
- APIs REST con ejemplos
- Patrones de diseño
- Testing y deployment

**Leer si:** Eres desarrollador o necesitas detalles técnicos

**Tiempo:** 90-120 minutos

---

### 📕 [REQUISITOS.md](./REQUISITOS.md)
- 8 Requisitos funcionales (100% implementados)
- 10 Requisitos no funcionales (80% implementados)
- Casos de uso
- Criterios de aceptación
- Matriz de trazabilidad

**Leer si:** Necesitas validar requisitos o funcionalidades

**Tiempo:** 60-75 minutos

---

### 📓 [INDICE_DOCUMENTACION.md](./INDICE_DOCUMENTACION.md)
- Índice completo de toda documentación
- Guía de lectura por rol
- Búsqueda rápida por concepto
- Checklist de completitud

**Leer si:** Necesitas encontrar información específica rápidamente

**Tiempo:** 15-20 minutos

---

### 🎯 [RESUMEN_EJECUTIVO.md](./RESUMEN_EJECUTIVO.md)
- Resumen para directivos
- Estadísticas y ROI
- Roadmap de desarrollo
- KPIs de éxito

**Leer si:** Eres directivo o stakeholder

**Tiempo:** 15-20 minutos

---

## 🏗️ Estructura del Proyecto

```
moviles/
│
├── 📁 app/                          # Módulo principal
│   ├── 📁 src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml  # Configuración app
│   │   │   ├── 📁 java/com/example/moviles/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── LoginActivity.kt
│   │   │   │   ├── PrincipalActivity.kt
│   │   │   │   ├── DatosEmpresaActivity.kt
│   │   │   │   ├── AuditoriasActivity.kt
│   │   │   │   ├── DatosSunatActivity.kt
│   │   │   │   ├── ProductosActivity.kt
│   │   │   │   └── CotizacionesActivity.kt
│   │   │   └── 📁 res/
│   │   │       ├── 📁 layout/       # Layouts XML
│   │   │       ├── 📁 values/       # Strings, colors, etc
│   │   │       ├── 📁 drawable/     # Iconos
│   │   │       ├── 📁 mipmap/       # Launcher icons
│   │   │       └── 📁 xml/          # Config adicional
│   │   ├── androidTest/             # Tests instrumentados
│   │   └── test/                    # Unit tests
│   │
│   ├── build.gradle.kts             # Configuración gradle
│   └── proguard-rules.pro           # Obfuscation
│
├── 📁 gradle/
│   ├── libs.versions.toml           # Versiones dependencias
│   └── 📁 wrapper/
│
├── 📄 build.gradle.kts              # Root gradle
├── 📄 settings.gradle.kts           # Settings
├── 📄 gradle.properties             # Propiedades
├── 📄 local.properties              # Config local
│
├── 📄 gradlew                       # Gradle wrapper Unix
├── 📄 gradlew.bat                   # Gradle wrapper Windows
│
├── 📄 README.md                     # Este archivo
├── 📄 README_USUARIO.md             # Guía usuario
├── 📄 DOCUMENTACION_PROYECTO.md     # Visión general
├── 📄 DOCUMENTACION_TECNICA.md      # Detalles técnicos
├── 📄 REQUISITOS.md                 # Especificación requisitos
├── 📄 INDICE_DOCUMENTACION.md       # Índice
└── 📄 RESUMEN_EJECUTIVO.md          # Resumen ejecutivo
```

---

## 💻 Stack Tecnológico

### Lenguajes
```
Kotlin 2.0.21      - Lenguaje principal (idiomático para Android)
XML 1.0            - Layouts y configuración
```

### Framework Android
```
androidx.core:core-ktx (1.17.0)              - Extensiones Kotlin
androidx.appcompat:appcompat (1.7.1)         - Compatibilidad
androidx.activity:activity (1.11.0)          - Activity management
androidx.constraintlayout (2.2.1)            - Flexible layouts
com.google.android.material (1.13.0)         - Material Design 3
```

### Build System
```
Gradle 8.13.0                  - Sistema de compilación
Android Gradle Plugin 8.13.0   - Plugin AGP
Kotlin Gradle Plugin 2.0.21    - Plugin Kotlin
```

### Testing
```
junit:junit (4.13.2)                         - Unit tests
androidx.test.ext:junit (1.3.0)              - JUnit 4 para Android
androidx.test.espresso:espresso-core (3.7.0) - UI testing
```

### Nativo
```
java.net.HttpURLConnection     - HTTP cliente
org.json.JSONObject            - JSON parsing
kotlin.coroutines              - Asincronismo
android.widget.*               - Componentes UI
```

---

## 🎯 Características Técnicas

### Arquitectura
- ✅ MVP (Model-View-Presenter)
- ✅ Separación de responsabilidades
- ✅ Modularidad
- ✅ Escalabilidad

### Performance
- ✅ Carga app: <2 segundos
- ✅ Búsquedas: <1 segundo
- ✅ Cálculos: <100ms
- ✅ Almacenamiento: 50MB+ libre

### Seguridad
- ✅ Token-based authentication
- ✅ Validación cliente/servidor
- ✅ Auditoría completa
- ✅ Control de acceso

### Disponibilidad
- ✅ 99% uptime esperado
- ✅ Manejo robusto de errores
- ✅ Reconexión automática
- ✅ Validación de conectividad

### Usabilidad
- ✅ Material Design
- ✅ Interfaz intuitiva
- ✅ Feedback visual claro
- ✅ Mensajes de error descriptivos

---

## 📊 Estadísticas del Proyecto

### Cobertura de Funcionalidades
```
Requisitos Funcionales:     8/8 (100%) ✅
Requisitos No Funcionales:  8/10 (80%) ⚠️
Componentes:                11 (Activities + Adapters)
Líneas de Código:           ~3,700+ líneas
Documentación:              ~130+ páginas
```

### Componentes Principales
```
Activities (Pantallas):     8
- MainActivity
- LoginActivity
- PrincipalActivity
- DatosEmpresaActivity
- AuditoriasActivity
- DatosSunatActivity
- ProductosActivity
- CotizacionesActivity

Adapters (RecyclerView):    3
- AuditoriasAdapter
- ProductosAdapter
- ProductosCotizacionAdapter

Data Classes:               4
- AuditoriaItem
- Producto
- ProductoCotizacion

Endpoints API:              7
- Autenticación
- Datos Empresa (GET/POST)
- Auditorías
- Productos CRUD
- Búsqueda RUC
- Búsqueda Productos
- Guardar Cotización
```

---

## 🔄 Desarrollo Continuo

### Versión Actual
```
v1.0 - Diciembre 2025 (Actual)
✅ Funcionalidad completa
✅ Producción lista
✅ Documentación completa
```

### Próximas Versiones

#### v1.1 (Enero 2026)
```
🔜 Mejoras de seguridad
🔜 Optimizaciones de performance
🔜 Bug fixes basados en feedback
🔜 Soporte mejorado
```

#### v2.0 (Q2 2026)
```
🔜 Modo offline con sincronización
🔜 Dashboard web
🔜 Reportes avanzados
🔜 Exportación a PDF/Excel
```

#### v3.0 (Futuro)
```
🔜 Aplicación iOS
🔜 Integración con otros sistemas
🔜 IA/ML para predicciones
🔜 Soporte multi-idioma
```

---

## 🤝 Contribuciones

Si encuentras bugs o tienes sugerencias:

1. **Reportar Issue**
   - Ir a GitHub Issues
   - Proporcionar descripción clara
   - Incluir pasos para reproducir

2. **Proponer Mejora**
   - Crear PR (Pull Request)
   - Seguir estándares de código
   - Documentar cambios

3. **Contactar Equipo**
   - Email: support@empresa.com
   - Teléfono: +51 1 XXX-XXXX

---

## 📄 Licencia

Este proyecto es **PRIVADO** y propiedad de [Nombre Empresa].

- ❌ No puede ser copiado
- ❌ No puede ser distribuido
- ❌ No puede ser modificado sin permiso
- ✅ Uso autorizado solo para empleados

**Para más información sobre licencia, contactar a administrador.**

---

## 👨‍💼 Información del Proyecto

| Item | Valor |
|------|-------|
| **Nombre** | Moviles ERP |
| **Versión** | 1.0 |
| **Estado** | Producción |
| **Propietario** | ELKERPD |
| **Repositorio** | PREOYECTO_MOVILES |
| **Rama** | master |
| **Última Actualización** | 09/12/2025 |
| **Documentación** | 100% Completada |

---

## 🙏 Créditos

Desarrollado por **ELKERPD** y equipo de desarrollo.

Especial agradecimiento a:
- Equipo de QA por testing exhaustivo
- Stakeholders por feedback continuo
- Usuarios piloto por adopción temprana

---

## 📞 Contacto y Soporte

### Equipo de Desarrollo
- 👨‍💻 **Developer:** ELKERPD
- 📧 **Email:** dev@empresa.com
- 📱 **WhatsApp:** +51 XXX-XXXX

### Soporte Técnico
- 📧 **Help Desk:** support@empresa.com
- 📞 **Teléfono:** +51 1 XXX-XXXX
- 💬 **Chat:** [Link]
- 🕐 **Horario:** Lun-Vie 9:00 AM - 5:00 PM (UTC-5)

### Reportar Problemas
- 🐛 **GitHub Issues:** [Link]
- 📋 **Formulario:** [Link]
- 📧 **Email:** bugs@empresa.com

---

## 📚 Lecturas Recomendadas

Para aprender más sobre las tecnologías utilizadas:

- [Kotlin Documentation](https://kotlinlang.org/docs)
- [Android Developer Guide](https://developer.android.com)
- [Material Design Guidelines](https://material.io)
- [Coroutines Guide](https://kotlinlang.org/docs/coroutines-overview.html)
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)

---

## ⚠️ Desclaimer

Esta aplicación está diseñada para uso empresarial en Perú. Requiere:
- Conexión de internet estable
- Dispositivo Android 7.0+
- Credenciales válidas
- Servidor API disponible

El desarrollador no se responsabiliza por:
- Pérdida de datos sin respaldo
- Uso no autorizado de credenciales
- Daños derivados de mala instalación
- Incompatibilidad con dispositivos obsoletos

---

<div align="center">

### 🚀 ¡LISTA PARA USAR!

**Descarga, instala y comienza a gestionar tu empresa hoy mismo**

[![Download APK](https://img.shields.io/badge/Download-APK-blue.svg?style=for-the-badge)](./app/build/outputs/apk/release/app-release.apk)
[![Open Issues](https://img.shields.io/github/issues/ELKERPD/PREOYECTO_MOVILES.svg?style=for-the-badge)](https://github.com/ELKERPD/PREOYECTO_MOVILES/issues)
[![Last Commit](https://img.shields.io/github/last-commit/ELKERPD/PREOYECTO_MOVILES.svg?style=for-the-badge)](https://github.com/ELKERPD/PREOYECTO_MOVILES/commits/master)

**Versión 1.0** | **Producción** | **©2025 - Todos los derechos reservados**

</div>

---

*Documento generado: 09/12/2025 | Actualizado regularmente*
