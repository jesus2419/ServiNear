package com.example.servinear

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.util.TypedValue
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
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class inicio : AppCompatActivity() {
    private lateinit var serviciosContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        serviciosContainer  = findViewById(R.id.usuarios_container)

        // Hacer la solicitud HTTP para obtener los servicios
        val url = "http://192.168.31.198/servinear/obtener_servicios.php"
        val queue: RequestQueue = Volley.newRequestQueue(this)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                try {
                    // Verificar si la respuesta contiene datos válidos
                    if (response.length() > 0) {
                        Log.d("Servicios", "Número de servicios recibidos: ${response.length()}")
                        for (i in 0 until response.length()) {
                            val servicio = response.getJSONObject(i)
                            Log.d("Servicio", servicio.toString())

                            // Obtener los datos del servicio
                            val nombre = servicio.getString("Nombre")
                            val descripcion = servicio.getString("descripcion")
                            val imagenBase64 = servicio.getString("Foto")

                            // Decodificar la imagen de base64 a Bitmap
                            val imagenBitmap = decodeBase64ToBitmap(imagenBase64)
                            if (imagenBitmap != null) {
                                // Mostrar el servicio en la interfaz
                                mostrarServicio(nombre, descripcion, imagenBitmap)
                            } else {
                                Log.e("DecodeError", "Error al decodificar la imagen base64")
                            }
                        }
                    } else {
                        showErrorToast("No se encontraron servicios")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    showErrorToast("Error al procesar la respuesta del servidor")
                }
            },
            Response.ErrorListener { error ->
                handleVolleyError(error)
            }
        )

        // Agregar la solicitud a la cola de solicitudes
        queue.add(jsonArrayRequest)
    }

    private fun mostrarServicio(nombre: String, descripcion: String, imagen: Bitmap) {
        // Crear un nuevo contenedor para el servicio
        val servicioLayout = LinearLayout(this)
        servicioLayout.orientation = LinearLayout.HORIZONTAL
        servicioLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        servicioLayout.setPadding(0, 0, 0, dpToPx(16))  // Agregar padding inferior

        // ImageView para mostrar la imagen
        val imageView = ImageView(this)
        imageView.layoutParams = LinearLayout.LayoutParams(
            dpToPx(120),  // Ancho de la imagen
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        imageView.setImageBitmap(imagen)

        // Layout para los datos de nombre y descripción
        val datosLayout = LinearLayout(this)
        datosLayout.orientation = LinearLayout.VERTICAL
        datosLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        datosLayout.setPadding(dpToPx(16), 0, 0, 0)  // Agregar padding izquierdo

        // TextView para el nombre
        val nombreTextView = TextView(this)
        nombreTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        nombreTextView.text = nombre
        nombreTextView.textSize = 20f  // Tamaño del texto grande
        nombreTextView.setTextColor(resources.getColor(R.color.white))  // Color del texto blanco

        // TextView para la descripción
        val descripcionTextView = TextView(this)
        descripcionTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        descripcionTextView.text = descripcion
        descripcionTextView.textSize = 16f  // Tamaño del texto mediano
        descripcionTextView.setTextColor(resources.getColor(R.color.white))  // Color del texto blanco

        // Agregar los TextView al layout de datos
        datosLayout.addView(nombreTextView)
        datosLayout.addView(descripcionTextView)

        // Agregar la imagen y los datos al layout del servicio
        servicioLayout.addView(imageView)
        servicioLayout.addView(datosLayout)

        // Agregar el layout del servicio al contenedor principal
        serviciosContainer.addView(servicioLayout)
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }

    private fun handleVolleyError(error: VolleyError) {
        error.printStackTrace()

        val errorMessage = when (error) {
            is TimeoutError -> "Tiempo de espera agotado. Inténtalo de nuevo."
            is VolleyError -> "Error de Volley: ${error.message}"
            else -> "Error al conectar con el servidor."
        }

        showErrorToast(errorMessage)
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}