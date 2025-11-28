package com.example.myapplication.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EspacoResponse(
    val id: Int,
    val nome: String,
    val endereco: String,
    val cep: String?,
    val imagens: List<String>,
    val zonaNome: String?,
    val condicaoGeralNome: String?,
    val condicaoGeralId: Int?,
    val categoriaNome: String?,
    val aprovado: Boolean,
    val distanciaEmKm: Double? = null
) : Parcelable

