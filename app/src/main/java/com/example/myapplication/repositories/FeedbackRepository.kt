package com.example.myapplication.repositories

import com.example.myapplication.data.FeedbackRequest
import com.example.myapplication.data.FeedbackResponse
import com.example.myapplication.remote.ApiService
import retrofit2.Call

class FeedbackRepository(private val api: ApiService) {

    fun listarFeedbacks(espacoId: Int): Call<List<FeedbackResponse>> {
        return api.getFeedbacks(espacoId)
    }

    fun criarFeedback(dto: FeedbackRequest): Call<FeedbackResponse> {
        return api.enviarFeedback(dto)
    }
}