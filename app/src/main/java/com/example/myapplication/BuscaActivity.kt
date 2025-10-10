package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BuscaActivity : AppCompatActivity() {

    private lateinit var spinnerTipo: Spinner
    private lateinit var btnAplicarBusca: Button
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_busca)

        // Inicialização dos componentes obrigatórios
        spinnerTipo = findViewById(R.id.spinnerTipo)
        btnAplicarBusca = findViewById(R.id.btnAplicarBusca)
        recyclerView = findViewById(R.id.rvResultadosBusca)

        // Configuração do Spinner (Componente Obrigatório)
        // O array de strings deve estar definido em res/values/strings.xml
        val tiposQuadra = resources.getStringArray(R.array.tipos_esporte_array)
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposQuadra)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipo.adapter = adapterSpinner

        // Configuração do RecyclerView (Requisito de Comprovação)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Simulação de dados (Para fins de MVP/Screenshot)
        val resultadosMock = listOf("Quadra A", "Quadra B", "Quadra C") // Usar sua classe Lugar aqui
        // Exemplo: recyclerView.adapter = ResultadosAdapter(resultadosMock)

        // Lógica para o botão de busca
        btnAplicarBusca.setOnClickListener {
            val tipoSelecionado = spinnerTipo.selectedItem.toString()
            Toast.makeText(this, "Buscando por: $tipoSelecionado...", Toast.LENGTH_SHORT).show()
            // Aqui entraria a lógica para chamar a API com os filtros selecionados
        }
    }
}