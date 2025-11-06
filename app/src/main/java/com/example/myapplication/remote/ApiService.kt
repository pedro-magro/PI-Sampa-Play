package com.example.myapplication.remote

import FeedbackResponse
import com.example.myapplication.data.CadastroResponse
import com.example.myapplication.data.Categoria
import com.example.myapplication.data.Espaco
import com.example.myapplication.data.Feedback
import com.example.myapplication.data.Zona
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    // MÃƒÂ©todo para obter a lista de espacos
    @GET("listar.php")
    fun getEspacos(@Query("usuario_zona_id") usuarioZonaId: Int?): Call<List<Espaco>>

    @GET("listar_admin.php")
    fun getEspacosAdmin(): Call<List<Espaco>>

    // MÃƒÂ©todo para incluir um espaco
    @FormUrlEncoded
    @POST("incluir_espaco.php")
    fun incluirEspaco(
        @Field("ESPACO_NOME") nome: String,
        @Field("ESPACO_ENDERECO") endereco: String,
        @Field("ESPACO_CEP") cep: String?,
        @Field("ESPACO_IMG") imgUrl: String?,
        @Field("CATEGORIA_ID")  categoriaId: Int,
        @Field("ESPACO_APROVADO") aprovado: Int,
        @Field("ZONA_ID") zonaId: Int?
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

    @GET("listar_zonas.php")
    fun getZonas(): Call<List<Zona>>

    @FormUrlEncoded
    @POST("cadastro.php")
    fun cadastrarUsuario(
        @Field("USUARIO_NOME") usuarioNome: String,
        @Field("USUARIO_EMAIL") usuarioEmail: String,
        @Field("USUARIO_SENHA") usuarioSenha : String,
        @Field("ZONA_ID") zonaId: Int?
    ): Call<CadastroResponse>


    @GET("busca.php")
    fun buscarEspaco(
        @Query("termo") termo:String?,
        @Query("categoria_id") categoriaId: Int?
    ): Call<List<Espaco>>


    @GET("categorias.php")
    fun getCategorias() : Call<List<Categoria>>

    @FormUrlEncoded
    @POST("enviar_feedback.php")
    fun enviarFeedback(
        @Field("espaco_id") espacoId: Int,
        @Field("usuario_id") usuarioId: Int,
        @Field("condicao_id") condicaoId: Int,
        @Field("observacao") feedbackObservacao: String
    ): Call<FeedbackResponse>

    @GET("feedbacks.php")
    fun getFeedbacks(
        @Query("espaco_id") espacoId: Int
    ): Call<List<Feedback>>

}