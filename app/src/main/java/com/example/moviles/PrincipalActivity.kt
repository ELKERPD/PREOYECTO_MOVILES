package com.example.moviles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PrincipalActivity : AppCompatActivity() {

    private lateinit var tvBienvenida: TextView
    private lateinit var btnDatosEmpresa: Button
    private lateinit var btnDatosSunat: Button
    private lateinit var btnAuditorias: Button
    private lateinit var btnProductos: Button
    private lateinit var btnCotizaciones: Button // ✅ Declarado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        tvBienvenida = findViewById(R.id.tvBienvenida)
        btnDatosEmpresa = findViewById(R.id.btnDatosEmpresa)
        btnDatosSunat = findViewById(R.id.btnDatosSunat)
        btnAuditorias = findViewById(R.id.btnAuditorias)
        btnProductos = findViewById(R.id.btnProductos)
        btnCotizaciones = findViewById(R.id.btnCotizaciones) // ✅ Vinculado

        val prefs = getSharedPreferences("datos_app", MODE_PRIVATE)
        val nombres = prefs.getString("nombres", "Usuario")
        tvBienvenida.text = "Bienvenido, $nombres"

        btnDatosEmpresa.setOnClickListener {
            startActivity(Intent(this, DatosEmpresaActivity::class.java))
        }

        btnDatosSunat.setOnClickListener {
            startActivity(Intent(this, DatosSunatActivity::class.java))
        }

        btnAuditorias.setOnClickListener {
            startActivity(Intent(this, AuditoriasActivity::class.java))
        }

        btnProductos.setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }

        btnCotizaciones.setOnClickListener {
            startActivity(Intent(this, CotizacionesActivity::class.java))
        }
    }
}
