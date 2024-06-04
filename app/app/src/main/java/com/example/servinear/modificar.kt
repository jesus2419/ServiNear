package com.example.servinear

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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


class modificar : AppCompatActivity() {
    private lateinit var nombreInput: EditText
    private lateinit var apellidosInput: EditText
    private lateinit var correoInput: EditText
    private lateinit var usernameText: TextView
    private lateinit var passwordInput: EditText
    private lateinit var modificarBtn: Button
    private lateinit var imageView: ImageView
    private lateinit var selectImageBtn: Button

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userManager: UserManager


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

        // Configurar el botón de modificar
        modificarBtn.setOnClickListener {
            // Aquí puedes implementar la lógica para modificar los datos del usuario
            // por ejemplo, realizar una nueva solicitud al servidor para guardar los cambios
        }

        // Configurar el botón para seleccionar imagen
        selectImageBtn.setOnClickListener {
            // Aquí puedes implementar la lógica para seleccionar una imagen desde la galería
        }
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

}