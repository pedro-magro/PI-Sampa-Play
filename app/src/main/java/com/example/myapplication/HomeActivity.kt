package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ApiService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private lateinit var apiService : ApiService

private lateinit var recyclerView: RecyclerView

private lateinit var adapter: HomeAdapter

private lateinit var addEspacoButton: FloatingActionButton


class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        setupBottomNavigation(navView, R.id.nav_home)

        recyclerView = findViewById(R.id.recyclerViewEspacos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        addEspacoButton = findViewById(R.id.incluirEspacoButton)


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
                    adapter = HomeAdapter(
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

        addEspacoButton.setOnClickListener {
            val intent = Intent(this, IncluirEspacoActivity::class.java)
            //envia a aprovação como pendente por ser um usuario comum, admins tem seu proprio painel.
            intent.putExtra("STATUS_APROVACAO", 0)
            startActivity(intent)
        }

    }
}