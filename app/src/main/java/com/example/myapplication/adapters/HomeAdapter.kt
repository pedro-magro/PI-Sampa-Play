package com.example.myapplication.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.squareup.picasso.Picasso
import com.example.myapplication.activities.ItemDetalheActivity
import com.example.myapplication.data.Espaco
import com.example.myapplication.remote.ApiService


class HomeAdapter(private var dataset:List<Espaco>, private val apiService: ApiService, ):
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.nomeEspaco)
        val endereco: TextView = view.findViewById(R.id.enderecoEspaco)
        val imagem: ImageView = view.findViewById(R.id.imagemEspaco)

        val saibaMaisButton: Button = view.findViewById(R.id.btnVerDetalhes)

    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.home_view_holder, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        var espaco = dataset[position]
        viewHolder.nome.text = espaco.nome ?: "Nome Não Informado"
        viewHolder.endereco.text = espaco.endereco ?: "Endereço Não Informado"
        Picasso.get().load(espaco.imagemUrl).into(viewHolder.imagem)

        // Passar os dados do produto para a Activity de ediÃƒÂ§ÃƒÂ£o
        viewHolder.saibaMaisButton.setOnClickListener {
            val intent = Intent(it.context, ItemDetalheActivity::class.java)
            val espaco = dataset[position]
            intent.putExtra("ESPACO_OBJETO", espaco)
            it.context.startActivity(intent)
        }
    }
    override fun getItemCount() = dataset.size

    fun updateData(novaLista: List<Espaco>) {
        dataset = novaLista
        notifyDataSetChanged() // <-- Comando MÁGICO que força o RecyclerView a redesenhar
    }

}