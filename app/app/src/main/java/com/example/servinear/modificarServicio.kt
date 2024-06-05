package com.example.servinear

import ServicioSeleccionado
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
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

class modificarServicio : AppCompatActivity() {

    private lateinit var nombreInput: EditText
    private lateinit var descripcionInput: EditText
    private lateinit var informacionInput: EditText
    private lateinit var precioInput: EditText
    private lateinit var imageView: ImageView
    private lateinit var selectImageBtn: Button
    private lateinit var registerBtn: Button

    private var selectedImageUri: Uri? = null
    private var idServicio: String? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                imageView.setImageURI(it)

                // Verificar tamaño de la imagen seleccionada
                val inputStream: InputStream? = contentResolver.openInputStream(it)
                val bytes: ByteArray? = inputStream?.readBytes()
                bytes?.let {
                    if (it.size > 60 * 1024) { // Verificar si el tamaño supera los 60 KB
                        selectedImageUri = null
                        imageView.setImageResource(android.R.color.transparent)
                        Toast.makeText(this, "La imagen no puede pesar más de 60 KB", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar_servicio)

        // Inicializar vistas
        nombreInput = findViewById(R.id.nombre_input)
        descripcionInput = findViewById(R.id.descripcion_input)
        informacionInput = findViewById(R.id.informacion_input)
        precioInput = findViewById(R.id.precio_input)
        imageView = findViewById(R.id.image_view)
        selectImageBtn = findViewById(R.id.select_image_btn)
        registerBtn = findViewById(R.id.register_btn)

        // Cargar los datos del servicio seleccionado desde el singleton
        val servicioSeleccionado = ServicioSeleccionado.getInstance()
        idServicio = servicioSeleccionado.idServicio
        nombreInput.setText(servicioSeleccionado.nombre)
        descripcionInput.setText(servicioSeleccionado.descripcion)
        informacionInput.setText(servicioSeleccionado.contacto)
        precioInput.setText(servicioSeleccionado.precioHora)
        val imagenBitmap =
            BitmapFactory.decodeByteArray(servicioSeleccionado.imagen, 0, servicioSeleccionado.imagen!!.size)
        imageView.setImageBitmap(imagenBitmap)

        // Botón para seleccionar imagen
        selectImageBtn.setOnClickListener {
            pickImage.launch("image/*")
        }

        // Botón para registrar servicio
        registerBtn.setOnClickListener {
            registrarServicio()
        }

        // Solicitar permiso de lectura de almacenamiento
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
            )
        }
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

        // Convertir imagen a Base64 después de comprimir
        val imagenBase64 = convertImageToBase64(selectedImageUri!!)
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // Realizar la solicitud HTTP para actualizar el servicio
        val url = "http://74.235.95.67/api/modificar_servicio.php"
        val request = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                Log.d("errores jaja", "error: ${response}")

                limpiarCampos()
               val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error al actualizar el servicio: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.d("errores jaja", "error: ${error.message}")
            }) {
            override fun getParams(): Map<String, String> {
                return mapOf(
                    "id_servicio" to idServicio.toString(),
                    "nombre" to nombre,
                    "descripcion" to descripcion,
                    "informacion" to informacion,
                    "precio" to precio,
                    "foto_base64" to imagenBase64
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

    private fun compressAndSetImage(uri: Uri) {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
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
            BitmapFactory.decodeByteArray(
                byteArrayOutputStream.toByteArray(),
                0,
                byteArrayOutputStream.size()
            )
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
    private fun convertAndCompressImageToBase64(uri: Uri): String {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        bitmap?.let {
            val resizedBitmap = resizeBitmap(it, 400, 400)
            val compressedBitmap = compressBitmap(resizedBitmap, 60 * 1024)

            compressedBitmap?.let { compressed ->
                val byteArrayOutputStream = ByteArrayOutputStream()
                compressed.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                return Base64.encodeToString(byteArray, Base64.DEFAULT)
            } ?: run {
                Toast.makeText(this, "La imagen no puede pesar más de 60 KB", Toast.LENGTH_SHORT).show()
            }
        }

        return ""
    }

}
