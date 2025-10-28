package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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

class IncluirEspacoActivity : BaseActivity() {

    private lateinit var toolbar : androidx.appcompat.widget.Toolbar

    private lateinit var nomeEditText: EditText
    private lateinit var enderecoEditText: EditText
    private lateinit var cepEditText: EditText
    private lateinit var imagemEditText: EditText

    private lateinit var spCategoira: Spinner
    private lateinit var salvarButton: Button

    private lateinit var apiService: ApiService

    private var listaCategorias = listOf<Categoria>()

    private var categoriaSelecionadaId: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incluir_espaco)

        toolbar = findViewById(R.id.toolbarIncluirEspaco)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        nomeEditText = findViewById(R.id.nomeEditText)
        enderecoEditText = findViewById(R.id.enderecoEditText)
        cepEditText = findViewById(R.id.cepEditText)
        imagemEditText = findViewById(R.id.imagemEditText)
        spCategoira = findViewById(R.id.spinnerCategoria)
        salvarButton = findViewById(R.id.salvarButton)

        val aprovacao: Int = 0

        // ConfiguraÃƒÂ§ÃƒÂ£o do Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.56.1/meu_projeto_api/") // Substitua pelo seu endereÃƒÂ§o base
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
        loadCategoriasSpinner()

        // LÃƒÂ³gica do BotÃƒÂ£o de Incluir

        salvarButton.setOnClickListener {
            // Fazer a requisiÃƒÂ§ÃƒÂ£o para incluir o produto
            val aprovacao = intent.getIntExtra("STATUS_APROVACAO",0)
            val cep = cepEditText.text.toString().let { if (it.isEmpty()) null else it }
            val imagem = imagemEditText.text.toString().let { if (it.isEmpty()) null else it }

            apiService.incluirEspaco(
                nomeEditText.text.toString(),
                enderecoEditText.text.toString(),
                cep,
                imagem,
                categoriaId = categoriaSelecionadaId,
                aprovado = aprovacao
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
    override fun onSupportNavigateUp(): Boolean {
        finish() // Fecha esta Activity e volta para a tela anterior
        return true
    }
    private fun loadCategoriasSpinner() {
        apiService.getCategorias().enqueue(object : Callback<List<Categoria>> {
            override fun onResponse(call: Call<List<Categoria>>, response: Response<List<Categoria>>) {
                if (response.isSuccessful) {
                    val listaComTodas = mutableListOf<Categoria>()
                    listaComTodas.add(Categoria(id = 0, nome = "Todas as Categorias")) // '0' ou 'null'

                    // 2. Adicione o restante das categorias da API
                    listaComTodas.addAll(response.body() ?: emptyList())
                    listaCategorias = listaComTodas

                    // Extrai apenas os nomes para o Adapter do Spinner
                    val nomesCategorias = listaCategorias.map { it.nome }

                    val spinnerAdapter = ArrayAdapter(this@IncluirEspacoActivity, android.R.layout.simple_spinner_item, nomesCategorias)
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spCategoira.adapter = spinnerAdapter

                    // Listener para saber qual ID foi selecionado
                    spCategoira.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            // Salva o ID da categoria selecionada
                            categoriaSelecionadaId = listaCategorias[position].id
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            categoriaSelecionadaId = 0
                        }
                    }
                } else {
                    Toast.makeText(this@IncluirEspacoActivity, "Erro ao carregar categorias", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Categoria>>, t: Throwable) {
                Log.e("API_CATEGORIA", "Falha: ${t.message}")
            }
        })
    }

}
