package com.example.myapplication.remote


import com.example.myapplication.data.CategoriaResponse
import com.example.myapplication.data.CondicaoResponse
import com.example.myapplication.data.EspacoRequest
import com.example.myapplication.data.EspacoResponse
import com.example.myapplication.data.FeedbackRequest
import com.example.myapplication.data.FeedbackResponse
import com.example.myapplication.data.ImagemResponse
import com.example.myapplication.data.LoginRequest
import com.example.myapplication.data.LoginResponse
import com.example.myapplication.data.RegisterRequest
import com.example.myapplication.data.RegisterResponse
import com.example.myapplication.data.UsuarioResponse
import com.example.myapplication.data.ZonaResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    //--AUTETIFICAÇÃO-->

    @POST("auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("auth/register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @GET("auth/me")
    fun getMe(): Call<UsuarioResponse>


    //--ESPACOS-->
    @GET("espacos")
    fun listarEspacos(): Call<List<EspacoResponse>>

    @GET("espacos/{id}")
    fun buscarPorId(@Path("id") id: Int): Call<EspacoResponse>

    @GET("espacos/filtrar")
    fun filtrar(
        @Query("termo") termo: String?,
        @Query("categoriaId") categoriaId: Int?,
        @Query("zonaId") zonaId: Int?
    ): Call<List<EspacoResponse>>

    @GET("espacos/em-alta")
    fun emAlta(): Call<List<EspacoResponse>>

    @GET("espacos/proximidade")
    fun proximidade(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): Call<List<EspacoResponse>>

    @GET("espacos/zona/{id}")
    fun porZona(@Path("id") zonaId: Int): Call<List<EspacoResponse>>

    @GET("espacos/condicao")
    fun porCondicao(): Call<List<EspacoResponse>>

    @GET("espacos/recentes")
    fun recentes(): Call<List<EspacoResponse>>

    // -------- ESPAÇOS (AUTENTICADOS) --------
    @POST("espacos")
    fun criarEspaco(@Body dto: EspacoRequest): Call<EspacoResponse>


    // -------- ESPAÇOS (ADMIN) --------
    @PUT("espacos/{id}")
    fun atualizarEspaco(
        @Path("id") id: Int,
        @Body dto: EspacoRequest
    ): Call<EspacoResponse>

    @DELETE("espacos/{id}")
    fun deletarEspaco(@Path("id") id: Int): Call<Void>

    @PATCH("espacos/{id}/aprovar")
    fun aprovarEspaco(
        @Path("id") id: Int,
        @Query("aprovado") aprovado: Boolean
    ): Call<EspacoResponse>

    @GET("espacos/admin")
    fun listarTodos(): Call<List<EspacoResponse>>


    // -------- FEEDBACK --------
    @POST("feedbacks")
    fun enviarFeedback(@Body dto: FeedbackRequest): Call<FeedbackResponse>

    @GET("feedbacks/{espacoId}")
    fun getFeedbacks(@Path("espacoId") espacoId: Int): Call<List<FeedbackResponse>>


    // -------- CATEGORIAS, ZONAS, CONDIÇÕES --------
    @GET("categorias")
    fun listarCategorias(): Call<List<CategoriaResponse>>

    @GET("zonas")
    fun listarZonas(): Call<List<ZonaResponse>>

    @GET("condicao")
    fun listarCondicao(): Call<List<CondicaoResponse>>

    // -------- IMAGENS --------
    @Multipart
    @POST("upload")
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Call<ImagemResponse>

}