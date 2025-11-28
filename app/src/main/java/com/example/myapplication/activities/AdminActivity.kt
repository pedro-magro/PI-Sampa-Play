package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.AdminAdapter
import com.example.myapplication.databinding.ActivityAdminBinding
import com.example.myapplication.remote.RetrofitClient
import com.example.myapplication.remote.SessionManager
import com.example.myapplication.repositories.EspacoRepository
import com.example.myapplication.screenViewModels.PainelAdminViewModel

class AdminActivity : BaseActivity() { // Herda de BaseActivity (sem menu ativo, mas com lógica comum)

    private lateinit var binding: ActivityAdminBinding
    private lateinit var viewModel: PainelAdminViewModel
    private lateinit var adapter: AdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Se quiser ativar o menu com item "vazio" selecionado (opcional)
        setupBottomNavigation(binding.bottomNavigation, 0)

        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupListeners()

        // Carrega a lista inicial
        viewModel.carregarEspacos()
    }

    // Recarrega a lista ao voltar da tela de edição
    override fun onResume() {
        super.onResume()
        viewModel.carregarEspacos()
    }

    private fun setupViewModel() {
        val session = SessionManager(this)
        val token = session.getToken()
        val api = RetrofitClient.getInstance { token }
        val repo = EspacoRepository(api)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                // Se o seu PainelAdminViewModel recebe 'session', passe aqui.
                // Se removeu do construtor como sugerido antes, passe só o repo.
                return PainelAdminViewModel(repo, session) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[PainelAdminViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = AdminAdapter(
            onEditClick = { espaco ->
                // Vai para a tela de edição passando o objeto
                val intent = Intent(this, EditarEspacoActivity::class.java)
                intent.putExtra("ESPACO_OBJETO", espaco)
                startActivity(intent)
            },
            onDeleteClick = { espaco ->
                confirmarDelecao(espaco.id, espaco.nome)
            }
        )
        binding.recyclerViewEspacos.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewEspacos.adapter = adapter
    }

    private fun setupObservers() {
        // Loading
        viewModel.loading.observe(this) { isLoading ->
            //binding.progressBarAdmin.visibility = if (isLoading) View.VISIBLE else View.GONE
            toggleLoading(isLoading, binding.progressBarAdmin)
        }

        // Erros
        viewModel.error.observe(this) { msg ->
            if (msg != null) {
                when (msg) {
                    "Erro ao deletar espaço" -> Toast.makeText(this, getString(R.string.toast_delete_fail), Toast.LENGTH_SHORT).show()
                    "Erro ao carregar espaços" -> Toast.makeText(this, getString(R.string.erro_carregar_espacos), Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                }

                viewModel.limparErro()
            }
        }

        // Lista de Espaços
        viewModel.espacos.observe(this) { lista ->
            adapter.submitList(lista)
            binding.tvVazio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
        }

        // Sucesso ao Deletar
        viewModel.deletado.observe(this) { deletado ->
            if (deletado) {
                Toast.makeText(this, getString(R.string.toast_delete_success), Toast.LENGTH_SHORT).show()
                // (A lista recarrega automaticamente porque o ViewModel chama carregarEspacos após deletar)
            }
        }
    }

    private fun setupListeners() {
        // Botão Flutuante (FAB) para incluir
        binding.incluirEspacoButton.setOnClickListener {
            startActivity(Intent(this, IncluirEspacoActivity::class.java))
        }
    }

    private fun confirmarDelecao(id: Int, nome: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_title))
            .setMessage(getString(R.string.delete_message)+ " $nome?")
            .setPositiveButton(getString(R.string.delete_button_delete)) { _, _ ->
                viewModel.deletarEspaco(id)
            }
            .setNegativeButton(getString(R.string.delete_button_cancel), null)
            .show()
    }
}