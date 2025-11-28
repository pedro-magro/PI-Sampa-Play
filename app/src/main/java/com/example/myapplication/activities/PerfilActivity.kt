package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.example.myapplication.remote.SessionManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityPerfilBinding
import com.example.myapplication.remote.RetrofitClient
import com.example.myapplication.repositories.AuthRepository
import com.example.myapplication.screenViewModels.PerfilViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class PerfilActivity : BaseActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var viewModel: PerfilViewModel

    private val idiomasMap = mapOf(
        "Português (Brasil)" to "pt",
        "English" to "en",
        "Español" to "es"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation(binding.bottomNavigation, R.id.nav_profile)

        setupViewModel()
        setupIdiomas()
        setupObservers()
        setupListeners()

        viewModel.getPerfilLocal()
    }

    private fun setupViewModel() {
        val session = SessionManager(this)
        val api = RetrofitClient.getInstance { session.getToken() }
        val repo = AuthRepository(api, session)

        val factory = object: ViewModelProvider.Factory {
            override fun<T: ViewModel> create(modelClass: Class<T>): T{
                return PerfilViewModel(repo) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[PerfilViewModel::class.java]
    }

    private fun setupIdiomas(){
        val nomesIdiomas = idiomasMap.keys.toList()

        val adapter = ArrayAdapter(
            this, android.R.layout.simple_list_item_1, nomesIdiomas

        )

        binding.actvIdioma.setAdapter(adapter)
        binding.actvIdioma.setOnItemClickListener { parent, _, position, _ ->
            val nomeSelecionado = parent.getItemAtPosition(position) as String
            val codigoIdioma = idiomasMap[nomeSelecionado]

            if(codigoIdioma != null) {
                mudarIdiomaApp(codigoIdioma)
            }
        }
    }

    private fun mudarIdiomaApp(languageCode: String) {
        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    private fun setupListeners(){
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun setupObservers(){
        viewModel.perfil.observe(this) { perfil ->
            if(perfil != null){
                binding.tvPerfilNome.text = perfil.nome
                binding.tvPerfilEmail.text = perfil.email

            }
        }

        viewModel.loading.observe(this){ isLoading ->
            //binding.progressBar.visibility = if(isLoading) View.VISIBLE else View.GONE
            toggleLoading(isLoading, binding.progressBar)
        }

        viewModel.error.observe(this){ errorMsg ->
            if (errorMsg != null) {
                when(errorMsg) {
                    "Erro ao carregar perfil" -> Toast.makeText(this, getString(R.string.erro_carregar_perfil), Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
                viewModel.limparErro()
            }
        }

        viewModel.logoutSuccess.observe(this) { success ->
            if(success){
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }
}