package com.example.myapplication.activities

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.ImagensAdapter
import com.example.myapplication.data.CategoriaResponse
import com.example.myapplication.data.EspacoResponse
import com.example.myapplication.data.ZonaResponse
import com.example.myapplication.databinding.ActivityEditarEspacoBinding
import com.example.myapplication.CloudinaryApplication.MyApplication
import com.example.myapplication.R
import com.example.myapplication.remote.RetrofitClient
import com.example.myapplication.remote.SessionManager
import com.example.myapplication.repositories.CategoriaRepository
import com.example.myapplication.repositories.EspacoRepository
import com.example.myapplication.repositories.ImagemRepository
import com.example.myapplication.repositories.ZonaRepository
import com.example.myapplication.screenViewModels.EditarViewModel
import com.example.myapplication.domainViewModels.UploadImagemViewModel

class EditarEspacoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarEspacoBinding
    private lateinit var viewModel: EditarViewModel
    private lateinit var uploadViewModel: UploadImagemViewModel
    private lateinit var imagensAdapter: ImagensAdapter

    private var espacoAtual: EspacoResponse? = null
    private var listaZonas: List<ZonaResponse> = emptyList()
    private var listaCategorias: List<CategoriaResponse> = emptyList()

    // Lista local para gerir as imagens antes de salvar
    private var listaUrisPendentes: MutableList<Uri> = mutableListOf()

    private var listaImagensFinal: MutableList<String> = mutableListOf()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let{
            listaUrisPendentes.add(it)
            imagensAdapter.adicionarImagem(uri.toString())

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarEspacoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        espacoAtual = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("ESPACO_OBJETO", EspacoResponse::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("ESPACO_OBJETO")
        }

        if (espacoAtual == null) {
            Toast.makeText(this, getString(R.string.erro_carregar_dados), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupViewModels()
        setupImagensRecyclerView()
        setupObservers()
        setupListeners()

        viewModel.carregarCategorias()
        viewModel.carregarZonas()
        preencherDados()
    }

    private fun setupImagensRecyclerView() {
        // Inicializa o adapter com callback de posição (Int)
        imagensAdapter = ImagensAdapter(mutableListOf()) { urlRemovida ->
            // 1. Remove da lista lógica local
            if(urlRemovida.startsWith("http")){
                listaImagensFinal.remove(urlRemovida)
            }
            else{
                listaUrisPendentes.removeIf { it.toString() == urlRemovida }
            }

            // 2. Remove visualmente do adapter
            imagensAdapter.removerImagem(urlRemovida)
        }

        binding.rvImagens.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvImagens.adapter = imagensAdapter
    }

    private fun preencherDados() {
        espacoAtual?.let { espaco ->
            binding.etNome.setText(espaco.nome)
            binding.etEndereco.setText(espaco.endereco)
            binding.etCep.setText(espaco.cep)
            binding.actvZona.setText(espaco.zonaNome, false)
            binding.actvCategoria.setText(espaco.categoriaNome, false)

            val session = SessionManager(this)
            if (session.isAdmin()) {
                binding.switchAprovado.visibility = View.VISIBLE
                binding.switchAprovado.isChecked = espaco.aprovado
            }

            // CORREÇÃO: Usar a lista de imagens do DTO
            val imagensDoEspaco = espaco.imagens ?: emptyList() // Garante que não é nulo

            listaImagensFinal.clear()
            listaImagensFinal.addAll(imagensDoEspaco)

            // Popula o adapter
            imagensAdapter.adicionarLista(imagensDoEspaco)

            // Esconde o preview antigo se ele ainda existir no XML
            // binding.ivPreviewImagem.visibility = View.GONE
        }
    }

    private fun setupViewModels() {
        val session = SessionManager(this)
        val api = RetrofitClient.getInstance { session.getToken() }

        val editarFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EditarViewModel(
                    EspacoRepository(api),
                    CategoriaRepository(api),
                    ZonaRepository(api)
                ) as T
            }
        }
        viewModel = ViewModelProvider(this, editarFactory)[EditarViewModel::class.java]

        val uploadFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UploadImagemViewModel(ImagemRepository()) as T
            }
        }
        uploadViewModel = ViewModelProvider(this, uploadFactory)[UploadImagemViewModel::class.java]
    }

    private fun setupObservers() {
        fun updateLoading(loading: Boolean) {
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnSalvar.isEnabled = !loading
        }
        viewModel.loading.observe(this) { updateLoading(it) }
        uploadViewModel.loading.observe(this) { updateLoading(it) }

        // Sucesso no Upload: Adiciona à lista e ao adapter
        uploadViewModel.uploadSucesso.observe(this) { novasUrls ->
            if (novasUrls != null) {
                listaImagensFinal.addAll(novasUrls)
                uploadViewModel.imagemUrl.value = null
                listaUrisPendentes.clear()
                finalizarEdicao()
            }
        }

        viewModel.sucessoEdicao.observe(this) {
            if (it != null) {
                Toast.makeText(this, getString(R.string.msg_sucesso_incluir), Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.error.observe(this) { msg ->
            if(msg!=null){
                when(msg){
                    "Erro ao atualizar dados" -> Toast.makeText(this, getString(R.string.erro_salvar_espaco), Toast.LENGTH_SHORT).show()
                    "Dados salvos, mas erro ao alterar status." -> Toast.makeText(this, getString(R.string.erro_salvar_espaco_status), Toast.LENGTH_SHORT).show()
                    "Falha de conexão" -> Toast.makeText(this, getString(R.string.erro_api_generico), Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                }

            }

        }
        uploadViewModel.error.observe(this) { msg ->
            if(msg!=null)
                Toast.makeText(this,getString(R.string.erro_upload_imagem)+": $msg", Toast.LENGTH_SHORT).show()
        }

        viewModel.zonas.observe(this) { lista ->
            listaZonas = lista
            binding.actvZona.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, lista.map { it.nome }))
        }
        viewModel.categorias.observe(this) { lista ->
            listaCategorias = lista
            binding.actvCategoria.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, lista.map { it.nome }))
        }
    }

    private fun setupListeners() {
        binding.toolbarEditarEspaco.setNavigationOnClickListener { finish() }
        binding.btnCancelar.setOnClickListener { finish() }

        binding.cardUploadImagem.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSalvar.setOnClickListener {

            if (binding.etNome.text.isNullOrBlank() || binding.etEndereco.text.isNullOrBlank()) {
                Toast.makeText(this, getString(R.string.erro_campos_obrigatorios), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(listaUrisPendentes.isNotEmpty()){
                uploadViewModel.uploadMultiplasImagens(listaUrisPendentes)
            }
            else{ finalizarEdicao()}
        }
    }
    private fun finalizarEdicao(){
        val nome = binding.etNome.text.toString()
        val endereco = binding.etEndereco.text.toString()
        val cep = binding.etCep.text.toString()
        val aprovado = binding.switchAprovado.isChecked // Switch de Admin

        val nomeCat = binding.actvCategoria.text.toString()
        val categoriaId = listaCategorias.find { it.nome == nomeCat }?.id

        val nomeZona = binding.actvZona.text.toString()
        val zonaId = listaZonas.find { it.nome == nomeZona }?.id

        if (nome.isNotBlank() && categoriaId != null) {
            // CORREÇÃO: Envia listaImagensFinal (que agora contem a lista atualizada)
            // Note: O 'aprovado' é passado para lógica do ViewModel se necessário

            // O método atualizarEspaco do ViewModel precisa ter sido atualizado para aceitar lista!
            viewModel.atualizarEspaco(
                espacoId = espacoAtual!!.id,
                nome = nome,
                endereco = endereco,
                cep = cep,
                categoriaId = categoriaId,
                imagemUrls = listaImagensFinal,
                zonaId = zonaId,
                aprovado = aprovado
            )
        } else {
            Toast.makeText(this, getString(R.string.erro_campos_obrigatorios), Toast.LENGTH_SHORT).show()
        }
    }
}