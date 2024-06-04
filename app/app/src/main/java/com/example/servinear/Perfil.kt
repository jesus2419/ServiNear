package com.example.servinear

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Perfil : AppCompatActivity() {
    private lateinit var userImage: ImageView
    private lateinit var username: TextView
    private lateinit var email: TextView
    private lateinit var firstName: TextView
    private lateinit var lastName: TextView
    private lateinit var modificarBtn: Button
    private lateinit var cerrarBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // Inicializar las vistas
        userImage = findViewById(R.id.user_image)
        username = findViewById(R.id.username)
        email = findViewById(R.id.email)
        firstName = findViewById(R.id.first_name)
        lastName = findViewById(R.id.last_name)
        modificarBtn = findViewById(R.id.modificar_btn)
        cerrarBtn = findViewById(R.id.cerrar_btn)

        // Cargar y mostrar los datos del perfil
        loadProfileData()

        // Configurar el listener para el botón de modificar cuenta
        modificarBtn.setOnClickListener {
            // Agrega la lógica para modificar la cuenta aquí
            val intent = Intent(this, modificar::class.java)
            startActivity(intent)
        }


        // Configurar el listener para el botón de cerrar sesión
        cerrarBtn.setOnClickListener {
            // Borrar todos los datos del usuario
            UserManager.getInstance(this).clearUser()

            // Abrir la actividad de inicio de sesión
            val intent = Intent(this, inicio_sesion::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Mostrar los datos del usuario en la consola
        showUserDataInConsole()
    }

    private fun loadProfileData() {
        val userManager = UserManager.getInstance(this)
        val user = userManager.getUser()

        user?.let {
            username.text = it.username
            email.text = it.correo
            firstName.text = it.nombre
            lastName.text = it.apellidos

            // Decodificar la imagen de base64 a Bitmap y mostrarla
            if (it.imagenBase64.isNotEmpty()) {
                val userBitmap = decodeBase64ToBitmap(it.imagenBase64)
                if (userBitmap != null) {
                    userImage.setImageBitmap(userBitmap)
                }
            }
        }
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

    private fun showUserDataInConsole() {
        val userManager = UserManager.getInstance(this)
        val user = userManager.getUser()

        user?.let {
            Log.d("Perfil", "Username: ${it.username}")
            Log.d("Perfil", "Email: ${it.correo}")
            Log.d("Perfil", "First Name: ${it.nombre}")
            Log.d("Perfil", "Last Name: ${it.apellidos}")
            Log.d("Perfil", "User Image Base64: ${it.imagenBase64}")
        }
    }
}
