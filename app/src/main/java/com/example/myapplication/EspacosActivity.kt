package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit
import com.example.myapplication.ApiService
import com.google.android.material.bottomnavigation.BottomNavigationView


class EspacosActivity : BaseActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EspacosAdapter
    private lateinit var addEspacoButton: Button  // BotÃƒÂ£o de Adicionar Produto

    private lateinit var apiService: ApiService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_espacos)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        setupBottomNavigation(navView, R.id.nav_home)

        recyclerView = findViewById(R.id.recyclerViewEspacos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        addEspacoButton = findViewById(R.id.incluirEspacoButton) //inicializando o botão


        // ConfiguraÇÃo do Logging Interceptor
        val logging = HttpLoggingInterceptor { message ->
            Log.d("OkHttp", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // ConfiguraÇÃo do OkHttpClient com o interceptor
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        // Configuração do Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.56.1/meu_projeto_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        apiService = retrofit.create(ApiService::class.java)
        apiService.getEspacos().enqueue(object : Callback<List<Espaco>> {
            override fun onResponse(call: Call<List<Espaco>>, response: Response<List<Espaco>>) {
                if (response.isSuccessful) {
                    val espacos = response.body() ?: emptyList()
                    adapter = EspacosAdapter(
                        espacos,
                        apiService = apiService
                    )
                    recyclerView.adapter = adapter
                } else {
                    Log.e("API Error", "Response not successful. Code: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<Espaco>>, t: Throwable) {
                Log.e("API Failure", "Error fetching products", t)
            }
        })

        // Adicionar listener para o botÃƒÂ£o de adicionar produto
        addEspacoButton.setOnClickListener {
            val intent = Intent(this, IncluirEspacoActivity::class.java)
            startActivity(intent)  // Abrir a tela de inclusÃƒÂ£o de produto
        }

    }
    override fun onResume() {
        super.onResume()
        loadEspacos()
    }
    private fun loadEspacos() {
        apiService.getEspacos().enqueue(object : Callback<List<Espaco>> {
            override fun onResponse(call: Call<List<Espaco>>, response: Response<List<Espaco>>) {
                if (response.isSuccessful) {
                    val espacos = response.body() ?: emptyList()
                    // Reutiliza o adapter (se já existir) ou cria um novo
                    if (::adapter.isInitialized) {
                        // Se o adapter já existe, só atualiza os dados
                        // (Você precisará implementar o método updateData no seu adapter)
                        // adapter.updateData(espacos)

                        // OU (A forma mais simples para seu MVP):
                        adapter = EspacosAdapter(espacos, apiService)
                        recyclerView.adapter = adapter
                    } else {
                        // Se for a primeira vez, cria o adapter
                        adapter = EspacosAdapter(espacos, apiService)
                        recyclerView.adapter = adapter
                    }
                } else {
                    Log.e("API Error", "Response not successful. Code: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<Espaco>>, t: Throwable) {
                Log.e("API Failure", "Error fetching products", t)
            }
        })
    }
}
