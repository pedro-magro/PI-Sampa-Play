package com.example.myapplication.adapters

import com.example.myapplication.data.EspacoResponse

sealed class HomeFeedItem{
    data class Titulo(val texto: String ): HomeFeedItem()

    data class CarroselHorizontal(val espacos: List<EspacoResponse>): HomeFeedItem()

    data class EspacoVertical(val espaco: EspacoResponse): HomeFeedItem()
}