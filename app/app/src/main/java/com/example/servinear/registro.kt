package com.example.servinear

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


class registro : AppCompatActivity() {

    private lateinit var nombreInput: EditText
    private lateinit var apellidosInput: EditText
    private lateinit var correoInput: EditText
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var prestadorSwitch: Switch
    private lateinit var registerButton: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Obtener referencias a los EditText y otros elementos de la vista
        nombreInput = findViewById(R.id.nombre_input)
        apellidosInput = findViewById(R.id.apellidos_input)
        correoInput = findViewById(R.id.correo_inout)
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        prestadorSwitch = findViewById(R.id.prestador_switch)
        registerButton = findViewById(R.id.register_btn)

        // Agregar un listener al botón de registro
        registerButton.setOnClickListener {
            // Obtener los valores de los EditText
            val nombre = nombreInput.text.toString()
            val apellidos = apellidosInput.text.toString()
            val correo = correoInput.text.toString()
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val esPrestador = prestadorSwitch.isChecked


            val params = JSONObject()
            params.put("nombre", nombre)
            params.put("apellidos", apellidos)
            params.put("correo", correo)
            params.put("username", username)
            params.put("password", password)
            params.put("esPrestador", esPrestador)

            // URL del servidor donde está el script PHP para manejar la solicitud POST
            val url = "http://192.168.31.198/servinear/procesar_registro.php"

            // Crear una solicitud POST utilizando Volley
            val request = object : StringRequest(
                Request.Method.POST, url,
                Response.Listener { response ->
                    // Manejar la respuesta del servidor si es necesario
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                },
                Response.ErrorListener { error ->
                    // Manejar errores de la solicitud
                    val errorMessage = "Error al registrar: ${error.message}"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    Log.e("RegistroActivity", errorMessage)
                }) {
                override fun getParams(): Map<String, String> {
                    // Convertir el objeto JSONObject a un Map<String, String>
                    val paramsMap = HashMap<String, String>()
                    for (key in params.keys()) {
                        paramsMap[key] = params.getString(key)
                    }
                    return paramsMap
                }
            }

            // Agregar la solicitud a la cola de Volley para que se envíe
            Volley.newRequestQueue(this).add(request)
        }



        /*
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

        */





    }
}