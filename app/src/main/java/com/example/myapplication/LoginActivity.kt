package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys



class LoginActivity : AppCompatActivity() {


    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var cadastroLink: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)

        cadastroLink = findViewById(R.id.CadastroLink)
        cadastroLink.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {

            blockLogin()
        }
    }

    private fun blockLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()


        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.56.1/ ")
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        val apiService = retrofit.create(ApiService::class.java)



        val call = apiService.login(email, password)
        call.enqueue(object : Callback<List<LoginResponse>> {
            override fun onResponse(call: Call<List<LoginResponse>>,
            response: Response<List<LoginResponse>>
        ) {
            if (response.isSuccessful && response.body() != null) { val loginResponses = response.body()!!
                if (loginResponses.isNotEmpty()) {
                    val usuario = loginResponses[0]
                    if (usuario.usuarioTipo.equals("ADMIN", ignoreCase = true)) {
                        // Usuário é Admin, vai para a tela de CRUD
                        // (Assumindo que sua tela de admin se chama 'EspacosActivity')
                        val intent = Intent(this@LoginActivity, EspacosActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Usuário é Comum, vai para a tela de visualização
                        // (Assumindo que sua tela comum se chama 'HomeActivity')
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                    }
                    finish() // Fecha o Login
                } else {

                    Toast.makeText(this@LoginActivity, "Usuário ou senha inválidos", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this@LoginActivity, "Erro no login", Toast.LENGTH_LONG).show()
            }
        }

            override fun onFailure(call: Call<List<LoginResponse>>, t: Throwable) { Toast.makeText(this@LoginActivity, "Erro: ${t.message}",
                Toast.LENGTH_LONG).show()
            }
        })
    }



    interface ApiService { @GET("/meu_projeto_api/login.php")
        fun login(
            @Query("usuario") usuario: String,
            @Query("senha") senha: String
        ): Call<List<LoginResponse>>
    }

    object SessionManager {
        private const val PREF_NAME = "SampaPlaySession"
        private const val KEY_USER_ID = "USER_ID"
        private const val KEY_USER_NOME = "USER_NOME"
        private const val KEY_USER_EMAIL = "USER_EMAIL"
        private const val KEY_USER_TIPO = "USER_TIPO"

        private fun getEncryptedPrefs(context: Context): SharedPreferences {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            return EncryptedSharedPreferences.create(
                PREF_NAME,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }

        // Salva todos os dados do usuário
        fun salvarSessao(context: Context, usuario: LoginResponse) {
            val editor = getEncryptedPrefs(context).edit()
            editor.putInt(KEY_USER_ID, usuario.usuarioId)
            editor.putString(KEY_USER_NOME, usuario.usuarioNome)
            editor.putString(KEY_USER_EMAIL, usuario.usuarioEmail)
            editor.putString(KEY_USER_TIPO, usuario.usuarioTipo) // Salva o TIPO
            editor.apply()
        }

        fun getUserTipo(context: Context): String? {
            return getEncryptedPrefs(context).getString(KEY_USER_TIPO, null)
        }

        fun getUserId(context: Context): Int {
            return getEncryptedPrefs(context).getInt(KEY_USER_ID, 0)
        }

        // ... (outras funções de recuperação se necessário)
    }
}
