package com.example.servinear

import HomeFragment
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity2 : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var userImage: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var navMenu: Menu

    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        userImage = navigationView.getHeaderView(0).findViewById(R.id.user_image)
        usernameTextView = navigationView.getHeaderView(0).findViewById(R.id.username_text_view)
        emailTextView = navigationView.getHeaderView(0).findViewById(R.id.email_text_view)
        navMenu = navigationView.menu

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        userManager = UserManager.getInstance(this)

        // Cargar los datos del usuario en el NavigationView
        loadUserData()

        // Mostrar u ocultar ítem del menú según si es prestador o no
        val user = userManager.getUser()
        if (user != null) {
            if (user.esPrestador) {
                navMenu.findItem(R.id.nav_servicio).isVisible = true
                navMenu.findItem(R.id.nav_agregarservicio).isVisible = true

            } else {
                navMenu.findItem(R.id.nav_servicio).isVisible = false
                navMenu.findItem(R.id.nav_agregarservicio).isVisible = false

            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    private fun loadUserData() {
        val user = userManager.getUser()

        usernameTextView.text = user?.username ?: ""
        emailTextView.text = user?.correo ?: ""

        // Decodificar la imagen de base64 a Bitmap y mostrarla en el ImageView
        val userBitmap = userManager.decodeBase64ToBitmap(user?.imagenBase64 ?: "")
        if (userBitmap != null) {
            userImage.setImageBitmap(userBitmap)
        } else {
            // Si la imagen no se pudo decodificar, puedes establecer una imagen por defecto aquí
            userImage.setImageResource(R.drawable.icon_account_circle)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
            R.id.nav_perfil -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ModificarPerfilFragment()).commit()
            R.id.nav_info -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, InfoFragment()).commit()
            R.id.nav_servicio -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MisServiciosFragment()).commit()

           /* R.id.nav_perfil -> {
                val intent = Intent(this, Perfil::class.java)
                startActivity(intent)
            }*/
            R.id.nav_agregarservicio -> {
                val intent = Intent(this, registrar_servicio::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                // Aquí deberías cerrar sesión y redirigir al inicio de sesión
                userManager.clearUser()
                // Redireccionar a la actividad de inicio de sesión
                val intent = Intent(this, inicio_sesion::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
