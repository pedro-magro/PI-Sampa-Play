package com.example.myapplication.repositories

import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ImagemRepository {

    /**
     * Faz o upload para o Cloudinary e retorna a URL (String)
     * Suspende a thread até terminar.
     */
    suspend fun enviarImagem(imageUri: Uri, pasta: String = "sampa_play"): String {

        // Transforma o Callback do Cloudinary em uma Coroutine
        return suspendCancellableCoroutine { continuation ->

            val requestId = MediaManager.get().upload(imageUri)
                .unsigned("sampa_upload") // SEU PRESET (Verifique no site se é esse nome mesmo)
                .option("folder", pasta)
                .callback(object : UploadCallback {

                    override fun onStart(requestId: String) {
                        Log.d("Cloudinary", "Iniciando upload: $requestId")
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        // (Opcional: Poderia implementar um callback de progresso aqui)
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String ?: ""

                        // SUCESSO: Acorda a coroutine devolvendo a URL
                        if (continuation.isActive) {
                            continuation.resume(url)
                        }
                    }

                    override fun onError(requestId: String, errorInfo: ErrorInfo) {
                        // ERRO: Acorda a coroutine lançando uma Exceção
                        if (continuation.isActive) {
                            continuation.resumeWithException(
                                RuntimeException("Erro Cloudinary: ${errorInfo.description}")
                            )
                        }
                    }

                    override fun onReschedule(requestId: String, errorInfo: ErrorInfo) {
                        // Upload adiado (sem internet)
                    }
                })
                .dispatch()

            // Se o ViewModel for cancelado, tenta cancelar o upload
            continuation.invokeOnCancellation {
                MediaManager.get().cancelRequest(requestId) // (Opcional)
            }
        }
    }
}