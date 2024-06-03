package com.example.servinear

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                // Verificar si existen datos en SharedPreferences
                val sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)
                val username = sharedPreferences.getString("username", null)
                val intent = if (username != null) {
                    // Si existe username, ir a inicio activity
                    Intent(this@MainActivity, MainActivity2::class.java)
                } else {
                    // Si no existe username, ir a inicio_sesion activity
                    Intent(this@MainActivity, inicio_sesion::class.java)
                }
                startActivity(intent)
                finish()
            }
        }

        val timer = Timer()
        timer.schedule(timerTask, 3000) // Aquí puedes ajustar el tiempo en milisegundos antes de que se inicie la actividad de inicio de sesión




    }
}