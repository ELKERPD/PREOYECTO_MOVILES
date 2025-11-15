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

class ProductosActivity : AppCompatActivity() {

    private lateinit var recyclerProductos: RecyclerView
    private val listaProductos = mutableListOf<Producto>()
    private lateinit var adapter: ProductoAdapter

    // URL de la API
    private val apiUrl = "http://10.0.2.2/PROYECTO_ERP/API_RES_TECNODESARROLLOPEREZ/prosesos_productos.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)

        recyclerProductos = findViewById(R.id.recyclerProductos)
        recyclerProductos.layoutManager = LinearLayoutManager(this)

        adapter = ProductoAdapter(listaProductos)
        recyclerProductos.adapter = adapter

        obtenerProductos()
    }

    private fun obtenerProductos() {
        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null
            try {
                val prefs = getSharedPreferences("datos_app", Context.MODE_PRIVATE)
                val token = prefs.getString("token", null)

                if (token == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ProductosActivity, "Token no encontrado. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }

                val url = URL(apiUrl)
                conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("Authorization", "Bearer $token")
                    connectTimeout = 15000
                    readTimeout = 10000
                }

                val responseCode = conn.responseCode
                val responseText = if (responseCode == HttpURLConnection.HTTP_OK) {
                    conn.inputStream.bufferedReader().use { it.readText() }
                } else {
                    conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "Error desconocido"
                }

                withContext(Dispatchers.Main) {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try {
                            val json = JSONObject(responseText)

                            // el backend devuelve: { success: true, productos: [...] }
                            if (!json.getBoolean("success")) {
                                Toast.makeText(this@ProductosActivity, json.getString("mensaje"), Toast.LENGTH_LONG).show()
                                return@withContext
                            }

                            val data = json.getJSONArray("productos")
                            listaProductos.clear()

                            for (i in 0 until data.length()) {
                                val item = data.getJSONObject(i)
                                listaProductos.add(
                                    Producto(
                                        nombre = item.optString("nombre", "Sin nombre"),
                                        precio = item.optDouble("precio_venta", 0.0),
                                        stock = item.optInt("stock", 0)
                                    )
                                )
                            }
                            adapter.notifyDataSetChanged()

                        } catch (e: Exception) {
                            Toast.makeText(this@ProductosActivity, "Error al procesar JSON: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        try {
                            val json = JSONObject(responseText)
                            val mensaje = json.optString("mensaje", "Error HTTP $responseCode")
                            Toast.makeText(this@ProductosActivity, mensaje, Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            Toast.makeText(this@ProductosActivity, "Error $responseCode: $responseText", Toast.LENGTH_LONG).show()
                        }
                    }
                }

            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProductosActivity, "Error de red: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            } finally {
                conn?.disconnect()
            }
        }
    }

    // Modelo de datos
    data class Producto(
        val nombre: String,
        val precio: Double,
        val stock: Int
    )

    // Adaptador del RecyclerView
    class ProductoAdapter(private val lista: List<Producto>) :
        RecyclerView.Adapter<ProductoAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val nombre: TextView = view.findViewById(R.id.txtNombre)
            val precio: TextView = view.findViewById(R.id.txtPrecio)
            val stock: TextView = view.findViewById(R.id.txtStock)
            val card: CardView = view.findViewById(R.id.cardProducto)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_producto, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val producto = lista[position]
            holder.nombre.text = "📦 ${producto.nombre}"
            holder.precio.text = "💰 ${producto.precio} S/"
            holder.stock.text = "📦 Stock: ${producto.stock}"
        }

        override fun getItemCount(): Int = lista.size
    }
}
