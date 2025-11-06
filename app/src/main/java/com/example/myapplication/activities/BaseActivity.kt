package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * BaseActivity gerencia a lógica da BottomNavigationView.
 * Todas as Activities principais (Home, Busca, etc.) devem herdar dela.
 */
// 'open' permite que a classe seja herdada
open class BaseActivity : AppCompatActivity() {

    protected lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // A Activity filha (ex: HomeActivity) chamará o setContentView() primeiro
    }

    /**
     * Configura a lógica de navegação.
     * @param navView O componente BottomNavigationView do layout da Activity filha.
     * @param currentScreenId O ID do item de menu que deve ser marcado como ativo.
     */
    protected fun setupBottomNavigation(navView: BottomNavigationView, currentScreenId: Int) {
        this.bottomNavigationView = navView

        // Marca o item correto como selecionado
        bottomNavigationView.selectedItemId = currentScreenId

        // Define o listener
        bottomNavigationView.setOnItemSelectedListener { item ->
            // Evita recarregar a tela se o usuário clicar no ícone da tela atual
            if (item.itemId == currentScreenId) {
                return@setOnItemSelectedListener false
            }

            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish() // Fecha a tela atual
                }
                R.id.nav_search -> {
                    startActivity(Intent(this, BuscaActivity::class.java))
                    finish()
                }
                R.id.nav_contact -> {
                    startActivity(Intent(this, SobreNosActivity::class.java))
                    finish()
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    finish()
                }
            }
            true
        }
    }
}