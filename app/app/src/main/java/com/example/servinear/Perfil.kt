package com.example.servinear



import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
    private lateinit var sharedPreferences: SharedPreferences

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

        // Obtener las preferencias compartidas
        sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)

        // Cargar y mostrar los datos del perfil
        loadProfileData()

        // Configurar el listener para el botón de modificar cuenta
        modificarBtn.setOnClickListener {
            // Agrega la lógica para modificar la cuenta aquí
        }

        // Configurar el listener para el botón de cerrar sesión
        cerrarBtn.setOnClickListener {
            // Borrar todas las preferencias compartidas
            with(sharedPreferences.edit()) {
                clear()
                apply()
            }

            // Abrir la actividad de inicio de sesión
            val intent = Intent(this, inicio_sesion::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Mostrar los datos de las preferencias en la consola
        showPreferencesDataInConsole()
    }

    private fun loadProfileData() {
        val usernameValue = sharedPreferences.getString("username", "Nombre de Usuario")
        val emailValue = sharedPreferences.getString("correo", "usuario@correo.com")
        val firstNameValue = sharedPreferences.getString("nombre", "Nombre")
        val lastNameValue = sharedPreferences.getString("apellidos", "Apellidos")
        val userImageBase64 = sharedPreferences.getString("imagenBase64", "")

        username.text = usernameValue
        email.text = emailValue
        firstName.text = firstNameValue
        lastName.text = lastNameValue

        // Decodificar la imagen de base64 a Bitmap y mostrarla
        if (!userImageBase64.isNullOrEmpty()) {
            val userBitmap = decodeBase64ToBitmap(userImageBase64)
            if (userBitmap != null) {
                userImage.setImageBitmap(userBitmap)
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

    private fun showPreferencesDataInConsole() {
        val usernameValue = sharedPreferences.getString("username", "Nombre de Usuario")
        val emailValue = sharedPreferences.getString("email", "usuario@correo.com")
        val firstNameValue = sharedPreferences.getString("first_name", "Nombre")
        val lastNameValue = sharedPreferences.getString("last_name", "Apellidos")
        val userImageBase64 = sharedPreferences.getString("user_image", "")

        Log.d("Perfil", "Username: $usernameValue")
        Log.d("Perfil", "Email: $emailValue")
        Log.d("Perfil", "First Name: $firstNameValue")
        Log.d("Perfil", "Last Name: $lastNameValue")
        Log.d("Perfil", "User Image Base64: $userImageBase64")
    }
}