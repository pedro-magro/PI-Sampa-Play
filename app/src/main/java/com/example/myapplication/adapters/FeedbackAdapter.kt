package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.FeedbackResponse

class FeedbackAdapter(private var lista: List<FeedbackResponse>) :
    RecyclerView.Adapter<FeedbackAdapter.ViewHolder>() {

    fun atualizarLista(novaLista: List<FeedbackResponse>) {
        lista = novaLista
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icone: ImageView = view.findViewById(R.id.ivFeedbackIcon)
        val status: TextView = view.findViewById(R.id.tvFeedbackStatus)
        val observacao: TextView = view.findViewById(R.id.tvFeedbackObservacao)
        val data: TextView = view.findViewById(R.id.tvFeedbackDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feedback_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feedback = lista[position]
        val context = holder.itemView.context

        // 1. Preenche os textos
        holder.status.text = feedback.condicaoNome
        holder.observacao.text = feedback.observacao ?: ""
        holder.data.text = feedback.dataEnvio // (Já vem formatada do Backend)

        // 2. Define a cor e o ícone baseado no ID da Condição
        when (feedback.condicaoId) {
            1 -> { // Boa
                holder.icone.setImageResource(R.drawable.ic_check_24)
                holder.icone.setColorFilter(ContextCompat.getColor(context, R.color.feedback_bom))
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.feedback_bom))
            }
            2 -> { // Razoável
                holder.icone.setImageResource(R.drawable.ic_feedback_ruim_24dp) // (use seu nome de arquivo)
                holder.icone.setColorFilter(ContextCompat.getColor(context, R.color.feedback_razoavel))
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.feedback_razoavel))
            }
            3 -> { // Ruim
                holder.icone.setImageResource(R.drawable.ic_error_24dp)
                holder.icone.setColorFilter(ContextCompat.getColor(context, R.color.feedback_ruim))
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.feedback_ruim))
            }
            else -> {
                holder.icone.setImageResource(R.drawable.ic_feedback_ruim_24dp) // Default
            }
        }
    }

    override fun getItemCount() = lista.size
}