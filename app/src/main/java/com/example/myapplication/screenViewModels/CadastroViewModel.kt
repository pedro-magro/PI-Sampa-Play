package com.example.myapplication.screenViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.RegisterRequest
import com.example.myapplication.data.RegisterResponse
import com.example.myapplication.data.ZonaResponse
import com.example.myapplication.repositories.AuthRepository
import com.example.myapplication.repositories.ZonaRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CadastroViewModel(private val repository: AuthRepository, private val zonaRepository: ZonaRepository): ViewModel() {

    private val _cadastroResult = MutableLiveData<RegisterResponse?>()
    val cadastroResult: LiveData<RegisterResponse?> get() = _cadastroResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _zonas = MutableLiveData<List<ZonaResponse>>()
    val zonas: LiveData<List<ZonaResponse>> get() = _zonas

    fun carregarZonas(){
        _loading.value = true


        zonaRepository.listarZonas().enqueue(object : Callback<List<ZonaResponse>> {
            override fun onResponse(
                call: Call<List<ZonaResponse>>,
                response: Response<List<ZonaResponse>>
            ) {
                _loading.value = false
                if (response.isSuccessful) {
                    _zonas.value = response.body()
                } else {
                    _error.value = "erro_carregar_zonas"
                }
            }
            override fun onFailure(call: Call<List<ZonaResponse>>, t: Throwable){
                _loading.value = false
                _error.value = t.message
            }
        })


    }

    fun cadastrar(nome: String, email: String, senha: String, zonaId: Int?) {
        _loading.value = true

        val request = RegisterRequest(nome, email, senha, zonaId)
        repository.cadastrarUsuario(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _loading.value = false
                if (response.isSuccessful) {
                    _cadastroResult.value = response.body()
                } else {
                    _error.value = "erro_cadastrar_usuario"
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _loading.value = false
                _error.value = t.message
            }
        })
    }
    fun limparErro(){
        _error.value = null
    }


}