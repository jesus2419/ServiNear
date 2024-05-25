package com.example.servinear

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class servicio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servicio)
        // Recibir la información del servicio desde el intent
        val nombre = intent.getStringExtra("nombre")
        val descripcion = intent.getStringExtra("descripcion")
        val contacto = intent.getStringExtra("contacto")
        val precioHora = intent.getStringExtra("precio_hora")
        val imagenByteArray = intent.getByteArrayExtra("imagen")

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
    }
}