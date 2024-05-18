package com.example.servinear

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.InputStream


class registro : AppCompatActivity() {

    private lateinit var nombreInput: EditText
    private lateinit var apellidosInput: EditText
    private lateinit var correoInput: EditText
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var prestadorSwitch: Switch
    private lateinit var registerButton: Button
    private lateinit var imageView: ImageView
    private lateinit var selectImageButton: Button
    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            imageView.setImageURI(it)
        }
    }

    // Método para convertir una imagen URI a Base64
    private fun convertImageToBase64(uri: Uri): String {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val bytes: ByteArray? = inputStream?.readBytes()
        bytes?.let {
            return Base64.encodeToString(it, Base64.DEFAULT)
        }
        return ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Obtener referencias a los EditText y otros elementos de la vista
        nombreInput = findViewById(R.id.nombre_input)
        apellidosInput = findViewById(R.id.apellidos_input)
        correoInput = findViewById(R.id.correo_inout)
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        prestadorSwitch = findViewById(R.id.prestador_switch)
        registerButton = findViewById(R.id.register_btn)
        imageView = findViewById(R.id.image_view)
        selectImageButton = findViewById(R.id.select_image_btn)

        // Solicitar permiso de lectura de almacenamiento
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }

        // Agregar un listener al botón de seleccionar imagen
        selectImageButton.setOnClickListener {
            pickImage.launch("image/*")
        }

        // Agregar un listener al botón de registro
        registerButton.setOnClickListener {
            // Obtener los valores de los EditText
            val nombre = nombreInput.text.toString()
            val apellidos = apellidosInput.text.toString()
            val correo = correoInput.text.toString()
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val esPrestador = prestadorSwitch.isChecked

            val imagenBase64 = if (selectedImageUri != null) {
                convertImageToBase64(selectedImageUri!!)
            } else {
                ""
            }

            val params = JSONObject()
            params.put("nombre", nombre)
            params.put("apellidos", apellidos)
            params.put("correo", correo)
            params.put("username", username)
            params.put("password", password)
            params.put("esPrestador", esPrestador)
            params.put("imagenBase64", imagenBase64) // Agregar la imagen en Base64 al JSON


            // URL del servidor donde está el script PHP para manejar la solicitud POST
            val url = "http://192.168.31.198/servinear/p2.php"

            // Crear una solicitud POST utilizando Volley
            val request = object : StringRequest(
                Request.Method.POST, url,
                Response.Listener { response ->
                    // Manejar la respuesta del servidor si es necesario
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                },
                Response.ErrorListener { error ->
                    // Manejar errores de la solicitud
                    val errorMessage = "Error al registrar: ${error.message}"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    Log.e("RegistroActivity", errorMessage)
                }) {
                override fun getParams(): Map<String, String> {
                    // Convertir el objeto JSONObject a un Map<String, String>
                    val paramsMap = HashMap<String, String>()
                    for (key in params.keys()) {
                        paramsMap[key] = params.getString(key)
                    }
                    return paramsMap
                }
            }

            // Agregar la solicitud a la cola de Volley para que se envíe
            Volley.newRequestQueue(this).add(request)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }
}
