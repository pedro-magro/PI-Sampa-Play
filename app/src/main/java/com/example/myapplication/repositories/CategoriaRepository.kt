package com.example.myapplication.repositories

import com.example.myapplication.data.CategoriaResponse
import com.example.myapplication.remote.ApiService
import retrofit2.Call

class CategoriaRepository(private val api: ApiService) {

    fun listarCategorias(): Call<List<CategoriaResponse>> {
        return api.listarCategorias()
    }
}