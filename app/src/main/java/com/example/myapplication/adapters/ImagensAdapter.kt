package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.squareup.picasso.Picasso

class ImagensAdapter(
    private val imagens: MutableList<String>,
    private val onRemoveClick: (String) -> Unit // Callback recebe a URL
) : RecyclerView.Adapter<ImagensAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.ivThumb)
        val btnRemover: View = view.findViewById(R.id.btnRemover)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_imagem_removivel, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val url = imagens[position]

        Picasso.get()
            .load(url)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.imageView)

        holder.btnRemover.setOnClickListener {
            onRemoveClick(url)     // ← Agora envia a URL, não a posição
            removerImagem(url)     // ← Remove internamente baseado na URL
        }
    }

    override fun getItemCount() = imagens.size

    fun adicionarImagem(url: String) {
        imagens.add(url)
        notifyItemInserted(imagens.size - 1)
    }

    fun adicionarLista(urls: List<String>) {
        imagens.clear()
        imagens.addAll(urls)
        notifyDataSetChanged()
    }

    fun removerImagem(url: String) {
        val index = imagens.indexOf(url)
        if (index != -1) {
            imagens.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun getListaAtual(): List<String> = imagens.toList()
}
