package com.example.myapplication.repositories

import com.example.myapplication.data.LoginRequest
import com.example.myapplication.data.LoginResponse
import com.example.myapplication.data.RegisterRequest
import com.example.myapplication.data.RegisterResponse
import com.example.myapplication.data.UsuarioResponse
import com.example.myapplication.remote.ApiService
import com.example.myapplication.remote.SessionManager
import retrofit2.Call
import retrofit2.Response

class AuthRepository (private val api: ApiService, private val session: SessionManager) {

    fun login(email: String, senha: String): Call<LoginResponse> {
        return api.login(LoginRequest(email, senha))

    }

    fun handleLoginSuccess(response: LoginResponse) {
        session.saveToken(response.token)
        session.saveUser(response.usuario)
    }

     fun cadastrarUsuario(request: RegisterRequest): Call<RegisterResponse> {
        return api.register(request)
    }

    fun getMe(): Call<UsuarioResponse> {
        return api.getMe()
    }

    fun getMeLocal(): UsuarioResponse?{

        val id = session.getUserId()
        val nome = session.getUserName().toString()
        val email = session.getUserEmail().toString()
        val tipo = if(session.isAdmin()) "ADMIN" else "COMUM"
        val zonaId = session.getUserZonaId()

        if(id != 0 && nome != null && email != null){
            return UsuarioResponse(id, nome, email, tipo, zonaId)
        }
        return null
    }

    fun logout(){
        session.clearSession()
    }
}