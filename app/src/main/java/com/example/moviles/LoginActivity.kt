package com.example.moviles

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsuario: EditText
    private lateinit var etContrasena: EditText
    private lateinit var etToken: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvMensaje: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsuario = findViewById(R.id.etUsuario)
        etContrasena = findViewById(R.id.etContrasena)
        etToken = findViewById(R.id.etToken)
        btnLogin = findViewById(R.id.btnLogin)
        tvMensaje = findViewById(R.id.tvMensaje)

        btnLogin.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()
            val token = etToken.text.toString().trim()

            if (usuario.isEmpty() || contrasena.isEmpty() || token.isEmpty()) {
                tvMensaje.text = "Por favor, completa todos loscampos."
            } else {
                realizarLogin(usuario, contrasena, token)
            }
        }
    }

    private fun realizarLogin(usuario: String, contrasena: String, token: String) {
        tvMensaje.text = "Iniciando sesión..."

        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null
            try {
                val url = URL("http://10.0.2.2/PROYECTO_ERP/API_RES_TECNODESARROLLOPEREZ/zona_acceso987654321.php")
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true
                // Añadir timeouts es una buena práctica
                conn.connectTimeout = 15000 // 15 segundos
                conn.readTimeout = 10000 // 10 segundos

                val jsonInput = JSONObject()
                jsonInput.put("usuario", usuario)
                jsonInput.put("contrasena", contrasena)
                jsonInput.put("token", token)

                conn.outputStream.use { os ->
                    os.write(jsonInput.toString().toByteArray(Charsets.UTF_8))
                }

                val responseCode = conn.responseCode

                // === ✅ CORRECCIÓN CLAVE: LEER EL STREAM ADECUADO ===
                val responseText: String
                if (responseCode == HttpURLConnection.HTTP_OK) { // Código 200
                    responseText = conn.inputStream.bufferedReader().use { it.readText() }
                } else {
                    // Leer del errorStream si el código no es 200
                    responseText = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "Error: Sin mensaje de respuesta"
                }

                // Ahora que tenemos la respuesta, volvemos al hilo principal
                withContext(Dispatchers.Main) {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try {
                            val json = JSONObject(responseText)
                            if (json.getBoolean("success")) {

                                // === GUARDAR DATOS EN SharedPreferences ===
                                val empresa = json.getJSONObject("empresa")
                                val usuarioJson = json.getJSONObject("usuario")
                                val tokenRecibido = json.getString("token")

                                val prefs = getSharedPreferences("datos_app", Context.MODE_PRIVATE)
                                val editor = prefs.edit()

                                editor.putString("token", tokenRecibido)
                                editor.putInt("id_empresa", empresa.getInt("id_empresa"))
                                editor.putString("razon_social", empresa.getString("razon_social"))
                                editor.putInt("id_usuario", usuarioJson.getInt("id_usuario"))
                                editor.putString("nombres", usuarioJson.getString("nombres"))
                                editor.putString("apellidos", usuarioJson.getString("apellidos"))
                                editor.putString("cargo", usuarioJson.getString("cargo"))

                                editor.apply() // Guarda los datos

                                // Mensaje y redirección
                                Toast.makeText(
                                    applicationContext,
                                    "Bienvenido ${usuarioJson.getString("nombres")}",
                                    Toast.LENGTH_LONG
                                ).show()

                                startActivity(Intent(this@LoginActivity, PrincipalActivity::class.java))
                                finish()

                            } else {
                                // El API devolvió success = false
                                tvMensaje.text = json.getString("mensaje")
                            }
                        } catch (e: Exception) {
                            // Error al parsear el JSON de éxito
                            tvMensaje.text = "Error al procesar la respuesta: ${e.localizedMessage}"
                        }
                    } else {
                        // Error de conexión (404, 500, 401, etc.)
                        // Intentamos mostrar el mensaje de error del API si existe
                        try {
                            val json = JSONObject(responseText)
                            tvMensaje.text = json.optString("mensaje", "Error $responseCode: $responseText")
                        } catch (e: Exception) {
                            // Si la respuesta de error no es JSON
                            tvMensaje.text = "Error $responseCode: $responseText"
                        }
                    }
                }
            } catch (e: Exception) {
                // Error de red (IOException, SocketTimeoutException, etc.)
                withContext(Dispatchers.Main) {
                    tvMensaje.text = "Error de conexión: ${e.localizedMessage}"
                }
            } finally {
                conn?.disconnect() // Asegurarse de cerrar la conexión
            }
        }
    }
}