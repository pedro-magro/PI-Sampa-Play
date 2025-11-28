package com.example.myapplication.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://pisampa-play-api-production.up.railway.app/api/"

    fun getInstance(tokenProvider: () -> String?): ApiService {

        // Configura o Log (para ver o JSON no Logcat)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Configura o Cliente com o Interceptor de Token
        val client = OkHttpClient.Builder()
            .addInterceptor(JwtInterceptor(tokenProvider)) // Usa a função de token atual
            .addInterceptor(logging)
            .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        // Constrói o Retrofit
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }



}