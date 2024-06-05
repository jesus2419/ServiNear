package com.example.servinear

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible

class miServicio : AppCompatActivity() {
    private lateinit var userManager: UserManager
    private lateinit var modificar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mi_servicio)

        modificar = findViewById(R.id.modificar_btn)
        userManager = UserManager.getInstance(this)

        // Obtener la instancia del singleton ServicioSeleccionado
        val servicioSeleccionado = ServicioSeleccionado.getInstance()

        // Verificar que el servicio seleccionado no sea null y tenga datos
        if (servicioSeleccionado.idServicio != null) {
            // Asignar los datos a los elementos del layout
            val nombreTextView = findViewById<TextView>(R.id.nombre_servicio_textview)
            val descripcionTextView = findViewById<TextView>(R.id.descripcion_servicio_textview)
            val contactoTextView = findViewById<TextView>(R.id.contacto_servicio_textview)
            val precioHoraTextView = findViewById<TextView>(R.id.precio_hora_servicio_textview)
            val imagenView = findViewById<ImageView>(R.id.imagen_servicio_imageview)

            nombreTextView.text = servicioSeleccionado.nombre
            descripcionTextView.text = servicioSeleccionado.descripcion
            contactoTextView.text = servicioSeleccionado.contacto
            precioHoraTextView.text = servicioSeleccionado.precioHora

            // Decodificar y establecer la imagen si está disponible
            servicioSeleccionado.imagen?.let { byteArray ->
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                imagenView.setImageBitmap(bitmap)
            }
        } else {
            Toast.makeText(this, "Error: No hay datos del servicio seleccionado", Toast.LENGTH_SHORT).show()
            // Manejar el caso donde los datos del servicio no están disponibles
            finish()  // Opcional: cerrar la actividad si no hay datos disponibles
        }

        modificar.setOnClickListener {
            //Toast.makeText(this, "*no hace nada*", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, modificarServicio::class.java)
            startActivity(intent)
        }

        val user = userManager.getUser()
        if (user != null) {
            modificar.isVisible = user.esPrestador
        }
    }
}
