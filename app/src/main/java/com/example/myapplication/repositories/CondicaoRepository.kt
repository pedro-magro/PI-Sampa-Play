package com.example.myapplication.repositories

import com.example.myapplication.data.CondicaoResponse
import com.example.myapplication.remote.ApiService
import retrofit2.Call

class CondicaoRepository(private val api: ApiService) {

    fun listarCondicoes(): Call<List<CondicaoResponse>> {
        return api.listarCondicao()
    }
}