package com.example.servinear

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.ByteArrayOutputStream
import java.io.InputStream
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

    private var selectedImageUri: Uri? = null
    private var idUsuario: Int = -1

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            imageView.setImageURI(it)

            // Verificar tamaño de la imagen seleccionada y comprimirla
            val inputStream: InputStream? = contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            bitmap?.let {
                val resizedBitmap = resizeBitmap(it, 800, 800)
                val compressedBitmap = compressBitmap(resizedBitmap, 60 * 1024)
                compressedBitmap?.let { compressed ->
                    imageView.setImageBitmap(compressed)
                } ?: run {
                    selectedImageUri = null
                    imageView.setImageResource(android.R.color.transparent)
                    Toast.makeText(this, "La imagen no puede pesar más de 60 KB", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

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
            pickImage.launch("image/*")
        }

        // Botón para registrar servicio
        registerBtn.setOnClickListener {
            registrarServicio()
        }

        // Solicitar permiso de lectura de almacenamiento
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
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

    private fun registrarServicio() {
        val nombre = nombreInput.text.toString().trim()
        val descripcion = descripcionInput.text.toString().trim()
        val informacion = informacionInput.text.toString().trim()
        val precio = precioInput.text.toString().trim()

        if (nombre.isEmpty() || descripcion.isEmpty() || informacion.isEmpty() || precio.isEmpty()) {
            Toast.makeText(this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Selecciona una imagen de servicio", Toast.LENGTH_SHORT).show()
            return
        }

        if (idUsuario == -1) {
            Toast.makeText(this, "Error obteniendo ID de usuario", Toast.LENGTH_SHORT).show()
            return
        }

        // Preparar la imagen para ser enviada al servidor
        val imagenBase64 = convertImageToBase64(selectedImageUri!!)
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // Realizar la solicitud HTTP para insertar el servicio
        val url = "http://74.235.95.67/api/insertar_servicio.php"
        val request = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                limpiarCampos()
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)

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
                    "foto_base64" to imagenBase64,
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
        selectedImageUri = null
    }

    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val aspectRatio = width.toFloat() / height.toFloat()

        val newWidth: Int
        val newHeight: Int
        if (width > height) {
            newWidth = maxWidth
            newHeight = (newWidth / aspectRatio).toInt()
        } else {
            newHeight = maxHeight
            newWidth = (newHeight * aspectRatio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun compressBitmap(bitmap: Bitmap, maxSize: Int): Bitmap? {
        var quality = 100
        var byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)

        while (byteArrayOutputStream.size() > maxSize && quality > 0) {
            quality -= 5
            byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        }

        return if (byteArrayOutputStream.size() <= maxSize) {
            BitmapFactory.decodeByteArray(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.size())
        } else {
            null
        }
    }

    private fun convertImageToBase64(uri: Uri): String {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val bytes: ByteArray? = inputStream?.readBytes()
        bytes?.let {
            return Base64.encodeToString(it, Base64.DEFAULT)
        }
        return ""
    }
}
