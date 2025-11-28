package com.example.myapplication.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UsuarioResponse(
    val id: Int,
    val nome: String,
    val email: String,
    val tipo: String,
    val zonaId: Int
): Parcelable