package br.adv.afgomes.androidmaps

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
import java.lang.Override as JavaLangOverride

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        googleMap.setOnMapLongClickListener { ajustaMapa(it) }
        googleMap.setOnMarkerClickListener {
            mostraPosicao(it)
        }
    }

    private fun ajustaMapa(onde: LatLng) {
        // Log.d("Verbose",onde.toString())
        val novoPino = MarkerOptions()
            .position(onde)
            .title("Marcador Manual")
        googleMap.addMarker(novoPino)
    }
}