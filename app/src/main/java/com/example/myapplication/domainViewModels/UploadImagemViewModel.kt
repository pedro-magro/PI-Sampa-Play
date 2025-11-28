package com.example.myapplication.domainViewModels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repositories.ImagemRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class UploadImagemViewModel(private val imagemRepository: ImagemRepository) : ViewModel() {

    val loading = MutableLiveData<Boolean>()
    val imagemUrl = MutableLiveData<String?>()
    val error = MutableLiveData<String?>()

    private val _uploadSucesso = MutableLiveData<List<String>>()
    val uploadSucesso: LiveData<List<String>> get() = _uploadSucesso

    fun uploadMultiplasImagens(uris: List<Uri>) {
        if (uris.isEmpty()) {
            _uploadSucesso.value = emptyList()
            return
        }

        loading.value = true
        viewModelScope.launch {
            try {
                // Lança todos os uploads em paralelo
                val deferredUploads = uris.map { uri ->
                    async { imagemRepository.enviarImagem(uri) }
                }

                // Espera todos terminarem e coleta as URLs
                val urlsFinais = deferredUploads.awaitAll()

                _uploadSucesso.value = urlsFinais

            } catch (e: Exception) {
                error.value = "Falha no upload: ${e.message}"
            } finally {
                loading.value = false
            }
        }
    }

    fun uploadImagem(uri: Uri, pasta: String = "sampa_play") {
        loading.value = true

        viewModelScope.launch {
            try {
                // Chama o repositório (que agora é suspend e espera o resultado)
                val url = imagemRepository.enviarImagem(uri, pasta)

                // Se chegou aqui, deu sucesso
                imagemUrl.value = url

            } catch (e: Exception) {
                // Se o repositório lançou exceção (onError), cai aqui
                error.value = e.message
            } finally {
                loading.value = false
            }
        }
    }
}