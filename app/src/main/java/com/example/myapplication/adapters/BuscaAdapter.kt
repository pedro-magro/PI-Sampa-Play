package com.example.myapplication.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.ItemDetalheActivity
import com.example.myapplication.data.EspacoResponse
import com.squareup.picasso.Picasso

class  BuscaAdapter(private var espacos: List<EspacoResponse> = emptyList()) :
    RecyclerView.Adapter<BuscaAdapter.ViewHolder>() {

    fun submitList(newList: List<EspacoResponse>) {
        espacos = newList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // IDs do seu layout horizontal
        val imagem: ImageView = view.findViewById(R.id.ivEspacoImagem)
        val nome: TextView = view.findViewById(R.id.tvEspacoNome)
        val local: TextView = view.findViewById(R.id.tvEspacoLocal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Reutiliza o card horizontal para a lista de busca
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_espaco_card_vertical, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val espaco = espacos[position]

        holder.nome.text = espaco.nome
        holder.local.text = espaco.zonaNome ?: "SP"

        // Carrega a imagem (Capa)
        if (!espaco.imagens.isNullOrEmpty()) {
            Picasso.get().load(espaco.imagens[0]).into(holder.imagem)
        } else {
            // Se a lista de imagens não for nula e tiver itens, pega a primeira
            // Caso contrário, placeholder
            holder.imagem.setImageResource(R.drawable.placeholder)
        }

        // Clique vai para Detalhes
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ItemDetalheActivity::class.java)
            intent.putExtra("ESPACO_OBJETO", espaco)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = espacos.size
}