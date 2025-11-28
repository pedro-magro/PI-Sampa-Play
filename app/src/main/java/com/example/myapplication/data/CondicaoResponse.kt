package com.example.myapplication.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CondicaoResponse(
    val id: Int,
    val nome: String,
): Parcelable {
    override fun toString(): String = nome
}
