package com.example.moviles

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class AuditoriasActivity : AppCompatActivity() {

    private lateinit var recyclerAuditorias: RecyclerView

    // 1. Crear la lista y el adaptador como propiedades de la clase
    private val listaAuditorias = mutableListOf<Auditoria>()
    private lateinit var adapter: AuditoriaAdapter

    private val apiUrl = "http://10.0.2.2/PROYECTO_ERP/API_RES_TECNODESARROLLOPEREZ/auditoria_ceciones.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auditorias)

        recyclerAuditorias = findViewById(R.id.recyclerAuditorias)
        recyclerAuditorias.layoutManager = LinearLayoutManager(this)

        // 2. Inicializar y configurar el adaptador UNA SOLA VEZ
        adapter = AuditoriaAdapter(listaAuditorias)
        recyclerAuditorias.adapter = adapter

        // 3. Cargar los datos
        obtenerAuditorias()
    }

    private fun obtenerAuditorias() {
        val token = getSharedPreferences("datos_app", Context.MODE_PRIVATE)
            .getString("token", null)

        if (token == null) {
            Toast.makeText(this, "Token no encontrado. Vuelva a iniciar sesión.", Toast.LENGTH_LONG).show()
            // Opcional: Redirigir a LoginActivity
            // startActivity(Intent(this, LoginActivity::class.java))
            // finish()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null
            try {
                conn = URL(apiUrl).openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Authorization", "Bearer $token")
                conn.setRequestProperty("Accept", "application/json")
                // Añadir timeouts
                conn.connectTimeout = 15000 // 15 segundos
                conn.readTimeout = 10000 // 10 segundos

                val responseCode = conn.responseCode

                // === ✅ CORRECCIÓN CLAVE: LEER EL STREAM ADECUADO ===
                val responseText: String
                if (responseCode == HttpURLConnection.HTTP_OK) { // Código 200
                    responseText = conn.inputStream.bufferedReader().use { it.readText() }
                } else {
                    // Leer del errorStream si el código no es 200 (ej. 401, 404, 500)
                    responseText = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "Error: Sin mensaje de respuesta"
                }

                withContext(Dispatchers.Main) {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try {
                            val json = JSONObject(responseText)

                            if (!json.getBoolean("success")) {
                                Toast.makeText(this@AuditoriasActivity, json.getString("mensaje"), Toast.LENGTH_LONG).show()
                                return@withContext
                            }

                            val data = json.getJSONArray("data")
                            listaAuditorias.clear() // Limpiar la lista actual

                            for (i in 0 until data.length()) {
                                val item = data.getJSONObject(i)
                                listaAuditorias.add(
                                    Auditoria(
                                        usuario = item.getString("usuario"),
                                        fecha = item.getString("fecha_hora"),
                                        ip = item.getString("ip")
                                    )
                                )
                            }

                            // 4. Notificar al adaptador que los datos cambiaron
                            adapter.notifyDataSetChanged()

                        } catch (e: Exception) {
                            Toast.makeText(this@AuditoriasActivity, "Error al procesar la respuesta: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }

                    } else {
                        // El API devolvió un error (401, 500, etc.)
                        // Intentamos mostrar el mensaje de error del API si existe
                        try {
                            val json = JSONObject(responseText)
                            val mensajeError = json.optString("mensaje", "Error $responseCode")
                            Toast.makeText(this@AuditoriasActivity, mensajeError, Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            // Si la respuesta de error no es JSON
                            Toast.makeText(this@AuditoriasActivity, "Error $responseCode: $responseText", Toast.LENGTH_LONG).show()
                        }
                    }
                }

            } catch (e: IOException) {
                // Error de red (sin conexión, timeout)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AuditoriasActivity, "Error de red: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                // Otro tipo de error
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AuditoriasActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            } finally {
                conn?.disconnect() // Asegurarse de cerrar la conexión
            }
        }
    }

    // ✅ MODELO
    data class Auditoria(
        val usuario: String,
        val fecha: String,
        val ip: String
    )

    // ✅ ADAPTADOR
    class AuditoriaAdapter(private val lista: List<Auditoria>) :
        RecyclerView.Adapter<AuditoriaAdapter.ViewHolder>() {

        // El ViewHolder no necesita cambios
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val usuario: TextView = view.findViewById(R.id.txtUsuario)
            val fecha: TextView = view.findViewById(R.id.txtFecha)
            val ip: TextView = view.findViewById(R.id.txtIP)
            val card: CardView = view.findViewById(R.id.cardAuditoria)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_auditoria, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val auditoria = lista[position]
            holder.usuario.text = "👤 ${auditoria.usuario}"
            holder.fecha.text = "⏱ ${auditoria.fecha}"
            holder.ip.text = "🌐 ${auditoria.ip}"
        }

        override fun getItemCount(): Int = lista.size
    }
}