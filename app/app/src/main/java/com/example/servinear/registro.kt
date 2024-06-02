package com.example.servinear

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
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
            // Verificar conexión a Internet
            if (isNetworkAvailable()) {
                registrarUsuarioDesdeUI()
            } else {
                // Guardar datos localmente en caso de no tener conexión
                guardarDatosLocalmenteDesdeUI()
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun registrarUsuarioDesdeUI() {
        val nombre = nombreInput.text.toString()
        val apellidos = apellidosInput.text.toString()
        val correo = correoInput.text.toString()
        val username = usernameInput.text.toString()
        val password = passwordInput.text.toString()
        val esPrestador = prestadorSwitch.isChecked

        // Verificar si hay campos vacíos
        if (nombre.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar si se ha seleccionado una imagen y si su tamaño es válido
        if (selectedImageUri == null) {
            Toast.makeText(this, "Seleccione una imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val imagenBase64 = if (selectedImageUri != null) {
            convertImageToBase64(selectedImageUri!!)
        } else {
            ""
        }

        verificarUsuarioExistente(username) { existe ->
            if (existe) {
                Toast.makeText(this, "El nombre de usuario ya está registrado", Toast.LENGTH_SHORT).show()
            } else {
                registrarUsuario(nombre, apellidos, correo, username, password, esPrestador, imagenBase64)
            }
        }
    }

    private fun guardarDatosLocalmenteDesdeUI() {
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

        guardarDatosLocalmente(nombre, apellidos, correo, username, password, esPrestador, imagenBase64)
        dirigirActividadSegunTipoUsuario(esPrestador)
    }

    private fun verificarUsuarioExistente(username: String, callback: (Boolean) -> Unit) {
        //val url = "http://192.168.31.198/servinear/verificar_usuario.php"
        val url = "http://74.235.95.67/api/verificar_usuario.php"
        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                callback(response.toBoolean())
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error al verificar el usuario: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("RegistroActivity", "Error al verificar el usuario: ${error.message}")
                callback(false)
            }) {
            override fun getParams(): Map<String, String> {
                return mapOf("username" to username)
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun registrarUsuario(nombre: String, apellidos: String, correo: String, username: String, password: String, esPrestador: Boolean, imagenBase64: String) {
        val params = JSONObject()
        params.put("nombre", nombre)
        params.put("apellidos", apellidos)
        params.put("correo", correo)
        params.put("username", username)
        params.put("password", password)
        params.put("esPrestador", esPrestador)
        params.put("imagenBase64", imagenBase64)

        //val url = "http://192.168.31.198/servinear/p2.php"
        val url = "http://74.235.95.67/api/p2.php"
        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                guardarDatosLocalmente(nombre, apellidos, correo, username, password, esPrestador, imagenBase64)
                dirigirActividadSegunTipoUsuario(esPrestador)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error al registrar: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("RegistroActivity", "Error al registrar: ${error.message}")
                // Guardar datos localmente en caso de fallo de conexión
                guardarDatosLocalmente(nombre, apellidos, correo, username, password, esPrestador, imagenBase64)
                dirigirActividadSegunTipoUsuario(esPrestador)
            }) {
            override fun getParams(): Map<String, String> {
                val paramsMap = HashMap<String, String>()
                for (key in params.keys()) {
                    paramsMap[key] = params.getString(key)
                }
                return paramsMap
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun guardarDatosLocalmente(nombre: String, apellidos: String, correo: String, username: String, password: String, esPrestador: Boolean, imagenBase64: String) {
        val sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("nombre", nombre)
        editor.putString("apellidos", apellidos)
        editor.putString("correo", correo)
        editor.putString("username", username)
        editor.putString("password", password)
        editor.putBoolean("esPrestador", esPrestador)
        editor.putString("imagenBase64", imagenBase64)

        // Confirmar los cambios
        editor.apply()
    }

    private fun dirigirActividadSegunTipoUsuario(esPrestador: Boolean) {
        if (esPrestador) {
            val intent = Intent(this, registrar_servicio::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, inicio::class.java)
            startActivity(intent)
        }
        finish()
    }
}
