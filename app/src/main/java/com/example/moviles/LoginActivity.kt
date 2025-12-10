package com.example.moviles

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsuario: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvMensaje: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsuario = findViewById(R.id.etUsuario)
        etContrasena = findViewById(R.id.etContrasena)
        btnLogin = findViewById(R.id.btnLogin)
        tvMensaje = findViewById(R.id.tvMensaje)

        btnLogin.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                tvMensaje.text = "Por favor, completa todos los campos."
            } else {
                realizarLogin(usuario, contrasena)
            }
        }
    }

    private fun realizarLogin(usuario: String, contrasena: String) {
        tvMensaje.text = "Iniciando sesión..."

        lifecycleScope.launch(Dispatchers.IO) {

            var conn: HttpURLConnection? = null

            try {
                val url = URL("https://sercon-aje.com/API_APP_MOVIL/API_RES_TECNODESARROLLOPEREZ/zona_acceso987654321.php")
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true
                conn.connectTimeout = 15000
                conn.readTimeout = 15000

                // JSON a enviar
                val jsonInput = JSONObject().apply {
                    put("usuario", usuario)
                    put("contrasena", contrasena)
                }

                conn.outputStream.use { os ->
                    os.write(jsonInput.toString().toByteArray(Charsets.UTF_8))
                }

                val responseCode = conn.responseCode

                val responseText = try {
                    (if (responseCode in 200..299) conn.inputStream else conn.errorStream)
                        ?.bufferedReader()?.use { it.readText() }
                        ?: "Respuesta vacía"
                } catch (e: Exception) {
                    "Error al leer respuesta: ${e.message}"
                }

                withContext(Dispatchers.Main) {
                    procesarRespuesta(responseCode, responseText)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    tvMensaje.text = "Error de conexión: ${e.message}"
                }
            } finally {
                conn?.disconnect()
            }
        }
    }

    private fun procesarRespuesta(responseCode: Int, responseText: String) {
        try {
            val json = JSONObject(responseText)

            if (responseCode != HttpURLConnection.HTTP_OK) {
                tvMensaje.text = json.optString("mensaje", "Error $responseCode")
                return
            }

            val success = json.optBoolean("success", false)
            if (!success) {
                tvMensaje.text = json.optString("mensaje", "Credenciales incorrectas")
                return
            }

            // Extraer datos
            val token = json.getString("token")
            val empresa = json.getJSONObject("empresa")
            val usuarioJson = json.getJSONObject("usuario")
            val permisos = json.getJSONArray("permisos")

            // Guardar en SharedPreferences (CAMBIO: commit() en lugar de apply())
            val prefs = getSharedPreferences("datos_app", Context.MODE_PRIVATE).edit()

            prefs.putString("token", token)

            prefs.putInt("id_empresa", empresa.optInt("id_empresa"))
            prefs.putString("razon_social", empresa.optString("razon_social"))
            prefs.putString("ruc", empresa.optString("ruc"))
            prefs.putString("logo", empresa.optString("logo"))
            prefs.putString("fecha_vencimiento", empresa.optString("fecha_vencimiento"))
            prefs.putString("fecha_corte", empresa.optString("fecha_corte"))
            prefs.putString("token_expira", empresa.optString("token_expira"))

            prefs.putInt("id_usuario", usuarioJson.optInt("id_usuario"))
            prefs.putString("nombres", usuarioJson.optString("nombres"))
            prefs.putString("apellidos", usuarioJson.optString("apellidos"))
            prefs.putString("cargo", usuarioJson.optString("cargo"))
            prefs.putString("usuario", usuarioJson.optString("usuario"))

            prefs.putString("permisos", permisos.toString())

            prefs.commit()  // <-- CORREGIDO

            Toast.makeText(this, "Bienvenido ${usuarioJson.optString("nombres")}", Toast.LENGTH_LONG).show()

            startActivity(Intent(this, PrincipalActivity::class.java))
            finish()

        } catch (e: Exception) {
            tvMensaje.text = "Error al procesar respuesta: ${e.message}"
        }
    }
}
