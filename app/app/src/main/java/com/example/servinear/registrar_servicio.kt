package com.example.servinear

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class registrar_servicio : AppCompatActivity() {

    private lateinit var nombreInput: EditText
    private lateinit var descripcionInput: EditText
    private lateinit var informacionInput: EditText
    private lateinit var precioInput: EditText
    private lateinit var imageView: ImageView
    private lateinit var selectImageBtn: Button
    private lateinit var registerBtn: Button

    private var imagenBase64: String? = null
    private var idUsuario: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_servicio)


        // Inicializar vistas
        nombreInput = findViewById(R.id.nombre_input)
        descripcionInput = findViewById(R.id.descripcion_input)
        informacionInput = findViewById(R.id.informacion_input)
        precioInput = findViewById(R.id.precio_input)
        imageView = findViewById(R.id.image_view)
        selectImageBtn = findViewById(R.id.select_image_btn)
        registerBtn = findViewById(R.id.register_btn)

        // Obtener ID de usuario desde SharedPreferences
        val sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")
        if (!username.isNullOrEmpty()) {
            obtenerIdUsuario(username)
        }

        // Botón para seleccionar imagen
        selectImageBtn.setOnClickListener {
            seleccionarImagen()
        }

        // Botón para registrar servicio
        registerBtn.setOnClickListener {
            registrarServicio()
        }
    }

    private fun obtenerIdUsuario(username: String) {
        val url = "http://74.235.95.67/api/verificar_usuario.php"
        val request = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                if (response == "false") {
                    Toast.makeText(this, "El nombre de usuario no existe", Toast.LENGTH_SHORT).show()
                } else {
                    idUsuario = response.toInt()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error al verificar el usuario: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                return mapOf("username" to username)
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_SELECT_IMAGE)
    }

    private fun registrarServicio() {
        val nombre = nombreInput.text.toString().trim()
        val descripcion = descripcionInput.text.toString().trim()
        val informacion = informacionInput.text.toString().trim()
        val precio = precioInput.text.toString().trim()

        if (nombre.isEmpty() || descripcion.isEmpty() || informacion.isEmpty() || precio.isEmpty()) {
            Toast.makeText(this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        if (imagenBase64.isNullOrEmpty()) {
            Toast.makeText(this, "Selecciona una imagen de servicio", Toast.LENGTH_SHORT).show()
            return
        }

        if (idUsuario == -1) {
            Toast.makeText(this, "Error obteniendo ID de usuario", Toast.LENGTH_SHORT).show()
            return
        }

        // Preparar la imagen para ser enviada al servidor
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // Realizar la solicitud HTTP para insertar el servicio
        val url = "http://74.235.95.67/api/insertar_servicio.php"
        val request = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                limpiarCampos()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error al registrar el servicio: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                return mapOf(
                    "id_usuario" to idUsuario.toString(),
                    "nombre" to nombre,
                    "descripcion" to descripcion,
                    "informacion" to informacion,
                    "precio" to precio,
                    "foto_base64" to imagenBase64!!,
                    "fecha_creacion" to currentDate,
                    "estado" to "1" // Suponemos que el estado es 1 por defecto (activo)
                )
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun limpiarCampos() {
        nombreInput.text.clear()
        descripcionInput.text.clear()
        informacionInput.text.clear()
        precioInput.text.clear()
        imageView.setImageResource(R.drawable.icon_account_circle)
        imagenBase64 = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK) {
            val selectedImageUri = data?.data
            selectedImageUri?.let {
                val inputStream = contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)
                    imagenBase64 = bitmapToBase64(bitmap)
                }
            }
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    companion object {
        private const val REQUEST_SELECT_IMAGE = 100
    }
}