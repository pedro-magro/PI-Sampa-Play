package com.example.myapplication.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.FeedbackAdapter
import com.example.myapplication.adapters.ImageCarouselAdapter
import com.example.myapplication.data.EspacoResponse
import com.example.myapplication.databinding.ActivityItemDetalheBinding
import com.example.myapplication.remote.RetrofitClient
import com.example.myapplication.remote.SessionManager
import com.example.myapplication.repositories.EspacoRepository
import com.example.myapplication.repositories.FeedbackRepository
import com.example.myapplication.screenViewModels.ItemDetalhesViewModel

class ItemDetalheActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemDetalheBinding
    private lateinit var viewModel: ItemDetalhesViewModel
    private var espacoAtual: EspacoResponse? = null

    private val feedbackAdapter = FeedbackAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetalheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        espacoAtual = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("ESPACO_OBJETO", EspacoResponse::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("ESPACO_OBJETO")
        }

        if (espacoAtual == null) {
            Toast.makeText(this, "Erro ao carregar espaço", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupViewModel()
        setupUI()
        setupObservers()
        setupListeners()

        viewModel.carregarFeedbacks(espacoAtual!!.id)
    }

    private fun setupViewModel() {
        val session = SessionManager(this)
        val api = RetrofitClient.getInstance { session.getToken() }

        val repoEspaco = EspacoRepository(api)
        val repoFeedback = FeedbackRepository(api)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ItemDetalhesViewModel(repoEspaco, repoFeedback) as T
            }
        }

        viewModel = ViewModelProvider(this, factory)[ItemDetalhesViewModel::class.java]
    }

    private fun setupUI() {
        val espaco = espacoAtual!!

        binding.tvDetalheNome.text = espaco.nome
        binding.tvDetalheEndereco.text = espaco.endereco
        binding.tvDetalheStatus.text = "Categoria: ${espaco.categoriaNome}"

        // Condição Geral
        val condicaoNome = espaco.condicaoGeralNome?: "Sem avaliações"
        val condicaoId = espaco.condicaoGeralId ?: 0

        binding.tvCondicaoGeral.text = "Condição Geral: $condicaoNome"

        val corCondicao = when (condicaoId) {
            1 -> R.color.feedback_bom
            2 -> R.color.feedback_razoavel
            3 -> R.color.feedback_ruim
            else -> R.color.sampa_cinza_texto
        }
        binding.tvCondicaoGeral.setTextColor(
            ContextCompat.getColor(this, corCondicao)
        )

        // Carrossel de imagens
        val imagens = mutableListOf<String>()
        if (espaco.imagens.isEmpty()) {
            imagens.add("android.resource://${packageName}/${R.drawable.placeholder}")
        }
        imagens.addAll(espaco.imagens)
        binding.viewPagerImagens.adapter = ImageCarouselAdapter(imagens)

        // Lista de feedback
        binding.rvFeedback.apply {
            layoutManager = LinearLayoutManager(this@ItemDetalheActivity)
            adapter = feedbackAdapter
        }
    }

    private fun setupListeners() {
        binding.toolbarDetalhe.setNavigationOnClickListener {
            finish()
        }
        binding.btnAbrirMaps.setOnClickListener {
            val endereco = espacoAtual?.endereco ?: return@setOnClickListener

            val uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(endereco)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)

            val mapsPackage = "com.google.android.apps.maps"

            if (isPackageInstalled(mapsPackage)) {
                intent.setPackage(mapsPackage)
            }

            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.toast_maps_error), Toast.LENGTH_SHORT).show()
            }
        }



        binding.btnConvidarWhatsapp.setOnClickListener {
            val texto = "Vamos jogar no *${espacoAtual?.nome}*?\n${espacoAtual?.endereco}"
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, texto)
                type = "text/plain"
                setPackage("com.whatsapp")
            }
            try {
                startActivity(sendIntent)
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.toast_whatsapp_error), Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnEnviarFeedback.setOnClickListener {
            val comentario = binding.etFeedbackObservacao.text.toString()

            val chipId = binding.cgCondicao.checkedChipId
            val condicaoId = when (chipId) {
                R.id.chipCondicaoBoa -> 1
                R.id.chipCondicaoRazoavel -> 2
                R.id.chipCondicaoRuim -> 3
                else -> 0
            }

            if (condicaoId == 0) {
                Toast.makeText(this, getString(R.string.erro_campos_obrigatorios), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.enviarFeedback(espacoAtual!!.id, condicaoId, comentario)
        }
    }

    private fun setupObservers() {
        viewModel.loading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnEnviarFeedback.isEnabled = !loading
        }

        viewModel.error.observe(this) { erro ->
            if (erro != null) {
                when (erro){
                    "Erro ao carregar feedbacks" ->  Toast.makeText(this, getString(R.string.toast_feedback_load_fail), Toast.LENGTH_SHORT).show()
                    "Erro ao enviar feedback" -> Toast.makeText(this, getString(R.string.toast_feedback_fail), Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(this, erro, Toast.LENGTH_SHORT).show()
                }

                viewModel.limparErro()
            }
        }

        viewModel.feedback.observe(this) { lista ->
            feedbackAdapter.atualizarLista(lista)

            val vazio = lista.isEmpty()
            binding.tvFeedbackVazio.visibility = if (vazio) View.VISIBLE else View.GONE
            binding.rvFeedback.visibility = if (vazio) View.GONE else View.VISIBLE
        }

        viewModel.feedbackEnviado.observe(this) { sucesso ->
            if (sucesso != null) {
                Toast.makeText(this, "Feedback enviado!", Toast.LENGTH_SHORT).show()
                binding.etFeedbackObservacao.text?.clear()
                binding.cgCondicao.clearCheck()
            }
        }
    }
    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }
}
