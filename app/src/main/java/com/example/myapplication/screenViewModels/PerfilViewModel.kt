package com.example.myapplication.screenViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.UsuarioResponse
import com.example.myapplication.repositories.AuthRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PerfilViewModel(private val repository: AuthRepository): ViewModel() {


    private val _perfil = MutableLiveData<UsuarioResponse?>()
    val perfil: LiveData<UsuarioResponse?> get() = _perfil
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean> get () = _logoutSuccess
    fun getPerfilLocal(){
        val me = repository.getMeLocal()
        if(me != null){
            _perfil.value = me
        } else {
            //_error.value = "Usuário não encontrado, faça login novamente"
            getPerfilRemoto()
        }
    }

    fun getPerfilRemoto(){
        _loading.value = true

        repository.getMe().enqueue(object : Callback<UsuarioResponse> {
            override fun onResponse(
                call: Call<UsuarioResponse>,
                response: Response<UsuarioResponse>
            ){
                _loading.value = false
                if(response.isSuccessful && response.body() != null){
                    _perfil.value = response.body()
                } else {
                    _error.value = "Erro ao carregar perfil"
                }
            }
            override fun onFailure(call: Call<UsuarioResponse>, t: Throwable){
                _loading.value = false
                _error.value = t.message
            }
        })
    }

    fun logout(){
        repository.logout()
        _logoutSuccess.value = true
    }

    fun limparErro(){
        _error.value = null
    }

}