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
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import android.content.Intent
import android.net.Uri

class InventarioCotizacionesActivity : AppCompatActivity() {

    // Views
    private lateinit var btnVolver: Button
    private lateinit var etBuscar: EditText
    private lateinit var btnBuscar: Button
    private lateinit var spinnerEstado: Spinner
    private lateinit var etFechaDesde: EditText
    private lateinit var etFechaHasta: EditText
    private lateinit var btnFiltrar: Button
    private lateinit var btnLimpiarFiltros: Button
    private lateinit var rvCotizaciones: RecyclerView
    private lateinit var tvTotalRegistros: TextView
    private lateinit var tvPagina: TextView
    private lateinit var btnAnterior: Button
    private lateinit var btnSiguiente: Button
    private lateinit var progressBar: ProgressBar

    // Estadísticas
    private lateinit var tvEstadPendiente: TextView
    private lateinit var tvEstadEnviada: TextView
    private lateinit var tvEstadAprobada: TextView
    private lateinit var tvEstadVenta: TextView
    private lateinit var tvEstadAnulada: TextView

    // Data
    private val listaCotizaciones = mutableListOf<CotizacionItem>()
    private lateinit var cotizacionesAdapter: CotizacionesAdapter

    // Datos de sesión
    private var idEmpresa: Int = 0
    private var idUsuario: Int = 0
    private var token: String = ""

