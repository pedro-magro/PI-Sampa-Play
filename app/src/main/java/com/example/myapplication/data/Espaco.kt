package com.example.myapplication.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Espaco(
    val id: Int,
    val nome: String,
    val endereco: String,
    val cep: String?,
    val imagemUrl: String?,
    val categoriaNome: String,
    var condicaoGeralId: Int?,
    var condicaoGeralNome: String?,
    val zonaId: Int?,
    val zonaNome: String?,

    @SerializedName("ESPACO_APROVADO")
    val ESPACO_APROVADO: Int = 0,
    @SerializedName("ESPACO_DATA")
    val ESPACO_DATA: String? = null,
    @SerializedName("CATEGORIA_ID")
    val CATEGORIA_ID: Int = 1
): Parcelable

