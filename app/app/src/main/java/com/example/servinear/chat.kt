package com.example.servinear


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
class chat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val username = intent.getStringExtra("USERNAME")
        Log.d("ChatActivity", "Username: $username")
        val id_usuario = intent.getStringExtra("id")
        Log.d("ChatActivity", "id: $id_usuario")
    }
}