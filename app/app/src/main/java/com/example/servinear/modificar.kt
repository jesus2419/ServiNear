package com.example.servinear

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream


class modificar : AppCompatActivity() {
    private lateinit var nombreInput: EditText
    private lateinit var apellidosInput: EditText
    private lateinit var correoInput: EditText
    private lateinit var usernameText: TextView
    private lateinit var passwordInput: EditText
    private lateinit var modificarBtn: Button
    private lateinit var eliminarBtn: Button

    private lateinit var imageView: ImageView
    private lateinit var selectImageBtn: Button



    private var selectedImageUri: Uri? = null

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userManager: UserManager

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
        setContentView(R.layout.activity_modificar)
        // Referencias a los elementos de la interfaz
        nombreInput = findViewById(R.id.nombre_input)
        apellidosInput = findViewById(R.id.apellidos_input)
        correoInput = findViewById(R.id.correo_input)
        usernameText = findViewById(R.id.username)
        passwordInput = findViewById(R.id.password_input)
        modificarBtn = findViewById(R.id.modificar_btn)
        eliminarBtn = findViewById(R.id.eliminar_btn)

        imageView = findViewById(R.id.image_view)
        selectImageBtn = findViewById(R.id.select_image_btn)

        userManager = UserManager.getInstance(this)





        // Obtener datos guardados en SharedPreferences (por ejemplo, id de usuario)
        val sharedPreferences: SharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)
        val idUsuario = sharedPreferences.getInt("idUsuario", -1)
        val username2 = userManager.getUser()?.username

        // Realizar la petición al servidor para obtener los datos del usuario
        val url = "http://74.235.95.67/api/obtener_datos_usuario.php"
        val queue: RequestQueue = Volley.newRequestQueue(this@modificar)
        val request = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val success = jsonObject.getBoolean("success")

                    if (success) {
                        // Obtener datos del usuario
                        val nombre = jsonObject.getString("nombre")
                        val apellidos = jsonObject.getString("apellidos")
                        val correo = jsonObject.getString("correo")
                        val username = jsonObject.getString("usuario")
                        val pass = jsonObject.getString("pass")

                        val imagenBase64 = jsonObject.getString("imagen")

                        // Mostrar datos en los elementos de la interfaz
                        nombreInput.setText(nombre)
                        apellidosInput.setText(apellidos)
                        correoInput.setText(correo)
                        usernameText.text = username
                        passwordInput.setText(pass)

                        // Cargar la imagen en el ImageView
                        if (imagenBase64.isNotEmpty()) {
                            val imageBytes = Base64.decode(imagenBase64, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            imageView.setImageBitmap(bitmap)
                        } else {
                            // Si no hay imagen, puedes mostrar una imagen por defecto
                            imageView.setImageResource(R.drawable.icon_account_circle)
                        }

                    } else {
                        Toast.makeText(this@modificar, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this@modificar, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                handleVolleyError(error)
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["usuario"] = username2.toString()
                return params
            }
        }

        queue.add(request)


        eliminarBtn.setOnClickListener {

            eliminarUsuario()
            //Toast.makeText(this, "*no hace nada*", Toast.LENGTH_SHORT).show()

            // Aquí puedes implementar la lógica para modificar los datos del usuario
            // por ejemplo, realizar una nueva solicitud al servidor para guardar los cambios
        }
        // Configurar el botón de modificar
        modificarBtn.setOnClickListener {

            if (isNetworkAvailable()) {
                modificarUsuarioDesdeUI()
            }else{
                Toast.makeText(this, "Se necesita estar en línea", Toast.LENGTH_SHORT).show()

            }
            // Aquí puedes implementar la lógica para modificar los datos del usuario
            // por ejemplo, realizar una nueva solicitud al servidor para guardar los cambios
        }

        // Configurar el botón para seleccionar imagen
        selectImageBtn.setOnClickListener {

            pickImage.launch("image/*")

        // Aquí puedes implementar la lógica para seleccionar una imagen desde la galería
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }



    private fun modificarUsuarioDesdeUI() {
        val nombre = nombreInput.text.toString()
        val apellidos = apellidosInput.text.toString()
        val correo = correoInput.text.toString()
        val password = passwordInput.text.toString()

        // Verificar si hay campos vacíos
        if (nombre.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || password.isEmpty()) {
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

        modificarUsuario(nombre, apellidos, correo, password, imagenBase64)



    }

    private fun modificarUsuario(nombre: String, apellidos: String, correo: String, password: String, imagenBase64: String) {
        val params = JSONObject()
        params.put("username", userManager.getUser()?.username.toString())

        params.put("nombre", nombre)
        params.put("apellidos", apellidos)
        params.put("correo", correo)
        params.put("password", password)
        params.put("imagenBase64", imagenBase64)

        Log.d("Perfil", "Username: ${userManager.getUser()?.username}")



        //val url = "http://192.168.31.198/servinear/p2.php"
        val url = "http://74.235.95.67/api/updateuser.php"
        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                val user = userManager.getUser()?.let {
                    User(nombre, apellidos, correo,
                        userManager.getUser()?.username.toString(), password, it.esPrestador, imagenBase64)

                }
                if (user != null) {
                    UserManager.getInstance(this).uploadUser(user)
                }
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error al registrar: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("RegistroActivity", "Error al registrar: ${error.message}")

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

    private fun eliminarUsuario() {
        val params = JSONObject()
        params.put("username", userManager.getUser()?.username.toString())



        Log.d("Perfil", "Username: ${userManager.getUser()?.username}")



        //val url = "http://192.168.31.198/servinear/p2.php"
        val url = "http://74.235.95.67/api/bajauser.php"
        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(this, "Eliminación exitosa", Toast.LENGTH_SHORT).show()

                userManager.clearUser()
                // Redireccionar a la actividad de inicio de sesión
                val intent = Intent(this, inicio_sesion::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error al registrar: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("RegistroActivity", "Error al registrar: ${error.message}")

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

    private fun handleVolleyError(error: VolleyError) {
        error.printStackTrace()

        val errorMessage = when (error) {
            is TimeoutError -> "Tiempo de espera agotado. Inténtalo de nuevo."
            is VolleyError -> "Error de Volley: ${error.message}"
            else -> "Error al conectar con el servidor."
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
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