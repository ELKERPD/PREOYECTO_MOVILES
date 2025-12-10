package com.example.moviles

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class AuditoriasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvMensaje: TextView
    private lateinit var btnVolver: Button
    private lateinit var btnAnterior: Button
    private lateinit var btnSiguiente: Button
    private lateinit var tvPaginacion: TextView
    private lateinit var tvTotalRegistros: TextView

    private var paginaActual = 1
    private var totalPaginas = 1
    private val auditoriasAdapter = AuditoriasAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auditorias)

        recyclerView = findViewById(R.id.recyclerAuditorias)
        progressBar = findViewById(R.id.progressBar)
        tvMensaje = findViewById(R.id.tvMensaje)
        btnVolver = findViewById(R.id.btnVolver)
        btnAnterior = findViewById(R.id.btnAnterior)
        btnSiguiente = findViewById(R.id.btnSiguiente)
        tvPaginacion = findViewById(R.id.tvPaginacion)
        tvTotalRegistros = findViewById(R.id.tvTotalRegistros)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = auditoriasAdapter

        btnVolver.setOnClickListener {
            finish()
        }

        btnAnterior.setOnClickListener {
            if (paginaActual > 1) {
                paginaActual--
                cargarAuditorias()
            }
        }

        btnSiguiente.setOnClickListener {
            if (paginaActual < totalPaginas) {
                paginaActual++
                cargarAuditorias()
            }
        }

        cargarAuditorias()
    }

    private fun cargarAuditorias() {
        progressBar.visibility = View.VISIBLE
        tvMensaje.visibility = View.GONE
        recyclerView.visibility = View.GONE

        val prefs = getSharedPreferences("datos_app", Context.MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""
        val idEmpresa = prefs.getInt("id_empresa", 0)

        if (token.isEmpty() || idEmpresa == 0) {
            mostrarError("Datos de sesión no encontrados")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null

            try {
                val url = URL("https://sercon-aje.com/API_APP_MOVIL/API_RES_TECNODESARROLLOPEREZ/auditoria_ceciones.php?token=$token&id_empresa=$idEmpresa&pagina=$paginaActual")
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 15000
                conn.readTimeout = 10000

                val responseCode = conn.responseCode
                val responseText = if (responseCode == HttpURLConnection.HTTP_OK) {
                    conn.inputStream.bufferedReader().use { it.readText() }
                } else {
                    conn.errorStream?.bufferedReader()?.use { it.readText() }
                        ?: "Error sin mensaje"
                }

                withContext(Dispatchers.Main) {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try {
                            val json = JSONObject(responseText)

                            if (json.getBoolean("success")) {
                                totalPaginas = json.getInt("total_paginas")
                                val totalRegistros = json.getInt("total_registros")
                                val dataArray = json.getJSONArray("data")

                                val lista = mutableListOf<AuditoriaItem>()
                                for (i in 0 until dataArray.length()) {
                                    val obj = dataArray.getJSONObject(i)
                                    lista.add(
                                        AuditoriaItem(
                                            id = obj.getInt("id_registro"),
                                            usuario = obj.getString("usuario"),
                                            cargo = obj.optString("cargo", "N/A"),
                                            fechaHora = obj.getString("fecha_hora"),
                                            ip = obj.optString("ip", "N/A"),
                                            exito = obj.getInt("exito") == 1,
                                            motivo = obj.optString("motivo", ""),
                                            detalle = obj.optString("detalle", "")
                                        )
                                    )
                                }

                                auditoriasAdapter.actualizar(lista)

                                tvPaginacion.text = "Página $paginaActual de $totalPaginas"
                                tvTotalRegistros.text = "Total: $totalRegistros registros"

                                btnAnterior.isEnabled = paginaActual > 1
                                btnSiguiente.isEnabled = paginaActual < totalPaginas

                                progressBar.visibility = View.GONE
                                recyclerView.visibility = View.VISIBLE

                            } else {
                                mostrarError(json.optString("mensaje", "Error desconocido"))
                            }

                        } catch (e: Exception) {
                            mostrarError("Error al procesar datos: ${e.message}")
                        }
                    } else {
                        mostrarError("Error del servidor: $responseCode")
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarError("Error de conexión: ${e.message}")
                }
            } finally {
                conn?.disconnect()
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        tvMensaje.visibility = View.VISIBLE
        tvMensaje.text = mensaje
    }
}

// ==============================================
// DATA CLASS
// ==============================================
data class AuditoriaItem(
    val id: Int,
    val usuario: String,
    val cargo: String,
    val fechaHora: String,
    val ip: String,
    val exito: Boolean,
    val motivo: String,
    val detalle: String
)

// ==============================================
// ADAPTER
// ==============================================
class AuditoriasAdapter : RecyclerView.Adapter<AuditoriasAdapter.ViewHolder>() {

    private var lista = mutableListOf<AuditoriaItem>()

    fun actualizar(nuevaLista: List<AuditoriaItem>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_auditoria, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount() = lista.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvUsuario: TextView = view.findViewById(R.id.tvUsuario)
        private val tvCargo: TextView = view.findViewById(R.id.tvCargo)
        private val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        private val tvIp: TextView = view.findViewById(R.id.tvIp)
        private val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        private val tvMotivo: TextView = view.findViewById(R.id.tvMotivo)
        private val viewIndicador: View = view.findViewById(R.id.viewIndicador)

        fun bind(item: AuditoriaItem) {
            tvUsuario.text = item.usuario
            tvCargo.text = item.cargo
            tvFecha.text = item.fechaHora
            tvIp.text = "IP: ${item.ip}"

            if (item.exito) {
                tvEstado.text = "✓ EXITOSO"
                tvEstado.setTextColor(0xFF2ECC71.toInt())
                viewIndicador.setBackgroundColor(0xFF2ECC71.toInt())
                tvMotivo.visibility = View.GONE
            } else {
                tvEstado.text = "✗ FALLIDO"
                tvEstado.setTextColor(0xFFE74C3C.toInt())
                viewIndicador.setBackgroundColor(0xFFE74C3C.toInt())
                tvMotivo.visibility = View.VISIBLE
                tvMotivo.text = "Motivo: ${item.motivo}"
            }
        }
    }
}