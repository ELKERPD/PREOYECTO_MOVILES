package com.example.moviles

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Esta Activity no necesita layout (setContentView)
        // Su único trabajo es decidir a dónde ir.

        // 1. Revisar SharedPreferences para ver si existe un token
        val prefs = getSharedPreferences("datos_app", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)

        // 2. Decidir la ruta
        if (token == null) {
            // No hay sesión activa, enviar a Login
            iniciarActividad(LoginActivity::class.java)
        } else {
            // Sí hay sesión, enviar a la pantalla principal
            iniciarActividad(PrincipalActivity::class.java)
        }
    }

    /**
     * Inicia una nueva actividad y limpia el historial para
     * que el usuario no pueda "volver" a esta pantalla de decisión.
     */
    private fun iniciarActividad(clase: Class<*>) {
        val intent = Intent(this, clase)
        // Estas banderas (flags) son importantes:
        // FLAG_ACTIVITY_NEW_TASK: Inicia la actividad en una nueva tarea.
        // FLAG_ACTIVITY_CLEAR_TASK: Borra todas las actividades anteriores.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // finish() no es estrictamente necesario aquí debido a CLEAR_TASK,
        // pero es una buena práctica para cerrar esta actividad inmediatamente.
        finish()
    }
}