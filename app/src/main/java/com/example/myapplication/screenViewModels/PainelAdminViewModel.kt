package com.example.myapplication.screenViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.EspacoResponse
import com.example.myapplication.remote.SessionManager
import com.example.myapplication.repositories.EspacoRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PainelAdminViewModel(
    private val repository: EspacoRepository,
    private val session : SessionManager
) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _espacos = MutableLiveData<List<EspacoResponse>>()
    val espacos: LiveData<List<EspacoResponse>> get() = _espacos

    private val _deletado = MutableLiveData<Boolean>()
    val deletado: LiveData<Boolean> get() = _deletado


    fun carregarEspacos(){
        _loading.value = true

        repository.listarTodos().enqueue(object : Callback<List<EspacoResponse>> {
            override fun onResponse(
                call: Call<List<EspacoResponse>>,
                response: Response<List<EspacoResponse>>
            ){
                _loading.value = false
                if(response.isSuccessful && response.body() != null) {
                    _espacos.value = response.body()
                } else {
                    _error.value = "Erro ao carregar espaços"
                    session.clearSession()
                }
            }
            override fun onFailure(call: Call<List<EspacoResponse>>, t: Throwable) {
                _loading.value = false
                _error.value = t.message
            }
        })
    }

    fun deletarEspaco(id: Int){
        _loading.value = true

        repository.deletarEspaco(id).enqueue(object: Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ){
                _loading.value = false
                if(response.isSuccessful) {
                    _deletado.value = true
                    carregarEspacos()
                } else {
                    _error.value = "Erro ao deletar espaço"
                }

            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                _loading.value = false
                _error.value = t.message
            }
        })
    }

    fun limparErro(){
        _error.value = null
    }


}