package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.EspacoResponse
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso

class AdminAdapter(
    private var espacos: List<EspacoResponse> = emptyList(),
    private val onEditClick: (EspacoResponse) -> Unit,
    private val onDeleteClick: (EspacoResponse) -> Unit
) : RecyclerView.Adapter<AdminAdapter.ViewHolder>() {

    fun submitList(newList: List<EspacoResponse>) {
        espacos = newList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagem: ImageView = view.findViewById(R.id.ivEspacoImagem)
        val nome: TextView = view.findViewById(R.id.tvEspacoNome)
        val local: TextView = view.findViewById(R.id.tvEspacoLocal)
        val status: TextView = view.findViewById(R.id.tvEspacoStatus)
        val btnEditar: MaterialButton = view.findViewById(R.id.btnEditar)
        val btnDeletar: MaterialButton = view.findViewById(R.id.btnDeletar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_espaco_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val espaco = espacos[position]
        val context = holder.itemView.context

        holder.nome.text = espaco.nome
        holder.local.text = espaco.zonaNome ?: "SP"

        // Lógica de Status (Cor)
        if (espaco.aprovado) {
            holder.status.text = context.getString(R.string.admin_card_status_approved)
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.feedback_bom)) // Verde
        } else {
            holder.status.text = context.getString(R.string.admin_card_status_pending)
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.feedback_razoavel)) // Amarelo
        }

        // Imagem
        if (!espaco.imagens.isNullOrEmpty()) {
            Picasso.get().load(espaco.imagens[0]).into(holder.imagem)
        } else {
            holder.imagem.setImageResource(R.drawable.placeholder)
        }

        // Listeners
        holder.btnEditar.setOnClickListener { onEditClick(espaco) }
        holder.btnDeletar.setOnClickListener { onDeleteClick(espaco) }
    }

    override fun getItemCount() = espacos.size
}