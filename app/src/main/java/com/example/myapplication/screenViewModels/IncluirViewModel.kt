package com.example.myapplication.screenViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.myapplication.data.CategoriaResponse
import com.example.myapplication.data.EspacoRequest
import com.example.myapplication.data.EspacoResponse
import com.example.myapplication.data.ZonaResponse
import com.example.myapplication.repositories.CategoriaRepository
import com.example.myapplication.repositories.EspacoRepository
import com.example.myapplication.repositories.ZonaRepository

class IncluirViewModel(
    private val espacoRepository: EspacoRepository,
    private val categoriaRepository: CategoriaRepository,
    private val zonaRepository: ZonaRepository
) : ViewModel(){

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _sucessoInclusao = MutableLiveData<EspacoResponse>()
    val sucessoInclusao: LiveData<EspacoResponse> get() = _sucessoInclusao
    private val _categorias = MutableLiveData<List<CategoriaResponse>>()
    val categorias: LiveData<List<CategoriaResponse>> get() = _categorias

    private val _zonas = MutableLiveData<List<ZonaResponse>>()
    val zonas: LiveData<List<ZonaResponse>> get () = _zonas

    fun carregarCategorias(){
        _loading.value = true

        categoriaRepository.listarCategorias().enqueue(createCallback(_categorias))
    }

    fun carregarZonas() {
        _loading.value = true

        zonaRepository.listarZonas().enqueue(createCallback(_zonas))
    }

    fun Salvar(nome: String, endereco: String, cep: String?, categoriaId: Int, imagemUrls:List<String?>, zonaId: Int? ){
        if(categoriaId == null){
            _error.value = "Selecione uma categoria"
            return
        }
        _loading.value = true

        val request = EspacoRequest(
            nome = nome,
            endereco = endereco,
            cep = cep,
            categoriaId = categoriaId,
            zonaId = zonaId,
            imagens = imagemUrls
        )

        espacoRepository.criarEspaco(request).enqueue(createCallback(_sucessoInclusao))
    }
    private fun <T> createCallback(liveData: MutableLiveData<T>) =
        object: Callback<T>{
            override fun onResponse(call: Call<T>, response: Response<T>) {
                _loading.value = false

                if(response.isSuccessful){
                    liveData.value = response.body()
                } else {
                    _error.value = "Erro ao salvar"
                }
            }
            override fun onFailure(call: Call<T>, t: Throwable) {
                _loading.value = false
                _error.value = t.message
            }
        }

    fun limparErro(){
        _error.value = null
    }

}