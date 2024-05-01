package com.example.servinear

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn: Button = findViewById(R.id.register_btn)

        val btn1: Button = findViewById(R.id.login_btn)

        btn1.setOnClickListener {

            val intent: Intent = Intent(this, inicio:: class.java)
            startActivity(intent)
        }


        btn.setOnClickListener {

            val intent: Intent = Intent(this, registro:: class.java)
            startActivity(intent)
        }



    }
}