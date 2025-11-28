package com.example.myapplication.domainViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.CategoriaResponse
import com.example.myapplication.repositories.CategoriaRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoriaViewModel(private val repository: CategoriaRepository) {

    private val _categorias = MutableLiveData<List<CategoriaResponse>>()
    val categorias: LiveData<List<CategoriaResponse>> get() = _categorias

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun listar(){
        _loading.value = true

        repository.listarCategorias().enqueue(object : Callback<List<CategoriaResponse>> {
            override fun onResponse(
                call: Call<List<CategoriaResponse>>,
                response: Response<List<CategoriaResponse>>
            ) {
                _loading.value = false
                if (response.isSuccessful) {
                    _categorias.value = response.body()
                } else {
                    _error.value = "Erro ao carregar condições"
                }
            }

            override fun onFailure(call: Call<List<CategoriaResponse>>, t: Throwable) {
                _loading.value = false
                _error.value = t.message
            }
        })

    }

}