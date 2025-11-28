package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.myapplication.remote.ApiService
import com.example.myapplication.data.CadastroResponse
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityCadastroBinding
import com.example.myapplication.remote.RetrofitClient
import com.example.myapplication.remote.SessionManager
import com.example.myapplication.repositories.AuthRepository
import com.example.myapplication.repositories.ZonaRepository
import com.example.myapplication.screenViewModels.CadastroViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CadastroActivity : AppCompatActivity() {

    // 1. Declaração das variáveis
    private lateinit var binding: ActivityCadastroBinding
    private lateinit var viewModel : CadastroViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        setupObservers()

        setupListeners()

        viewModel.carregarZonas()


    }
    private fun setupViewModel() {
        val sessionManager = SessionManager(this)
        val apiService = RetrofitClient.getInstance { null }
        val authRepo = AuthRepository(apiService, sessionManager)
        val zonaRepo = ZonaRepository(apiService)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CadastroViewModel(authRepo, zonaRepo) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[CadastroViewModel::class.java]
    }

    fun setupObservers(){
        viewModel.loading.observe(this) { isLoading ->
            if(isLoading){
                binding.progressBarCadastro.visibility = View.VISIBLE
                binding.btnCadastrar.isEnabled = false
            } else {
                binding.progressBarCadastro.visibility = View.GONE
                binding.btnCadastrar.isEnabled = true
            }
        }
        viewModel.zonas.observe(this){ zonas ->
            val nomesZonas = zonas.map{it.nome}
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, nomesZonas)
            binding.actvZona.setAdapter(adapter)
        }

        viewModel.cadastroResult.observe(this){ response ->
            if(response != null){
                Toast.makeText(this, getString(R.string.toast_register_success), Toast.LENGTH_LONG).show()
                finish()
            }

        }
        viewModel.error.observe(this){ msg->
            if (msg != null){
                when(msg){
                    "erro_cadastrar_usuario" -> Toast.makeText(this, getString(R.string.toast_register_fail), Toast.LENGTH_LONG).show()
                    "erro_carregar_zonas" -> Toast.makeText(this, getString(R.string.erro_carregar_zonas), Toast.LENGTH_LONG).show()
                    else -> Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

                }
                viewModel.limparErro()
            }
        }
    }

    private fun setupListeners() {
        binding.btnCadastrar.setOnClickListener {
            val nome = binding.etNome.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val senha = binding.etSenha.text.toString().trim()

            val nomeZona = binding.actvZona.text.toString().trim()
            val zonaId = viewModel.zonas.value?.find { it.nome == nomeZona }?.id

            if(nome.isNotEmpty() && email.isNotEmpty() && senha.isNotEmpty()){
                viewModel.cadastrar(nome, email, senha, zonaId)
            } else {
                Toast.makeText(this, getString(R.string.erro_campos_obrigatorios), Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnIrLogin.setOnClickListener {
            finish()
        }
    }
}
