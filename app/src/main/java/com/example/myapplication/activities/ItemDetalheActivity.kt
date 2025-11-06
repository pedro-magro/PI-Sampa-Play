package com.example.myapplication.activities

import FeedbackResponse
import android.content.Intent
import android.graphics.Color // Importe para a cor
import android.net.Uri
import android.os.Build // Importe para a verificação de versão
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat // Importe para a cor
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.remote.ApiService
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import com.example.myapplication.data.Feedback
import com.example.myapplication.adapters.FeedbackAdapter
import com.example.myapplication.R
import com.example.myapplication.data.Espaco
import retrofit2.converter.gson.GsonConverterFactory

class ItemDetalheActivity : AppCompatActivity() {

    // --- 1. Declarações de Vistas ---
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var imageImageView: ImageView
    private lateinit var nomeTextView: TextView
    private lateinit var enderecoCompletoTextView: TextView
    private lateinit var categoriaTextView: TextView
    private lateinit var tvCondicaoGeral: TextView // <-- A NOVA VISTA
    private lateinit var tvFeedbackVazio: TextView
    private lateinit var whatsappButton: Button
    private lateinit var mapsButton: Button
    private lateinit var btnEnviarFeedback: Button
    private lateinit var rgCondicao: RadioGroup
    private lateinit var etFeedbackObservacao: EditText
    private lateinit var rvFeedback: RecyclerView
    private lateinit var feedbackAdapter: FeedbackAdapter

