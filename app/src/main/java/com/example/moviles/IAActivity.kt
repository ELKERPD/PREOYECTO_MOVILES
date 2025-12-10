package com.example.moviles

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class IAActivity : AppCompatActivity() {

    // Views
    private lateinit var btnVolver: Button
    private lateinit var etPregunta: EditText
    private lateinit var btnConsultar: Button
    private lateinit var btnLimpiar: Button
    private lateinit var tvSQL: TextView
    private lateinit var scrollViewSQL: HorizontalScrollView
    private lateinit var layoutSQL: LinearLayout
    private lateinit var rvResultados: RecyclerView
    private lateinit var tvMensaje: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEjemplos: TextView

    // Data
    private val resultadosList = mutableListOf<Map<String, String>>()
    private lateinit var resultadosAdapter: ResultadosAdapter

    // Sesión
    private var token = ""
    private var idEmpresa = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ia)

        cargarDatosSesion()
        inicializarVistas()
        configurarRecyclerView()
        configurarEventos()
        mostrarEjemplos()
    }

    private fun cargarDatosSesion() {
        val prefs = getSharedPreferences("datos_app", Context.MODE_PRIVATE)
        token = prefs.getString("token", "") ?: ""
        idEmpresa = prefs.getInt("id_empresa", 0)
    }

    private fun inicializarVistas() {
        btnVolver = findViewById(R.id.btnVolver)
        etPregunta = findViewById(R.id.etPregunta)
        btnConsultar = findViewById(R.id.btnConsultar)
        btnLimpiar = findViewById(R.id.btnLimpiar)
        tvSQL = findViewById(R.id.tvSQL)
        scrollViewSQL = findViewById(R.id.scrollViewSQL)
        layoutSQL = findViewById(R.id.layoutSQL)
        rvResultados = findViewById(R.id.rvResultados)
        tvMensaje = findViewById(R.id.tvMensaje)
        progressBar = findViewById(R.id.progressBar)
        tvEjemplos = findViewById(R.id.tvEjemplos)
    }

    private fun configurarRecyclerView() {
        resultadosAdapter = ResultadosAdapter(resultadosList)
        rvResultados.apply {
            layoutManager = LinearLayoutManager(this@IAActivity)
            adapter = resultadosAdapter
        }
    }

    private fun configurarEventos() {
        btnVolver.setOnClickListener { finish() }
        btnConsultar.setOnClickListener { consultarIA() }
        btnLimpiar.setOnClickListener { limpiarResultados() }

        // Click en ejemplos para auto-completar
        tvEjemplos.setOnClickListener {
            mostrarDialogoEjemplos()
        }
    }

    private fun mostrarEjemplos() {
        val ejemplos = """
            📋 EJEMPLOS DE PREGUNTAS:
            
            • ¿Cuántos productos tengo?
            • Muéstrame los productos con stock bajo
            • ¿Cuáles son las ventas de hoy?
            • Total de ventas este mes
            • Productos más vendidos
            • Cotizaciones pendientes
            • Clientes con más ventas
            
            Toca aquí para ver más ejemplos
        """.trimIndent()

        tvEjemplos.text = ejemplos
    }

    private fun mostrarDialogoEjemplos() {
        val ejemplos = arrayOf(
            "¿Cuántos productos tengo?",
            "Muéstrame los productos con stock menor a 10",
            "¿Cuáles son las ventas de hoy?",
            "Total de ventas este mes",
            "Productos más vendidos en el último mes",
            "Cotizaciones pendientes",
            "Lista de clientes con más de 5 compras",
            "Productos sin stock",
            "Ventas por forma de pago",
            "Promedio de ventas diarias este mes"
        )

        android.app.AlertDialog.Builder(this)
            .setTitle("Selecciona un ejemplo")
            .setItems(ejemplos) { _, which ->
                etPregunta.setText(ejemplos[which])
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun limpiarResultados() {
        etPregunta.text?.clear()
        tvSQL.text = ""
        layoutSQL.visibility = View.GONE
        resultadosList.clear()
        resultadosAdapter.notifyDataSetChanged()
        tvMensaje.visibility = View.GONE
        rvResultados.visibility = View.GONE
    }

    private fun consultarIA() {
        val pregunta = etPregunta.text.toString().trim()

        if (pregunta.isEmpty()) {
            Toast.makeText(this, "Escribe una pregunta", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE
        btnConsultar.isEnabled = false
        layoutSQL.visibility = View.GONE
        rvResultados.visibility = View.GONE
        tvMensaje.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null
            try {
                val url = URL("https://sercon-aje.com/API_APP_MOVIL/API_RES_TECNODESARROLLOPEREZ/ia.php" +
                        "?token=$token&id_empresa=$idEmpresa")

                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.connectTimeout = 30000 // 30 segundos para IA
                conn.readTimeout = 30000
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                val postData = "pregunta=${URLEncoder.encode(pregunta, "UTF-8")}"
                conn.outputStream.use { it.write(postData.toByteArray()) }

                val responseCode = conn.responseCode
                val response = if (responseCode == HttpURLConnection.HTTP_OK) {
                    conn.inputStream.bufferedReader().readText()
                } else {
                    conn.errorStream?.bufferedReader()?.readText() ?: "Error desconocido"
                }

                val json = JSONObject(response)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnConsultar.isEnabled = true

                    if (json.getBoolean("success")) {
                        // Mostrar SQL generado
                        val sqlGenerado = json.optString("sql", "")
                        if (sqlGenerado.isNotEmpty()) {
                            tvSQL.text = sqlGenerado
                            layoutSQL.visibility = View.VISIBLE
                        }

                        // Procesar resultados
                        val resultadosArray = json.optJSONArray("resultado")

                        if (resultadosArray != null && resultadosArray.length() > 0) {
                            resultadosList.clear()

                            for (i in 0 until resultadosArray.length()) {
                                val obj = resultadosArray.getJSONObject(i)
                                val mapa = mutableMapOf<String, String>()

                                val keys = obj.keys()
                                while (keys.hasNext()) {
                                    val key = keys.next()
                                    mapa[key] = obj.optString(key, "")
                                }

                                resultadosList.add(mapa)
                            }

                            resultadosAdapter.notifyDataSetChanged()
                            rvResultados.visibility = View.VISIBLE

                            tvMensaje.text = "✓ ${resultadosList.size} resultado(s) encontrado(s)"
                            tvMensaje.setTextColor(0xFF28A745.toInt())
                            tvMensaje.visibility = View.VISIBLE

                        } else {
                            tvMensaje.text = "⚠ No se encontraron resultados"
                            tvMensaje.setTextColor(0xFFFFA500.toInt())
                            tvMensaje.visibility = View.VISIBLE
                            rvResultados.visibility = View.GONE
                        }

                    } else {
                        val mensaje = json.optString("mensaje", "Error desconocido")
                        tvMensaje.text = "✗ Error: $mensaje"
                        tvMensaje.setTextColor(0xFFDC3545.toInt())
                        tvMensaje.visibility = View.VISIBLE

                        // Mostrar SQL si existe (para debugging)
                        val sqlError = json.optString("sql", "")
                        if (sqlError.isNotEmpty()) {
                            tvSQL.text = sqlError
                            layoutSQL.visibility = View.VISIBLE
                        }

                        // Mostrar error de SQL si existe
                        val errorSQL = json.optString("error", "")
                        if (errorSQL.isNotEmpty()) {
                            Toast.makeText(
                                applicationContext,
                                "Error SQL: $errorSQL",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        if (mensaje.contains("Token", ignoreCase = true) ||
                            mensaje.contains("expirado", ignoreCase = true)) {
                            cerrarSesion()
                        }
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnConsultar.isEnabled = true

                    tvMensaje.text = "✗ Error de conexión: ${e.message}"
                    tvMensaje.setTextColor(0xFFDC3545.toInt())
                    tvMensaje.visibility = View.VISIBLE

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

// ADAPTER PARA RESULTADOS DINÁMICOS
class ResultadosAdapter(
    private val resultados: List<Map<String, String>>
) : RecyclerView.Adapter<ResultadosAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: LinearLayout = view.findViewById(R.id.layoutResultado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_resultado_ia, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resultado = resultados[position]
        holder.layout.removeAllViews()

        resultado.forEach { (key, value) ->
            // Crear TextView para cada campo
            val textView = TextView(holder.itemView.context).apply {
                text = "$key: $value"
                textSize = 14f
                setTextColor(0xFF212529.toInt())
                setPadding(0, 4, 0, 4)
            }
            holder.layout.addView(textView)
        }
    }

    override fun getItemCount() = resultados.size
}