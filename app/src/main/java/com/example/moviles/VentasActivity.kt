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
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import android.content.Intent

class VentasActivity : AppCompatActivity() {

    // Views
    private lateinit var btnVolver: Button
    private lateinit var etRuc: EditText
    private lateinit var btnBuscarRuc: Button
    private lateinit var etRazonSocial: EditText
    private lateinit var etDireccion: EditText
    private lateinit var spinnerMoneda: Spinner
    private lateinit var spinnerFormaPago: Spinner
    private lateinit var etBuscarProducto: EditText
    private lateinit var btnBuscarProducto: Button
    private lateinit var rvProductos: RecyclerView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvIgv: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button
    private lateinit var progressBar: ProgressBar

    // Data
    private val productosAgregados = mutableListOf<ProductoVenta>()
    private lateinit var productosAdapter: ProductosVentaAdapter

    // Datos de sesión
    private var idEmpresa: Int = 0
    private var idUsuario: Int = 0
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ventas_activity)

        cargarDatosSesion()
        inicializarVistas()
        configurarSpinners()
        configurarRecyclerView()
        configurarEventos()
    }

    private fun cargarDatosSesion() {
        val prefs = getSharedPreferences("datos_app", Context.MODE_PRIVATE)
        token = prefs.getString("token", "") ?: ""
        idEmpresa = prefs.getInt("id_empresa", 0)
        idUsuario = prefs.getInt("id_usuario", 0)
    }

    private fun inicializarVistas() {
        btnVolver = findViewById(R.id.btnVolver)
        etRuc = findViewById(R.id.etRuc)
        btnBuscarRuc = findViewById(R.id.btnBuscarRuc)
        etRazonSocial = findViewById(R.id.etRazonSocial)
        etDireccion = findViewById(R.id.etDireccion)
        spinnerMoneda = findViewById(R.id.spinnerMoneda)
        spinnerFormaPago = findViewById(R.id.spinnerFormaPago)
        etBuscarProducto = findViewById(R.id.etBuscarProducto)
        btnBuscarProducto = findViewById(R.id.btnBuscarProducto)
        rvProductos = findViewById(R.id.rvProductos)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvIgv = findViewById(R.id.tvIgv)
        tvTotal = findViewById(R.id.tvTotal)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnCancelar = findViewById(R.id.btnCancelar)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun configurarSpinners() {
        // Spinner Moneda
        val monedas = arrayOf("PEN", "USD")
        val adapterMoneda = ArrayAdapter(this, android.R.layout.simple_spinner_item, monedas)
        adapterMoneda.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMoneda.adapter = adapterMoneda

        // Spinner Forma de Pago
        val formasPago = arrayOf("Contado", "Crédito", "Transferencia")
        val adapterFormaPago = ArrayAdapter(this, android.R.layout.simple_spinner_item, formasPago)
        adapterFormaPago.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFormaPago.adapter = adapterFormaPago
    }

    private fun configurarRecyclerView() {
        productosAdapter = ProductosVentaAdapter(productosAgregados) {
            calcularTotales()
        }
        rvProductos.apply {
            layoutManager = LinearLayoutManager(this@VentasActivity)
            adapter = productosAdapter
        }
    }

    private fun configurarEventos() {
        btnVolver.setOnClickListener { finish() }
        btnBuscarRuc.setOnClickListener { buscarRuc() }
        btnBuscarProducto.setOnClickListener { buscarProducto() }
        btnGuardar.setOnClickListener { guardarVenta() }
        btnCancelar.setOnClickListener { limpiarFormulario() }
    }

    private fun buscarRuc() {
        val ruc = etRuc.text.toString().trim()

        if (ruc.length != 11 || !ruc.all { it.isDigit() }) {
            Toast.makeText(this, "Ingrese un RUC válido de 11 dígitos", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null
            try {
                val url = URL("https://sercon-aje.com/API_APP_MOVIL/API_RES_TECNODESARROLLOPEREZ/busqueda_ruc.php")
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                conn.doOutput = true

                val postData = "token=$token&id_empresa=$idEmpresa&ruc=$ruc"
                conn.outputStream.use { it.write(postData.toByteArray()) }

                val response = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(response)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    if (json.getBoolean("success")) {
                        etRazonSocial.setText(json.getString("razon_social"))
                        etDireccion.setText(json.optString("direccion", ""))
                        Toast.makeText(applicationContext, "Cliente encontrado", Toast.LENGTH_SHORT).show()
                    } else {
                        val mensaje = json.getString("mensaje")
                        Toast.makeText(applicationContext, mensaje, Toast.LENGTH_SHORT).show()

                        if (mensaje.contains("Token", ignoreCase = true) ||
                            mensaje.contains("expirado", ignoreCase = true)) {
                            cerrarSesion()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                conn?.disconnect()
            }
        }
    }

    private fun buscarProducto() {
        val texto = etBuscarProducto.text.toString().trim()

        if (texto.isEmpty()) {
            Toast.makeText(this, "Ingrese texto de búsqueda", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null
            try {
                val url = URL("https://sercon-aje.com/API_APP_MOVIL/API_RES_TECNODESARROLLOPEREZ/busqueda_producto.php")
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                conn.doOutput = true

                val postData = "token=${token}&id_empresa=${idEmpresa}&busqueda=${texto}"
                conn.outputStream.use { it.write(postData.toByteArray(Charsets.UTF_8)) }

                val response = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(response)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (json.getBoolean("success")) {
                        val productosArray = json.getJSONArray("productos")
                        val productosFiltrados = mutableListOf<JSONObject>()

                        for (i in 0 until productosArray.length()) {
                            val p = productosArray.getJSONObject(i)
                            val nombre = p.getString("nombre").lowercase()

                            if (nombre.contains(texto.lowercase())) {
                                productosFiltrados.add(p)
                            }
                        }

                        when {
                            productosFiltrados.isEmpty() -> {
                                Toast.makeText(applicationContext, "No se encontraron productos", Toast.LENGTH_SHORT).show()
                            }
                            productosFiltrados.size == 1 -> {
                                agregarProductoTabla(productosFiltrados[0])
                            }
                            else -> {
                                mostrarSelectorProductos(productosFiltrados)
                            }
                        }
                    } else {
                        val mensaje = json.getString("mensaje")
                        Toast.makeText(applicationContext, mensaje, Toast.LENGTH_SHORT).show()

                        if (mensaje.contains("Token", ignoreCase = true) ||
                            mensaje.contains("expira", ignoreCase = true)) {
                            cerrarSesion()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                conn?.disconnect()
            }
        }
    }

    private fun agregarProductoTabla(productoJson: JSONObject) {
        val idProducto = productoJson.getInt("id_producto")

        if (productosAgregados.any { it.idProducto == idProducto }) {
            Toast.makeText(this, "El producto ya está agregado", Toast.LENGTH_SHORT).show()
            return
        }

        val producto = ProductoVenta(
            idProducto = idProducto,
            codigo = productoJson.optString("codigo", ""),
            nombre = productoJson.getString("nombre"),
            precioUnitario = productoJson.optDouble("precio_venta", 0.0),
            cantidad = 1
        )

        productosAgregados.add(producto)
        productosAdapter.notifyItemInserted(productosAgregados.size - 1)
        calcularTotales()

        etBuscarProducto.setText("")
        Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show()
    }

    private fun mostrarSelectorProductos(productos: List<JSONObject>) {
        val items = productos.map { p ->
            "${p.getString("nombre")} - S/ ${p.getDouble("precio_venta")}"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Selecciona un producto")
            .setItems(items) { _, which ->
                agregarProductoTabla(productos[which])
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

    private fun guardarVenta() {
        // Validar productos
        if (productosAgregados.isEmpty()) {
            Toast.makeText(this, "Agregue al menos un producto", Toast.LENGTH_SHORT).show()
            return
        }

        val ruc = etRuc.text.toString().trim()
        if (ruc.length != 11 || !ruc.all { it.isDigit() }) {
            Toast.makeText(this, "Ingrese un RUC válido", Toast.LENGTH_SHORT).show()
            return
        }

        val razonSocial = etRazonSocial.text.toString().trim()
        if (razonSocial.isEmpty()) {
            Toast.makeText(this, "Ingrese la razón social", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE
        btnGuardar.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null
            try {
                // 🔥 CONSTRUIR JSON COMO LO ESPERA EL PHP
                val jsonBody = JSONObject().apply {
                    put("ruc", ruc)
                    put("razon_social", razonSocial)
                    put("direccion", etDireccion.text.toString())
                    put("moneda", spinnerMoneda.selectedItem.toString())
                    put("forma_pago", spinnerFormaPago.selectedItem.toString())
                    put("subtotal", tvSubtotal.text.toString().toDouble())
                    put("igv", tvIgv.text.toString().toDouble())
                    put("total", tvTotal.text.toString().toDouble())

                    // 🔥 ARRAY DE PRODUCTOS CON LA ESTRUCTURA CORRECTA
                    val productosArray = JSONArray()
                    productosAgregados.forEach { p ->
                        val productoObj = JSONObject().apply {
                            put("id_producto", p.idProducto)
                            put("codigo", p.codigo)
                            put("nombre", p.nombre)
                            put("precio", p.precioUnitario)
                            put("cantidad", p.cantidad)
                            put("subtotal", p.precioUnitario * p.cantidad)
                        }
                        productosArray.put(productoObj)
                    }
                    put("productos", productosArray)
                }

                // 🔥 URL CON PARÁMETROS GET (token, id_empresa, id_usuario)
                val urlWithParams = "https://sercon-aje.com/API_APP_MOVIL/API_RES_TECNODESARROLLOPEREZ/ventas.php" +
                        "?token=$token&id_empresa=$idEmpresa&id_usuario=$idUsuario"

                val url = URL(urlWithParams)
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")

                // 🔥 ENVIAR JSON EN EL BODY
                conn.outputStream.use {
                    it.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
                }

                val responseCode = conn.responseCode
                val response = if (responseCode == HttpURLConnection.HTTP_OK) {
                    conn.inputStream.bufferedReader().readText()
                } else {
                    conn.errorStream?.bufferedReader()?.readText() ?: "Error desconocido"
                }

                val json = JSONObject(response)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnGuardar.isEnabled = true

                    if (json.getBoolean("success")) {
                        val idGenerado = json.optInt("id_venta", -1)
                        val numero = json.optString("numero_venta", "")

                        Toast.makeText(
                            applicationContext,
                            "Venta guardada: $numero",
                            Toast.LENGTH_LONG
                        ).show()

                        limpiarFormulario()

                        // Abrir PDF si existe el ID
                        if (idGenerado != -1) {
                            val pdfUrl = "https://sercon-aje.com/PROYECTO_GRUPO_11/CONTROLADOR/boleta_vent_movil.php?id=$idGenerado"
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = android.net.Uri.parse(pdfUrl)
                            startActivity(intent)
                        }
                    } else {
                        val mensaje = json.getString("mensaje")
                        Toast.makeText(applicationContext, mensaje, Toast.LENGTH_LONG).show()

                        if (mensaje.contains("Token", ignoreCase = true) ||
                            mensaje.contains("expirado", ignoreCase = true)) {
                            cerrarSesion()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnGuardar.isEnabled = true
                    Toast.makeText(
                        applicationContext,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } finally {
                conn?.disconnect()
            }
        }
    }

    private fun limpiarFormulario() {
        etRuc.text?.clear()
        etRazonSocial.text?.clear()
        etDireccion.text?.clear()
        etBuscarProducto.text?.clear()
        spinnerMoneda.setSelection(0)
        spinnerFormaPago.setSelection(0)
        productosAgregados.clear()
        productosAdapter.notifyDataSetChanged()
        tvSubtotal.text = "0.00"
        tvIgv.text = "0.00"
        tvTotal.text = "0.00"
    }

    private fun cerrarSesion() {
        val prefs = getSharedPreferences("datos_app", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        Toast.makeText(this, "Sesión expirada. Por favor, inicie sesión nuevamente.", Toast.LENGTH_LONG).show()
        finish()
    }
}

// DATA CLASS
data class ProductoVenta(
    val idProducto: Int,
    val codigo: String,
    val nombre: String,
    val precioUnitario: Double,
    var cantidad: Int
)

// ADAPTER
class ProductosVentaAdapter(
    private val productos: MutableList<ProductoVenta>,
    private val onUpdate: () -> Unit
) : RecyclerView.Adapter<ProductosVentaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvCodigo: TextView = view.findViewById(R.id.tvCodigo)
        val etCantidad: EditText = view.findViewById(R.id.etCantidad)
        val etPrecio: EditText = view.findViewById(R.id.etPrecio)
        val tvSubtotal: TextView = view.findViewById(R.id.tvSubtotal)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_venta, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = productos[position]

        holder.tvNombre.text = producto.nombre
        holder.tvCodigo.text = "Código: ${producto.codigo}"
        holder.etCantidad.setText(producto.cantidad.toString())
        holder.etPrecio.setText(String.format("%.2f", producto.precioUnitario))
        holder.tvSubtotal.text = String.format("%.2f", producto.precioUnitario * producto.cantidad)

        holder.etCantidad.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val cantidad = holder.etCantidad.text.toString().toIntOrNull() ?: 1
                if (cantidad > 0) {
                    producto.cantidad = cantidad
                    onUpdate()
                    notifyItemChanged(position)
                }
            }
        }

        holder.etPrecio.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val precio = holder.etPrecio.text.toString().toDoubleOrNull() ?: 0.0
                if (precio > 0) {
                    // Nota: ProductoVenta ya no tiene campo 'precio' mutable
                    // Si necesitas modificar el precio, debes ajustar la data class
                    onUpdate()
                    notifyItemChanged(position)
                }
            }
        }

        holder.btnEliminar.setOnClickListener {
            productos.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, productos.size)
            onUpdate()
        }
    }

    override fun getItemCount() = productos.size
}