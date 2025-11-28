package com.example.myapplication.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.ImagensAdapter
import com.example.myapplication.data.CategoriaResponse
import com.example.myapplication.data.ZonaResponse
import com.example.myapplication.databinding.ActivityIncluirEspacoBinding
import com.example.myapplication.CloudinaryApplication.MyApplication
import com.example.myapplication.remote.RetrofitClient
import com.example.myapplication.remote.SessionManager
import com.example.myapplication.repositories.*
import com.example.myapplication.screenViewModels.IncluirViewModel
import com.example.myapplication.domainViewModels.UploadImagemViewModel

class IncluirEspacoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIncluirEspacoBinding
    private lateinit var incluirViewModel: IncluirViewModel
    private lateinit var uploadViewModel: UploadImagemViewModel
    private lateinit var imagensAdapter: ImagensAdapter

    private lateinit var toolbar: Toolbar

    private var listaZonas: List<ZonaResponse> = emptyList()
    private var listaCategorias: List<CategoriaResponse> = emptyList()

    private val listaUrisPendentes = mutableListOf<Uri>()

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                listaUrisPendentes.add(it)
                imagensAdapter.adicionarImagem(it.toString())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIncluirEspacoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(!SessionManager(this).isAdmin()){
            binding.toolbarIncluir.title = getString(R.string.form_title_include_user)

        }

        setupViewModels()
        setupRecyclerView()
        setupObservers()
        setupListeners()

        incluirViewModel.carregarCategorias()
        incluirViewModel.carregarZonas()
    }

    private fun setupRecyclerView() {
        imagensAdapter = ImagensAdapter(mutableListOf()) { urlRemovida ->

            imagensAdapter.removerImagem(urlRemovida)
            listaUrisPendentes.removeIf { it.toString() == urlRemovida }
        }

        binding.rvImagens.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.rvImagens.adapter = imagensAdapter
    }

    private fun setupViewModels() {
        val token = SessionManager(this).getToken()
        val api = RetrofitClient.getInstance { token }

        val incluirFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(c: Class<T>): T {
                return IncluirViewModel(
                    EspacoRepository(api),
                    CategoriaRepository(api),
                    ZonaRepository(api)
                ) as T
            }
        }
        incluirViewModel = ViewModelProvider(this, incluirFactory)[IncluirViewModel::class.java]

        val uploadFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(c: Class<T>): T {
                return UploadImagemViewModel(ImagemRepository()) as T
            }
        }
        uploadViewModel = ViewModelProvider(this, uploadFactory)[UploadImagemViewModel::class.java]
    }

    private fun setupObservers() {

        incluirViewModel.loading.observe(this) { controlar(it) }
        uploadViewModel.loading.observe(this) { controlar(it) }

        incluirViewModel.zonas.observe(this) { zonas ->
            listaZonas = zonas
            binding.actvZona.setAdapter(
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, zonas.map { it.nome })
            )
        }

        incluirViewModel.categorias.observe(this) { categorias ->
            listaCategorias = categorias
            binding.actvCategoria.setAdapter(
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categorias.map { it.nome })
            )
        }

        uploadViewModel.uploadSucesso.observe(this) { urls ->
            if(urls != null){
                finalizarUpload(urls)
            }
        }

        incluirViewModel.sucessoInclusao.observe(this) {
            Toast.makeText(this, getString(R.string.msg_sucesso_incluir), Toast.LENGTH_SHORT).show()
            finish()
        }

        incluirViewModel.error.observe(this) { erro ->
            if (erro != null) {
                when(erro){
                    "Selecione uma categoria" -> Toast.makeText(this, getString(R.string.erro_categoria_nao_selecionada), Toast.LENGTH_SHORT).show()
                    "Erro ao salvar" -> Toast.makeText(this, getString(R.string.erro_salvar_espaco), Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(this, erro, Toast.LENGTH_SHORT).show()
                }
                incluirViewModel.limparErro()


            }
        }
    }

    private fun setupListeners() {

        binding.cardUpload.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        toolbar = binding.toolbarIncluir
        toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnSalvar.setOnClickListener {

            binding.btnSalvar.isEnabled = false
            binding.cardUpload.isEnabled = false
            binding.toolbarIncluir.setOnClickListener{ false }


            val nome = binding.etNome.text.toString()
            val endereco = binding.etEndereco.text.toString()

            if (nome.isBlank() || endereco.isBlank()) {
                Toast.makeText(this, getString(R.string.erro_campos_obrigatorios), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val categoriaNome = binding.actvCategoria.text.toString()
            val zonaNome = binding.actvZona.text.toString()

            val categoriaId = listaCategorias.find { it.nome == categoriaNome }?.id
            val zonaId = listaZonas.find { it.nome == zonaNome }?.id

            if (categoriaId == null) {
                binding.actvCategoria.error = getString(R.string.erro_categoria_nao_selecionada)
                Toast.makeText(this, getString(R.string.erro_categoria_invalida), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (zonaId == null) {
                binding.actvZona.error = getString(R.string.erro_zona_nao_selecionada)
                Toast.makeText(this, getString(R.string.erro_zona_invalida), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(listaUrisPendentes.isNotEmpty()){
                uploadViewModel.uploadMultiplasImagens(listaUrisPendentes)
            }else{
                finalizarUpload(emptyList())
            }
        }
    }

    private fun finalizarUpload(urls: List<String>) {
        val categoriaId = listaCategorias.find { it.nome == binding.actvCategoria.text.toString() }?.id
        val zonaId = listaZonas.find { it.nome == binding.actvZona.text.toString() }?.id

        incluirViewModel.Salvar(
            binding.etNome.text.toString(),
            binding.etEndereco.text.toString(),
            binding.etCep.text.toString(),
            categoriaId ?: 0,
            urls, // Envia as URLs finais
            zonaId
        )
    }

    private fun controlar(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnSalvar.isEnabled = !loading
        binding.cardUpload.isEnabled = !loading
    }
}
