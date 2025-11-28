package com.example.myapplication.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.HomeFeedAdapter
import com.example.myapplication.adapters.HomeFeedItem
import com.example.myapplication.data.EspacoResponse
import com.example.myapplication.databinding.ActivityHomeBinding
import com.example.myapplication.remote.RetrofitClient
import com.example.myapplication.remote.SessionManager
import com.example.myapplication.repositories.EspacoRepository
import com.example.myapplication.screenViewModels.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var feedAdapter: HomeFeedAdapter
    private lateinit var fusedLocationProvider: FusedLocationProviderClient

    private val REQUEST_LOCATION_PERMISSION = 1001

    private var listaZona: List<EspacoResponse> = emptyList()
    private var listaEmAlta: List<EspacoResponse> = emptyList()
    private var listaNovos: List<EspacoResponse> = emptyList()
    private var listaPerto: List<EspacoResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation(binding.bottomNavigation, R.id.nav_home)

        // ViewModel sem factory
        setupViewModel()

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)

        feedAdapter = HomeFeedAdapter(emptyList())
        binding.rvHomeFeed.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = feedAdapter
        }

        binding.incluirEspacoButton.setOnClickListener {
            startActivity(Intent(this, IncluirEspacoActivity::class.java))
        }

        setupObservers()
        pedirLocalizacao()
    }

    private fun setupViewModel() {
        val session = SessionManager(this)
        val token = session.getToken()
        val apiService = RetrofitClient.getInstance { token }
        val repository = EspacoRepository(apiService)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(repository, session) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }

    private fun pedirLocalizacao() {
        val fine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fine != PackageManager.PERMISSION_GRANTED && coarse != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSION
            )
            return
        }

        pegarLocalizacao()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pegarLocalizacao()
            } else {
                viewModel.carregarHome()
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun pegarLocalizacao() {
        fusedLocationProvider.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                viewModel.buscarProximos(location.latitude, location.longitude)
                viewModel.carregarHome()
            } else {
                viewModel.carregarHome()
            }
        }.addOnFailureListener {
            viewModel.carregarHome()
        }
    }

    private fun setupObservers() {

        viewModel.loading.observe(this) {
            //binding.progressBarHome.visibility = if (it) View.VISIBLE else View.GONE
            toggleLoading(it, binding.progressBarHome)
        }

        viewModel.error.observe(this) { erro ->
            if (erro != null) {
                Toast.makeText(this, erro, Toast.LENGTH_SHORT).show()
                viewModel.limparErro()
            }
        }

        viewModel.espacosZona.observe(this) {
            listaZona = it
            reconstruirFeed()
        }

        viewModel.espacosEmAlta.observe(this) {
            listaEmAlta = it
            reconstruirFeed()
        }

        viewModel.espacosRecentes.observe(this) {
            listaNovos = it
            reconstruirFeed()
        }

        viewModel.espacosProximos.observe(this) {
            listaPerto = it
            reconstruirFeed()
        }
    }

    private fun reconstruirFeed() {
        val feed = mutableListOf<HomeFeedItem>()

        // Perto de Você
        if (listaPerto.isNotEmpty()) {
            feed.add(HomeFeedItem.Titulo("Perto de Você"))
            feed.add(HomeFeedItem.CarroselHorizontal(listaPerto))
        }

        // Na sua Zona (sem nome)
        if (listaZona.isNotEmpty()) {
            feed.add(HomeFeedItem.Titulo("Na sua Zona"))
            feed.add(HomeFeedItem.CarroselHorizontal(listaZona))
        }

        // Melhores avaliados
        if (listaEmAlta.isNotEmpty()) {
            feed.add(HomeFeedItem.Titulo("Melhores Avaliados"))
            feed.add(HomeFeedItem.CarroselHorizontal(listaEmAlta))
        }

        // Novos
        if (listaNovos.isNotEmpty()) {
            feed.add(HomeFeedItem.Titulo("Novidades"))
            listaNovos.forEach { espaco ->
                feed.add(HomeFeedItem.EspacoVertical(espaco))
            }
        }

        feedAdapter.updateItems(feed)

        binding.tvVazio.visibility = if (feed.isEmpty()) View.VISIBLE else View.GONE
    }
}
