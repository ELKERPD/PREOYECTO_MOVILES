package com.example.moviles

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class DatosSunatActivity : AppCompatActivity() {

    private lateinit var etUsuarioSOL: EditText
    private lateinit var etClaveSOL: EditText
    private lateinit var etClientID: EditText
    private lateinit var etClientSecret: EditText
    private lateinit var etClaveCertificado: EditText
    private lateinit var spinnerModo: Spinner
    private lateinit var etEndpoint: EditText

    private lateinit var btnSeleccionarCert: Button
    private lateinit var btnGuardar: Button
    private lateinit var progress: ProgressBar

    private var certificadoUri: Uri? = null
    private val apiUrl = "http://10.0.2.2/PROYECTO_ERP/API_RES_TECNODESARROLLOPEREZ/datos_sunat_98765.php"

    private val seleccionarCertLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        certificadoUri = it
        Toast.makeText(this, "Certificado seleccionado ✅", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_sunat)

        etUsuarioSOL = findViewById(R.id.etUsuarioSOL)
        etClaveSOL = findViewById(R.id.etClaveSOL)
        etClientID = findViewById(R.id.etClientID)
        etClientSecret = findViewById(R.id.etClientSecret)
        etClaveCertificado = findViewById(R.id.etClaveCertificado)
        spinnerModo = findViewById(R.id.spinnerModoEnvio)
        etEndpoint = findViewById(R.id.etEndpointSunat)
        btnSeleccionarCert = findViewById(R.id.btnSeleccionarCertificado)
        btnGuardar = findViewById(R.id.btnGuardarSunat)
        progress = findViewById(R.id.progressSunat)

        ArrayAdapter.createFromResource(
            this,
            R.array.modo_envio_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            spinnerModo.adapter = adapter
        }

        btnSeleccionarCert.setOnClickListener { seleccionarCertLauncher.launch("*/*") }
        btnGuardar.setOnClickListener { guardarDatos() }

        obtenerDatos()
    }

    private fun obtenerDatos() {
        progress.visibility = android.view.View.VISIBLE
        val token = getSharedPreferences("datos_app", Context.MODE_PRIVATE).getString("token", null) ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val conn = URL(apiUrl).openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Authorization", "Bearer $token")

                val jsonResp = JSONObject(conn.inputStream.bufferedReader().readText())
                val data = jsonResp.getJSONObject("data")

                withContext(Dispatchers.Main) {
                    progress.visibility = android.view.View.GONE
                    etUsuarioSOL.setText(data.getString("usuario_sol"))
                    etClaveSOL.setText(data.getString("clave_sol"))
                    etClientID.setText(data.getString("client_id"))
                    etClientSecret.setText(data.getString("client_secret"))
                    etClaveCertificado.setText(data.getString("clave_certificado"))
                    spinnerModo.setSelection(if (data.getString("modo_envio") == "produccion") 1 else 0)
                    etEndpoint.setText(data.getString("endpoint_sunat"))
                }

            } catch (e: Exception) {
                runOnUiThread { progress.visibility = android.view.View.GONE }
            }
        }
    }

    private fun guardarDatos() {
        val token = getSharedPreferences("datos_app", Context.MODE_PRIVATE).getString("token", null) ?: return
        progress.visibility = android.view.View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val boundary = "----AndroidBoundary${System.currentTimeMillis()}"
                val conn = URL(apiUrl).openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Authorization", "Bearer $token")
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
                conn.doOutput = true

                val output = DataOutputStream(conn.outputStream)

                fun writeField(name: String, value: String) {
                    output.writeBytes("--$boundary\r\n")
                    output.writeBytes("Content-Disposition: form-data; name=\"$name\"\r\n\r\n$value\r\n")
                }

                writeField("usuario_sol", etUsuarioSOL.text.toString())
                writeField("clave_sol", etClaveSOL.text.toString())
                writeField("client_id", etClientID.text.toString())
                writeField("client_secret", etClientSecret.text.toString())
                writeField("clave_certificado", etClaveCertificado.text.toString())
                writeField("modo_envio", spinnerModo.selectedItem.toString())
                writeField("endpoint_sunat", etEndpoint.text.toString())

                certificadoUri?.let {
                    val bytes = contentResolver.openInputStream(it)!!.readBytes()
                    output.writeBytes("--$boundary\r\n")
                    output.writeBytes("Content-Disposition: form-data; name=\"certificado\"; filename=\"cert.pfx\"\r\n")
                    output.writeBytes("Content-Type: application/octet-stream\r\n\r\n")
                    output.write(bytes)
                    output.writeBytes("\r\n")
                }

                output.writeBytes("--$boundary--\r\n")
                output.flush()
                output.close()

                val json = JSONObject(conn.inputStream.bufferedReader().readText())

                withContext(Dispatchers.Main) {
                    progress.visibility = android.view.View.GONE
                    Toast.makeText(this@DatosSunatActivity, json.getString("mensaje"), Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progress.visibility = android.view.View.GONE
                    Toast.makeText(this@DatosSunatActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
