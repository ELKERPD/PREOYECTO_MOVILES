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

class InventarioVentasActivity : AppCompatActivity() {

    // Views
    private lateinit var btnVolver: Button
    private lateinit var etBuscar: EditText
    private lateinit var btnBuscar: Button
    private lateinit var spinnerEstado: Spinner
    private lateinit var etFechaDesde: EditText
    private lateinit var etFechaHasta: EditText
    private lateinit var btnFiltrar: Button
    private lateinit var btnLimpiar: Button
    private lateinit var rvVentas: RecyclerView
    private lateinit var tvPendientes: TextView
    private lateinit var tvPagadas: TextView
    private lateinit var tvAnuladas: TextView
    private lateinit var tvFacturadas: TextView
    private lateinit var tvTotalPendiente: TextView
    private lateinit var tvTotalPagado: TextView
    private lateinit var tvTotalAnulado: TextView
    private lateinit var tvTotalFacturado: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnAnterior: Button
    private lateinit var btnSiguiente: Button
    private lateinit var tvPaginaActual: TextView

    // Data
    private val ventasList = mutableListOf<Venta>()
    private lateinit var ventasAdapter: VentasAdapter
    private var paginaActual = 1
    private var totalPaginas = 1

    // Sesión
    private var token = ""
    private var idEmpresa = 0
    private var idUsuario = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario_ventas)

        cargarDatosSesion()
        inicializarVistas()
        configurarRecyclerView()
        configurarEventos()
        cargarVentas()
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
        rvVentas = findViewById(R.id.rvVentas)
        tvPendientes = findViewById(R.id.tvPendientes)
        tvPagadas = findViewById(R.id.tvPagadas)
        tvAnuladas = findViewById(R.id.tvAnuladas)
        tvFacturadas = findViewById(R.id.tvFacturadas)
        tvTotalPendiente = findViewById(R.id.tvTotalPendiente)
        tvTotalPagado = findViewById(R.id.tvTotalPagado)
        tvTotalAnulado = findViewById(R.id.tvTotalAnulado)
        tvTotalFacturado = findViewById(R.id.tvTotalFacturado)
        progressBar = findViewById(R.id.progressBar)
        btnAnterior = findViewById(R.id.btnAnterior)
        btnSiguiente = findViewById(R.id.btnSiguiente)
        tvPaginaActual = findViewById(R.id.tvPaginaActual)

        // Configurar Spinner
        val estados = arrayOf("Todos", "PENDIENTE", "PAGADA", "ANULADA", "FACTURADA")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstado.adapter = adapter
    }

    private fun configurarRecyclerView() {
        ventasAdapter = VentasAdapter(
            ventas = ventasList,
            onVerPdf = { venta -> abrirPdf(venta.idVenta) },
            onCambiarEstado = { venta -> mostrarDialogoEstado(venta) }
        )
        rvVentas.apply {
            layoutManager = LinearLayoutManager(this@InventarioVentasActivity)
            adapter = ventasAdapter
        }
    }

    private fun configurarEventos() {
        btnVolver.setOnClickListener { finish() }
        btnBuscar.setOnClickListener { buscarVentas() }
        btnFiltrar.setOnClickListener { buscarVentas() }
        btnLimpiar.setOnClickListener { limpiarFiltros() }
        btnAnterior.setOnClickListener {
            if (paginaActual > 1) {
                paginaActual--
                cargarVentas()
            }
        }
        btnSiguiente.setOnClickListener {
            if (paginaActual < totalPaginas) {
                paginaActual++
                cargarVentas()
            }
        }
    }

    private fun buscarVentas() {
        paginaActual = 1
        cargarVentas()
    }

    private fun limpiarFiltros() {
        etBuscar.text?.clear()
        spinnerEstado.setSelection(0)
        etFechaDesde.text?.clear()
        etFechaHasta.text?.clear()
        paginaActual = 1
        cargarVentas()
    }

    private fun cargarVentas() {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null
            try {
                // Construir URL con parámetros
                val urlBuilder = StringBuilder()
                urlBuilder.append("https://sercon-aje.com/API_APP_MOVIL/API_RES_TECNODESARROLLOPEREZ/inventario_ventas.php")
                urlBuilder.append("?token=$token")
                urlBuilder.append("&id_empresa=$idEmpresa")
                urlBuilder.append("&id_usuario=$idUsuario")
                urlBuilder.append("&page=$paginaActual")

                // Filtros opcionales
                val buscar = etBuscar.text.toString().trim()
                if (buscar.isNotEmpty()) {
                    urlBuilder.append("&buscar=$buscar")
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
                        // Procesar ventas
                        val ventasArray = json.getJSONArray("ventas")
                        ventasList.clear()

                        for (i in 0 until ventasArray.length()) {
                            val v = ventasArray.getJSONObject(i)
                            ventasList.add(
                                Venta(
                                    idVenta = v.getInt("id_venta"),
                                    numeroVenta = v.getString("numero_venta"),
                                    rucCliente = v.optString("ruc_cliente", ""),
                                    razonSocial = v.getString("razon_social_cliente"),
                                    fechaEmision = v.getString("fecha_emision"),
                                    fechaVencimiento = v.optString("fecha_vencimiento", ""),
                                    moneda = v.getString("moneda"),
                                    subtotal = v.getDouble("subtotal"),
                                    descuento = v.optDouble("descuento_total", 0.0),
                                    igv = v.getDouble("igv"),
                                    total = v.getDouble("total"),
                                    estado = v.getString("estado"),
                                    formaPago = v.getString("forma_pago"),
                                    usuarioCreador = v.optString("usuario_creador", "")
                                )
                            )
                        }

                        ventasAdapter.notifyDataSetChanged()

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

        // PAGADA
        if (stats.has("PAGADA")) {
            val pagada = stats.getJSONObject("PAGADA")
            tvPagadas.text = pagada.getInt("cantidad").toString()
            tvTotalPagado.text = String.format("S/ %.2f", pagada.getDouble("monto"))
        } else {
            tvPagadas.text = "0"
            tvTotalPagado.text = "S/ 0.00"
        }

        // ANULADA
        if (stats.has("ANULADA")) {
            val anulada = stats.getJSONObject("ANULADA")
            tvAnuladas.text = anulada.getInt("cantidad").toString()
            tvTotalAnulado.text = String.format("S/ %.2f", anulada.getDouble("monto"))
        } else {
            tvAnuladas.text = "0"
            tvTotalAnulado.text = "S/ 0.00"
        }

        // FACTURADA
        if (stats.has("FACTURADA")) {
            val facturada = stats.getJSONObject("FACTURADA")
            tvFacturadas.text = facturada.getInt("cantidad").toString()
            tvTotalFacturado.text = String.format("S/ %.2f", facturada.getDouble("monto"))
        } else {
            tvFacturadas.text = "0"
            tvTotalFacturado.text = "S/ 0.00"
        }
    }

    private fun abrirPdf(idVenta: Int) {
        val pdfUrl = "https://sercon-aje.com/PROYECTO_GRUPO_11/CONTROLADOR/boleta_vent_movil.php?id=$idVenta"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = android.net.Uri.parse(pdfUrl)
        startActivity(intent)
    }

    private fun mostrarDialogoEstado(venta: Venta) {
        val estados = arrayOf("PENDIENTE", "PAGADA", "ANULADA", "FACTURADA")

        AlertDialog.Builder(this)
            .setTitle("Cambiar estado de venta")
            .setItems(estados) { _, which ->
                val nuevoEstado = estados[which]
                cambiarEstado(venta.idVenta, nuevoEstado)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cambiarEstado(idVenta: Int, nuevoEstado: String) {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null
            try {
                val url = URL("https://sercon-aje.com/API_APP_MOVIL/API_RES_TECNODESARROLLOPEREZ/inventario_ventas.php" +
                        "?token=$token&id_empresa=$idEmpresa&id_usuario=$idUsuario")

                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                val postData = "id_venta=$idVenta&estado=$nuevoEstado"
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
                        cargarVentas() // Recargar lista
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
data class Venta(
    val idVenta: Int,
    val numeroVenta: String,
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
class VentasAdapter(
    private val ventas: List<Venta>,
    private val onVerPdf: (Venta) -> Unit,
    private val onCambiarEstado: (Venta) -> Unit
) : RecyclerView.Adapter<VentasAdapter.ViewHolder>() {

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
            .inflate(R.layout.item_venta, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val venta = ventas[position]

        holder.tvNumero.text = venta.numeroVenta
        holder.tvCliente.text = venta.razonSocial
        holder.tvRuc.text = "RUC: ${venta.rucCliente}"
        holder.tvFecha.text = venta.fechaEmision
        holder.tvTotal.text = String.format("%s %.2f", venta.moneda, venta.total)
        holder.tvEstado.text = venta.estado

        // Color según estado
        when (venta.estado) {
            "PENDIENTE" -> holder.tvEstado.setTextColor(0xFFFFA500.toInt()) // Naranja
            "PAGADA" -> holder.tvEstado.setTextColor(0xFF28A745.toInt()) // Verde
            "ANULADA" -> holder.tvEstado.setTextColor(0xFFDC3545.toInt()) // Rojo
            "FACTURADA" -> holder.tvEstado.setTextColor(0xFF007BFF.toInt()) // Azul
        }

        holder.btnVerPdf.setOnClickListener { onVerPdf(venta) }
        holder.btnCambiarEstado.setOnClickListener { onCambiarEstado(venta) }
    }

    override fun getItemCount() = ventas.size
}