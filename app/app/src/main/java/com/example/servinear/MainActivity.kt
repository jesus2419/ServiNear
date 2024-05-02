package com.example.servinear

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.util.TimerTask
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                val intent = Intent(this@MainActivity, inicio_sesion::class.java)
                startActivity(intent)
                finish()
            }
        }

        val timer = Timer()
        timer.schedule(timerTask, 3000) // Aquí puedes ajustar el tiempo en milisegundos antes de que se inicie la actividad de inicio de sesión




    }
}