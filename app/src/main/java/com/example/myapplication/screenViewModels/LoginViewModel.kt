package com.example.myapplication.screenViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.LoginResponse
import com.example.myapplication.repositories.AuthRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val repository: AuthRepository): ViewModel() {

    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> get() = _loginResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun login(email: String, senha: String){
        _loading.value = true

        repository.login(email, senha).enqueue(object : Callback<LoginResponse>{
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _loading.value = false

                if (response.isSuccessful && response.body() != null) {

                    repository.handleLoginSuccess(response.body()!!)
                    _loginResult.value = response.body()
                } else {
                    _error.value = "erro_credenciais_invalidas"
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loading.value = false
                _error.value = t.message
            }
        })
    }
    fun limparErro(){
        _error.value = null
    }
}