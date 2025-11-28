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

class EspacoHorizontalAdapter(private val espacos: List<EspacoResponse>): RecyclerView.Adapter<EspacoHorizontalAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val imagem: ImageView = view. findViewById(R.id.ivEspacoImagem)
        val nome: TextView = view.findViewById(R.id.tvEspacoNome)
        val local: TextView = view.findViewById(R.id.tvEspacoLocal)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_espaco_card_horizontal, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val espaco = espacos[position]
        holder.nome.text = espaco.nome
        holder.local.text = espaco.zonaNome ?: "São Paulo"
        if (!espaco.imagens.isNullOrEmpty()) {
            Picasso.get()
                .load(espaco.imagens[0])
                .placeholder(R.drawable.placeholder)
                .into(holder.imagem)
        } else {
            holder.imagem.setImageResource(R.drawable.placeholder)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ItemDetalheActivity::class.java)
            // Passa o objeto inteiro via Parcelable
            intent.putExtra("ESPACO_OBJETO", espaco)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return espacos.size

    }


}