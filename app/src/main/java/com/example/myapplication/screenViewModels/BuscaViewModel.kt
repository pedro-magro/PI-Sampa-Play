package com.example.myapplication.screenViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.CategoriaResponse
import com.example.myapplication.data.EspacoResponse
import com.example.myapplication.data.ZonaResponse
import com.example.myapplication.repositories.CategoriaRepository
import com.example.myapplication.repositories.EspacoRepository
import com.example.myapplication.repositories.ZonaRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuscaViewModel(
    private val repository: EspacoRepository,
    private val zonasRepo: ZonaRepository,
    private val categoriasRepo: CategoriaRepository
) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error : LiveData<String?> get() = _error

    private val _zonas = MutableLiveData<List<ZonaResponse>>()
    val zonas : LiveData<List<ZonaResponse>> get() = _zonas

    private val _categorias = MutableLiveData<List<CategoriaResponse>>()
    val categorias : LiveData<List<CategoriaResponse>> get() = _categorias


    private val _resultadosBusca = MutableLiveData<List<EspacoResponse>>()
    val resultadosBusca : LiveData<List<EspacoResponse>> get() = _resultadosBusca

    fun buscar(termo: String?, categoriaId: Int?, zonaId: Int?){
        _loading.value = true

        repository.filtrar(termo, categoriaId, zonaId).enqueue(object: Callback<List<EspacoResponse>>{
            override fun onResponse(
                call: Call<List<EspacoResponse>>,
                response : Response<List<EspacoResponse>>
            ){
                _loading.value = false
                if(response.isSuccessful && response.body() != null){
                    _resultadosBusca.value = response.body()
                } else {
                    _error.value = "Erro ao buscar por espaços"
                }
            }
            override fun onFailure(call: Call<List<EspacoResponse>>, t: Throwable){
                _loading.value = false
                _error.value = t.message
            }
        })
    }

    fun getCategorias(){
        _loading.value = true

        categoriasRepo.listarCategorias().enqueue(createCallback(_categorias))
    }

    fun getZonas(){
        _loading.value = true

        zonasRepo.listarZonas().enqueue(createCallback(_zonas))
    }
    private fun<T> createCallback(liveData: MutableLiveData<List<T>>) =
        object: Callback<List<T>>{
            override fun onResponse(
                call: Call<List<T>>,
                response: Response<List<T>>
            ){
                _loading.value = false
                if(response.isSuccessful){
                    liveData.value = response.body() ?: emptyList()
                } else {
                    liveData.value = emptyList()
                    _error.value = "Erro ao carregar dados"
                }

            }
            override fun onFailure(call: Call<List<T>>, t: Throwable) {
                liveData.value = emptyList()
                _error.value = t.message
            }

        }
    fun limparErro(){
        _error.value = null
    }


}