    // --- 2. Variáveis de Estado ---
    private lateinit var espacoAtual: Espaco // Objeto completo
    private var espacoId: Int = 0
    private var usuarioId: Int = 1 // << MOCK ID (Lembre-se de pegar isto do SessionManager)
    private lateinit var apiService: ApiService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_item_detalhe)

        // --- 3. Inicializar a API (Seu código está ótimo) ---
        var retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.56.1/meu_projeto_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        // --- 4. Encontrar TODAS as Vistas (findViewById) ---
        toolbar = findViewById(R.id.toolbarDetalhe)
        imageImageView = findViewById(R.id.imgDetalheEspaco)
        enderecoCompletoTextView = findViewById(R.id.tvDetalheEndereco)
        nomeTextView = findViewById(R.id.tvDetalheNome)
        categoriaTextView = findViewById(R.id.tvDetalheStatus)
        whatsappButton = findViewById(R.id.btnConvidarWhatsapp)
        mapsButton = findViewById(R.id.btnAbrirMaps)
        tvFeedbackVazio = findViewById(R.id.tvFeedbackVazio)
        tvCondicaoGeral = findViewById(R.id.tvCondicaoGeral) // <-- ENCONTRE A NOVA VISTA
        btnEnviarFeedback = findViewById(R.id.btnEnviarFeedback)
        rgCondicao = findViewById(R.id.rgCondicao)
        etFeedbackObservacao = findViewById(R.id.etFeedbackObservacao)
        rvFeedback = findViewById(R.id.rvFeedback)
        rvFeedback.layoutManager = LinearLayoutManager(this)

        // --- 5. Configurar a Toolbar (Seu código está ótimo) ---
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // --- 6. LIMPEZA: Receber o Objeto Espaco (O NOVO MÉTODO) ---
        // (Este bloco substitui todo o seu `intent.getStringExtra`)

        // Lógica segura para pegar o Parcelable
        espacoAtual = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("ESPACO_OBJETO", Espaco::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Espaco>("ESPACO_OBJETO")
        })!!

        // Verificação de segurança
        if (espacoAtual == null) {
            Toast.makeText(this, "Erro fatal: Não foi possível carregar o espaço.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Guarde o ID para as chamadas de API (como getFeedbacks)
        espacoId = espacoAtual.id

        // --- 7. LIMPEZA: Preencher a UI (Usando o Objeto 'espacoAtual') ---
        // (Este bloco substitui seu antigo `setText(intent.getStringExtra(...))`)

        Picasso.get().load(espacoAtual.imagemUrl).into(imageImageView)
        nomeTextView.text = espacoAtual.nome
        categoriaTextView.text = "Categoria: ${espacoAtual.categoriaNome}"
        enderecoCompletoTextView.text = "${espacoAtual.endereco}, ${espacoAtual.cep}"

        // --- 8. A NOVA LÓGICA: Exibir a Condição Geral ---
        // (Esta função lê o `espacoAtual.condicaoGeralNome` que veio da API)
        atualizarStatusGeralUI(espacoAtual)

        // --- 9. Carregar Feedbacks (Seu código está ótimo) ---
        loadFeedbacks()

        // --- 10. Configurar Listeners (Seu código está ótimo) ---
        configurarListeners()
    }

    /**
     * Nova função simples que apenas LÊ os dados.
     * Ela não calcula nada, pois a API já fez o trabalho.
     */
    private fun atualizarStatusGeralUI(espaco: Espaco) {
        if (espaco.condicaoGeralId == null || espaco.condicaoGeralNome == null) {
            // Caso o espaço ainda não tenha feedbacks (condicaoGeralId é NULL)
            tvCondicaoGeral.text = "Condição: Nenhuma informação"
            tvCondicaoGeral.setTextColor(Color.GRAY)
            return
        }

        // 1. Define o texto (ex: "Condição: Ótima")
        // (O 'condicaoGeralNome' veio pronto do PHP)
        tvCondicaoGeral.text = "Condição: ${espaco.condicaoGeralNome}"

        // 2. Define a cor (baseado no ID)
        when (espaco.condicaoGeralId) {
            1 -> tvCondicaoGeral.setTextColor(ContextCompat.getColor(this, R.color.feedback_bom))
            2 -> tvCondicaoGeral.setTextColor(ContextCompat.getColor(this, R.color.feedback_razoavel))
            3 -> tvCondicaoGeral.setTextColor(ContextCompat.getColor(this, R.color.feedback_ruim))
            else -> tvCondicaoGeral.setTextColor(Color.GRAY)
        }
    }

    /**
     * Função auxiliar para organizar o onCreate
     */
    private fun configurarListeners() {
        mapsButton.setOnClickListener {
            val address = enderecoCompletoTextView.text.toString()
            val mapUri = "google.navigation:q=$address"
            val mapintent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUri))
            mapintent.setPackage("com.google.android.apps.maps")
            if (mapintent.resolveActivity(packageManager) != null) {
                startActivity(mapintent)
            } else {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/maps?q=$address"))
                startActivity(webIntent)
            }
        }

        whatsappButton.setOnClickListener {
            val addres = enderecoCompletoTextView.text.toString()
            val textMessage = "Olha esse espaço: ${nomeTextView.text} no endereço: ${addres}. Baixe o Sampa Play!"
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.setPackage("com.whatsapp")
            intent.putExtra(Intent.EXTRA_TEXT, textMessage)

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "WhatsApp não instalado.", Toast.LENGTH_SHORT).show()
            }
        }

        btnEnviarFeedback.setOnClickListener {
            val selectedRadioId = rgCondicao.checkedRadioButtonId
            if (selectedRadioId == -1) {
                Toast.makeText(this, "Por favor, selecione uma condição.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val condicaoId = when (selectedRadioId) {
                R.id.rbCondicaoBoa -> 1
                R.id.rbCondicaoRazoavel -> 2
                R.id.rbCondicaoRuim -> 3
                else -> 0
            }

            val observacao = etFeedbackObservacao.text.toString()

            // A API 'enviarFeedback' agora atualiza a condição geral no servidor!
            apiService.enviarFeedback(espacoId, usuarioId, condicaoId, observacao)
                .enqueue(object : Callback<FeedbackResponse> {
                    override fun onResponse(call: Call<FeedbackResponse>, response: Response<FeedbackResponse>) {
                        if (response.isSuccessful) {
                            val feedbackResponse = response.body()!!
                            Toast.makeText(this@ItemDetalheActivity, "Feedback enviado!", Toast.LENGTH_SHORT).show()
                            loadFeedbacks() // Recarrega a lista

                            espacoAtual.condicaoGeralId = feedbackResponse.novaCondicaoId
                            espacoAtual.condicaoGeralNome = feedbackResponse.novaCondicaoNome

                            // 4. ATUALIZE A UI IMEDIATAMENTE
                            // Chame a função que exibe a condição, agora com os dados novos
                            atualizarStatusGeralUI(espacoAtual)

                            // 5. Limpa os campos do formulário
                            etFeedbackObservacao.text.clear()
                            rgCondicao.clearCheck()


                        } else {
                            Toast.makeText(this@ItemDetalheActivity, "Erro ao enviar.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<FeedbackResponse>, t: Throwable) {
                        Toast.makeText(this@ItemDetalheActivity, "Falha: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    /**
     * Função que carrega os feedbacks
     * (Seu código aqui está ótimo e não precisa mudar)
     */
    private fun loadFeedbacks() {
        apiService.getFeedbacks(espacoId).enqueue(object : Callback<List<Feedback>> {
            override fun onResponse(call: Call<List<Feedback>>, response: Response<List<Feedback>>) {
                if (response.isSuccessful) {
                    val feedbacks = response.body() ?: emptyList()

                    if(feedbacks.isEmpty()){
                        tvFeedbackVazio.visibility = View.VISIBLE
                        rvFeedback.visibility = View.GONE
                    }else{
                        tvFeedbackVazio.visibility = View.GONE
                        rvFeedback.visibility = View.VISIBLE
                        feedbackAdapter = FeedbackAdapter(feedbacks)
                        rvFeedback.adapter = feedbackAdapter
                    }

                } else {
                    Toast.makeText(this@ItemDetalheActivity, "Erro ao carregar lista (${response.code()})", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Feedback>>, t: Throwable) {
                Toast.makeText(this@ItemDetalheActivity, "Falha de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}