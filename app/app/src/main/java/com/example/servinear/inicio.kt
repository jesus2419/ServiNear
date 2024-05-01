package com.example.servinear

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class inicio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        val btn: Button = findViewById(R.id.descripcion_id)

        btn.setOnClickListener {

            val intent: Intent = Intent(this, servicio:: class.java)
            startActivity(intent)
        }

    }
}