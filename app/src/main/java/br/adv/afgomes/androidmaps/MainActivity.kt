package br.adv.afgomes.androidmaps

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import android.location.LocationManager
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import java.lang.Override as JavaLangOverride

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val MAP_REQUEST_TICKET = 9999

    private lateinit var googleMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private val locationListener = LocationListener { localizacao ->
        // TODO: o usuário se moveu, o que vamos fazer agora?
        Log.d("MAPLOCATION", "Latitude: ${localizacao.latitude}  ; Longitude: ${localizacao.longitude}")
        onde = LatLng(localizacao.latitude, localizacao.longitude)
    }
    private var onde: LatLng = LatLng(-15.834963163926998, -47.91285006006427)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Recupera o serviço de localização do sistema
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Recupera a instância do mapa configurado na atividade
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        val view = mapFragment.view
        view?.isLongClickable = true

        // Solicita a apresentação do mapa em background
        mapFragment.getMapAsync(this)
    }

    private fun mostraPosicao(pino: Marker): Boolean {
        Toast.makeText(getApplicationContext(), pino.position.toString(), Toast.LENGTH_LONG).show()
        return true
    }

    override fun onMapReady(mapa: GoogleMap) {
        googleMap = mapa
        val posicaoIESB = LatLng(-15.834963163926998, -47.91285006006427)
        val pinoIESB = MarkerOptions()
            .position(posicaoIESB)
            .title("IESB Campus Sul")
        googleMap.addMarker(pinoIESB)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicaoIESB, 17f))

        googleMap.setOnMapClickListener { movePraPosicaoCorrente() }

        googleMap.setOnMapLongClickListener { ajustaMapa(it) }
        googleMap.setOnMarkerClickListener { pos ->
            mostraPosicao(pos)
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == MAP_REQUEST_TICKET) { // Verifica se a resposta é para a solicitação do mapa
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupLocation()
            } else {
                throwSnackBar("A permissão de localização é obrigatória!") {
                    checkPermission()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkPermission()
    }

    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(locationListener)
    }

    // Verifica se o usuário já concedeu a permissão para acesso à localização e,
    // em caso negativo, pede a permissão ao usuário.
    //
    // Caso contrário, configura o acesso à localização por parte do app.
    private fun checkPermission() {
        if( ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ),
                MAP_REQUEST_TICKET
            )
        } else {
            setupLocation()
        }
    }

    private fun movePraPosicaoCorrente() {
        googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        onde,
                        googleMap.cameraPosition.zoom
                )
        )
    }

    private fun ajustaMapa(onde: LatLng) {
        // Log.d("Verbose",onde.toString())
        val novoPino = MarkerOptions()
            .position(onde)
            .title("Marcador Manual")
        googleMap.addMarker(novoPino)
    }

    private fun setupLocation() {
        if( ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Configura a função para ser executada respeitando os critérios de
        // tempo (3000 ms = 3 s) entre as atualizações e
        // distância mínima de locomoção (1 metro)
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                3000,
                1.0f,
                locationListener)
    }

    private fun throwSnackBar(msg: String, callback: () -> Unit) {
        val rootView = findViewById<View>(android.R.id.content).rootView
        Snackbar
            .make(rootView, msg, Snackbar.LENGTH_LONG)
            .setAction("Aceito") {
                callback()
            }
            .show()
    }
}