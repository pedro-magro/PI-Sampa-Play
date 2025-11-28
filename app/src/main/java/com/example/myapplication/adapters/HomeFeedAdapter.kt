package com.example.myapplication.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import com.example.myapplication.activities.ItemDetalheActivity


class HomeFeedAdapter(private var items: List<HomeFeedItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_TITULO = 1
        const val VIEW_TYPE_CARROSSEL = 2
        const val VIEW_TYPE_VERTICAL = 3
    }

    fun updateItems(newItems: List<HomeFeedItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HomeFeedItem.Titulo -> VIEW_TYPE_TITULO
            is HomeFeedItem.CarroselHorizontal -> VIEW_TYPE_CARROSSEL
            is HomeFeedItem.EspacoVertical -> VIEW_TYPE_VERTICAL
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType){
            VIEW_TYPE_TITULO ->{
                val view = inflater.inflate(R.layout.item_home_section_header, parent, false)
                TituloViewHolder(view)
            }
            VIEW_TYPE_CARROSSEL -> {
                val view = inflater.inflate(R.layout.item_home_section_carousel, parent, false)
                CarroselHorizonatalViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_espaco_card_vertical, parent, false)
                EspacoVerticalViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int){
        when(val item = items[position]) {
            is HomeFeedItem.Titulo -> (holder as TituloViewHolder).bind(item)
            is HomeFeedItem.CarroselHorizontal -> (holder as CarroselHorizonatalViewHolder).bind(item)
            is HomeFeedItem.EspacoVertical -> (holder as EspacoVerticalViewHolder).bind(item)
        }
    }

    override fun getItemCount() = items.size

    class TituloViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val tvTitulo: TextView = view.findViewById(R.id.tvTituloSecao)
        fun bind(item: HomeFeedItem.Titulo){
            tvTitulo.text = item.texto
        }
    }


    class CarroselHorizonatalViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val rvHorizontal : RecyclerView = view.findViewById(R.id.rvSecaoHorizontal)

        fun bind(item: HomeFeedItem.CarroselHorizontal){
            rvHorizontal.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            rvHorizontal.adapter = EspacoHorizontalAdapter(item.espacos)

        }
    }

    class EspacoVerticalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imagem: ImageView = view.findViewById(R.id.ivEspacoImagem)
        private val nome: TextView = view.findViewById(R.id.tvEspacoNome)
        private val local: TextView = view.findViewById(R.id.tvEspacoLocal)
        private val condicao: TextView = view.findViewById(R.id.tvEspacoCondicao)

        fun bind(item: HomeFeedItem.EspacoVertical) {
            val espaco = item.espaco
            nome.text = espaco.nome
            local.text = espaco.zonaNome ?: "SP"
            condicao.text = "Condição: ${espaco.condicaoGeralNome ?: "N/A"}"

            if (!espaco.imagens.isNullOrEmpty()) {
               Picasso.get().load(espaco.imagens[0]).into(imagem)
            } else{
                imagem.setImageResource(R.drawable.placeholder)
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ItemDetalheActivity::class.java)
                intent.putExtra("ESPACO_OBJETO", espaco)
                itemView.context.startActivity(intent)
            }
        }
    }

}