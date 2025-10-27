package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {

            blockLogin()
        }
    }
    //Salvar depois em arquivo unico sobre sessão do usuário.
    fun salvarEmail(email: String){
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        val sharedPreferences = EncryptedSharedPreferences.create(
            "email_seguro",
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.apply()
    }
    fun recuperarEmailUsuarioSeguro(): String? {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        val sharedPreferences = EncryptedSharedPreferences.create(
            "email-seguro",
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        return sharedPreferences.getString("USER_EMAIL", null)
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
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    salvarEmail(email)
                    startActivity(intent)
                    finish()
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
}
