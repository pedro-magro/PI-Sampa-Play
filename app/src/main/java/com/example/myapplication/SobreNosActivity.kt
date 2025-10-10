package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class SobreNosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sobre_nos)

        // Não há lógica de negócio complexa, apenas a exibição das informações.
        // O atributo android:autoLink="email" no XML já torna os links clicáveis.
    }
}
