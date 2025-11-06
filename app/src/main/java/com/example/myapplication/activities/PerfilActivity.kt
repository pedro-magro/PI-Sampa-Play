package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.R
import com.example.myapplication.util.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class PerfilActivity : BaseActivity() {

    private lateinit var tvNome: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        setupBottomNavigation(navView, R.id.nav_profile)

        tvNome = findViewById(R.id.tvPerfilNome)
        tvEmail = findViewById(R.id.tvPerfilEmail)
        btnLogout = findViewById(R.id.btnLogout)

        tvNome.text = SessionManager.getUserNome(this)
        tvEmail.text = SessionManager.getUserEmail(this)

        btnLogout.setOnClickListener {
            logout()
        }


    }

    private fun logout() {
        SessionManager.clearSession(this)
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

        startActivity(intent)
        finish()


    }
}