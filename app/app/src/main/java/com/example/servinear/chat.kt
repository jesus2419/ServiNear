package com.example.servinear

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class chat : AppCompatActivity() {

    private lateinit var nombreInfo: TextView
    private lateinit var correoInfo: TextView
    private lateinit var imagenInfo: ImageView
    private lateinit var enviarbtn: Button
    private lateinit var mensajeInput: EditText
    private lateinit var userManager: UserManager
    private lateinit var chatContainer: LinearLayout





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Inicializar los elementos de la interfaz
        userManager = UserManager.getInstance(this)

        nombreInfo = findViewById(R.id.nombre_info)
        correoInfo = findViewById(R.id.correo)
        imagenInfo = findViewById(R.id.imagen_info)
        enviarbtn = findViewById(R.id.send_button)
        mensajeInput = findViewById(R.id.message_input)
        chatContainer = findViewById(R.id.chat_container)




        // Obtener username e id_usuario de la intención
        val username = intent.getStringExtra("USERNAME")
        val idUsuario = intent.getStringExtra("id")
        val username2 = userManager.getUser()?.username  // Obtener el nombre de usuario de UserManager


        Log.d("ChatActivity", "Username: $username")
        Log.d("ChatActivity", "id: $idUsuario")

        // Cargar datos del perfil
        loadProfileData(idUsuario)

        if (idUsuario != null) {

                if (username2 != null) {
                    obtenerMensajesDesdeServidor(idUsuario, username2)
                }

        }



        // Botón para registrar servicio
        enviarbtn.setOnClickListener {
            val mensaje = mensajeInput.text.toString().trim()


            if (mensaje.isEmpty()) {
                Toast.makeText(this, "mensaje vacío", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(this, "Enviando...", Toast.LENGTH_SHORT).show()


                sendMessage(idUsuario, mensaje, username2)

            }

        }
    }

    private fun sendMessage(idDestinatario: String?, contenido: String, remitente: String?) {
        if (idDestinatario == null || remitente == null) {
            Toast.makeText(this, "Datos del mensaje incompletos", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://74.235.95.67/api/enviarMensaje.php"
        val queue: RequestQueue = Volley.newRequestQueue(this@chat)
        val request = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                // Imprimir la respuesta del servidor para depuración
                Log.d("SendMessageResponse", response)
                try {
                    val jsonObject = JSONObject(response)
                    val success = jsonObject.getBoolean("success")

                    if (success) {
                        Toast.makeText(this@chat, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                    } else {
                        Toast.makeText(this@chat, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this@chat, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                handleVolleyError(error)
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["idDestinatario"] = idDestinatario
                params["contenido"] = contenido
                params["remitente"] = remitente
                return params
            }
        }

        queue.add(request)
    }

    private fun limpiarCampos() {
        mensajeInput.text.clear()

    }

    private fun obtenerMensajesDesdeServidor(idUsuario : String, usuario : String) {
        val url = "http://74.235.95.67/api/mostrarmensaje.php"
        val queue: RequestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    val jsonArray = JSONArray(response)
                    if (jsonArray.length() > 0) {
                        Log.d("Mensajes", "Número de mensajes recibidos: ${jsonArray.length()}")
                        for (i in 0 until jsonArray.length()) {
                            val chatMessage = jsonArray.getJSONObject(i)
                            val remitente = chatMessage.getString("Remitente")
                            val destinatario = chatMessage.getString("Destinatario")
                            val fecha = chatMessage.getString("fecha_de_creacion")
                            val contenido = chatMessage.getString("contenido")

                            val username = userManager.getUser()?.username

                            // Determinar el layout según el remitente y el usuario actual
                            val layoutId = if (remitente == username) {
                                crearLayoutRemitente(remitente, contenido)
                            } else {
                                crearLayoutDestinatario(destinatario, contenido)
                            }

                            // Mostrar el mensaje en el layout correspondiente
                            chatContainer.addView(layoutId)
                        }
                    } else {

                        showErrorToast("No se encontraron mensajes")
                        Log.d("responseeee", "res: $jsonArray")
                        Log.d("responseeee", "id: $idUsuario")
                        Log.d("responseeee", "user: $usuario")


                        // Puedes intentar cargar mensajes locales si no hay datos remotos
                        // cargarMensajesLocales()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    showErrorToast("Error al procesar la respuesta del servidor")
                    // Puedes intentar cargar mensajes locales en caso de error
                    // cargarMensajesLocales()
                }
            },
            Response.ErrorListener { error ->
                handleVolleyError(error)
                // Puedes intentar cargar mensajes locales en caso de error
                // cargarMensajesLocales()
            }) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["remitente"] = usuario
                params["idDestinatario"] = idUsuario
                return params
            }
        }

        queue.add(stringRequest)
    }


    private fun crearLayoutRemitente(remitente: String, contenido: String): LinearLayout {
        // Crear layout para remitente
        val remitenteLayout = LinearLayout(this)
        remitenteLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        remitenteLayout.orientation = LinearLayout.VERTICAL
        remitenteLayout.gravity = Gravity.END
        remitenteLayout.setPadding(16, 8, 16, 8)

        // TextView para el remitente
        val remitenteTextView = TextView(this)
        remitenteTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        remitenteTextView.text = remitente
        remitenteTextView.textSize = 16f
        remitenteTextView.setTextColor(resources.getColor(R.color.white))
        remitenteTextView.gravity = Gravity.END

        // TextView para el contenido
        val contenidoTextView = TextView(this)
        contenidoTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        contenidoTextView.text = contenido
        contenidoTextView.textSize = 14f
        contenidoTextView.setTextColor(resources.getColor(R.color.white))
        contenidoTextView.gravity = Gravity.END

        // Agregar TextViews al layout
        remitenteLayout.addView(remitenteTextView)
        remitenteLayout.addView(contenidoTextView)

        return remitenteLayout
    }

    private fun crearLayoutDestinatario(destinatario: String, contenido: String): LinearLayout {
        // Crear layout para destinatario
        val destinatarioLayout = LinearLayout(this)
        destinatarioLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        destinatarioLayout.orientation = LinearLayout.VERTICAL
        destinatarioLayout.gravity = Gravity.START
        destinatarioLayout.setPadding(16, 8, 16, 8)
        destinatarioLayout.setBackgroundResource(R.drawable.rounded_background)

        // TextView para el destinatario
        val destinatarioTextView = TextView(this)
        destinatarioTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        destinatarioTextView.text = destinatario
        destinatarioTextView.textSize = 16f
        destinatarioTextView.setTextColor(resources.getColor(R.color.white))
        destinatarioTextView.gravity = Gravity.START

        // TextView para el contenido
        val contenidoTextView = TextView(this)
        contenidoTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        contenidoTextView.text = contenido
        contenidoTextView.textSize = 14f
        contenidoTextView.setTextColor(resources.getColor(R.color.white))
        contenidoTextView.gravity = Gravity.START

        // Agregar TextViews al layout
        destinatarioLayout.addView(destinatarioTextView)
        destinatarioLayout.addView(contenidoTextView)

        return destinatarioLayout
    }





    private fun loadProfileData(idUsuario: String?) {
        // Verificar que idUsuario no sea nulo
        if (idUsuario == null) {
            Toast.makeText(this, "ID de usuario no proporcionado", Toast.LENGTH_SHORT).show()
            return
        }

        // Realizar la petición al servidor para obtener los datos del usuario
        val url = "http://74.235.95.67/api/id_obtener_datos_usuario.php"
        val queue: RequestQueue = Volley.newRequestQueue(this@chat)
        val request = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val success = jsonObject.getBoolean("success")

                    if (success) {
                        // Obtener datos del usuario
                        val nombre = jsonObject.getString("nombre")
                        val correo = jsonObject.getString("correo")
                        val imagenBase64 = jsonObject.getString("imagen")

                        // Mostrar datos en los elementos de la interfaz
                        nombreInfo.text = nombre
                        correoInfo.text = correo

                        // Cargar la imagen en el ImageView
                        if (imagenBase64.isNotEmpty()) {
                            val imageBytes = Base64.decode(imagenBase64, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            imagenInfo.setImageBitmap(bitmap)
                        } else {
                            // Si no hay imagen, puedes mostrar una imagen por defecto
                            imagenInfo.setImageResource(R.drawable.icon_account_circle)
                        }

                    } else {
                        Toast.makeText(this@chat, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this@chat, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                handleVolleyError(error)
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["idUsuario"] = idUsuario
                return params
            }
        }

        queue.add(request)
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
