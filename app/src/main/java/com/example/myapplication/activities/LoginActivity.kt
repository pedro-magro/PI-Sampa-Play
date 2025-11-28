package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.myapplication.data.LoginResponse
import com.example.myapplication.R
import com.example.myapplication.remote.SessionManager
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.remote.RetrofitClient
import com.example.myapplication.repositories.AuthRepository
import com.example.myapplication.screenViewModels.LoginViewModel


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var sessionManager: SessionManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        if( (sessionManager.isLoggedIn())) {
            irParaHome()
            return
        }

        setupViewModel()

        setupObservers()

        setupListeners()

    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.getInstance { null }
        val repository = AuthRepository(apiService, sessionManager)

        val factory = object : ViewModelProvider.Factory {
            override fun<T: ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
                    @Suppress("UNCHECKED_CAST")
                    return LoginViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")


            }
        }
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    private fun setupObservers(){
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBarLogin.visibility = View.VISIBLE
                binding.btnLogin.isEnabled = false
                binding.etEmail.isEnabled = false
                binding.etSenha.isEnabled = false
            } else {
                binding.progressBarLogin.visibility = View.GONE
                binding.btnLogin.isEnabled = true
                binding.etEmail.isEnabled = true
                binding.etSenha.isEnabled = true
            }
        }
        viewModel.loginResult.observe(this) { LoginResponse ->
            if(LoginResponse != null) {
                val nomeUsuario = sessionManager.getUserName() ?: "Usuário"
                Toast.makeText(this, getString(R.string.toast_login_success) + " $nomeUsuario", Toast.LENGTH_SHORT).show()
                irParaHome()
            }
        }
        viewModel.error.observe(this) { mensagemErro ->
            if (mensagemErro != null) {
                when(mensagemErro){
                    "erro_credenciais_invalidas" -> Toast.makeText(this, getString(R.string.erro_credencias_invalidas), Toast.LENGTH_LONG).show()
                    else -> Toast.makeText(this, mensagemErro, Toast.LENGTH_LONG).show()
                }
                viewModel.limparErro()
            }
        }
    }

    fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val senha = binding.etSenha.text.toString().trim()

            if (email.isNotEmpty() && senha.isNotEmpty()) {
                viewModel.login(email, senha)
            } else {
                if (email.isEmpty()) binding.tilEmail.error = getString(R.string.login_email_hint)
                if (senha.isEmpty()) binding.tilSenha.error = getString(R.string.login_password_hint)
            }
        }
        binding.etEmail.setOnFocusChangeListener {_, _ -> binding.tilEmail.error = null}
        binding.etSenha.setOnFocusChangeListener {_, _ -> binding.tilSenha.error = null}


        binding.btnIrCadastro.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

    }

    private fun irParaHome(){
        if(sessionManager.isAdmin()){
            val intent = Intent(this, AdminActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


}
