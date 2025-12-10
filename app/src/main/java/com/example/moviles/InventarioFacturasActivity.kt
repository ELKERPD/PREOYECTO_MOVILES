package com.example.moviles

import android.content.Context
import android.content.Intent
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
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class InventarioFacturasActivity : AppCompatActivity() {

    // Views
    private lateinit var btnVolver: Button
    private lateinit var etBuscar: EditText
    private lateinit var btnBuscar: Button
    private lateinit var spinnerEstado: Spinner
    private lateinit var etFechaDesde: EditText
    private lateinit var etFechaHasta: EditText
    private lateinit var btnFiltrar: Button
    private lateinit var btnLimpiar: Button
    private lateinit var rvFacturas: RecyclerView
    private lateinit var tvPendientes: TextView
    private lateinit var tvGeneradas: TextView
    private lateinit var tvEnviadas: TextView
    private lateinit var tvAceptadas: TextView
    private lateinit var tvAnuladas: TextView
    private lateinit var tvRechazadas: TextView
    private lateinit var tvTotalPendiente: TextView
    private lateinit var tvTotalGenerada: TextView
    private lateinit var tvTotalEnviada: TextView
    private lateinit var tvTotalAceptada: TextView
    private lateinit var tvTotalAnulada: TextView
    private lateinit var tvTotalRechazada: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnAnterior: Button
    private lateinit var btnSiguiente: Button
    private lateinit var tvPaginaActual: TextView

    // Data
    private val facturasList = mutableListOf<Factura>()
    private lateinit var facturasAdapter: FacturasAdapter
    private var paginaActual = 1
    private var totalPaginas = 1

    // Sesión
    private var token = ""
    private var idEmpresa = 0
    private var idUsuario = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario_facturas)

        cargarDatosSesion()
        inicializarVistas()
        configurarRecyclerView()
        configurarEventos()
        cargarFacturas()
    }

    private fun cargarDatosSesion() {
        val prefs = getSharedPreferences("datos_app", Context.MODE_PRIVATE)
        token = prefs.getString("token", "") ?: ""
        idEmpresa = prefs.getInt("id_empresa", 0)
        idUsuario = prefs.getInt("id_usuario", 0)
    }

    private fun inicializarVistas() {
        btnVolver = findViewById(R.id.btnVolver)
        etBuscar = findViewById(R.id.etBuscar)
        btnBuscar = findViewById(R.id.btnBuscar)
        spinnerEstado = findViewById(R.id.spinnerEstado)
        etFechaDesde = findViewById(R.id.etFechaDesde)
        etFechaHasta = findViewById(R.id.etFechaHasta)
        btnFiltrar = findViewById(R.id.btnFiltrar)
        btnLimpiar = findViewById(R.id.btnLimpiar)
        rvFacturas = findViewById(R.id.rvFacturas)
        tvPendientes = findViewById(R.id.tvPendientes)
        tvGeneradas = findViewById(R.id.tvGeneradas)
        tvEnviadas = findViewById(R.id.tvEnviadas)
        tvAceptadas = findViewById(R.id.tvAceptadas)
        tvAnuladas = findViewById(R.id.tvAnuladas)
        tvRechazadas = findViewById(R.id.tvRechazadas)
        tvTotalPendiente = findViewById(R.id.tvTotalPendiente)
        tvTotalGenerada = findViewById(R.id.tvTotalGenerada)
        tvTotalEnviada = findViewById(R.id.tvTotalEnviada)
        tvTotalAceptada = findViewById(R.id.tvTotalAceptada)
        tvTotalAnulada = findViewById(R.id.tvTotalAnulada)
        tvTotalRechazada = findViewById(R.id.tvTotalRechazada)
        progressBar = findViewById(R.id.progressBar)
        btnAnterior = findViewById(R.id.btnAnterior)
        btnSiguiente = findViewById(R.id.btnSiguiente)
        tvPaginaActual = findViewById(R.id.tvPaginaActual)

        // Configurar Spinner
        val estados = arrayOf("Todos", "PENDIENTE", "GENERADA", "ENVIADA", "ACEPTADA", "ANULADA", "RECHAZADA")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstado.adapter = adapter
    }

    private fun configurarRecyclerView() {
        facturasAdapter = FacturasAdapter(
            facturas = facturasList,
            onVerPdf = { factura -> abrirPdfFactura(factura) },
            onCambiarEstado = { factura -> mostrarDialogoEstado(factura) }
        )
        rvFacturas.apply {
            layoutManager = LinearLayoutManager(this@InventarioFacturasActivity)
            adapter = facturasAdapter
        }
    }

    private fun configurarEventos() {
        btnVolver.setOnClickListener { finish() }
        btnBuscar.setOnClickListener { buscarFacturas() }
        btnFiltrar.setOnClickListener { buscarFacturas() }
        btnLimpiar.setOnClickListener { limpiarFiltros() }
        btnAnterior.setOnClickListener {
            if (paginaActual > 1) {
                paginaActual--
                cargarFacturas()
            }
        }
        btnSiguiente.setOnClickListener {
            if (paginaActual < totalPaginas) {
                paginaActual++
                cargarFacturas()
            }
        }
    }

    private fun buscarFacturas() {
        paginaActual = 1
        cargarFacturas()
    }

    private fun limpiarFiltros() {
        etBuscar.text?.clear()
        spinnerEstado.setSelection(0)
        etFechaDesde.text?.clear()
        etFechaHasta.text?.clear()
        paginaActual = 1
        cargarFacturas()
    }

    private fun cargarFacturas() {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null
            try {
                // Construir URL con parámetros
                val urlBuilder = StringBuilder()
                urlBuilder.append("https://sercon-aje.com/API_APP_MOVIL/API_RES_TECNODESARROLLOPEREZ/inventario_factura.php")
                urlBuilder.append("?token=$token")
                urlBuilder.append("&id_empresa=$idEmpresa")
                urlBuilder.append("&page=$paginaActual")

                // Filtros opcionales
                val buscar = etBuscar.text.toString().trim()
                if (buscar.isNotEmpty()) {
                    urlBuilder.append("&buscar=${URLEncoder.encode(buscar, "UTF-8")}")
                }

                val estadoSeleccionado = spinnerEstado.selectedItem.toString()
                if (estadoSeleccionado != "Todos") {
                    urlBuilder.append("&estado=$estadoSeleccionado")
                }

                val fechaDesde = etFechaDesde.text.toString().trim()
                if (fechaDesde.isNotEmpty()) {
                    urlBuilder.append("&fecha_desde=$fechaDesde")
                }

                val fechaHasta = etFechaHasta.text.toString().trim()
                if (fechaHasta.isNotEmpty()) {
                    urlBuilder.append("&fecha_hasta=$fechaHasta")
                }

                val url = URL(urlBuilder.toString())
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 10000
                conn.readTimeout = 10000

                val responseCode = conn.responseCode
                val response = if (responseCode == HttpURLConnection.HTTP_OK) {
                    conn.inputStream.bufferedReader().readText()
                } else {
                    conn.errorStream?.bufferedReader()?.readText() ?: "Error"
                }

                val json = JSONObject(response)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (json.getBoolean("success")) {
                        // Procesar facturas
                        val facturasArray = json.getJSONArray("facturas")
                        facturasList.clear()

                        for (i in 0 until facturasArray.length()) {
                            val f = facturasArray.getJSONObject(i)
                            facturasList.add(
                                Factura(
                                    idFactura = f.getInt("id_factura"),
                                    numeroFactura = f.getString("numero_factura"),
                                    rucCliente = f.optString("ruc_cliente", ""),
                                    razonSocial = f.getString("razon_social_cliente"),
                                    fechaEmision = f.getString("fecha_emision"),
                                    fechaVencimiento = f.optString("fecha_vencimiento", ""),
                                    moneda = f.getString("moneda"),
                                    subtotal = f.getDouble("subtotal"),
                                    descuento = f.optDouble("descuento_total", 0.0),
                                    igv = f.getDouble("igv"),
                                    total = f.getDouble("total"),
                                    estado = f.getString("estado"),
                                    formaPago = f.getString("forma_pago"),
                                    usuarioCreador = f.optString("usuario_creador", "")
                                )
                            )
                        }

                        facturasAdapter.notifyDataSetChanged()

                        // Actualizar paginación
                        totalPaginas = json.getInt("total_paginas")
                        paginaActual = json.getInt("pagina_actual")
                        actualizarPaginacion()

                        // Actualizar estadísticas
                        if (json.has("estadisticas")) {
                            actualizarEstadisticas(json.getJSONObject("estadisticas"))
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

    private fun actualizarPaginacion() {
        tvPaginaActual.text = "Página $paginaActual de $totalPaginas"
        btnAnterior.isEnabled = paginaActual > 1
        btnSiguiente.isEnabled = paginaActual < totalPaginas
    }

    private fun actualizarEstadisticas(stats: JSONObject) {
        // PENDIENTE
        if (stats.has("PENDIENTE")) {
            val pendiente = stats.getJSONObject("PENDIENTE")
            tvPendientes.text = pendiente.getInt("cantidad").toString()
            tvTotalPendiente.text = String.format("S/ %.2f", pendiente.getDouble("monto"))
        } else {
            tvPendientes.text = "0"
            tvTotalPendiente.text = "S/ 0.00"
        }

        // GENERADA
        if (stats.has("GENERADA")) {
            val generada = stats.getJSONObject("GENERADA")
            tvGeneradas.text = generada.getInt("cantidad").toString()
            tvTotalGenerada.text = String.format("S/ %.2f", generada.getDouble("monto"))
        } else {
            tvGeneradas.text = "0"
            tvTotalGenerada.text = "S/ 0.00"
        }

        // ENVIADA
        if (stats.has("ENVIADA")) {
            val enviada = stats.getJSONObject("ENVIADA")
            tvEnviadas.text = enviada.getInt("cantidad").toString()
            tvTotalEnviada.text = String.format("S/ %.2f", enviada.getDouble("monto"))
        } else {
            tvEnviadas.text = "0"
            tvTotalEnviada.text = "S/ 0.00"
        }

        // ACEPTADA
        if (stats.has("ACEPTADA")) {
            val aceptada = stats.getJSONObject("ACEPTADA")
            tvAceptadas.text = aceptada.getInt("cantidad").toString()
            tvTotalAceptada.text = String.format("S/ %.2f", aceptada.getDouble("monto"))
        } else {
            tvAceptadas.text = "0"
            tvTotalAceptada.text = "S/ 0.00"
        }

        // ANULADA
        if (stats.has("ANULADA")) {
            val anulada = stats.getJSONObject("ANULADA")
            tvAnuladas.text = anulada.getInt("cantidad").toString()
            tvTotalAnulada.text = String.format("S/ %.2f", anulada.getDouble("monto"))
        } else {
            tvAnuladas.text = "0"
            tvTotalAnulada.text = "S/ 0.00"
        }

        // RECHAZADA
        if (stats.has("RECHAZADA")) {
            val rechazada = stats.getJSONObject("RECHAZADA")
            tvRechazadas.text = rechazada.getInt("cantidad").toString()
            tvTotalRechazada.text = String.format("S/ %.2f", rechazada.getDouble("monto"))
        } else {
            tvRechazadas.text = "0"
            tvTotalRechazada.text = "S/ 0.00"
        }
    }

    private fun abrirPdfFactura(factura: Factura) {
        // 🔥 IMPORTANTE: El PHP requiere id y numero como parámetros GET
        val idEncoded = URLEncoder.encode(factura.idFactura.toString(), "UTF-8")
        val numeroEncoded = URLEncoder.encode(factura.numeroFactura, "UTF-8")

        val pdfUrl = "https://sercon-aje.com/PROYECTO_GRUPO_11/CONTROLADOR/factura_movil.php?id=$idEncoded&numero=$numeroEncoded"

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = android.net.Uri.parse(pdfUrl)
        startActivity(intent)
    }

    private fun mostrarDialogoEstado(factura: Factura) {
        val estados = arrayOf("PENDIENTE", "GENERADA", "ENVIADA", "ACEPTADA", "ANULADA", "RECHAZADA")

        AlertDialog.Builder(this)
            .setTitle("Cambiar estado de factura")
            .setItems(estados) { _, which ->
                val nuevoEstado = estados[which]
                cambiarEstado(factura.idFactura, nuevoEstado)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cambiarEstado(idFactura: Int, nuevoEstado: String) {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null
            try {
                val url = URL("https://sercon-aje.com/API_APP_MOVIL/API_RES_TECNODESARROLLOPEREZ/inventario_factura.php" +
                        "?token=$token&id_empresa=$idEmpresa")

                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                val postData = "id_factura=$idFactura&estado=$nuevoEstado"
                conn.outputStream.use { it.write(postData.toByteArray()) }

                val response = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(response)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (json.getBoolean("success")) {
                        Toast.makeText(
                            applicationContext,
                            "Estado actualizado a: $nuevoEstado",
                            Toast.LENGTH_SHORT
                        ).show()
                        cargarFacturas() // Recargar lista
                    } else {
                        val error = json.optString("error", "Error al actualizar")
                        Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
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

    private fun cerrarSesion() {
        val prefs = getSharedPreferences("datos_app", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        Toast.makeText(this, "Sesión expirada", Toast.LENGTH_LONG).show()
        finish()
    }
}

// DATA CLASS
data class Factura(
    val idFactura: Int,
    val numeroFactura: String,
    val rucCliente: String,
    val razonSocial: String,
    val fechaEmision: String,
    val fechaVencimiento: String,
    val moneda: String,
    val subtotal: Double,
    val descuento: Double,
    val igv: Double,
    val total: Double,
    val estado: String,
    val formaPago: String,
    val usuarioCreador: String
)

// ADAPTER
class FacturasAdapter(
    private val facturas: List<Factura>,
    private val onVerPdf: (Factura) -> Unit,
    private val onCambiarEstado: (Factura) -> Unit
) : RecyclerView.Adapter<FacturasAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNumero: TextView = view.findViewById(R.id.tvNumero)
        val tvCliente: TextView = view.findViewById(R.id.tvCliente)
        val tvRuc: TextView = view.findViewById(R.id.tvRuc)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvTotal: TextView = view.findViewById(R.id.tvTotal)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val btnVerPdf: Button = view.findViewById(R.id.btnVerPdf)
        val btnCambiarEstado: Button = view.findViewById(R.id.btnCambiarEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_factura, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val factura = facturas[position]

        holder.tvNumero.text = factura.numeroFactura
        holder.tvCliente.text = factura.razonSocial
        holder.tvRuc.text = "RUC: ${factura.rucCliente}"
        holder.tvFecha.text = factura.fechaEmision
        holder.tvTotal.text = String.format("%s %.2f", factura.moneda, factura.total)
        holder.tvEstado.text = factura.estado

        // Color según estado
        when (factura.estado) {
            "PENDIENTE" -> holder.tvEstado.setTextColor(0xFFFFA500.toInt()) // Naranja
            "GENERADA" -> holder.tvEstado.setTextColor(0xFF17A2B8.toInt()) // Cian
            "ENVIADA" -> holder.tvEstado.setTextColor(0xFF007BFF.toInt()) // Azul
            "ACEPTADA" -> holder.tvEstado.setTextColor(0xFF28A745.toInt()) // Verde
            "ANULADA" -> holder.tvEstado.setTextColor(0xFFDC3545.toInt()) // Rojo
            "RECHAZADA" -> holder.tvEstado.setTextColor(0xFF6C757D.toInt()) // Gris
        }

        holder.btnVerPdf.setOnClickListener { onVerPdf(factura) }
        holder.btnCambiarEstado.setOnClickListener { onCambiarEstado(factura) }
    }

    override fun getItemCount() = facturas.size
}