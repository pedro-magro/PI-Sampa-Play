package com.example.myapplication.repositories

import com.example.myapplication.data.EspacoRequest
import com.example.myapplication.data.EspacoResponse
import com.example.myapplication.remote.ApiService
import retrofit2.Call

class EspacoRepository(private val api: ApiService) {

    fun listarPublicos(): Call<List<EspacoResponse>> {
        return api.listarEspacos()
    }

    fun buscarPorId(id: Int): Call<EspacoResponse> {
        return api.buscarPorId(id)
    }

    fun filtrar(termo: String?, categoriaId: Int?, zonaId: Int?): Call<List<EspacoResponse>> {
        return api.filtrar(termo, categoriaId, zonaId)
    }

    fun emAlta(): Call<List<EspacoResponse>> {
        return api.emAlta()
    }

    fun listarPorZona(zonaId: Int): Call<List<EspacoResponse>> {
        return api.porZona(zonaId)
    }

    fun listarPorProximidade(lat: Double, lng: Double): Call<List<EspacoResponse>> {
        return api.proximidade(lat, lng)
    }

    fun listarPorCondicao(): Call<List<EspacoResponse>> {
        return api.porCondicao()
    }

    fun listarRecentes(): Call<List<EspacoResponse>>{
        return api.recentes()
    }

    fun criarEspaco(dto: EspacoRequest): Call<EspacoResponse> {
        return api.criarEspaco(dto)
    }

    fun listarTodos(): Call<List<EspacoResponse>> {
        return api.listarTodos()
    }


    fun atualizarEspaco(id: Int, dto: EspacoRequest): Call<EspacoResponse>{
        return api.atualizarEspaco(id, dto)
    }

    fun deletarEspaco(id: Int): Call<Void> {
        return api.deletarEspaco(id)
    }

    fun aprovarEspaco(id: Int, aprovado: Boolean): Call<EspacoResponse> {
        return api.aprovarEspaco(id, aprovado)
    }
}