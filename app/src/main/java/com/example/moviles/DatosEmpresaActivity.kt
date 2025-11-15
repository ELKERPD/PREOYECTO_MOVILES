package com.example.moviles

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class DatosEmpresaActivity : AppCompatActivity() {

    private lateinit var etRuc: EditText
    private lateinit var etRazonSocial: EditText
    private lateinit var etNombreComercial: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etDireccion: EditText
    private lateinit var etCelular: EditText
    private lateinit var etIgv: EditText
    private lateinit var btnGuardar: Button
    private lateinit var progress: ProgressBar

    private val apiUrl = "http://10.0.2.2/PROYECTO_ERP/API_RES_TECNODESARROLLOPEREZ/datos_empresa_54321.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_empresa)

        etRuc = findViewById(R.id.etRuc)
        etRazonSocial = findViewById(R.id.etRazonSocial)
        etNombreComercial = findViewById(R.id.etNombreComercial)
        etCorreo = findViewById(R.id.etCorreo)
        etDireccion = findViewById(R.id.etDireccion)
        etCelular = findViewById(R.id.etCelular)
        etIgv = findViewById(R.id.etIgv)
        btnGuardar = findViewById(R.id.btnGuardarEmpresa)
        progress = findViewById(R.id.progressEmpresa)

        btnGuardar.setOnClickListener { guardarDatos() }

        obtenerDatosEmpresa()
    }

    private fun setCargando(cargando: Boolean) {
        progress.visibility = if (cargando) View.VISIBLE else View.GONE
        btnGuardar.isEnabled = !cargando
    }

    private fun obtenerDatosEmpresa() {
        setCargando(true)
        val token = getSharedPreferences("datos_app", Context.MODE_PRIVATE).getString("token", null)

        if (token == null) {
            Toast.makeText(this, "Token no encontrado", Toast.LENGTH_LONG).show()
            setCargando(false)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null
            try {
                conn = URL(apiUrl).openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Authorization", "Bearer $token")
                conn.setRequestProperty("Accept", "application/json")
                conn.connectTimeout = 15000
                conn.readTimeout = 10000

                val responseCode = conn.responseCode
                val responseText: String

                // === ✅ CORRECCIÓN: LEER EL STREAM CORRECTO ===
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    responseText = conn.inputStream.bufferedReader().use { it.readText() }
                } else {
                    responseText = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "Error sin mensaje"
                }

                withContext(Dispatchers.Main) {
                    setCargando(false)
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try {
                            val json = JSONObject(responseText)
                            // Asumiendo que el GET devuelve el objeto directamente
                            // Si también tiene un "success", habría que anidarlo
                            etRuc.setText(json.getString("ruc"))
                            etRazonSocial.setText(json.getString("razon_social"))
                            etNombreComercial.setText(json.getString("nombre_comercial"))
                            etCorreo.setText(json.getString("correo"))
                            etDireccion.setText(json.getString("direccion"))
                            etCelular.setText(json.getString("celular"))
                            etIgv.setText(json.getString("igv"))
                        } catch (e: Exception) {
                            Toast.makeText(applicationContext, "Error al procesar datos: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // Mostrar error del servidor (ej. 401, 404)
                        Toast.makeText(applicationContext, "Error $responseCode: $responseText", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    setCargando(false)
                    Toast.makeText(applicationContext, "Error de red: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    setCargando(false)
                    Toast.makeText(applicationContext, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            } finally {
                conn?.disconnect()
            }
        }
    }

    private fun guardarDatos() {
        val token = getSharedPreferences("datos_app", Context.MODE_PRIVATE).getString("token", null)
        if (token == null) {
            Toast.makeText(this, "Token no encontrado", Toast.LENGTH_LONG).show()
            return
        }

        setCargando(true)

        // Leer los datos en el Hilo Principal antes de la corutina
        val ruc = etRuc.text.toString()
        val razonSocial = etRazonSocial.text.toString()
        val nombreComercial = etNombreComercial.text.toString()
        val correo = etCorreo.text.toString()
        val direccion = etDireccion.text.toString()
        val celular = etCelular.text.toString()
        val igv = etIgv.text.toString()

        // Validación simple
        if (ruc.isEmpty() || razonSocial.isEmpty() || igv.isEmpty()) {
            Toast.makeText(this, "RUC, Razón Social e IGV son obligatorios", Toast.LENGTH_SHORT).show()
            setCargando(false)
            return
        }


        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null
            try {
                // === ✅ CORRECCIÓN 1: CODIFICAR DATOS PARA URL ===
                val postData = StringBuilder()
                postData.append("ruc=").append(URLEncoder.encode(ruc, "UTF-8"))
                postData.append("&razon_social=").append(URLEncoder.encode(razonSocial, "UTF-8"))
                postData.append("&nombre_comercial=").append(URLEncoder.encode(nombreComercial, "UTF-8"))
                postData.append("&correo=").append(URLEncoder.encode(correo, "UTF-8"))
                postData.append("&direccion=").append(URLEncoder.encode(direccion, "UTF-8"))
                postData.append("&celular=").append(URLEncoder.encode(celular, "UTF-8"))
                postData.append("&igv=").append(URLEncoder.encode(igv, "UTF-8"))

                conn = URL(apiUrl).openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Authorization", "Bearer $token")
                // === ✅ CORRECCIÓN 2: DEFINIR EL CONTENT-TYPE ===
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                conn.setRequestProperty("Accept", "application/json")
                conn.connectTimeout = 15000
                conn.readTimeout = 10000
                conn.doOutput = true

                conn.outputStream.use { os ->
                    os.write(postData.toString().toByteArray(Charsets.UTF_8))
                }

                val responseCode = conn.responseCode
                val responseText: String

                // === ✅ CORRECCIÓN 3: LEER EL STREAM CORRECTO ===
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    responseText = conn.inputStream.bufferedReader().use { it.readText() }
                } else {
                    responseText = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "Error sin mensaje"
                }

                withContext(Dispatchers.Main) {
                    setCargando(false)
                    try {
                        // El API siempre debe devolver un JSON, sea éxito o error
                        val json = JSONObject(responseText)
                        val mensaje = json.optString("mensaje", "Respuesta sin mensaje")
                        Toast.makeText(applicationContext, mensaje, Toast.LENGTH_LONG).show()

                        // Opcional: Si fue exitoso, volver a cargar los datos
                        if (responseCode == HttpURLConnection.HTTP_OK && json.optBoolean("success", true)) {
                            // Éxito, no es necesario hacer nada más que mostrar el toast
                        }

                    } catch (e: Exception) {
                        Toast.makeText(applicationContext, "Error al procesar respuesta: $responseText", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    setCargando(false)
                    Toast.makeText(applicationContext, "Error de red: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    setCargando(false)
                    Toast.makeText(applicationContext, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            } finally {
                conn?.disconnect()
            }
        }
    }
}