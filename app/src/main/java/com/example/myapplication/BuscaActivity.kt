package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.myapplication.Categoria
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Callback

class BuscaActivity : BaseActivity() {

    private lateinit var etBuscaTexto: EditText
    private lateinit var spinnerTipo: Spinner
    private lateinit var btnAplicarBusca: Button
    private lateinit var rvResultadosBusca: RecyclerView

    private lateinit var apiService: ApiService
    private lateinit var espacosAdapter: HomeAdapter

    // Armazena a lista de categorias e o ID selecionado
    private var listaCategorias = listOf<Categoria>()
    private var categoriaSelecionadaId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_busca)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        setupBottomNavigation(navView, R.id.nav_search)

        // Inicialização dos componentes obrigatórios
        etBuscaTexto = findViewById(R.id.etBuscaTexto)
        spinnerTipo = findViewById(R.id.spinnerTipo)
        btnAplicarBusca = findViewById(R.id.btnAplicarBusca)
        rvResultadosBusca = findViewById(R.id.rvResultadosBusca)

        rvResultadosBusca.layoutManager = LinearLayoutManager(this)


        // Configuração do Spinner (Componente Obrigatório)
        // O array de strings deve estar definido em res/values/strings.xml
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.56.1/meu_projeto_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        espacosAdapter = HomeAdapter(emptyList(), apiService)
        rvResultadosBusca.adapter = espacosAdapter

        // 1. Carrega as categorias para o Spinner
        loadCategoriasSpinner()

        // 2. Define o clique do botão de busca
        btnAplicarBusca.setOnClickListener {
            executarBusca()
        }
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

                    val spinnerAdapter = ArrayAdapter(this@BuscaActivity, android.R.layout.simple_spinner_item, nomesCategorias)
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerTipo.adapter = spinnerAdapter

                    // Listener para saber qual ID foi selecionado
                    spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            // Salva o ID da categoria selecionada
                            categoriaSelecionadaId = listaCategorias[position].id
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            categoriaSelecionadaId = null
                        }
                    }
                } else {
                    Toast.makeText(this@BuscaActivity, "Erro ao carregar categorias", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Categoria>>, t: Throwable) {
                Log.e("API_CATEGORIA", "Falha: ${t.message}")
            }
        })
    }
    private fun executarBusca() {
        val termo = etBuscaTexto.text.toString().let { if (it.isEmpty()) null else it }
        val categoriaId = categoriaSelecionadaId // (Vindo do Spinner)

        // 2. A API de busca retorna o mesmo tipo de dado: List<Espaco>
        apiService.buscarEspaco(termo, categoriaId).enqueue(object : Callback<List<Espaco>> {
            override fun onResponse(call: Call<List<Espaco>>, response: Response<List<Espaco>>) {
                if (response.isSuccessful) {
                    val espacosFiltrados = response.body() ?: emptyList()


                    // 3. A REUTILIZAÇÃO ACONTECE AQUI:
                    // Você instancia o MESMO EspacosAdapter,
                    // mas alimenta ele com a LISTA FILTRADA.
                    espacosAdapter.updateData(espacosFiltrados)

                    // 4. O RecyclerView da Busca usa o Adapter
                    rvResultadosBusca.adapter = espacosAdapter
                }
            }

            override fun onFailure(call: Call<List<Espaco>>, t: Throwable) {
                Log.e("API_BUSCA", "Falha: ${t.message}")
            }
        })
    }



}