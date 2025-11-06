package com.example.myapplication.activities

import android.os.Bundle
import com.example.myapplication.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class SobreNosActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sobre_nos)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        setupBottomNavigation(navView, R.id.nav_contact)

        // Não há lógica de negócio complexa, apenas a exibição das informações.
        // O atributo android:autoLink="email" no XML já torna os links clicáveis.
    }
}
