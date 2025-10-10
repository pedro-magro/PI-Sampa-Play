package com.example.myapplication

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
import com.example.myapplication.ApiService
import com.example.myapplication.Espaco

class EditarEspacoActivity : AppCompatActivity() {

    private lateinit var nomeEditText: EditText
    private lateinit var enderecoEditText: EditText
    private lateinit var cepEditText: EditText
    private lateinit var imagemEditText: EditText
    private lateinit var salvarButton: Button

    private var espacoId: Int = 0

    // VARIÁVEIS DE ESTADO (Para preservar os dados originais)
    private var imagemUrlOriginal: String? = null
    private var categoriaIdOriginal: Int = 1
    private var aprovadoOriginal: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_espaco)

        // ... (Inicialização dos Componentes OK) ...
        nomeEditText = findViewById(R.id.nomeEditText)
        enderecoEditText = findViewById(R.id.enderecoEditText)
        cepEditText = findViewById(R.id.cepEditText)
        imagemEditText = findViewById(R.id.imagemEditText)
        salvarButton = findViewById(R.id.salvarButton)

        // 2. RESGATE DE DADOS DA INTENT E ARMAZENAMENTO ORIGINAL
        espacoId = intent.getIntExtra("ESPACO_ID", 0)

        // Armazena o URL original da Intent na variável de estado
        imagemUrlOriginal = intent.getStringExtra("ESPACO_IMG")

        // Resgata os outros valores imutáveis
        // CORREÇÃO DE TIPAGEM: Adiciona !! ao final do Elvis Operator para garantir String não nula

        categoriaIdOriginal = intent.getIntExtra("CATEGORIA_ID", 1)
        aprovadoOriginal = intent.getIntExtra("ESPACO_APROVADO", 1)
        val dataOriginal: String = intent.getStringExtra("ESPACO_DATA") ?: "2025-01-01 00:00:00"

        // Preenche os EditTexts (OK)
        nomeEditText.setText(intent.getStringExtra("ESPACO_NOME") ?: "")
        enderecoEditText.setText(intent.getStringExtra("ESPACO_ENDERECO") ?: "")
        cepEditText.setText(intent.getStringExtra("ESPACO_CEP") ?: "")
        imagemEditText.setText(imagemUrlOriginal ?: "")

        // Configuração do Retrofit (OK)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.56.1/meu_projeto_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        salvarButton.setOnClickListener {

            // 3. COLETA E LÓGICA DE PRESERVAÇÃO (CORRIGIDO)
            val novoNome = nomeEditText.text.toString()
            val novoEndereco = enderecoEditText.text.toString()
            val cepDoEditText = cepEditText.text.toString()
            val imagemDoEditText = imagemEditText.text.toString()

            // Lógica 1: CEP (Transforma "" em null)
            val novoCep = if (cepDoEditText.isEmpty()) null else cepDoEditText

            // Lógica 2: IMAGEM (Se campo vazio, usa o original (imagemUrlOriginal))
            val novaImagem = if (imagemDoEditText.isEmpty()) {
                imagemUrlOriginal
            } else {
                imagemDoEditText.let { if (it.isEmpty()) null else it }
            }


            // 4. Chamada à API (Usando a nova lógica de preservação)
            apiService.editarEspaco(
                id = espacoId,
                nome = novoNome,
                endereco = novoEndereco,
                cep = novoCep,
                imgUrl = novaImagem,
                categoriaId = categoriaIdOriginal,
                aprovado = aprovadoOriginal
            ).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditarEspacoActivity, "Espaço atualizado com sucesso!", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@EditarEspacoActivity, "Erro na atualização", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@EditarEspacoActivity, "Erro ao atualizar o espaço", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}