package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import com.example.myapplication.Feedback
import retrofit2.converter.gson.GsonConverterFactory

class ItemDetalheActivity : BaseActivity() {


//inicializando antecipadamente as variaveis dos elementos graficos.
    private lateinit var imageImageView: ImageView
    private lateinit var nomeTextView: TextView
    private lateinit var enderecoCompletoTextView : TextView
    private lateinit var categoriaTextView : TextView
    private lateinit var whatsappButton: Button
    private lateinit var mapsButton: Button

    private lateinit var btnEnviarFeedback: Button
    private lateinit var rgCondicao: RadioGroup
    private lateinit var etFeedbackObservacao: EditText
    private lateinit var rvFeedback: RecyclerView
    private lateinit var feedbackAdapter: FeedbackAdapter

    private var espacoId: Int = 0
    private var usuarioId: Int = 1 // << MOCK ID: Pegar o ID do usuário da sessão/SharedPreferences
    private lateinit var apiService: ApiService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_item_detalhe)

        //atribuindo as variaveis os elementos gráficos.
        imageImageView = findViewById(R.id.imgDetalheEspaco)
        enderecoCompletoTextView = findViewById(R.id.tvDetalheEndereco)
        nomeTextView = findViewById(R.id.tvDetalheNome)
        categoriaTextView = findViewById(R.id.tvDetalheStatus)
        whatsappButton = findViewById(R.id.btnConvidarWhatsapp)
        mapsButton = findViewById(R.id.btnAbrirMaps)

        var intent : Intent = getIntent()
        var endereco = intent.getStringExtra("ESPACO_ENDERECO")
        var cep = intent.getStringExtra("ESPACO_CEP")
        var enderecoCompleto = endereco + ", " + cep
        var picUrl = intent.getStringExtra("ESPACO_IMG")
    //inicializando a retrofit
        var retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.56.1/meu_projeto_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        //setando os elementos gráficos.
        Picasso.get().load(picUrl).into(imageImageView)
        nomeTextView.setText(intent.getStringExtra("ESPACO_NOME"))
        categoriaTextView.setText("Categoria: " + intent.getStringExtra("ESPACO_CATEGORIA"))
        enderecoCompletoTextView.setText(enderecoCompleto)

        mapsButton.setOnClickListener {
            val address = enderecoCompletoTextView.text.toString()
            val mapUri = "google.navigation:q=$address"
            val mapintent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUri))
            mapintent.setPackage("com.google.android.apps.maps")
            startActivity(mapintent)
        }
        whatsappButton.setOnClickListener {
            val addres = enderecoCompletoTextView.text.toString()
            val textMessage = "Olha esse espaço: ${nomeTextView.text} no endereço: ${addres}. Baixe o Sampa Play!"
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.setPackage("com.whatsapp")
            intent.putExtra(Intent.EXTRA_TEXT, textMessage)
            startActivity(intent)
        }

        btnEnviarFeedback = findViewById(R.id.btnEnviarFeedback)
        rgCondicao = findViewById(R.id.rgCondicao)
        etFeedbackObservacao = findViewById(R.id.etFeedbackObservacao)
        rvFeedback = findViewById(R.id.rvFeedback)
        rvFeedback.layoutManager = LinearLayoutManager(this)

        // Pega o ID do espaço que abriu esta tela
        espacoId = intent.getIntExtra("ESPACO_ID", 0)

        // Carrega os feedbacks existentes
        if (espacoId != 0) {
            loadFeedbacks()
        }
        btnEnviarFeedback.setOnClickListener {
            // 1. Pega a Condição selecionada no RadioGroup
            val selectedRadioId = rgCondicao.checkedRadioButtonId
            if (selectedRadioId == -1) {
                Toast.makeText(this, "Por favor, selecione uma condição.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Mapeia o ID do RadioButton para o ID da Condição no B.D.
            val condicaoId = when (selectedRadioId) {
                R.id.rbCondicaoBoa -> 1 // (Assumindo que 1 = Boa)
                R.id.rbCondicaoRazoavel -> 2 // (Assumindo que 2 = Razoável)
                R.id.rbCondicaoRuim -> 3 // (Assumindo que 3 = Ruim)
                else -> 0
            }

            // 2. Pega a observação
            val observacao = etFeedbackObservacao.text.toString()

            // 3. Chama a API
            apiService.enviarFeedback(espacoId, usuarioId, condicaoId, observacao)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@ItemDetalheActivity,
                                "Feedback enviado com sucesso!",
                                Toast.LENGTH_SHORT
                            ).show()
                            // 4. Recarrega a lista de feedbacks
                            loadFeedbacks()
                            // Limpa os campos
                            etFeedbackObservacao.text.clear()
                            rgCondicao.clearCheck()
                        } else {
                            Toast.makeText(
                                this@ItemDetalheActivity,
                                "Erro ao enviar. Tente novamente.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(
                            this@ItemDetalheActivity,
                            "Falha de conexão: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        }

    }
    private fun loadFeedbacks() {
        // (Implemente a chamada apiService.getFeedbacks(espacoId) aqui)
        apiService.getFeedbacks(espacoId).enqueue(object : Callback<List<Feedback>> {
            override fun onResponse(call: Call<List<Feedback>>, response: Response<List<Feedback>>) {
                val feedbacks = response.body() ?: emptyList()
                feedbackAdapter = FeedbackAdapter(feedbacks)
                rvFeedback.adapter = feedbackAdapter
            }

            override fun onFailure(call: Call<List<Feedback>?>, t: Throwable) {
                Toast.makeText(this@ItemDetalheActivity, "Falha de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
        // ...
        // No onResponse:
        // val feedbacks = response.body() ?: emptyList()
        // feedbackAdapter = FeedbackAdapter(feedbacks)
        // rvFeedback.adapter = feedbackAdapter
    }
}