package com.example.myapplication.domainViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.CondicaoResponse
import com.example.myapplication.repositories.CondicaoRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CondicaoViewModel(private val repository: CondicaoRepository) {

    private val _condicoes = MutableLiveData<List<CondicaoResponse>>()
    val condicoes: LiveData<List<CondicaoResponse>> get() = _condicoes

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun listar(){
        _loading.value = true

        repository.listarCondicoes().enqueue(object : Callback<List<CondicaoResponse>> {
            override fun onResponse(
                call: Call<List<CondicaoResponse>>,
                response: Response<List<CondicaoResponse>>
            ) {
                _loading.value = false
                if (response.isSuccessful) {
                    _condicoes.value = response.body()
                } else {
                    _error.value = "Erro ao carregar condições"
                }
            }

            override fun onFailure(call: Call<List<CondicaoResponse>>, t: Throwable) {
                _loading.value = false
                _error.value = t.message
            }
        })

    }

}