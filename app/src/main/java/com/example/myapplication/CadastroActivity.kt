package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.myapplication.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CadastroActivity : AppCompatActivity() {

    // 1. Declaração das variáveis
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginLink: TextView

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        registerButton = findViewById(R.id.registerButton)
        loginLink = findViewById(R.id.loginLink)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.56.1/ ")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Lógica do Botão de Cadastro
        registerButton.setOnClickListener {
            cadastrarUsuario()
        }
        // Lógica do link "Já tem uma conta? Faça o Login"
        loginLink.setOnClickListener {
             val intent = Intent(this, LoginActivity::class.java)
             startActivity(intent)
            finish() // Fecha a tela de cadastro e retorna à anterior (se for a Login)
        }
    }
    private fun cadastrarUsuario(){
        val nome = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val senha = passwordEditText.text.toString().trim()

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return // Interrompe a função se os campos estiverem vazios
        }

        val call = apiService.cadastrarUsuario(nome, email, senha)

        call.enqueue(object : Callback<CadastroResponse> {
            override fun onResponse(call: Call<CadastroResponse>, response: Response<CadastroResponse>) {
                val cadastroResponse = response.body()

                if (response.isSuccessful && cadastroResponse?.status == "sucesso") {
                    // SUCESSO: Cadastro OK
                    Toast.makeText(this@CadastroActivity, cadastroResponse.message, Toast.LENGTH_LONG).show()

                    // Redireciona o usuário para a tela de Login
                    val intent = Intent(this@CadastroActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    // FALHA (Erro HTTP ou JSON de erro retornado pelo PHP)
                    val errorMessage = cadastroResponse?.error ?: "Erro desconhecido ao cadastrar."
                    Toast.makeText(this@CadastroActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<CadastroResponse>, t: Throwable) {
                // FALHA (Erro de Conexão: sem internet, servidor offline, etc.)
                Toast.makeText(this@CadastroActivity, "Erro de Conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
