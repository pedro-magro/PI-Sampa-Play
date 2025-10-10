package com.example.myapplication

import android.provider.ContactsContract
import com.google.gson.annotations.SerializedName

data class Espaco(
    val id: Int,
    val nome: String,
    val endereco: String,
    val cep: String?,
    val imagemUrl: String?,
    val categoriaNome: String,

    @SerializedName("ESPACO_APROVADO")
    val ESPACO_APROVADO: Int = 0,
    @SerializedName("ESPACO_DATA")
    val ESPACO_DATA: String? = null,
    @SerializedName("CATEGORIA_ID")
    val CATEGORIA_ID: Int = 1
)

