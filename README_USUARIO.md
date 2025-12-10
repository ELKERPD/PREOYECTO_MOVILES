# README.md - Proyecto Moviles ERP

![Version](https://img.shields.io/badge/version-1.0-blue)
![Status](https://img.shields.io/badge/status-Production-success)
![License](https://img.shields.io/badge/license-Private-red)
![Android](https://img.shields.io/badge/android-7.0%2B-green)

## 📱 Descripción General

**Moviles ERP** es una aplicación móvil nativa Android desarrollada en Kotlin que permite la gestión integral de operaciones empresariales. La aplicación proporciona herramientas para autenticación, administración de datos empresariales, configuración fiscal, auditoría de accesos, gestión de productos y generación de cotizaciones comerciales.

### 🎯 Objetivo Principal

Proporcionar una solución móvil completa y segura para profesionales del área comercial y administrativa que necesiten gestionar información de su empresa sobre la marcha, con sincronización en tiempo real con un servidor ERP central.

---

## 🚀 Características Principales

✅ **Autenticación Segura**

- Login con usuario y contraseña
- Token-based authentication
- Gestión de sesiones

✅ **Gestión Empresarial**

- Visualización y edición de datos de la empresa
- Información de suscripción y plan
- Monitoreo de uso de espacio en disco

✅ **Configuración Fiscal**

- Integración con SUNAT (Perú)
- Gestión de credenciales SOL
- Configuración de certificados digitales
- Soporte para modo Beta/Producción

✅ **Auditoría y Seguridad**

- Registro completo de accesos de usuarios
- Visualización de intentos fallidos
- Información de IP y ubicación
- Paginación de registros

✅ **Gestión de Productos**

- CRUD completo de catálogo
- Gestión de stock
- Información de precios (compra/venta)
- Categorización y marcas

✅ **Cotizaciones Comerciales**

- Búsqueda de clientes por RUC
- Selección dinámica de productos
- Cálculo automático de totales (IGV, subtotales)
- Generación y guardado de cotizaciones
- Asignación de números secuenciales

---

## 📋 Requisitos

### Hardware

- **Dispositivo:** Teléfono Android con pantalla de 4.5" a 6.5"
- **RAM:** Mínimo 2GB
- **Almacenamiento:** 50MB libres

### Software

- **Android:** Versión 7.0 (API 24) o superior
- **Conexión:** WiFi o datos móviles 4G/5G
- **Servidor API:** Debe estar disponible y accesible

### Desarrollo (Para desarrolladores)

- **Android Studio:** 2024.1 o superior
- **JDK:** 11+
- **Gradle:** 8.13.0
- **Kotlin:** 2.0.21

---

## 🔧 Instalación

### Para Usuarios Finales

1. **Descarga el APK**

   - Obtener archivo `app-release.apk` del administrador

2. **Instalación**

   - Transferir APK al dispositivo
   - Abrir archivo con el administrador de archivos
   - Permitir instalación desde fuentes desconocidas
   - Completar instalación

3. **Primer Inicio**
   - Abrir aplicación
   - Se abrirá pantalla de login
   - Ingresar usuario y contraseña

### Para Desarrolladores

1. **Clonar Repositorio**

   ```bash
   git clone https://github.com/ELKERPD/PREOYECTO_MOVILES.git
   cd PREOYECTO_MOVILES
   ```

2. **Abrir en Android Studio**

   - Archivo → Abrir → Seleccionar carpeta del proyecto
   - Android Studio sincronizará automáticamente

3. **Sincronizar Gradle**

   ```bash
   ./gradlew sync
   ```

4. **Ejecutar en Emulador**

   - Abrir Android Virtual Device Manager
   - Crear o seleccionar emulador Android 7.0+
   - Run → Run 'app'

5. **Ejecutar en Dispositivo Real**
   - Conectar dispositivo Android por USB
   - Habilitar USB Debugging en opciones de desarrollador
   - Run → Run 'app'

---

## 🎮 Guía de Uso

### Flujo Principal

#### 1. Pantalla de Inicio

```
App Abre
    ↓
¿Hay sesión activa?
    ├─ SÍ → Dashboard (PrincipalActivity)
    └─ NO → Pantalla de Login (LoginActivity)
```

#### 2. Login

```
1. Ingresar usuario (email o usuario)
2. Ingresar contraseña
3. Clic en "Iniciar Sesión"
4. Esperar validación (2-3 segundos)
5. Si es exitoso → Ir a Dashboard
6. Si falla → Ver mensaje de error
```

**Errores Comunes:**

- "Credenciales incorrectas" → Verificar usuario y contraseña
- "Error de conexión" → Verificar WiFi/datos móviles

#### 3. Dashboard (Panel Principal)

```
Bienvenido, [Tu Nombre]

Opciones disponibles:
├─ [Datos de Empresa]
├─ [Configuración SUNAT]
├─ [Auditorías]
├─ [Productos]
└─ [Cotizaciones]
```

### Módulos Específicos

#### 📊 Datos de Empresa

**Objetivo:** Ver y editar información de tu empresa

**Uso:**

1. Dashboard → Clic en "Datos Empresa"
2. Esperar carga de datos (ProgressBar)
3. Ver información en pantalla
4. Para editar:
   - Modificar campos que necesites
   - Clic en "Actualizar"
   - Esperar confirmación
5. Volver: Clic en "Volver"

**Campos Editables:**

- RUC, Razón Social, Nombre Comercial
- Nombres y Apellidos de contacto
- Correo electrónico, Dirección, Celular
- Porcentaje IGV

**Campos de Solo Lectura:**

- Estado empresa (Activa/Inactiva)
- Fechas (Creación, Vencimiento, Corte)
- Información de Plan y Suscripción
- Uso de Espacio en Disco

---

#### 🔐 Configuración SUNAT

**Objetivo:** Configurar integración con servicios fiscales

**Uso:**

1. Dashboard → Clic en "Datos SUNAT"
2. Completar campos de Credenciales SOL
3. Seleccionar modo (Beta o Producción)
4. Configurar integraciones opcional (OpenAI, Webhooks)
5. Clic en "Actualizar"
6. Esperar confirmación

**Secciones:**

- **Credenciales SOL:** Usuario y clave de SUNAT
- **Certificado Digital:** Gestor de certificados
- **Modo de Envío:** Seleccionar ambiente (Beta/Producción)
- **Integraciones:** API Key OpenAI, Webhooks

**⚠️ Importante:**

- Mantener credenciales seguras
- No compartir clave SOL
- Verificar modo (Beta para pruebas, Producción para envíos reales)

---

#### 📋 Auditorías

**Objetivo:** Ver registro de accesos de usuarios

**Uso:**

1. Dashboard → Clic en "Auditorías"
2. Esperar carga de registros
3. Navegar entre páginas:
   - Clic "Anterior" para página anterior
   - Clic "Siguiente" para página siguiente
4. Ver información por registro

**Información Disponible:**

- Usuario que accedió
- Cargo del usuario
- Fecha y hora del acceso
- IP de origen
- Estado (Exitoso/Fallido)
- Motivo del fallo (si aplica)

**Indicadores Visuales:**

- ✅ Verde = Acceso exitoso
- ❌ Rojo = Acceso fallido

---

#### 📦 Productos

**Objetivo:** Gestionar catálogo de productos

**Listar Productos:**

1. Dashboard → Clic en "Productos"
2. Ver lista de todos los productos
3. Información: Nombre, Categoría, Marca, Precio, Stock

**Agregar Producto:**

1. Clic en "Nuevo Producto"
2. Llenar formulario:
   - Nombre (obligatorio)
   - Descripción
   - Precio Venta
   - Precio Compra
   - Stock
3. Clic "Guardar"
4. Esperar confirmación

**Editar Producto:**

1. Clic en "Editar" del producto
2. Modificar datos
3. Clic "Guardar"

**Eliminar Producto:**

1. Clic en "Eliminar" del producto
2. Confirmar eliminación en diálogo
3. Producto se elimina de la lista

**⚠️ Validaciones:**

- Nombre es obligatorio
- Precios deben ser números positivos
- Stock no puede ser negativo

---

#### 💰 Cotizaciones

**Objetivo:** Crear cotizaciones para clientes

**Paso 1: Buscar Cliente**

1. Dashboard → Clic en "Cotizaciones"
2. Ingresar RUC del cliente (11 dígitos)
3. Clic en "Buscar RUC"
4. Esperar respuesta
5. Campos se autocompletan:
   - Razón Social
   - Dirección

**Paso 2: Completar Datos del Cliente**

- Persona que Autoriza (contacto)
- Asunto de la Cotización

**Paso 3: Agregar Productos**

1. Ingrese nombre/código del producto
2. Clic en "Buscar Producto"
3. Sistema busca en catálogo
4. Si hay 1 resultado → Agrega automáticamente
5. Si hay múltiples → Seleccionar de lista
6. Repetir para agregar más productos

**Paso 4: Editar Cantidades y Precios**

- Clic en cantidad → Editar → Enter
- Clic en precio → Editar → Enter
- Precio total se calcula automáticamente

**Paso 5: Revisar Totales**

- Subtotal: Suma de todos los productos
- IGV: Impuesto (18%)
- Total: Subtotal + IGV

**Paso 6: Guardar Cotización**

1. Verificar que todos los datos son correctos
2. Clic en "Guardar"
3. Sistema valida datos
4. Si todo es correcto:
   - Mensaje: "Cotización guardada: COT-2025-00512"
   - Formulario se limpia
5. Si hay error → Ver mensaje específico

**Validaciones Antes de Guardar:**

- ✓ Al menos 1 producto debe estar agregado
- ✓ RUC debe ser válido (11 dígitos)
- ✓ Razón Social no puede estar vacía
- ✓ Cantidades deben ser > 0
- ✓ Precios deben ser > 0

---

## 🔒 Seguridad

### Buenas Prácticas

✅ **Protección de Credenciales**

- Nunca compartir usuario o contraseña
- Cerrar sesión al terminar
- Cambiar contraseña regularmente

✅ **Datos Sensibles**

- No capturar capturas de pantalla con datos
- No permitir que otros accedan con tu usuario
- Limpiar cache de la app si se comparte dispositivo

✅ **Conexión de Red**

- Usar WiFi empresarial o confiable
- Evitar redes públicas para datos sensibles
- Verificar que URL sea HTTPS (cuando esté disponible)

### Manejo de Errores de Seguridad

**Token Expirado:**

```
Síntoma: "Token inválido" o "Token expirado"
Acción: Volver a iniciar sesión
```

**Acceso Denegado:**

```
Síntoma: "No tienes permiso para esta acción"
Acción: Contactar administrador de la empresa
```

---

## 📞 Soporte y Troubleshooting

### Problemas Comunes

#### ❌ No puedo iniciar sesión

**Posibles causas:**

1. Usuario/contraseña incorrectos

   - Verificar mayúsculas/minúsculas
   - Verificar espacios al inicio/final

2. Servidor no disponible

   - Verificar conectividad de red
   - Contactar administrador IT

3. Aplicación se congela
   - Cerrar aplicación
   - Limpiar cache: Configuración → Aplicaciones → Moviles → Borrar Caché
   - Reinstalar aplicación

#### ❌ Error de conexión

**Soluciones:**

```
1. Verificar WiFi/Datos móviles
   Settings → WiFi/Datos

2. Habilitar datos móviles
   Pull down desde arriba → Habilitar datos

3. Reiniciar dispositivo
   Apagar 5 segundos → Encender

4. Verificar firewall/proxy
   Contactar administrador de red
```

#### ❌ Aplicación se cierra inesperadamente

**Acciones:**

```
1. Verificar actualizaciones del sistema
2. Limpiar cache de la app
3. Desinstalar y reinstalar
4. Contactar soporte técnico
```

#### ❌ RecyclerView/Lista vacía

**Verificar:**

```
- ¿Hay datos en el servidor?
- ¿Tienes permisos para ver datos?
- ¿Conexión de red activa?
- Esperar 2-3 segundos a carga completar
```

### Contacto de Soporte

**Email:** support@empresa.com  
**Teléfono:** +51 1 XXX-XXXX  
**Horario:** Lunes-Viernes 9:00 AM - 5:00 PM (Zona Perú)

---

## 🛠️ Configuración Avanzada

### Cambiar Servidor API

> ⚠️ Solo para administradores

**Ubicación:** Código fuente `LoginActivity.kt`

```kotlin
// Cambiar esta línea:
val url = URL("http://10.0.2.2/PROYECTO_ERP/API_RES_TECNODESARROLLOPEREZ/zona_acceso987654321.php")

// A tu servidor:
val url = URL("http://tu-servidor.com/API/zona_acceso987654321.php")
```

### Limpiar Datos Locales

```
Configuración del Dispositivo
  → Aplicaciones
  → Moviles ERP
  → Almacenamiento
  → Borrar Datos / Borrar Caché
```

---

## 📚 Documentación Completa

Para documentación más detallada, ver:

- **`DOCUMENTACION_PROYECTO.md`** - Visión general del proyecto
- **`DOCUMENTACION_TECNICA.md`** - Especificaciones técnicas
- **`REQUISITOS.md`** - Requisitos funcionales y no funcionales

---

## 🔄 Versionamiento

| Versión | Fecha      | Cambios         |
| ------- | ---------- | --------------- |
| 1.0     | 09/12/2025 | Release inicial |

---

## 📄 Licencia

Este proyecto es **privado** y propiedad de [Nombre Empresa].

Uso no autorizado está prohibido.

---

## 👨‍💼 Información del Proyecto

- **Propietario:** ELKERPD
- **Repositorio:** PREOYECTO_MOVILES
- **Rama:** master
- **Estado:** Producción
- **Fecha de Última Actualización:** 09/12/2025

---

## 🙏 Créditos

Desarrollado por el equipo de desarrollo de ELKERPD.

---

**Para más información o reportar problemas, contactar al equipo de soporte.**
