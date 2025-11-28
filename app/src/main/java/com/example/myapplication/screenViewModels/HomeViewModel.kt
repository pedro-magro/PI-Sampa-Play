package com.example.myapplication.screenViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.myapplication.data.EspacoResponse
import com.example.myapplication.remote.SessionManager
import com.example.myapplication.repositories.EspacoRepository

class HomeViewModel(
    private val repository: EspacoRepository,
    private val sessionManager: SessionManager): ViewModel(){

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _espacosZona = MutableLiveData<List<EspacoResponse>>()
    val espacosZona: LiveData<List<EspacoResponse>> get() = _espacosZona

    private val _espacosEmAlta = MutableLiveData<List<EspacoResponse>>()
    val espacosEmAlta: LiveData<List<EspacoResponse>> get() = _espacosEmAlta

    private val _espacosRecentes = MutableLiveData<List<EspacoResponse>>()
    val espacosRecentes: LiveData<List<EspacoResponse>> get() = _espacosRecentes

    private val _espacosProximos = MutableLiveData<List<EspacoResponse>>()
    val espacosProximos: LiveData<List<EspacoResponse>> get() = _espacosProximos

    private var pendingRequests = 0


    fun carregarHome(){
        _loading.value = true
        pendingRequests = 3

        carregarPorZona()
        carregarEmAlta()
        carregarRecentes()
    }

    fun carregarPorZona(){
        val zonaId = sessionManager.getUserZonaId()
        if(zonaId == 0 || zonaId == null){
            _espacosZona.value = emptyList()
            requestFinished()
            return
        }

        repository.listarPorZona(zonaId).enqueue(createCallback(_espacosZona))

    }

    fun carregarEmAlta(){
        repository.emAlta().enqueue(createCallback(_espacosEmAlta))

    }

    fun carregarRecentes(){
        repository.listarRecentes().enqueue(createCallback(_espacosRecentes))

    }

    private fun createCallback(liveData: MutableLiveData<List<EspacoResponse>>) =
        object: Callback<List<EspacoResponse>>{
            override fun onResponse(
                call: Call<List<EspacoResponse>>,
                response: Response<List<EspacoResponse>>
            ){
                if(response.isSuccessful){
                    liveData.value = response.body() ?: emptyList()
                } else {
                    liveData.value = emptyList()
                }
                requestFinished()
            }
            override fun onFailure(call: Call<List<EspacoResponse>>, t: Throwable) {
                liveData.value = emptyList()
                requestFinished()
            }

        }
    private fun requestFinished(){
        pendingRequests--
        if(pendingRequests <= 0){
            _loading.value = false
        }
    }

    fun buscarProximos(lat: Double, lng: Double){
        _loading.value = true

        repository.listarPorProximidade(lat, lng).enqueue(object: Callback<List<EspacoResponse>>{
            override fun onResponse(
                call: Call<List<EspacoResponse>>,
                response: Response<List<EspacoResponse>>
            ){
                _loading.value = false
                _espacosProximos.value = response.body() ?: emptyList()
            }

            override fun onFailure(call: Call<List<EspacoResponse>>, t: Throwable) {
                _loading.value = false
                _espacosProximos.value = emptyList()
            }
        })

    }
    fun limparErro(){
        _error.value = null
    }


}