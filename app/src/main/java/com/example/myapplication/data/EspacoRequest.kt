package com.example.myapplication.data

data class EspacoRequest(
    val nome: String,
    val endereco: String,
    val cep: String?,
    val categoriaId: Int,
    val zonaId: Int?,
    val imagens: List<String?>
)
