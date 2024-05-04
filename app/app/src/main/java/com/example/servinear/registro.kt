package com.example.servinear

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class registro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        val btn: Button = findViewById(R.id.iniciar_btn)

        val btn1: Button = findViewById(R.id.register_btn)

        btn1.setOnClickListener {

            val intent: Intent = Intent(this, inicio:: class.java)
            startActivity(intent)
            finish()

        }

        btn.setOnClickListener {

            val intent: Intent = Intent(this, inicio_sesion:: class.java)
            startActivity(intent)
            finish()

        }




    }
}