package com.example.servinear

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class ModificarPerfilFragment : Fragment() {

    private lateinit var userImage: ImageView
    private lateinit var username: TextView
    private lateinit var email: TextView
    private lateinit var firstName: TextView
    private lateinit var lastName: TextView
    private lateinit var modificarBtn: Button
    private lateinit var cerrarBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_modificar_perfil, container, false)

        // Inicializar las vistas
        userImage = view.findViewById(R.id.user_image)
        username = view.findViewById(R.id.username)
        email = view.findViewById(R.id.email)
        firstName = view.findViewById(R.id.first_name)
        lastName = view.findViewById(R.id.last_name)
        modificarBtn = view.findViewById(R.id.modificar_btn)
        cerrarBtn = view.findViewById(R.id.cerrar_btn)

        // Cargar y mostrar los datos del perfil
        loadProfileData()

        // Configurar el listener para el botón de modificar cuenta
        modificarBtn.setOnClickListener {
            // Agrega la lógica para modificar la cuenta aquí
            val intent = Intent(activity, modificar::class.java)
            startActivity(intent)
        }



        // Configurar el listener para el botón de cerrar sesión
        cerrarBtn.setOnClickListener {
            // Borrar todos los datos del usuario
            UserManager.getInstance(requireActivity()).clearUser()

            // Abrir la actividad de inicio de sesión
            val intent = Intent(activity, inicio_sesion::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }

        // Mostrar los datos del usuario en la consola
        showUserDataInConsole()

        return view
    }

    private fun loadProfileData() {
        val userManager = UserManager.getInstance(requireActivity())
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
        val userManager = UserManager.getInstance(requireActivity())
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
