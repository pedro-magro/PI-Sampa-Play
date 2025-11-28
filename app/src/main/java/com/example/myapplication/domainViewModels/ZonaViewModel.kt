package com.example.myapplication.domainViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.ZonaResponse
import com.example.myapplication.repositories.ZonaRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ZonaViewModel(private val repository: ZonaRepository){

    private val _zonas = MutableLiveData<List<ZonaResponse>>()
    val zonas : LiveData<List<ZonaResponse>> get() = _zonas

    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error : LiveData<String?> get() = _error

    fun listar(){
        _loading.value = true

        repository.listarZonas().enqueue(object : Callback<List<ZonaResponse>> {
            override fun onResponse(
                call: Call<List<ZonaResponse>>,
                response: Response<List<ZonaResponse>>
            ){
                _loading.value = false
                if(response.isSuccessful) {
                    _zonas.value = response.body()
                } else {
                    _error.value = "Erro ao carregar zonas"
                }
            }
            override fun onFailure(call: Call<List<ZonaResponse>>, t: Throwable){
                _loading.value = false
                _error.value = t.message
            }
        })
    }
}