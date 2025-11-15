package com.example.moviles

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class CotizacionesActivity : AppCompatActivity() {

    // Views
    private lateinit var etRuc: TextInputEditText
    private lateinit var btnBuscarRuc: Button
    private lateinit var etRazonSocial: TextInputEditText
    private lateinit var etDireccion: TextInputEditText
    private lateinit var etPersonaAutoriza: TextInputEditText
    private lateinit var etAsunto: TextInputEditText
    private lateinit var spinnerMoneda: Spinner
    private lateinit var spinnerFormaPago: Spinner
    private lateinit var spinnerCuentaBancaria: Spinner
    private lateinit var spinnerValidez: Spinner
    private lateinit var spinnerTiempoEntrega: Spinner
    private lateinit var etBuscarProducto: TextInputEditText
    private lateinit var btnBuscarProducto: Button
    private lateinit var rvProductos: RecyclerView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvIgv: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    // Data
    private val productosAgregados = mutableListOf<ProductoDetalle>()
    private lateinit var productosAdapter: ProductosAdapter

    // Datos de sesión
    private var idEmpresa: Int = 0
    private var idUsuario: Int = 0
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cotizaciones_activity)

        // Cargar datos de sesión
        cargarDatosSesion()

        // Inicializar views
        inicializarVistas()

        // Configurar RecyclerView
        configurarRecyclerView()

        // Cargar datos comerciales
        cargarDatosComerciales()

        // Configurar eventos
        configurarEventos()
    }

    private fun cargarDatosSesion() {
        val prefs = getSharedPreferences("datos_app", Context.MODE_PRIVATE)
        token = prefs.getString("token", "") ?: ""
        idEmpresa = prefs.getInt("id_empresa", 0)
        idUsuario = prefs.getInt("id_usuario", 0)
    }

    private fun inicializarVistas() {
        etRuc = findViewById(R.id.etRuc)
        btnBuscarRuc = findViewById(R.id.btnBuscarRuc)
        etRazonSocial = findViewById(R.id.etRazonSocial)
        etDireccion = findViewById(R.id.etDireccion)
        etPersonaAutoriza = findViewById(R.id.etPersonaAutoriza)
        etAsunto = findViewById(R.id.etAsunto)
        spinnerMoneda = findViewById(R.id.spinnerMoneda)
        spinnerFormaPago = findViewById(R.id.spinnerFormaPago)
        spinnerCuentaBancaria = findViewById(R.id.spinnerCuentaBancaria)
        spinnerValidez = findViewById(R.id.spinnerValidez)
        spinnerTiempoEntrega = findViewById(R.id.spinnerTiempoEntrega)
        etBuscarProducto = findViewById(R.id.etBuscarProducto)
        btnBuscarProducto = findViewById(R.id.btnBuscarProducto)
        rvProductos = findViewById(R.id.rvProductos)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvIgv = findViewById(R.id.tvIgv)
        tvTotal = findViewById(R.id.tvTotal)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnCancelar = findViewById(R.id.btnCancelar)
    }

    private fun configurarRecyclerView() {
        productosAdapter = ProductosAdapter(productosAgregados) {
            calcularTotales()
        }
        rvProductos.apply {
            layoutManager = LinearLayoutManager(this@CotizacionesActivity)
            adapter = productosAdapter
        }
    }

    private fun configurarEventos() {
        btnBuscarRuc.setOnClickListener { buscarRuc() }
        btnBuscarProducto.setOnClickListener { buscarProducto() }
        btnGuardar.setOnClickListener { guardarCotizacion() }
        btnCancelar.setOnClickListener { limpiarFormulario() }

        // Búsqueda con Enter
        etBuscarProducto.setOnEditorActionListener { _, _, _ ->
            buscarProducto()
            true
        }
    }

    private fun cargarDatosComerciales() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                cargarDatoSpinner("moneda", spinnerMoneda)
                cargarDatoSpinner("forma_pago", spinnerFormaPago)
                cargarDatoSpinner("cuenta_bancaria", spinnerCuentaBancaria)
                cargarDatoSpinner("validez", spinnerValidez)
                cargarDatoSpinner("tiempo_entrega", spinnerTiempoEntrega)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarNotificacion("Error al cargar datos comerciales", "error")
                }
            }
        }
    }

    private suspend fun cargarDatoSpinner(tipo: String, spinner: Spinner) {
        val url = URL("http://10.0.2.2/PROYECTO_ERP/BACK/datos_comerciales.php?accion=listar&tipo=$tipo")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"

        val response = conn.inputStream.bufferedReader().readText()
        val json = JSONObject(response)

        if (json.getBoolean("estado")) {
            val data = json.getJSONArray("data")
            val items = mutableListOf("Seleccione...")

            for (i in 0 until data.length()) {
                val item = data.getJSONObject(i)
                val nombre = when {
                    item.has(tipo) -> item.getString(tipo)
                    item.has("forma_pago") -> item.getString("forma_pago")
                    item.has("banco") -> item.getString("banco")
                    item.has("validez") -> item.getString("validez")
                    item.has("tiempo_entrega") -> item.getString("tiempo_entrega")
                    else -> ""
                }
                if (nombre.isNotEmpty()) items.add(nombre)
            }

            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(this@CotizacionesActivity, android.R.layout.simple_spinner_item, items)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
        }
    }

    private fun buscarRuc() {
        val ruc = etRuc.text.toString().trim()

        if (!ruc.matches(Regex("\\d{11}"))) {
            mostrarNotificacion("Ingrese un RUC válido de 11 dígitos", "warning")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("http://10.0.2.2/PROYECTO_ERP/BACK/busqueda_ruc.php")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true

                val postData = "ruc=$ruc"
                OutputStreamWriter(conn.outputStream).use { it.write(postData) }

                val response = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(response)

                withContext(Dispatchers.Main) {
                    if (json.getBoolean("success")) {
                        etRazonSocial.setText(json.getString("razon_social"))
                        etDireccion.setText(json.getString("direccion"))
                        mostrarNotificacion("Cliente encontrado correctamente", "success")
                    } else {
                        mostrarNotificacion(json.getString("mensaje"), "warning")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarNotificacion("Error al buscar RUC", "error")
                }
            }
        }
    }

    private fun buscarProducto() {
        val texto = etBuscarProducto.text.toString().trim()

        if (texto.isEmpty()) {
            mostrarNotificacion("Ingrese texto de búsqueda", "warning")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("http://10.0.2.2/PROYECTO_ERP/BACK/buscar_producto.php")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true

                val postData = "busqueda=$texto"
                OutputStreamWriter(conn.outputStream).use { it.write(postData) }

                val response = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(response)

                withContext(Dispatchers.Main) {
                    if (json.getBoolean("success")) {
                        val productos = json.getJSONArray("productos")

                        when {
                            productos.length() == 0 -> {
                                mostrarNotificacion("No se encontraron productos", "warning")
                            }
                            productos.length() == 1 -> {
                                agregarProductoTabla(productos.getJSONObject(0))
                            }
                            else -> {
                                mostrarSelectorProductos(productos)
                            }
                        }
                    } else {
                        mostrarNotificacion(json.getString("mensaje"), "warning")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarNotificacion("Error al buscar productos", "error")
                }
            }
        }
    }

    private fun agregarProductoTabla(productoJson: JSONObject) {
        val idProducto = productoJson.getInt("id_producto")

        // Verificar si ya existe
        if (productosAgregados.any { it.idProducto == idProducto }) {
            mostrarNotificacion("El producto ya está agregado", "warning")
            return
        }

        val precioVenta = productoJson.getDouble("precio_venta")
        val precioMayorista = productoJson.optDouble("precio_mayorista", 0.0)
        val precioOferta = productoJson.optDouble("precio_oferta", 0.0)
        val ofertaActiva = productoJson.optInt("oferta_activa", 0) == 1
        val cantidadMinimaMayorista = productoJson.optInt("cantidad_minima_mayorista", 0)

        val precioFinal = determinarPrecio(
            precioVenta,
            precioMayorista,
            precioOferta,
            ofertaActiva,
            cantidadMinimaMayorista,
            1
        )

        val producto = ProductoDetalle(
            idProducto = idProducto,
            codigo = productoJson.optString("codigo_interno", productoJson.optString("codigo_barras", "")),
            nombre = productoJson.getString("nombre"),
            precioUnitario = precioFinal,
            cantidad = 1,
            precioVenta = precioVenta,
            precioMayorista = precioMayorista,
            precioOferta = precioOferta,
            ofertaActiva = ofertaActiva,
            cantidadMinimaMayorista = cantidadMinimaMayorista
        )

        productosAgregados.add(producto)
        productosAdapter.notifyItemInserted(productosAgregados.size - 1)
        calcularTotales()

        mostrarNotificacion("Producto agregado correctamente", "success")
    }

    private fun determinarPrecio(
        precioVenta: Double,
        precioMayorista: Double,
        precioOferta: Double,
        ofertaActiva: Boolean,
        cantidadMinima: Int,
        cantidad: Int
    ): Double {
        val precios = mutableListOf(precioVenta)

        if (ofertaActiva && precioOferta > 0) precios.add(precioOferta)
        if (cantidadMinima > 0 && cantidad >= cantidadMinima && precioMayorista > 0) {
            precios.add(precioMayorista)
        }

        return precios.minOrNull() ?: precioVenta
    }

    private fun mostrarSelectorProductos(productos: JSONArray) {
        val items = Array(productos.length()) { i ->
            val p = productos.getJSONObject(i)
            "${p.getString("nombre")} - ${p.optString("codigo_interno", "")} - S/ ${p.getDouble("precio_venta")}"
        }

        AlertDialog.Builder(this)
            .setTitle("Selecciona un producto")
            .setItems(items) { _, which ->
                agregarProductoTabla(productos.getJSONObject(which))
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

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

    private fun guardarCotizacion() {
        // Validaciones
        if (productosAgregados.isEmpty()) {
            mostrarNotificacion("Agregue al menos un producto", "warning")
            return
        }

        val ruc = etRuc.text.toString().trim()
        if (!ruc.matches(Regex("\\d{11}"))) {
            mostrarNotificacion("Ingrese un RUC válido", "warning")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("http://10.0.2.2/PROYECTO_ERP/BACK/cotizaciones.php")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                // Preparar productos JSON
                val productosJson = JSONArray()
                productosAgregados.forEach { p ->
                    val obj = JSONObject().apply {
                        put("id_producto", p.idProducto)
                        put("descripcion", p.nombre)
                        put("cantidad", p.cantidad)
                        put("precio_unitario", p.precioUnitario)
                        put("total", p.precioUnitario * p.cantidad)
                    }
                    productosJson.put(obj)
                }

                // Preparar datos
                val postData = buildString {
                    append("accion=crear")
                    append("&id_empresa=$idEmpresa")
                    append("&id_usuario=$idUsuario")
                    append("&ruc_cliente=$ruc")
                    append("&razon_social_cliente=${etRazonSocial.text}")
                    append("&direccion_cliente=${etDireccion.text}")
                    append("&persona_autoriza=${etPersonaAutoriza.text}")
                    append("&asunto=${etAsunto.text}")
                    append("&moneda=${spinnerMoneda.selectedItem}")
                    append("&forma_pago=${spinnerFormaPago.selectedItem}")
                    append("&cuenta_bancaria=${spinnerCuentaBancaria.selectedItem}")
                    append("&tiempo_entrega=${spinnerTiempoEntrega.selectedItem}")
                    append("&validez=${spinnerValidez.selectedItem}")
                    append("&subtotal=${tvSubtotal.text}")
                    append("&igv=${tvIgv.text}")
                    append("&total=${tvTotal.text}")
                    append("&productos=${productosJson}")
                }

                OutputStreamWriter(conn.outputStream).use { it.write(postData) }

                val response = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(response)

                withContext(Dispatchers.Main) {
                    if (json.getBoolean("success")) {
                        mostrarNotificacion("Cotización guardada correctamente", "success")
                        limpiarFormulario()
                        // Opcional: navegar a otra actividad
                        // val intent = Intent(this@CotizacionesActivity, BoletaActivity::class.java)
                        // intent.putExtra("id_cotizacion", json.getInt("id_cotizacion"))
                        // startActivity(intent)
                    } else {
                        mostrarNotificacion(json.getString("mensaje"), "error")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarNotificacion("Error al guardar cotización: ${e.message}", "error")
                }
            }
        }
    }

    private fun limpiarFormulario() {
        etRuc.text?.clear()
        etRazonSocial.text?.clear()
        etDireccion.text?.clear()
        etPersonaAutoriza.text?.clear()
        etAsunto.text?.clear()
        etBuscarProducto.text?.clear()
        productosAgregados.clear()
        productosAdapter.notifyDataSetChanged()
        tvSubtotal.text = "0.00"
        tvIgv.text = "0.00"
        tvTotal.text = "0.00"
        spinnerMoneda.setSelection(0)
        spinnerFormaPago.setSelection(0)
        spinnerCuentaBancaria.setSelection(0)
        spinnerValidez.setSelection(0)
        spinnerTiempoEntrega.setSelection(0)
    }

    private fun mostrarNotificacion(mensaje: String, tipo: String) {
        val color = when (tipo) {
            "success" -> R.color.success_green
            "error" -> R.color.error_red
            "warning" -> R.color.warning_orange
            else -> R.color.info_blue
        }

        Snackbar.make(findViewById(android.R.id.content), mensaje, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(color))
            .show()
    }
}

// Data classes
data class ProductoDetalle(
    val idProducto: Int,
    val codigo: String,
    val nombre: String,
    var precioUnitario: Double,
    var cantidad: Int,
    val precioVenta: Double,
    val precioMayorista: Double,
    val precioOferta: Double,
    val ofertaActiva: Boolean,
    val cantidadMinimaMayorista: Int
)

// Adapter para RecyclerView
class ProductosAdapter(
    private val productos: MutableList<ProductoDetalle>,
    private val onUpdate: () -> Unit
) : RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder>() {

    inner class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCodigo: TextView = view.findViewById(R.id.tvCodigo)
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val etPrecio: EditText = view.findViewById(R.id.etPrecio)
        val etCantidad: EditText = view.findViewById(R.id.etCantidad)
        val tvSubtotal: TextView = view.findViewById(R.id.tvSubtotal)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_cotizacion, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]

        holder.tvCodigo.text = producto.codigo
        holder.tvNombre.text = producto.nombre
        holder.etPrecio.setText(String.format("%.2f", producto.precioUnitario))
        holder.etCantidad.setText(producto.cantidad.toString())
        holder.tvSubtotal.text = String.format("%.2f", producto.precioUnitario * producto.cantidad)

        // Eventos
        holder.etCantidad.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val cantidad = holder.etCantidad.text.toString().toIntOrNull() ?: 1
                producto.cantidad = cantidad
                onUpdate()
                notifyItemChanged(position)
            }
        }

        holder.etPrecio.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val precio = holder.etPrecio.text.toString().toDoubleOrNull() ?: 0.0
                producto.precioUnitario = precio
                onUpdate()
                notifyItemChanged(position)
            }
        }

        holder.btnEliminar.setOnClickListener {
            productos.removeAt(position)
            notifyItemRemoved(position)
            onUpdate()
        }
    }

    override fun getItemCount() = productos.size
}