package com.example.myapplication.data

import com.google.gson.annotations.SerializedName

data class Zona(
    @SerializedName("ZONA_ID") val id: Int,
    @SerializedName("ZONA_NOME") val nome: String
){
    override fun toString(): String {
        return nome
    }
}
