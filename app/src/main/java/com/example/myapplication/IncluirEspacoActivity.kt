package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class IncluirEspacoActivity : AppCompatActivity() {

    private lateinit var nomeEditText: EditText
    private lateinit var enderecoEditText: EditText
    private lateinit var cepEditText: EditText
    private lateinit var imagemEditText: EditText

    private lateinit var categoiraEditText: EditText
    private lateinit var salvarButton: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incluir_espaco)

        nomeEditText = findViewById(R.id.nomeEditText)
        enderecoEditText = findViewById(R.id.enderecoEditText)
        cepEditText = findViewById(R.id.cepEditText)
        imagemEditText = findViewById(R.id.imagemEditText)
        categoiraEditText = findViewById(R.id.categoriaEditText)
        salvarButton = findViewById(R.id.salvarButton)



        // ConfiguraÃƒÂ§ÃƒÂ£o do Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.56.1/meu_projeto_api/") // Substitua pelo seu endereÃƒÂ§o base
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        salvarButton.setOnClickListener {
            // Fazer a requisiÃƒÂ§ÃƒÂ£o para incluir o produto
            val categoria : Int = 1
            apiService.incluirEspaco(
                nomeEditText.text.toString(),
                enderecoEditText.text.toString(),
                cepEditText.text.toString(),
                imagemEditText.text.toString(),
                categoriaId = categoiraEditText.text.toString().toInt()
            ).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@IncluirEspacoActivity, "Espaço incluido com sucesso!", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@IncluirEspacoActivity, "Erro na inclusÃƒÂ£o", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@IncluirEspacoActivity, "Erro ao incluir o espaço", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
