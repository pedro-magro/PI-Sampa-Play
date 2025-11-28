package com.example.myapplication.domainViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.myapplication.data.LoginResponse
import com.example.myapplication.data.RegisterRequest
import com.example.myapplication.data.RegisterResponse
import com.example.myapplication.repositories.AuthRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel(private val repository: AuthRepository) {

    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> get() = _loginResult

    private val _registerResult = MutableLiveData<RegisterResponse?>()
    val registerResult: LiveData<RegisterResponse?> get() = _registerResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun login(email: String, senha: String) {
        _loading.value = true

        repository.login(email, senha).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _loading.value = false
                if (response.isSuccessful) {
                    _loginResult.value = response.body()
                } else {
                    _error.value = "Credenciais inválidas"
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loading.value = false
                _error.value = t.message
            }
        })

    }

    fun register(request: RegisterRequest){
        _loading.value = true

        repository.cadastrarUsuario(request).enqueue(object : Callback<RegisterResponse> {

            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _loading.value = false
                if (response.isSuccessful) {
                    _registerResult.value = response.body()
                } else {
                    _error.value = "Erro ao cadastrar usuário"
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _loading.value = false
                _error.value = t.message
            }

        })
    }

}