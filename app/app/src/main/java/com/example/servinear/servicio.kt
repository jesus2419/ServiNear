package com.example.servinear


import ServicioSeleccionado
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
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

class servicio : AppCompatActivity() {
    private lateinit var userManager: UserManager
    private lateinit var userImage: ImageView
    private lateinit var usernametext: TextView
    private lateinit var email: TextView
    private lateinit var firstName: TextView
    private lateinit var lastName: TextView
    private lateinit var chatbtn: Button

    private lateinit var username: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servicio)

        userManager = UserManager.getInstance(this)

        userImage = findViewById(R.id.user_image)
        usernametext = findViewById(R.id.username)
        email = findViewById(R.id.email)
        firstName = findViewById(R.id.first_name)
        lastName = findViewById(R.id.last_name)
        chatbtn = findViewById(R.id.chat_btn)


        // Obtener la instancia del servicio seleccionado
        val servicioSeleccionado = ServicioSeleccionado.getInstance()

        // Obtener los datos del servicio seleccionado desde el singleton
        val nombre = servicioSeleccionado.nombre
        val descripcion = servicioSeleccionado.descripcion
        val contacto = servicioSeleccionado.contacto
        val precioHora = servicioSeleccionado.precioHora
        val imagenByteArray = servicioSeleccionado.imagen

        // Convertir el ByteArray de la imagen a Bitmap
        val imagenBitmap = BitmapFactory.decodeByteArray(imagenByteArray, 0, imagenByteArray!!.size)

        // Asignar los datos a los elementos del layout
        val nombreTextView = findViewById<TextView>(R.id.nombre_servicio_textview)
        val descripcionTextView = findViewById<TextView>(R.id.descripcion_servicio_textview)
        val contactoTextView = findViewById<TextView>(R.id.contacto_servicio_textview)
        val precioHoraTextView = findViewById<TextView>(R.id.precio_hora_servicio_textview)
        val imagenView = findViewById<ImageView>(R.id.imagen_servicio_imageview)

        nombreTextView.text = nombre
        descripcionTextView.text = descripcion
        contactoTextView.text = contacto
        precioHoraTextView.text = precioHora
        imagenView.setImageBitmap(imagenBitmap)

        loadProfileData()

        chatbtn.setOnClickListener {
            val username2 = userManager.getUser()?.username  // Obtener el nombre de usuario de UserManager
            Log.d("Perfil", "Username: ${username2}")
            Log.d("Perfil2", "Username2: ${username}")
            val id_user = ServicioSeleccionado.getInstance().idServicio.toString()
            Log.d("Perfil3", "id: ${id_user}")



            if (username == username2 ){
                Toast.makeText(this, "Usted es el creador de este servicio", Toast.LENGTH_SHORT).show()

            }else  {
                val intent = Intent(this, chat::class.java)
                intent.putExtra("USERNAME", username)
                intent.putExtra("id", id_user)
                startActivity(intent)

            }

        }

        // Configurar la visibilidad del botón de modificar según el tipo de usuario

    }




    private fun loadProfileData() {

        // Realizar la petición al servidor para obtener los datos del usuario
        val url = "http://74.235.95.67/api/id_obtener_datos_usuario.php"
        val queue: RequestQueue = Volley.newRequestQueue(this@servicio)
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
                        username = jsonObject.getString("usuario")
                        val pass = jsonObject.getString("pass")

                        val imagenBase64 = jsonObject.getString("imagen")



                        // Mostrar datos en los elementos de la interfaz
                        firstName.setText(nombre)
                        lastName.setText(apellidos)
                        email.setText(correo)
                        usernametext.text = username


                        // Cargar la imagen en el ImageView
                        if (imagenBase64.isNotEmpty()) {
                            val imageBytes = Base64.decode(imagenBase64, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            userImage.setImageBitmap(bitmap)
                        } else {
                            // Si no hay imagen, puedes mostrar una imagen por defecto
                            userImage.setImageResource(R.drawable.icon_account_circle)
                        }

                    } else {
                        Toast.makeText(this@servicio, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this@servicio, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                handleVolleyError(error)
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["idUsuario"] =  ServicioSeleccionado.getInstance().idServicio.toString()
                return params
            }
        }

        queue.add(request)

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
