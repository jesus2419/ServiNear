
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.servinear.R
import com.example.servinear.servicio
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream

class HomeFragment : Fragment() {

    private lateinit var serviciosContainer: LinearLayout
    private lateinit var searchView: SearchView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        serviciosContainer = view.findViewById(R.id.usuarios_container)
        searchView = view.findViewById(R.id.search_view)

        // Inicialización de SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("DatosServicios", Context.MODE_PRIVATE)

        ServicioSeleccionado.getInstance().clear()

        // Configurar el listener para el SearchView
        setupSearchView()

        // Intentar cargar los servicios desde la base de datos remota
        obtenerServiciosDesdeServidor()

        return view
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterServicios(newText)
                return true
            }
        })
    }

    private fun filterServicios(query: String?) {
        val count = serviciosContainer.childCount
        for (i in 0 until count) {
            val child = serviciosContainer.getChildAt(i) as LinearLayout
            val textView = child.getChildAt(1) as LinearLayout // Acceder al layout de datos
            val nombreTextView = textView.getChildAt(0) as TextView // Nombre del servicio

            val userName = nombreTextView.text.toString()
            if (userName.contains(query ?: "", ignoreCase = true)) {
                child.visibility = View.VISIBLE
            } else {
                child.visibility = View.GONE
            }
        }
    }

    private fun obtenerServiciosDesdeServidor() {
        val url = "http://74.235.95.67/api/obtener_servicios.php"
        val queue: RequestQueue = Volley.newRequestQueue(requireActivity())
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                try {
                    if (response.length() > 0) {
                        Log.d("Servicios", "Número de servicios recibidos: ${response.length()}")
                        for (i in 0 until response.length()) {
                            val servicio = response.getJSONObject(i)
                            val id_usuario = servicio.getString("id_usuario")

                            val nombre = servicio.getString("Nombre")
                            val descripcion = servicio.getString("descripcion")
                            val contacto = servicio.getString("contacto")
                            val precio_hora = servicio.getString("precio_hora")

                            val imagenBase64 = servicio.getString("Foto")

                            // Decodificar la imagen de base64 a Bitmap
                            val imagenBitmap = decodeBase64ToBitmap(imagenBase64)
                            if (imagenBitmap != null) {
                                // Mostrar el servicio en la interfaz
                                mostrarServicio(id_usuario, nombre, descripcion, contacto, precio_hora, imagenBitmap)
                            } else {
                                Log.e("DecodeError", "Error al decodificar la imagen base64")
                            }
                        }
                    } else {
                        showErrorToast("No se encontraron servicios")
                        // Intentar cargar servicios locales si no hay datos remotos
                        //cargarServiciosLocales()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    showErrorToast("Error al procesar la respuesta del servidor")
                    // Intentar cargar servicios locales en caso de error
                    //cargarServiciosLocales()
                }
            },
            Response.ErrorListener { error ->
                handleVolleyError(error)
                // Intentar cargar servicios locales en caso de error
                //cargarServiciosLocales()
            }
        )

        queue.add(jsonArrayRequest)
    }

    private fun cargarServiciosLocales() {
        // Obtener la lista de servicios almacenada localmente
        val serviciosJson = sharedPreferences.getString("servicios", "[]")
        val serviciosArray = JSONArray(serviciosJson)

        if (serviciosArray.length() > 0) {
            Log.d("ServiciosLocales", "Número de servicios locales: ${serviciosArray.length()}")
            try {
                for (i in 0 until serviciosArray.length()) {
                    val servicioData = serviciosArray.getJSONObject(i)

                    val nombre = servicioData.getString("nombre")
                    val descripcion = servicioData.getString("descripcion")
                    val contacto = servicioData.getString("contacto")
                    val precio_hora = servicioData.getString("precio_hora")
                    val imagenBase64 = servicioData.getString("foto_base64")

                    // Decodificar la imagen de base64 a Bitmap
                    val imagenBitmap = decodeBase64ToBitmap(imagenBase64)
                    if (imagenBitmap != null) {
                        // Mostrar el servicio en la interfaz
                        mostrarServicio("", nombre, descripcion, contacto, precio_hora, imagenBitmap)
                    } else {
                        Log.e("DecodeError", "Error al decodificar la imagen base64")
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                showErrorToast("Error al procesar los servicios locales")
            }
        } else {
            showErrorToast("No se encontraron servicios locales")
        }
    }

    private fun mostrarServicio(id_user: String, nombre: String, descripcion: String, contacto: String, precio_hora: String, imagen: Bitmap) {
        // Crear un nuevo contenedor para el servicio
        val servicioLayout = LinearLayout(requireActivity())
        servicioLayout.orientation = LinearLayout.HORIZONTAL
        servicioLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        servicioLayout.setPadding(0, 0, 0, dpToPx(16))  // Agregar padding inferior
        servicioLayout.isClickable = true  // Hacer el layout clickable

        servicioLayout.setOnClickListener {
            // Convertir la imagen a ByteArray
            val byteArrayOutputStream = ByteArrayOutputStream()
            imagen.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            // Abrir la actividad de detalle del servicio
            val intent = Intent(activity, servicio::class.java)
            /*intent.putExtra("nombre", nombre)
            intent.putExtra("descripcion", descripcion)
            intent.putExtra("contacto", contacto)
            intent.putExtra("precio_hora", precio_hora)*/

            val servicioSeleccionado = ServicioSeleccionado.getInstance()
            servicioSeleccionado.setServicio(id_user, nombre, descripcion, contacto, precio_hora, byteArray)

            intent.putExtra("imagen", byteArray)  // Pasar el ByteArray en lugar del Bitmap
            startActivity(intent)
        }

        // ImageView para mostrar la imagen
        val imageView = ImageView(requireActivity())
        imageView.layoutParams = LinearLayout.LayoutParams(
            dpToPx(120),  // Ancho de la imagen
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        imageView.setImageBitmap(imagen)

        // Layout para los datos de nombre y descripción
        val datosLayout = LinearLayout(requireActivity())
        datosLayout.orientation = LinearLayout.VERTICAL
        datosLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        datosLayout.setPadding(dpToPx(16), 0, 0, 0)  // Agregar padding izquierdo

        // TextView para el nombre
        val nombreTextView = TextView(requireActivity())
        nombreTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        nombreTextView.text = nombre
        nombreTextView.textSize = 20f  // Tamaño del texto grande
        nombreTextView.setTextColor(resources.getColor(R.color.white))  // Color del texto blanco

        // TextView para la descripción
        val descripcionTextView = TextView(requireActivity())
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
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}
