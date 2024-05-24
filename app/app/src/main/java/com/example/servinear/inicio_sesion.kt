package com.example.servinear

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class inicio_sesion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)
        val btn: Button = findViewById(R.id.register_btn)

        val btn1: Button = findViewById(R.id.login_btn)

        val btn2: Button = findViewById(R.id.test_btn)

        btn2.setOnClickListener {

            val intent: Intent = Intent(this, registrar_servicio:: class.java)
            startActivity(intent)
            finish()
        }

        btn1.setOnClickListener {

            val intent: Intent = Intent(this, inicio:: class.java)
            startActivity(intent)
            finish()
        }


        btn.setOnClickListener {

            val intent: Intent = Intent(this, registro:: class.java)
            startActivity(intent)
            finish()

        }
    }
}