import com.google.gson.annotations.SerializedName

data class FeedbackResponse(
    @SerializedName("status") val status: String,
    @SerializedName("novaCondicaoId") val novaCondicaoId: Int?,
    @SerializedName("novaCondicaoNome") val novaCondicaoNome: String?
)