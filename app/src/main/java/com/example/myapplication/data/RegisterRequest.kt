package com.example.myapplication.data

data class RegisterRequest(
    val nome: String,
    val email: String,
    val senha: String,
    val zonaId: Int?
)