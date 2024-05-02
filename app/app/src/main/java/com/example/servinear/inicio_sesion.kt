package com.example.servinear

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.view.View


class inicio_sesion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)
        val btn: Button = findViewById(R.id.register_btn)

        val btn1: Button = findViewById(R.id.login_btn)

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