    // Paginación
    private var paginaActual: Int = 1
    private var totalPaginas: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inventario_cotizaciones_activity)

        cargarDatosSesion()
        inicializarVistas()
        configurarSpinnerEstado()
        configurarRecyclerView()
        configurarEventos()
        cargarCotizaciones()
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
        btnLimpiarFiltros = findViewById(R.id.btnLimpiarFiltros)
        rvCotizaciones = findViewById(R.id.rvCotizaciones)
        tvTotalRegistros = findViewById(R.id.tvTotalRegistros)
        tvPagina = findViewById(R.id.tvPagina)
        btnAnterior = findViewById(R.id.btnAnterior)
        btnSiguiente = findViewById(R.id.btnSiguiente)
        progressBar = findViewById(R.id.progressBar)

        tvEstadPendiente = findViewById(R.id.tvEstadPendiente)
        tvEstadEnviada = findViewById(R.id.tvEstadEnviada)
        tvEstadAprobada = findViewById(R.id.tvEstadAprobada)
        tvEstadVenta = findViewById(R.id.tvEstadVenta)
        tvEstadAnulada = findViewById(R.id.tvEstadAnulada)
    }

    private fun configurarSpinnerEstado() {
        val estados = arrayOf(
            "TODOS",
            "PENDIENTE",
            "ENVIADA",
            "APROBADA",
            "VENTA",
            "ANULADA"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstado.adapter = adapter
    }

    private fun configurarRecyclerView() {
        cotizacionesAdapter = CotizacionesAdapter(
            listaCotizaciones,
            onVerPdf = { idCotizacion ->
                abrirPdf(idCotizacion)
            },
            onCambiarEstado = { cotizacion ->
                mostrarDialogoCambiarEstado(cotizacion)
            }
        )
        rvCotizaciones.apply {
            layoutManager = LinearLayoutManager(this@InventarioCotizacionesActivity)
            adapter = cotizacionesAdapter
        }
    }

    private fun configurarEventos() {
        btnVolver.setOnClickListener { finish() }

        btnBuscar.setOnClickListener {
            paginaActual = 1
            cargarCotizaciones()
        }

        btnFiltrar.setOnClickListener {
            paginaActual = 1
            cargarCotizaciones()
        }

        btnLimpiarFiltros.setOnClickListener {
            limpiarFiltros()
        }

        btnAnterior.setOnClickListener {
            if (paginaActual > 1) {
                paginaActual--
                cargarCotizaciones()
            }
        }

        btnSiguiente.setOnClickListener {
            if (paginaActual < totalPaginas) {
                paginaActual++
                cargarCotizaciones()
            }
        }
    }

    private fun limpiarFiltros() {
        etBuscar.text?.clear()
        spinnerEstado.setSelection(0)
        etFechaDesde.text?.clear()
        etFechaHasta.text?.clear()
        paginaActual = 1
        cargarCotizaciones()
    }

    private fun cargarCotizaciones() {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null
            try {
                // Construir URL con parámetros
                val buscar = etBuscar.text.toString().trim()
                val estadoSeleccionado = spinnerEstado.selectedItem.toString()
                val estado = if (estadoSeleccionado == "TODOS") "" else estadoSeleccionado
                val fechaDesde = etFechaDesde.text.toString().trim()
                val fechaHasta = etFechaHasta.text.toString().trim()

                val urlString = buildString {
                    append("https://sercon-aje.com/API_APP_MOVIL/API_RES_TECNODESARROLLOPEREZ/inventario_cotizaciones.php")
                    append("?token=$token")
                    append("&id_empresa=$idEmpresa")
                    append("&page=$paginaActual")
                    if (buscar.isNotEmpty()) append("&buscar=$buscar")
                    if (estado.isNotEmpty()) append("&estado=$estado")
                    if (fechaDesde.isNotEmpty()) append("&fecha_desde=$fechaDesde")
                    if (fechaHasta.isNotEmpty()) append("&fecha_hasta=$fechaHasta")
                }

                val url = URL(urlString)
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"

                val response = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(response)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (json.getBoolean("success")) {
                        // Limpiar lista
                        listaCotizaciones.clear()

                        // Procesar cotizaciones
                        val cotizacionesArray = json.getJSONArray("cotizaciones")
                        for (i in 0 until cotizacionesArray.length()) {
                            val cotJson = cotizacionesArray.getJSONObject(i)
                            listaCotizaciones.add(
                                CotizacionItem(
                                    idCotizacion = cotJson.getInt("id_cotizacion"),
                                    numeroCotizacion = cotJson.getString("numero_cotizacion"),
                                    rucCliente = cotJson.getString("ruc_cliente"),
                                    razonSocialCliente = cotJson.getString("razon_social_cliente"),
                                    fechaEmision = cotJson.getString("fecha_emision"),
                                    fechaVencimiento = cotJson.getString("fecha_vencimiento"),
                                    moneda = cotJson.getString("moneda"),
                                    subtotal = cotJson.getDouble("subtotal"),
                                    igv = cotJson.getDouble("igv"),
                                    total = cotJson.getDouble("total"),
                                    estado = cotJson.getString("estado"),
                                    formaPago = cotJson.getString("forma_pago"),
                                    usuarioCreador = cotJson.optString("usuario_creador", "N/A")
                                )
                            )
                        }

                        cotizacionesAdapter.notifyDataSetChanged()

                        // Actualizar paginación
                        val totalRegistros = json.getInt("total_registros")
                        totalPaginas = json.getInt("total_paginas")
                        paginaActual = json.getInt("pagina_actual")

                        tvTotalRegistros.text = "Total: $totalRegistros registros"
                        tvPagina.text = "Página $paginaActual de $totalPaginas"

                        btnAnterior.isEnabled = paginaActual > 1
                        btnSiguiente.isEnabled = paginaActual < totalPaginas

                        // Actualizar estadísticas
                        actualizarEstadisticas(json.getJSONObject("estadisticas"))

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
                    Toast.makeText(
                        applicationContext,
                        "Error al cargar cotizaciones: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } finally {
                conn?.disconnect()
            }
        }
    }

    private fun actualizarEstadisticas(estadisticas: JSONObject) {
        fun obtenerEstadistica(estado: String): String {
            return if (estadisticas.has(estado)) {
                val obj = estadisticas.getJSONObject(estado)
                val cantidad = obj.getInt("cantidad")
                val monto = obj.getDouble("monto")
                "$cantidad (S/ ${String.format("%.2f", monto)})"
            } else {
                "0 (S/ 0.00)"
            }
        }

        tvEstadPendiente.text = obtenerEstadistica("PENDIENTE")
        tvEstadEnviada.text = obtenerEstadistica("ENVIADA")
        tvEstadAprobada.text = obtenerEstadistica("APROBADA")
        tvEstadVenta.text = obtenerEstadistica("VENTA")
        tvEstadAnulada.text = obtenerEstadistica("ANULADA")
    }

    private fun abrirPdf(idCotizacion: Int) {
        val pdfUrl = "https://sercon-aje.com/PROYECTO_GRUPO_11/CONTROLADOR/boleta_movil.php?id=$idCotizacion"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(pdfUrl)
        startActivity(intent)
    }

    private fun mostrarDialogoCambiarEstado(cotizacion: CotizacionItem) {
        val estados = arrayOf("PENDIENTE", "ENVIADA", "APROBADA", "VENTA", "ANULADA")
        val estadoActualIndex = estados.indexOf(cotizacion.estado)

        AlertDialog.Builder(this)
            .setTitle("Cambiar estado")
            .setSingleChoiceItems(estados, estadoActualIndex) { dialog, which ->
                val nuevoEstado = estados[which]
                dialog.dismiss()
                cambiarEstadoCotizacion(cotizacion.idCotizacion, nuevoEstado)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cambiarEstadoCotizacion(idCotizacion: Int, nuevoEstado: String) {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null
            try {
                val url = URL("https://sercon-aje.com/API_APP_MOVIL/API_RES_TECNODESARROLLOPEREZ/inventario_cotizaciones.php")
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                conn.doOutput = true

                val postData = buildString {
                    append("token=$token")
                    append("&id_empresa=$idEmpresa")
                    append("&id_usuario=$idUsuario")
                    append("&id_cotizacion=$idCotizacion")
                    append("&estado=$nuevoEstado")
                }

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
                        cargarCotizaciones() // Recargar lista
                    } else {
                        val mensaje = json.optString("error", "No se pudo actualizar")
                        Toast.makeText(applicationContext, mensaje, Toast.LENGTH_SHORT).show()
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
        Toast.makeText(this, "Sesión expirada. Por favor, inicie sesión nuevamente.", Toast.LENGTH_LONG).show()
        finish()
    }
}

// DATA CLASS
data class CotizacionItem(
    val idCotizacion: Int,
    val numeroCotizacion: String,
    val rucCliente: String,
    val razonSocialCliente: String,
    val fechaEmision: String,
    val fechaVencimiento: String,
    val moneda: String,
    val subtotal: Double,
    val igv: Double,
    val total: Double,
    val estado: String,
    val formaPago: String,
    val usuarioCreador: String
)

// ADAPTER
class CotizacionesAdapter(
    private val cotizaciones: MutableList<CotizacionItem>,
    private val onVerPdf: (Int) -> Unit,
    private val onCambiarEstado: (CotizacionItem) -> Unit
) : RecyclerView.Adapter<CotizacionesAdapter.ViewHolder>() {

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
            .inflate(R.layout.item_cotizacion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cotizacion = cotizaciones[position]

        holder.tvNumero.text = cotizacion.numeroCotizacion
        holder.tvCliente.text = cotizacion.razonSocialCliente
        holder.tvRuc.text = "RUC: ${cotizacion.rucCliente}"
        holder.tvFecha.text = "Emisión: ${cotizacion.fechaEmision}"
        holder.tvTotal.text = "Total: ${cotizacion.moneda} ${String.format("%.2f", cotizacion.total)}"
        holder.tvEstado.text = cotizacion.estado

        // Color según estado
        val colorEstado = when (cotizacion.estado) {
            "PENDIENTE" -> 0xFFFFA500.toInt() // Naranja
            "ENVIADA" -> 0xFF0000FF.toInt()   // Azul
            "APROBADA" -> 0xFF00FF00.toInt()  // Verde
            "VENTA" -> 0xFF008000.toInt()     // Verde oscuro
            "ANULADA" -> 0xFFFF0000.toInt()   // Rojo
            else -> 0xFF808080.toInt()        // Gris
        }
        holder.tvEstado.setTextColor(colorEstado)

        holder.btnVerPdf.setOnClickListener {
            onVerPdf(cotizacion.idCotizacion)
        }

        holder.btnCambiarEstado.setOnClickListener {
            onCambiarEstado(cotizacion)
        }
    }

    override fun getItemCount() = cotizaciones.size
}