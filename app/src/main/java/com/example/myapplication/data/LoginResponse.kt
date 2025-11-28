package com.example.myapplication.data


data class LoginResponse(
    val token: String,
    val usuario: UsuarioResponse
)