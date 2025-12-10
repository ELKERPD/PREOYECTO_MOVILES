package com.example.moviles

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ProductosActivity : AppCompatActivity() {

    private lateinit var btnVolver: Button
    private lateinit var btnNuevoProducto: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvMensaje: TextView

    private val productosAdapter = ProductosAdapter(
        onEdit = { producto -> mostrarDialogoProducto(producto) },
        onDelete = { producto -> confirmarEliminar(producto) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)

        btnVolver = findViewById(R.id.btnVolver)
        btnNuevoProducto = findViewById(R.id.btnNuevoProducto)
        recyclerView = findViewById(R.id.recyclerProductos)
        progressBar = findViewById(R.id.progressBar)
        tvMensaje = findViewById(R.id.tvMensaje)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = productosAdapter

        btnVolver.setOnClickListener { finish() }
        btnNuevoProducto.setOnClickListener { mostrarDialogoProducto(null) }

        cargarProductos()
    }

    private fun cargarProductos() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvMensaje.visibility = View.GONE

        val prefs = getSharedPreferences("datos_app", Context.MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""
        val idEmpresa = prefs.getInt("id_empresa", 0)

        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null
            try {
                val url = URL("https://sercon-aje.com/API_APP_MOVIL/API_RES_TECNODESARROLLOPEREZ/prosesos_productos.php")
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                conn.doOutput = true

                val postData = "token=$token&id_empresa=$idEmpresa&accion=listar"
                conn.outputStream.use { it.write(postData.toByteArray()) }

                val responseCode = conn.responseCode
                val responseText = if (responseCode == HttpURLConnection.HTTP_OK) {
                    conn.inputStream.bufferedReader().use { it.readText() }
                } else {
                    conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "Error"
                }

                withContext(Dispatchers.Main) {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val json = JSONObject(responseText)
                        if (json.getBoolean("success")) {
                            val productosArray = json.getJSONArray("productos")
                            val lista = mutableListOf<Producto>()

                            for (i in 0 until productosArray.length()) {
                                val obj = productosArray.getJSONObject(i)
                                lista.add(
                                    Producto(
                                        id = obj.getInt("id_producto"),
                                        nombre = obj.getString("nombre"),
                                        descripcion = obj.optString("descripcion", ""),
                                        categoria = obj.optString("categoria", "Sin categoría"),
                                        marca = obj.optString("marca", "Sin marca"),
                                        precioVenta = obj.optDouble("precio_venta", 0.0),
                                        precioCompra = obj.optDouble("precio_compra", 0.0),
                                        stock = obj.optInt("stock", 0),
                                        imagen = obj.optString("imagen", "")
                                    )
                                )
                            }

                            productosAdapter.actualizar(lista)
                            progressBar.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        } else {
                            mostrarError(json.optString("mensaje", "Error al cargar"))
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

    private fun mostrarDialogoProducto(producto: Producto?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_producto, null)

        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcion)
        val etPrecioVenta = dialogView.findViewById<EditText>(R.id.etPrecioVenta)
        val etPrecioCompra = dialogView.findViewById<EditText>(R.id.etPrecioCompra)
        val etStock = dialogView.findViewById<EditText>(R.id.etStock)

        // Si es edición, cargar datos
        producto?.let {
            etNombre.setText(it.nombre)
            etDescripcion.setText(it.descripcion)
            etPrecioVenta.setText(it.precioVenta.toString())
            etPrecioCompra.setText(it.precioCompra.toString())
            etStock.setText(it.stock.toString())
        }

        AlertDialog.Builder(this)
            .setTitle(if (producto == null) "Nuevo Producto" else "Editar Producto")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString().trim()
                val descripcion = etDescripcion.text.toString().trim()
                val precioVenta = etPrecioVenta.text.toString().toDoubleOrNull() ?: 0.0
                val precioCompra = etPrecioCompra.text.toString().toDoubleOrNull() ?: 0.0
                val stock = etStock.text.toString().toIntOrNull() ?: 0

                if (nombre.isEmpty()) {
                    Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (producto == null) {
                    guardarProducto(nombre, descripcion, precioVenta, precioCompra, stock)
                } else {
                    actualizarProducto(producto.id, nombre, descripcion, precioVenta, precioCompra, stock)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun guardarProducto(
        nombre: String,
        descripcion: String,
        precioVenta: Double,
        precioCompra: Double,
        stock: Int
    ) {
        progressBar.visibility = View.VISIBLE

        val prefs = getSharedPreferences("datos_app", Context.MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""
        val idEmpresa = prefs.getInt("id_empresa", 0)

        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null
            try {
                val url = URL("https://sercon-aje.com/API_APP_MOVIL/API_RES_TECNODESARROLLOPEREZ/prosesos_productos.php")
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                conn.doOutput = true

                // 🔥 Parámetros LIMPIOS (solo lo necesario)
                val params = mutableListOf(
                    "token=$token",
                    "id_empresa=$idEmpresa",
                    "accion=agregar",
                    "nombre=$nombre",
                    "descripcion=$descripcion",
                    "precio_venta=$precioVenta",
                    "precio_compra=$precioCompra",
                    "stock=$stock"
                )

                val postData = params.joinToString("&")
                conn.outputStream.use { it.write(postData.toByteArray()) }

                val responseCode = conn.responseCode
                val responseText = if (responseCode == HttpURLConnection.HTTP_OK) {
                    conn.inputStream.bufferedReader().use { it.readText() }
                } else {
                    conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "Error"
                }

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    val json = JSONObject(responseText)
                    Toast.makeText(applicationContext, json.optString("mensaje"), Toast.LENGTH_SHORT).show()
                    if (json.optBoolean("success")) cargarProductos()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                conn?.disconnect()
            }
        }
    }


    private fun actualizarProducto(
        id: Int,
        nombre: String,
        descripcion: String,
        precioVenta: Double,
        precioCompra: Double,
        stock: Int
    ) {
        progressBar.visibility = View.VISIBLE

        val prefs = getSharedPreferences("datos_app", Context.MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""
        val idEmpresa = prefs.getInt("id_empresa", 0)

        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null
            try {
                val url = URL("https://sercon-aje.com/API_APP_MOVIL/API_RES_TECNODESARROLLOPEREZ/prosesos_productos.php")
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                conn.doOutput = true

                val params = listOf(
                    "token=$token",
                    "id_empresa=$idEmpresa",
                    "accion=actualizar",
                    "id_producto=$id",
                    "nombre=$nombre",
                    "descripcion=$descripcion",
                    "precio_venta=$precioVenta",
                    "precio_compra=$precioCompra",
                    "stock=$stock"
                )

                val postData = params.joinToString("&")
                conn.outputStream.use { it.write(postData.toByteArray()) }

                val responseCode = conn.responseCode
                val responseText = if (responseCode == HttpURLConnection.HTTP_OK) {
                    conn.inputStream.bufferedReader().use { it.readText() }
                } else {
                    conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "Error"
                }

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    val json = JSONObject(responseText)
                    Toast.makeText(applicationContext, json.optString("mensaje"), Toast.LENGTH_SHORT).show()
                    if (json.optBoolean("success")) cargarProductos()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                conn?.disconnect()
            }
        }
    }


    private fun confirmarEliminar(producto: Producto) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Producto")
            .setMessage("¿Está seguro de eliminar '${producto.nombre}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarProducto(producto.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarProducto(id: Int) {
        progressBar.visibility = View.VISIBLE

        val prefs = getSharedPreferences("datos_app", Context.MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""
        val idEmpresa = prefs.getInt("id_empresa", 0)

        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null
            try {
                val url = URL("https://sercon-aje.com/API_APP_MOVIL/API_RES_TECNODESARROLLOPEREZ/prosesos_productos.php")
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                conn.doOutput = true

                val postData = "token=$token&id_empresa=$idEmpresa&accion=eliminar&id_producto=$id"
                conn.outputStream.use { it.write(postData.toByteArray()) }

                val responseCode = conn.responseCode
                val responseText = if (responseCode == HttpURLConnection.HTTP_OK) {
                    conn.inputStream.bufferedReader().use { it.readText() }
                } else {
                    conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "Error"
                }

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val json = JSONObject(responseText)
                        Toast.makeText(applicationContext, json.optString("mensaje"), Toast.LENGTH_SHORT).show()
                        if (json.getBoolean("success")) {
                            cargarProductos()
                        }
                    } else {
                        Toast.makeText(applicationContext, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                conn?.disconnect()
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        tvMensaje.visibility = View.VISIBLE
        tvMensaje.text = mensaje
    }
}

// DATA CLASS
data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val marca: String,
    val precioVenta: Double,
    val precioCompra: Double,
    val stock: Int,
    val imagen: String
)

// ADAPTER
class ProductosAdapter(
    private val onEdit: (Producto) -> Unit,
    private val onDelete: (Producto) -> Unit
) : RecyclerView.Adapter<ProductosAdapter.ViewHolder>() {

    private var lista = mutableListOf<Producto>()

    fun actualizar(nuevaLista: List<Producto>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lista[position], onEdit, onDelete)
    }

    override fun getItemCount() = lista.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        private val tvCategoria: TextView = view.findViewById(R.id.tvCategoria)
        private val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
        private val tvStock: TextView = view.findViewById(R.id.tvStock)
        private val btnEditar: Button = view.findViewById(R.id.btnEditar)
        private val btnEliminar: Button = view.findViewById(R.id.btnEliminar)

        fun bind(producto: Producto, onEdit: (Producto) -> Unit, onDelete: (Producto) -> Unit) {
            tvNombre.text = producto.nombre
            tvCategoria.text = "${producto.categoria} • ${producto.marca}"
            tvPrecio.text = "S/ ${String.format("%.2f", producto.precioVenta)}"
            tvStock.text = "Stock: ${producto.stock}"

            btnEditar.setOnClickListener { onEdit(producto) }
            btnEliminar.setOnClickListener { onDelete(producto) }
        }
    }
}