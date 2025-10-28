package com.example.myapplication

data class Feedback(
    // Use os aliases exatos do SELECT do feedbacks.php
    val id: Int,
    val observacao: String?,
    val dataEnvio: String?, // (PHP: dataEnvio)
    val condicaoNome: String?, // (PHP: condicaoNome)
    val condicaoCor: String?  // (PHP: condicaoCor)
)
