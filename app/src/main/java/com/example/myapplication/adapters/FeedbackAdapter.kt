package com.example.myapplication.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Feedback

class FeedbackAdapter(private val dataset: List<Feedback>) :
    RecyclerView.Adapter<FeedbackAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Mapeia os IDs do item_feedback_card.xml
        val tvStatus: TextView = view.findViewById(R.id.tvFeedbackStatus)
        val ivIcon: ImageView = view.findViewById(R.id.ivFeedbackIcon)
        val tvObservacao: TextView = view.findViewById(R.id.tvFeedbackObservacao)
        val tvData: TextView = view.findViewById(R.id.tvFeedbackDate)
        val ivIconData: ImageView = view.findViewById(R.id.ivFeedbackDateIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feedback_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var feedback = dataset[position]

        val context = holder.itemView.context

        // 1. Preenche os dados de texto
        holder.tvStatus.text = feedback.condicaoNome ?: "N:A"
        holder.tvObservacao.text = feedback.observacao ?: "" // Usa string vazia se a observação for nula
        holder.tvData.text = feedback.dataEnvio

        // 2. Lógica para definir o ícone e a cor
        when (feedback.condicaoId) {
            1 -> {
                holder.ivIcon.setImageResource(R.drawable.ic_check_24) // Ícone de Check
                // Tenta usar a cor da API, senão usa um verde padrão
                val color = try { Color.parseColor(feedback.condicaoCor) } catch (e: Exception) { ContextCompat.getColor(context, R.color.feedback_bom) }
                holder.ivIcon.setColorFilter(color)
            }
            2 -> {
                holder.ivIcon.setImageResource(R.drawable.ic_feedback_ruim_24dp) // Ícone de Aviso
                val color = try { Color.parseColor(feedback.condicaoCor) } catch (e: Exception) { ContextCompat.getColor(context, R.color.feedback_razoavel) }
                holder.ivIcon.setColorFilter(color)
            }
            3 -> {
                holder.ivIcon.setImageResource(R.drawable.ic_error_24dp) // Ícone de Erro/Cancel
                val color = try { Color.parseColor(feedback.condicaoCor) } catch (e: Exception) { ContextCompat.getColor(context, R.color.feedback_ruim) }
                holder.ivIcon.setColorFilter(color)
            }
            else -> {
                holder.ivIcon.setImageResource(R.drawable.ic_send_24) // Ícone padrão
                holder.ivIcon.setColorFilter(Color.GRAY)
            }
        }

        // Garante que o ícone de data (calendário) esteja visível
        holder.ivIconData.setImageResource(R.drawable.ic_calendar_black_24dp)
    }

    override fun getItemCount() = dataset.size
}