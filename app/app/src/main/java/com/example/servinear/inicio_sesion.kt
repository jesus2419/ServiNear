package com.example.servinear

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject


class inicio_sesion : AppCompatActivity() {
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginBtn: Button
    private lateinit var registerBtn: Button
    private lateinit var testBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        registerBtn = findViewById(R.id.register_btn)
        testBtn = findViewById(R.id.test_btn)

        // Iniciar sesión
        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Realizar la petición al servidor
            val url = "http://192.168.31.198/servinear/login_usuario.php"
            val queue: RequestQueue = Volley.newRequestQueue(this@inicio_sesion)
            val request = object : StringRequest(
                Request.Method.POST, url,
                Response.Listener { response ->
                    try {
                        val jsonObject = JSONObject(response)
                        val success = jsonObject.getBoolean("success")
                        val idUsuario = jsonObject.getInt("id")

                        if (success) {
                            // Guardar los datos en SharedPreferences
                            val sharedPreferences: SharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()

                            editor.putString("username", username)
                            editor.putInt("idUsuario", idUsuario)
                            editor.apply()

                            // Iniciar la pantalla de inicio
                            val intent = Intent(this@inicio_sesion, inicio::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@inicio_sesion, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@inicio_sesion, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener { error ->
                    handleVolleyError(error)
                }
            ) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["username"] = username
                    params["password"] = password
                    return params
                }
            }

            queue.add(request)
        }

        // Redireccionar a la pantalla de registro
        registerBtn.setOnClickListener {
            val intent = Intent(this@inicio_sesion, registro::class.java)
            startActivity(intent)
        }

        // Redireccionar a la pantalla de prueba (test)
        testBtn.setOnClickListener {
            val intent = Intent(this@inicio_sesion, registrar_servicio::class.java)
            startActivity(intent)
        }
    }

    private fun handleVolleyError(error: VolleyError) {
        error.printStackTrace()

        val errorMessage = when (error) {
            is TimeoutError -> "Tiempo de espera agotado. Inténtalo de nuevo."
            is VolleyError -> "Error de Volley: ${error.message}"
            else -> "Error al conectar con el servidor."
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}