package com.example.myapplication

import retrofit2.Call
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    // MÃƒÂ©todo para obter a lista de espacos
    @GET("listar.php")
    fun getEspacos(): Call<List<Espaco>>

    // MÃƒÂ©todo para incluir um espaco
    @FormUrlEncoded
    @POST("incluir_espaco.php")
    fun incluirEspaco(
        @Field("ESPACO_NOME") nome: String,
        @Field("ESPACO_ENDERECO") endereco: String,
        @Field("ESPACO_CEP") cep: String?,
        @Field("ESPACO_IMG") imgUrl: String?,
        @Field("CATEGORIA_ID")  categoriaId: Int
    ): Call<Void>

    // MÃƒÂ©todo para editar um espaco
    @FormUrlEncoded
    @POST("editar_espaco.php")
    fun editarEspaco(
        @Field("ESPACO_ID") id: Int,
        @Field("ESPACO_NOME") nome: String,
        @Field("ESPACO_ENDERECO") endereco: String,
        @Field("ESPACO_CEP") cep: String?,
        @Field("ESPACO_IMG") imgUrl: String?,
        @Field("CATEGORIA_ID") categoriaId: Int, // Adicionado
        @Field("ESPACO_APROVADO") aprovado: Int, // Adicionado
    ): Call<Void>

    // MÃƒÂ©todo para deletar um espaco
    @FormUrlEncoded
    @POST("deletar_espaco.php")
    fun deletarEspaco(
        @Field("ESPACO_ID") id: Int
    ): Call<Void>

    @FormUrlEncoded
    @POST("cadastro.php")
    fun cadastrarUsuario(
        @Field("USUARIO_NOME") nome: String,
        @Field("USUARIO_EMAIL") email: String,
        @Field("USUARIO_SENHA") senha : String
    ):Call<CadastroResponse>

    @FormUrlEncoded
    @GET("busca.php")
    fun buscarEspaco(
        @Field("ESPACO_NOME") nome:String?,
        @Field("CATEGORIA_ID") categoriaId: Int?
    ):Call<Void>
}
