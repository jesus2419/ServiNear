package com.example.servinear

import ServicioSeleccionado
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class servicio : AppCompatActivity() {
    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servicio)

        userManager = UserManager.getInstance(this)

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

        // Configurar la visibilidad del botón de modificar según el tipo de usuario

    }
}
