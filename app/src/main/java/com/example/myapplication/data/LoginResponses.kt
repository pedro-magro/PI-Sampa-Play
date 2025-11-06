package com.example.myapplication.data

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val usuarioId: Int,
    val usuarioNome: String,
    val usuarioEmail: String,
    val usuarioSenha: String,
    val usuarioTipo: String, val
    condicaoGeralId: Int?,
    @SerializedName("ZONA_ID") val zonaId: Int?,
    @SerializedName("ZONA_NOME") val zonaNome: String?
)

