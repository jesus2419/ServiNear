package com.example.servinear



import DatabaseHelper
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.InputStream
import java.sql.Connection
import java.sql.SQLException


class prueba : AppCompatActivity() {

    private lateinit var nombreInput: EditText
    private lateinit var apellidosInput: EditText
    private lateinit var correoInput: EditText
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var prestadorSwitch: Switch
    private lateinit var registerButton: Button
    private lateinit var imageView: ImageView
    private lateinit var selectImageButton: Button
    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            imageView.setImageURI(it)

            // Verificar tamaño de la imagen seleccionada
            val inputStream: InputStream? = contentResolver.openInputStream(it)
            val bytes: ByteArray? = inputStream?.readBytes()
            bytes?.let {
                if (it.size > 60 * 1024) { // Verificar si el tamaño supera los 60 KB
                    selectedImageUri = null
                    imageView.setImageResource(android.R.color.transparent)
                    Toast.makeText(this, "La imagen no puede pesar más de 60 KB", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prueba)
        // Obtener referencias a los EditText y otros elementos de la vista
        nombreInput = findViewById(R.id.nombre_input)
        apellidosInput = findViewById(R.id.apellidos_input)
        correoInput = findViewById(R.id.correo_inout)
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        prestadorSwitch = findViewById(R.id.prestador_switch)
        registerButton = findViewById(R.id.register_btn)
        imageView = findViewById(R.id.image_view)
        selectImageButton = findViewById(R.id.select_image_btn)

        // Solicitar permiso de lectura de almacenamiento
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }

        // Agregar un listener al botón de seleccionar imagen
        selectImageButton.setOnClickListener {
            pickImage.launch("image/*")
        }

        // Agregar un listener al botón de registro
        registerButton.setOnClickListener {
            // Obtener los valores de los EditText
            val nombre = nombreInput.text.toString()
            val apellidos = apellidosInput.text.toString()
            val correo = correoInput.text.toString()
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val esPrestador = prestadorSwitch.isChecked

            // Verificar si hay campos vacíos
            if (nombre.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificar si se ha seleccionado una imagen y si su tamaño es válido
            if (selectedImageUri == null) {
                Toast.makeText(this, "Seleccione una imagen", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val imagenBase64 = if (selectedImageUri != null) {
                convertImageToBase64(selectedImageUri!!)
            } else {
                ""
            }

            // Verificar si el nombre de usuario ya está registrado
            verificarUsuarioExistente(username) { existe ->
                if (existe) {
                    Toast.makeText(this, "El nombre de usuario ya está registrado", Toast.LENGTH_SHORT).show()
                } else {
                    registrarUsuario(nombre, apellidos, correo, username, password, esPrestador, imagenBase64)
                }
            }
        }
    }

    private fun convertImageToBase64(uri: Uri): String {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val bytes: ByteArray? = inputStream?.readBytes()
        bytes?.let {
            return Base64.encodeToString(it, Base64.DEFAULT)
        }
        return ""
    }

    private fun verificarUsuarioExistente(username: String, callback: (Boolean) -> Unit) {
        val connection: Connection? = DatabaseHelper.connect()
        val query = "SELECT ID FROM Usuarios WHERE usuario = ?"

        try {
            val statement = connection?.prepareStatement(query)
            statement?.setString(1, username)
            val resultSet = statement?.executeQuery()
            if (resultSet != null && resultSet.next()) {
                callback(true)
            } else {
                callback(false)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            callback(false)
        } finally {
            connection?.close()
        }
    }

    private fun registrarUsuario(nombre: String, apellidos: String, correo: String, username: String, password: String, esPrestador: Boolean, imagenBase64: String) {
        val connection: Connection? = DatabaseHelper.connect()
        val query = "INSERT INTO Usuarios (Nombre, Apellidos, Correo, usuario, pass, id_rol, Foto) VALUES (?, ?, ?, ?, ?, ?, ?)"

        try {
            val statement = connection?.prepareStatement(query)
            statement?.setString(1, nombre)
            statement?.setString(2, apellidos)
            statement?.setString(3, correo)
            statement?.setString(4, username)
            statement?.setString(5, password)
            statement?.setBoolean(6, esPrestador)
            statement?.setString(7, imagenBase64)

            val rowsInserted = statement?.executeUpdate()
            if (rowsInserted != null && rowsInserted > 0) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()

                // Guardar los datos en SharedPreferences
                val sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                editor.putString("nombre", nombre)
                editor.putString("apellidos", apellidos)
                editor.putString("correo", correo)
                editor.putString("username", username)
                editor.putString("password", password)
                editor.putBoolean("esPrestador", esPrestador)
                editor.putString("imagenBase64", imagenBase64)

                // Confirmar los cambios
                editor.apply()

                // Decidir a qué actividad dirigir al usuario
                if (esPrestador) {
                    val intent = Intent(this, registrar_servicio::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, inicio::class.java)
                    startActivity(intent)
                }
                finish()
            } else {
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al registrar: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            connection?.close()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }
}