package com.example.myapplication

import com.google.gson.annotations.SerializedName
data class Feedback(
    // O ID único do feedback
    @SerializedName("FEEDBACK_ID")
    val id: Int,

    // O texto da observação (pode ser nulo)
    @SerializedName("FEEDBACK_OBS")
    val observacao: String?,

    // A data formatada (ex: "10/10/2025")
    @SerializedName("FEEDBACK_DATA")
    val dataEnvio: String,

    // O nome da condição (ex: "Boa", "Ruim/Insegura")
    @SerializedName("CONDICAO_NOME")
    val condicaoNome: String,

    // A cor (ex: "#D50000") - Opcional, mas útil
    @SerializedName("CONDICAO_COR")
    val condicaoCor: String?
)
