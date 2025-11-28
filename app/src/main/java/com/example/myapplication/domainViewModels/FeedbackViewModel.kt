package com.example.myapplication.domainViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.FeedbackRequest
import com.example.myapplication.data.FeedbackResponse
import com.example.myapplication.repositories.FeedbackRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedbackViewModel(private val repository: FeedbackRepository) {

    private val _feedbacks = MutableLiveData<List<FeedbackResponse>>()
    val feedbacks: LiveData<List<FeedbackResponse>> get() =_feedbacks

    private val _result = MutableLiveData<FeedbackResponse?>()
    val result: LiveData<FeedbackResponse?> get() = _result

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun listar(espacoId: Int){
        _loading.value = true

        repository.listarFeedbacks(espacoId).enqueue(object : Callback<List<FeedbackResponse>> {
            override fun onResponse(
                call: Call<List<FeedbackResponse>>,
                response: Response<List<FeedbackResponse>>
            ){
                _loading.value = false
                if(response.isSuccessful){
                    _feedbacks.value = response.body()
                } else {
                    _error.value = "Erro ao listar feedbacks"
                }
            }
            override fun onFailure(call: Call<List<FeedbackResponse>>, t: Throwable){
                _loading.value = false
                _error.value = t.message
            }
        })
    }

    fun enviar(request: FeedbackRequest){
        _loading.value = true

        repository.criarFeedback(request).enqueue(object : Callback<FeedbackResponse> {
            override fun onResponse(
                call: Call<FeedbackResponse>,
                response: Response<FeedbackResponse>
            ) {
                _loading.value = false
                if (response.isSuccessful) {
                    _result.value = response.body()
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




}