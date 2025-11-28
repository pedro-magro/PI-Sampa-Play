package com.example.myapplication.screenViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.FeedbackRequest
import com.example.myapplication.data.FeedbackResponse
import com.example.myapplication.repositories.EspacoRepository
import com.example.myapplication.repositories.FeedbackRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItemDetalhesViewModel(
    private val espacoRepository: EspacoRepository,
    private val feedbackRepository: FeedbackRepository
): ViewModel(){

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get()= _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get()= _error

    private val _feedback = MutableLiveData<List<FeedbackResponse>>()
    val feedback: LiveData<List<FeedbackResponse>> get()= _feedback

    private val _feedbackEnviado = MutableLiveData<FeedbackResponse>()
    val feedbackEnviado: LiveData<FeedbackResponse> get()= _feedbackEnviado

    fun carregarFeedbacks(espacoId: Int){
        _loading.value = true

        feedbackRepository.listarFeedbacks(espacoId).enqueue(object : Callback<List<FeedbackResponse>>{
            override fun onResponse(
                call: Call<List<FeedbackResponse>>,
                response: Response<List<FeedbackResponse>>
            ){
                _loading.value = false
                if(response.isSuccessful && response.body() != null) {
                    _feedback.value = response.body()
                } else {
                    _error.value = "Erro ao carregar feedbacks"
                }
            }
            override fun onFailure(call: Call<List<FeedbackResponse>>, t: Throwable) {
                _loading.value = false
                _error.value = t.message
            }
        })
    }

    fun enviarFeedback(espacoId: Int, condicaoId: Int, comentario: String?){
        _loading.value = true

        val request = FeedbackRequest(
            espacoId = espacoId,
            condicaoId = condicaoId,
            observacao = comentario?: ""
        )
        feedbackRepository.criarFeedback(request).enqueue(object: Callback<FeedbackResponse>{
            override fun onResponse(
                call: Call<FeedbackResponse>,
                response: Response<FeedbackResponse>
            ){
                _loading.value = false
                if(response.isSuccessful && response.body() != null) {
                    _feedbackEnviado.value = response.body()
                    carregarFeedbacks(espacoId)
                } else {
                    _error.value = "Erro ao enviar feedback"
                }
            }
            override fun onFailure(call: Call<FeedbackResponse>, t: Throwable) {
                _loading.value = false
                _error.value = t.message
            }
        })
    }

    fun limparErro(){
        _error.value = null
    }
}