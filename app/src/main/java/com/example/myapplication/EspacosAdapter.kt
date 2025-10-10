package com.example.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast
import com.example.myapplication.ApiService


class EspacosAdapter(private val dataset:List<Espaco>, private val apiService: ApiService, ):
    RecyclerView.Adapter<EspacosAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.nomeEspaco)
        val endereco: TextView = view.findViewById(R.id.enderecoEspaco)
        val cep: TextView = view.findViewById(R.id.cepEspaco)
        val imagem: ImageView = view.findViewById(R.id.imagemEspaco)

        val editarButton: Button = view.findViewById(R.id.editarButton)
        val deletarButton: Button = view.findViewById(R.id.deletarButton)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.espacos_view_holder, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val espaco = dataset[position]
        viewHolder.nome.text = espaco.nome ?: "Nome Não Informado"
        viewHolder.endereco.text = espaco.endereco ?: "Endereço Não Informado"
        viewHolder.cep.text = espaco.cep ?: ""
        Picasso.get().load(espaco.imagemUrl).into(viewHolder.imagem)

        // Passar os dados do produto para a Activity de ediÃƒÂ§ÃƒÂ£o
        viewHolder.editarButton.setOnClickListener {
            val intent = Intent(it.context, EditarEspacoActivity::class.java)
            intent.putExtra("ESPACO_ID", espaco.id)
            intent.putExtra("ESPACO_NOME", espaco.nome)
            intent.putExtra("ESPACO_ENDERECO", espaco.endereco)
            intent.putExtra("ESPACO_CEP", espaco.cep)
            intent.putExtra("ESPACO_IMG", espaco.imagemUrl)
            intent.putExtra("ESPACO_CATEGORIA", espaco.categoriaNome)
            intent.putExtra("ESPACO_DATA", espaco.ESPACO_DATA)
            it.context.startActivity(intent)
        }

            // Deletar produto ao clicar no botÃƒÂ£o
        viewHolder.deletarButton.setOnClickListener {
            apiService.deletarEspaco(espaco.id).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Toast.makeText(
                        it.context,
                        "Espaco deletado com sucesso!",
                        Toast.LENGTH_LONG
                    ).show()
                    }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(it.context, "Erro ao deletar o espaco", Toast.LENGTH_LONG)
                        .show()
                }
            })

        }



    }

    override fun getItemCount() = dataset.size




}



