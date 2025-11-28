package com.example.myapplication.repositories

import com.example.myapplication.data.ZonaResponse
import com.example.myapplication.remote.ApiService
import retrofit2.Call

class ZonaRepository(private val api: ApiService) {

    fun listarZonas(): Call<List<ZonaResponse>> {
        return api.listarZonas()
    }
}