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

class DatosEmpresaActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var scrollView: ScrollView
    private lateinit var btnVolver: Button
    private lateinit var btnActualizar: Button
    private lateinit var tvMensaje: TextView

    // Campos de datos
    private lateinit var tvIdEmpresa: TextView
    private lateinit var etRuc: EditText
    private lateinit var etRazonSocial: EditText
    private lateinit var etNombreComercial: EditText
    private lateinit var etNombres: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etDireccion: EditText
    private lateinit var etCelular: EditText
    private lateinit var etIgv: EditText

    // Datos de solo lectura
    private lateinit var tvEstado: TextView
    private lateinit var tvFechaCreacion: TextView
    private lateinit var tvFechaVencimiento: TextView
    private lateinit var tvFechaCorte: TextView
    private lateinit var tvTipoPlan: TextView
    private lateinit var tvMaxUsuarios: TextView
    private lateinit var tvUsuariosActivos: TextView
    private lateinit var tvEspacioTotal: TextView
    private lateinit var tvEspacioUsado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_empresa)

        inicializarVistas()

        btnVolver.setOnClickListener {
            finish()
        }

        btnActualizar.setOnClickListener {
            actualizarDatosEmpresa()
        }

        cargarDatosEmpresa()
    }

    private fun inicializarVistas() {
        progressBar = findViewById(R.id.progressBar)
        scrollView = findViewById(R.id.scrollView)
        btnVolver = findViewById(R.id.btnVolver)
        btnActualizar = findViewById(R.id.btnActualizar)
        tvMensaje = findViewById(R.id.tvMensaje)

        tvIdEmpresa = findViewById(R.id.tvIdEmpresa)
        etRuc = findViewById(R.id.etRuc)
        etRazonSocial = findViewById(R.id.etRazonSocial)
        etNombreComercial = findViewById(R.id.etNombreComercial)
        etNombres = findViewById(R.id.etNombres)
        etApellidos = findViewById(R.id.etApellidos)
        etCorreo = findViewById(R.id.etCorreo)
        etDireccion = findViewById(R.id.etDireccion)
        etCelular = findViewById(R.id.etCelular)
        etIgv = findViewById(R.id.etIgv)

        tvEstado = findViewById(R.id.tvEstado)
        tvFechaCreacion = findViewById(R.id.tvFechaCreacion)
        tvFechaVencimiento = findViewById(R.id.tvFechaVencimiento)
        tvFechaCorte = findViewById(R.id.tvFechaCorte)
        tvTipoPlan = findViewById(R.id.tvTipoPlan)
        tvMaxUsuarios = findViewById(R.id.tvMaxUsuarios)
        tvUsuariosActivos = findViewById(R.id.tvUsuariosActivos)
        tvEspacioTotal = findViewById(R.id.tvEspacioTotal)
        tvEspacioUsado = findViewById(R.id.tvEspacioUsado)
    }

    private fun cargarDatosEmpresa() {
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
                val url = URL("https://sercon-aje.com/API_APP_MOVIL/API_RES_TECNODESARROLLOPEREZ/datos_empresa_54321.php?token=$token&id_empresa=$idEmpresa")
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

        // Datos editables
        tvIdEmpresa.text = "ID: ${data.optInt("id_empresa")}"
        etRuc.setText(data.optString("ruc", ""))
        etRazonSocial.setText(data.optString("razon_social", ""))
        etNombreComercial.setText(data.optString("nombre_comercial", ""))
        etNombres.setText(data.optString("nombres", ""))
        etApellidos.setText(data.optString("apellidos", ""))
        etCorreo.setText(data.optString("correo", ""))
        etDireccion.setText(data.optString("direccion", ""))
        etCelular.setText(data.optString("celular", ""))
        etIgv.setText(data.optString("igv", "18.00"))

        // Datos de solo lectura
        val estado = data.optInt("estado", 0)
        tvEstado.text = if (estado == 1) "✓ ACTIVA" else "✗ INACTIVA"
        tvEstado.setTextColor(if (estado == 1) 0xFF2ECC71.toInt() else 0xFFE74C3C.toInt())

        tvFechaCreacion.text = data.optString("fecha_creacion", "N/A")
        tvFechaVencimiento.text = data.optString("fecha_vencimiento", "N/A")
        tvFechaCorte.text = data.optString("fecha_corte", "N/A")
        tvTipoPlan.text = data.optString("tipo_plan", "N/A")
        tvMaxUsuarios.text = "${data.optInt("max_usuarios", 0)}"
        tvUsuariosActivos.text = "${data.optInt("usuarios_activos", 0)}"
        tvEspacioTotal.text = "${data.optInt("espacio_total_mb", 0)} MB"
        tvEspacioUsado.text = "${data.optInt("espacio_usado_mb", 0)} MB"
    }

    private fun actualizarDatosEmpresa() {
        val ruc = etRuc.text.toString().trim()
        val razonSocial = etRazonSocial.text.toString().trim()
        val nombreComercial = etNombreComercial.text.toString().trim()
        val nombres = etNombres.text.toString().trim()
        val apellidos = etApellidos.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val direccion = etDireccion.text.toString().trim()
        val celular = etCelular.text.toString().trim()
        val igv = etIgv.text.toString().trim()

        if (razonSocial.isEmpty() || ruc.isEmpty()) {
            Toast.makeText(this, "RUC y Razón Social son obligatorios", Toast.LENGTH_SHORT).show()
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
                val url = URL("https://sercon-aje.com/API_APP_MOVIL/API_RES_TECNODESARROLLOPEREZ/datos_empresa_54321.php")
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                conn.doOutput = true
                conn.connectTimeout = 15000
                conn.readTimeout = 10000

                val postData = "token=$token" +
                        "&id_empresa=$idEmpresa" +
                        "&ruc=$ruc" +
                        "&razon_social=$razonSocial" +
                        "&nombre_comercial=$nombreComercial" +
                        "&nombres=$nombres" +
                        "&apellidos=$apellidos" +
                        "&correo=$correo" +
                        "&direccion=$direccion" +
                        "&celular=$celular" +
                        "&igv=$igv"

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

                                // Actualizar SharedPreferences
                                val editor = prefs.edit()
                                editor.putString("razon_social", razonSocial)
                                editor.putString("ruc", ruc)
                                editor.apply()

                                cargarDatosEmpresa()
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