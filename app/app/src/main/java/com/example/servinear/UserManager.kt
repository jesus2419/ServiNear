package com.example.servinear

import android.content.Context
import android.content.SharedPreferences

data class User(
    val nombre: String,
    val apellidos: String,
    val correo: String,
    val username: String,
    val password: String,
    val esPrestador: Boolean,
    val imagenBase64: String
)

class UserManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var INSTANCE: UserManager? = null

        fun getInstance(context: Context): UserManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserManager(context).also { INSTANCE = it }
            }
        }
    }

    fun saveUser(user: User) {
        with(sharedPreferences.edit()) {
            putString("nombre", user.nombre)
            putString("apellidos", user.apellidos)
            putString("correo", user.correo)
            putString("username", user.username)
            putString("password", user.password)
            putBoolean("esPrestador", user.esPrestador)
            putString("imagenBase64", user.imagenBase64)
            apply()
        }
    }



    fun getUser(): User? {
        val nombre = sharedPreferences.getString("nombre", null) ?: return null
        val apellidos = sharedPreferences.getString("apellidos", null) ?: return null
        val correo = sharedPreferences.getString("correo", null) ?: return null
        val username = sharedPreferences.getString("username", null) ?: return null
        val password = sharedPreferences.getString("password", null) ?: return null
        val esPrestador = sharedPreferences.getBoolean("esPrestador", false)
        val imagenBase64 = sharedPreferences.getString("imagenBase64", null) ?: return null

        return User(nombre, apellidos, correo, username, password, esPrestador, imagenBase64)
    }

    fun clearUser() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}
