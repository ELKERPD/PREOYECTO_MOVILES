package com.example.moviles

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class DatosSunatActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var scrollView: ScrollView
    private lateinit var btnVolver: Button
    private lateinit var btnActualizar: Button
    private lateinit var tvMensaje: TextView

    // Campos SUNAT
    private lateinit var etUsuarioSol: EditText
    private lateinit var etClaveSol: EditText
    private lateinit var etClientId: EditText
    private lateinit var etClientSecret: EditText
    private lateinit var etClaveCertificado: EditText
    private lateinit var etEndpointSunat: EditText
    private lateinit var spinnerModoEnvio: Spinner
    private lateinit var tvCertificadoActual: TextView
    private lateinit var tvTokenSunat: TextView
    private lateinit var tvTokenExpira: TextView

    // Campos SaaS
    private lateinit var etApiKeyOpenai: EditText
    private lateinit var etWebhookEndpoint: EditText
    private lateinit var etWebhookToken: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_sunat)

        inicializarVistas()
        configurarSpinner()

        btnVolver.setOnClickListener {
            finish()
        }

        btnActualizar.setOnClickListener {
            actualizarDatosSunat()
        }

        cargarDatosSunat()
    }

    private fun inicializarVistas() {
        progressBar = findViewById(R.id.progressBar)
        scrollView = findViewById(R.id.scrollView)
        btnVolver = findViewById(R.id.btnVolver)
        btnActualizar = findViewById(R.id.btnActualizar)
        tvMensaje = findViewById(R.id.tvMensaje)

        etUsuarioSol = findViewById(R.id.etUsuarioSol)
        etClaveSol = findViewById(R.id.etClaveSol)
        etClientId = findViewById(R.id.etClientId)
        etClientSecret = findViewById(R.id.etClientSecret)
        etClaveCertificado = findViewById(R.id.etClaveCertificado)
        etEndpointSunat = findViewById(R.id.etEndpointSunat)
        spinnerModoEnvio = findViewById(R.id.spinnerModoEnvio)
        tvCertificadoActual = findViewById(R.id.tvCertificadoActual)
        tvTokenSunat = findViewById(R.id.tvTokenSunat)
        tvTokenExpira = findViewById(R.id.tvTokenExpira)

        etApiKeyOpenai = findViewById(R.id.etApiKeyOpenai)
        etWebhookEndpoint = findViewById(R.id.etWebhookEndpoint)
        etWebhookToken = findViewById(R.id.etWebhookToken)
    }

    private fun configurarSpinner() {
        val modos = arrayOf("Beta (Pruebas)", "Producción")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, modos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerModoEnvio.adapter = adapter
    }

    private fun cargarDatosSunat() {
        progressBar.visibility = View.VISIBLE
        scrollView.visibility = View.GONE
        tvMensaje.visibility = View.GONE

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
                val url = URL("https://sercon-aje.com/API_APP_MOVIL/API_RES_TECNODESARROLLOPEREZ/datos_sunat_98765.php?token=$token&id_empresa=$idEmpresa")
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
                                val data = json.getJSONObject("data")
                                mostrarDatos(data)
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

    private fun mostrarDatos(data: JSONObject) {
        progressBar.visibility = View.GONE
        scrollView.visibility = View.VISIBLE

        // Datos SUNAT
        etUsuarioSol.setText(data.optString("usuario_sol", ""))
        etClaveSol.setText(data.optString("clave_sol", ""))
        etClientId.setText(data.optString("client_id", ""))
        etClientSecret.setText(data.optString("client_secret", ""))
        etClaveCertificado.setText(data.optString("clave_certificado", ""))
        etEndpointSunat.setText(data.optString("endpoint_sunat", ""))

        // Modo envío
        val modoEnvio = data.optString("modo_envio", "beta")
        spinnerModoEnvio.setSelection(if (modoEnvio == "produccion") 1 else 0)

        // Certificado
        val certificado = data.optString("certificado", "")
        tvCertificadoActual.text = if (certificado.isNotEmpty()) {
            "📄 $certificado"
        } else {
            "No hay certificado cargado"
        }

        // Token SUNAT
        val tokenSunat = data.optString("token_sunat", "")
        tvTokenSunat.text = if (tokenSunat.isNotEmpty()) {
            "Token: ${tokenSunat.take(30)}..."
        } else {
            "No generado"
        }

        val tokenExpira = data.optString("token_expira", "")
        tvTokenExpira.text = if (tokenExpira.isNotEmpty()) {
            "Expira: $tokenExpira"
        } else {
            "N/A"
        }

        // SaaS
        etApiKeyOpenai.setText(data.optString("api_key_openai", ""))
        etWebhookEndpoint.setText(data.optString("webhook_endpoint", ""))
        etWebhookToken.setText(data.optString("webhook_token_seguridad", ""))
    }

    private fun actualizarDatosSunat() {
        val usuarioSol = etUsuarioSol.text.toString().trim()
        val claveSol = etClaveSol.text.toString().trim()
        val clientId = etClientId.text.toString().trim()
        val clientSecret = etClientSecret.text.toString().trim()
        val claveCertificado = etClaveCertificado.text.toString().trim()
        val endpointSunat = etEndpointSunat.text.toString().trim()
        val modoEnvio = if (spinnerModoEnvio.selectedItemPosition == 0) "beta" else "produccion"

        val apiKeyOpenai = etApiKeyOpenai.text.toString().trim()
        val webhookEndpoint = etWebhookEndpoint.text.toString().trim()
        val webhookToken = etWebhookToken.text.toString().trim()

        if (usuarioSol.isEmpty() || claveSol.isEmpty()) {
            Toast.makeText(this, "Usuario y Clave SOL son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE
        btnActualizar.isEnabled = false

        val prefs = getSharedPreferences("datos_app", Context.MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""
        val idEmpresa = prefs.getInt("id_empresa", 0)

        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null

            try {
                val url = URL("https://sercon-aje.com/API_APP_MOVIL/API_RES_TECNODESARROLLOPEREZ/datos_sunat_98765.php")
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                conn.doOutput = true
                conn.connectTimeout = 15000
                conn.readTimeout = 10000

                val postData = "token=$token" +
                        "&id_empresa=$idEmpresa" +
                        "&usuario_sol=$usuarioSol" +
                        "&clave_sol=$claveSol" +
                        "&client_id=$clientId" +
                        "&client_secret=$clientSecret" +
                        "&clave_certificado=$claveCertificado" +
                        "&modo_envio=$modoEnvio" +
                        "&endpoint_sunat=$endpointSunat" +
                        "&api_key_openai=$apiKeyOpenai" +
                        "&webhook_endpoint=$webhookEndpoint" +
                        "&webhook_token_seguridad=$webhookToken"

                conn.outputStream.use { os ->
                    os.write(postData.toByteArray(Charsets.UTF_8))
                }

                val responseCode = conn.responseCode
                val responseText = if (responseCode == HttpURLConnection.HTTP_OK) {
                    conn.inputStream.bufferedReader().use { it.readText() }
                } else {
                    conn.errorStream?.bufferedReader()?.use { it.readText() }
                        ?: "Error sin mensaje"
                }

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnActualizar.isEnabled = true

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try {
                            val json = JSONObject(responseText)

                            if (json.getBoolean("success")) {
                                Toast.makeText(
                                    applicationContext,
                                    json.optString("mensaje", "Datos actualizados"),
                                    Toast.LENGTH_LONG
                                ).show()

                                cargarDatosSunat()
                            } else {
                                tvMensaje.text = json.optString("mensaje", "Error al actualizar")
                                tvMensaje.visibility = View.VISIBLE
                            }

                        } catch (e: Exception) {
                            tvMensaje.text = "Error al procesar respuesta: ${e.message}"
                            tvMensaje.visibility = View.VISIBLE
                        }
                    } else {
                        tvMensaje.text = "Error del servidor: $responseCode"
                        tvMensaje.visibility = View.VISIBLE
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnActualizar.isEnabled = true
                    tvMensaje.text = "Error de conexión: ${e.message}"
                    tvMensaje.visibility = View.VISIBLE
                }
            } finally {
                conn?.disconnect()
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        progressBar.visibility = View.GONE
        scrollView.visibility = View.GONE
        tvMensaje.visibility = View.VISIBLE
        tvMensaje.text = mensaje
    }
}