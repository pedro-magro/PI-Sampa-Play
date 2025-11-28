package com.example.myapplication.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeedbackResponse(
    val id: Int,
    val dataEnvio: String,
    val observacao: String,
    val condicaoId: Int,
    val condicaoNome: String,
    val usuarioNome: String

): Parcelable