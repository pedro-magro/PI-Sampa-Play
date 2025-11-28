package com.example.myapplication.screenViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.CategoriaResponse
import com.example.myapplication.data.EspacoRequest
import com.example.myapplication.data.EspacoResponse
import com.example.myapplication.data.ZonaResponse
import com.example.myapplication.repositories.CategoriaRepository
import com.example.myapplication.repositories.EspacoRepository
import com.example.myapplication.repositories.ZonaRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditarViewModel(
    private val espacoRepository: EspacoRepository,
    private val categoriaRepository: CategoriaRepository,
    private val zonaRepository: ZonaRepository
) : ViewModel(){

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get () = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get () = _error

    private val _carregarDados = MutableLiveData<EspacoResponse>()
    val carregarDados: LiveData<EspacoResponse> get () = _carregarDados
    private val _sucessoEdicao = MutableLiveData<EspacoResponse>()
    val sucessoEdicao: LiveData<EspacoResponse> get () = _sucessoEdicao

    private val _categorias = MutableLiveData<List<CategoriaResponse>>()
    val categorias: LiveData<List<CategoriaResponse>> get () = _categorias

    private val _zonas = MutableLiveData<List<ZonaResponse>>()
    val zonas: LiveData<List<ZonaResponse>> get () = _zonas

    fun atualizarEspaco(espacoId:Int, nome: String, endereco: String, cep: String?, categoriaId: Int, imagemUrls:List<String?>, zonaId: Int?, aprovado: Boolean){
        _loading.value = true

        val request = EspacoRequest(
            nome = nome,
            endereco = endereco,
            cep = cep,
            categoriaId = categoriaId,
            zonaId = zonaId,
            imagens = imagemUrls
        )
        espacoRepository.atualizarEspaco(espacoId, request).enqueue(object: Callback<EspacoResponse> {
            override fun onResponse(call: Call<EspacoResponse>, response: Response<EspacoResponse>) {
                if (response.isSuccessful) {

                    // 3. SUCESSO NO PUT -> AGORA CHAMAMOS O PATCH (Aprovação)
                    chamadaAprovarEspaco(espacoId, aprovado)

                } else {
                    _loading.value = false
                    _error.value = "Erro ao atualizar dados"
                }
            }

            override fun onFailure(call: Call<EspacoResponse>, t: Throwable) {
                _loading.value = false
                _error.value = "Falha de conexão"
            }
        })
    }
    fun chamadaAprovarEspaco(id: Int, aprovado: Boolean){
        espacoRepository.aprovarEspaco(id, aprovado).enqueue(object : Callback<EspacoResponse> {
            override fun onResponse(call: Call<EspacoResponse>, response: Response<EspacoResponse>) {
                _loading.value = false // FIM DO PROCESSO

                if (response.isSuccessful) {
                    // TUDO CERTO!
                    _sucessoEdicao.value = response.body()
                } else {
                    // O PUT funcionou, mas o PATCH falhou.
                    // Podemos considerar sucesso parcial ou erro.
                    // Como o user queria salvar, vamos dar erro para ele saber.
                    _error.value = "Dados salvos, mas erro ao alterar status."
                }
            }

            override fun onFailure(call: Call<EspacoResponse>, t: Throwable) {
                _loading.value = false
                _error.value = "Dados salvos, mas falha ao alterar status."
            }
        })
    }

    fun carregarZonas(){
        _loading.value = true
        zonaRepository.listarZonas().enqueue(createCallback(_zonas))
    }

    fun carregarCategorias(){
        categoriaRepository.listarCategorias().enqueue(createCallback(_categorias))

    }

    private fun <T> createCallback(liveData: MutableLiveData<T>) =
        object: Callback<T>{
            override fun onResponse(call: Call<T>, response: Response<T>) {
                _loading.value = false

                if(response.isSuccessful){
                    liveData.value = response.body()
                } else {
                    _error.value = "Erro ao salvar"
                }
            }
            override fun onFailure(call: Call<T>, t: Throwable) {
                _loading.value = false
                _error.value = t.message
            }
        }
    fun limparErro(){
        _error.value = null
    }

}