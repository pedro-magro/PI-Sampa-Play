package com.example.myapplication.domainViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.EspacoRequest
import com.example.myapplication.data.EspacoResponse
import com.example.myapplication.repositories.EspacoRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EspacoViewModel(private val repository: EspacoRepository) {

    private val _espacos = MutableLiveData<List<EspacoResponse>>()
    val espacos: LiveData<List<EspacoResponse>> get() = _espacos

    private val _detalhes = MutableLiveData<EspacoResponse?>()
    val detalhes: LiveData<EspacoResponse?> get() = _detalhes

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun listarEspacos(){
        _loading.value = true

        repository.listarPublicos().enqueue(object : Callback<List<EspacoResponse>> {
            override fun onResponse(
                call: Call<List<EspacoResponse>>,
                response: Response<List<EspacoResponse>>
            ) {
                _loading.value = false
                if (response.isSuccessful) {
                    _espacos.value = response.body()
                } else {
                    _error.value = "Erro ao carregar espaços"
                }
            }

            override fun onFailure(call: Call<List<EspacoResponse>>, t: Throwable) {
                _loading.value = false
                _error.value = t.message
            }
        })


    }

    fun buscarPorId(id: Int) {
        _loading.value = true

        repository.buscarPorId(id).enqueue(object : Callback<EspacoResponse> {
            override fun onResponse(
                call: Call<EspacoResponse>,
                response: Response<EspacoResponse>
            ) {
                _loading.value = false
                if (response.isSuccessful) {
                    _detalhes.value = response.body()

                } else {
                    _error.value = "Erro ao carregar detalhes"
                }
            }

            override fun onFailure(call: Call<EspacoResponse>, t: Throwable) {
                _loading.value = false
                _error.value = t.message
            }
        })
    }

    fun listarEmAlta() {
        _loading.value = true
        repository.emAlta().enqueue(object : Callback<List<EspacoResponse>> {
            override fun onResponse(
                call: Call<List<EspacoResponse>>,
                response: Response<List<EspacoResponse>>
            ) {
                _loading.value = false
                if (response.isSuccessful) {
                    _espacos.value = response.body()
                } else {
                    _error.value = "Erro ao carregar espaços em alta"
                }
            }
            override fun onFailure(call: Call<List<EspacoResponse>>, t: Throwable) {
                _loading.value = false
                _error.value = t.message
            }
        })
    }
    fun listarPorPerto(lat: Double, lng: Double) {
        _loading.value = true
        repository.listarPorProximidade(lat, lng).enqueue(object : Callback<List<EspacoResponse>> {
           override fun onResponse(
               call: Call<List<EspacoResponse>>,
               response: Response<List<EspacoResponse>>
           ) {
               _loading.value = false
               if (response.isSuccessful) {
                  _espacos.value = response.body()
               } else {
                  _error.value = "Erro ao carregar espaços perto"
               }
           }
           override fun onFailure(call: Call<List<EspacoResponse>>, t: Throwable) {
               _loading.value = false
               _error.value = t.message
           }
        })
    }
    fun listarPorZona(zonaId: Int) {
        _loading.value = true
        repository.listarPorZona(zonaId).enqueue(object : Callback<List<EspacoResponse>> {
           override fun onResponse(
              call: Call<List<EspacoResponse>>,
              response: Response<List<EspacoResponse>>
           ) {
               _loading.value = false
               if (response.isSuccessful) {
                  _espacos.value = response.body()
               } else {
                  _error.value = "Erro ao carregar espaços por zona"
               }
           }
           override fun onFailure(call: Call<List<EspacoResponse>>, t: Throwable) {
               _loading.value = false
               _error.value = t.message
               }
        })

    }
    fun listarPorMelhorCondicao(){
        _loading.value = true
        repository.listarPorCondicao().enqueue(object : Callback<List<EspacoResponse>> {
           override fun onResponse(
               call: Call<List<EspacoResponse>>,
               response: Response<List<EspacoResponse>>
           ){
               _loading.value = false
               if(response.isSuccessful){
                   _espacos.value = response.body()
               } else{
                   _error.value = "Erro ao carregar espaços por condição"
               }
           }
           override fun onFailure(call: Call<List<EspacoResponse>>, t: Throwable){
                _loading.value = false
                _error.value = t.message
           }
        })
    }

    fun filtrar(termo: String?, categoriaId: Int?, zonaId: Int?){
        _loading.value = true
        repository.filtrar(termo, categoriaId, zonaId).enqueue(object : Callback<List<EspacoResponse>> {
           override fun onResponse(
               call: Call<List<EspacoResponse>>,
               response: Response<List<EspacoResponse>>
           ){
               _loading.value = false
               if(response.isSuccessful) {
                   _espacos.value = response.body()
               } else{
                   _error.value = "Erro ao filtrar Espacos"
               }
           }
           override fun onFailure(call: Call<List<EspacoResponse>>, t: Throwable){
               _loading.value = false
               _error.value = t.message
           }
        })
    }

    fun criar(dto: EspacoRequest){
        _loading.value = true
        repository.criarEspaco(dto).enqueue(object : Callback<EspacoResponse>{
            override fun onResponse(
                call: Call<EspacoResponse>,
                response: Response<EspacoResponse>
            ){
                _loading.value = false
                if(response.isSuccessful){
                    _detalhes.value = response.body()
                } else {
                    _error.value = "Erro ao criar espaço"
                }
            }
            override fun onFailure(call:Call<EspacoResponse>, t: Throwable){
                _loading.value = false
                _error.value = t.message
            }
         })
    }
    fun atualizar(id: Int, dto: EspacoRequest){
        _loading.value = true
        repository.atualizarEspaco(id, dto).enqueue(object : Callback<EspacoResponse>{
           override fun onResponse(
               call: Call<EspacoResponse>,
               response: Response<EspacoResponse>
           ){
               _loading.value = false
               if(response.isSuccessful){
                    _detalhes.value = response.body()
               } else {
                   _error.value = "Erro ao atualizar espaço"
               }
           }
           override fun onFailure(call: Call<EspacoResponse>, t: Throwable) {
               _loading.value = false
               _error.value = t.message
           }
        })
    }

    fun deletar(id: Int){
        _loading.value = true
        repository.deletarEspaco(id).enqueue(object: Callback<Void>{
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ){
                _loading.value = false
                if (response.isSuccessful) {
                    _detalhes.value = null
                } else {
                    _error.value = "Erro ao deletar"
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                _loading.value = false
                _error.value = t.message
            }
        })

    }

}