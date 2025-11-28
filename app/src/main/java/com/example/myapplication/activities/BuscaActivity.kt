package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.BuscaAdapter
import com.example.myapplication.data.CategoriaResponse
import com.example.myapplication.data.ZonaResponse
import com.example.myapplication.databinding.ActivityBuscaBinding
import com.example.myapplication.remote.RetrofitClient
import com.example.myapplication.remote.SessionManager
import com.example.myapplication.repositories.CategoriaRepository
import com.example.myapplication.repositories.EspacoRepository
import com.example.myapplication.repositories.ZonaRepository
import com.example.myapplication.screenViewModels.BuscaViewModel

class BuscaActivity : BaseActivity() {

    private lateinit var binding: ActivityBuscaBinding
    private lateinit var viewModel: BuscaViewModel
    private val adapter = BuscaAdapter()

    // Listas para mapear Nome -> ID
    private var listaZonas: List<ZonaResponse> = emptyList()
    private var listaCategorias: List<CategoriaResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuscaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Configurar Menu Inferior (BaseActivity)
        setupBottomNavigation(binding.bottomNavigation, R.id.nav_search)

        // 2. Configurar ViewModel Manualmente
        setupViewModel()

        // 3. Configurar RecyclerView
        binding.rvResultadosBusca.layoutManager = LinearLayoutManager(this)
        binding.rvResultadosBusca.adapter = adapter

        // 4. Observadores e Listeners
        setupObservers()
        setupListeners()

        // 5. Carregar Filtros Iniciais
        viewModel.getZonas()
        viewModel.getCategorias()
    }

    private fun setupViewModel() {
        val session = SessionManager(this)
        val token = session.getToken()
        val api = RetrofitClient.getInstance { token }

        val espacoRepo = EspacoRepository(api)
        val zonaRepo = ZonaRepository(api)
        val catRepo = CategoriaRepository(api)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return BuscaViewModel(espacoRepo, zonaRepo, catRepo) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[BuscaViewModel::class.java]
    }

    private fun setupObservers() {
        // Loading
        viewModel.loading.observe(this) { isLoading ->
            //binding.progressBarBusca.visibility = if (isLoading) View.VISIBLE else View.GONE
            toggleLoading(isLoading, binding.progressBarBusca)
        }

        // Erro
        viewModel.error.observe(this) { msg ->
            if (msg != null) {
                when(msg){
                    "Erro ao buscar por espaços" -> Toast.makeText(this, getString(R.string.toast_search_no_results), Toast.LENGTH_SHORT).show()
                    "Erro ao carregar dados" -> Toast.makeText(this, getString(R.string.toast_search_fail_load_data), Toast.LENGTH_SHORT).show()
                    "Categoria Inválida!" -> Toast.makeText(this, getString(R.string.erro_categoria_nao_selecionada), Toast.LENGTH_SHORT).show()
                    "Zona Inválida!" -> Toast.makeText(this, getString(R.string.erro_zona_nao_selecionada), Toast.LENGTH_SHORT).show()

                    else-> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

                }

                viewModel.limparErro()
            }
        }

        // Resultados da Busca
        viewModel.resultadosBusca.observe(this) { lista ->
            adapter.submitList(lista)

            if (lista.isEmpty()) {
                binding.tvVazio.visibility = View.VISIBLE
                binding.rvResultadosBusca.visibility = View.GONE
            } else {
                binding.tvVazio.visibility = View.GONE
                binding.rvResultadosBusca.visibility = View.VISIBLE
            }
        }

        // Filtro: Zonas
        viewModel.zonas.observe(this) { zonas ->
            listaZonas = zonas
            val nomes = zonas.map { it.nome }
            val adapterZonas = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, nomes)
            binding.actvBuscaZona.setAdapter(adapterZonas)
        }

        // Filtro: Categorias
        viewModel.categorias.observe(this) { categorias ->
            listaCategorias = categorias
            val nomes = categorias.map { it.nome }
            val adapterCats = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, nomes)
            binding.actvBuscaCategoria.setAdapter(adapterCats)
        }
    }

    private fun setupListeners() {
        // Botão Aplicar
        binding.btnAplicarBusca.setOnClickListener {
            executarBusca()
        }

        // Teclado (Enter) no campo de texto
        binding.etBuscaTexto.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                executarBusca()
                true
            } else false
        }

        // (Opcional) Botão de ícone no fim do campo de texto para limpar
        binding.tilBuscaTexto.setEndIconOnClickListener {
            binding.etBuscaTexto.text?.clear()
            // Se quiser recarregar tudo: executarBusca()
        }
    }

    private fun executarBusca() {
        // 1. Texto
        val termo = binding.etBuscaTexto.text.toString().takeIf { it.isNotBlank() }

        // 2. ID da Zona (busca na lista local pelo nome selecionado)
        val nomeZona = binding.actvBuscaZona.text.toString()
        val zonaId = listaZonas.find { it.nome == nomeZona }?.id

        // 3. ID da Categoria
        val nomeCat = binding.actvBuscaCategoria.text.toString()
        val catId = listaCategorias.find { it.nome == nomeCat }?.id

        // 4. Chama ViewModel
        viewModel.buscar(termo, catId, zonaId)
    }
}