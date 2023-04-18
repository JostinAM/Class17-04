package cr.ac.menufragment

import android.Manifest
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import cr.ac.menufragment.dao.UbicacionDao
import cr.ac.menufragment.db.AppDatabase
import cr.ac.menufragment.entity.Ubicacion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date


class MapsFragment : Fragment() {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    ///
    private lateinit var locationRequest: LocationRequest

    ///
    private lateinit var ubicacionDao: UbicacionDao



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        val floating = view.findViewById<FloatingActionButton>(R.id.floatingActionButton2)

        floating.setOnClickListener {
            getLocation()

//            val entity = Ubicacion(
//                id = null,
//                latitud = map.cameraPosition.target.latitude,
//                longitud = map.cameraPosition.target.longitude,
//                fecha = Date()
//            )
//
//            insertEntity(entity)

        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync{
                googleMap ->
                map = googleMap
                getLocation()
        }



    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        ubicacionDao = AppDatabase.getInstance(this).ubicacionDao()



    }
    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        } else {
            fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    map.addMarker(MarkerOptions().position(currentLatLng).title(Config.myProperty))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    fusedLocationClient.removeLocationUpdates(this)

                    val entity = Ubicacion(
                        id = null,
                        latitud = location.latitude,
                        longitud = location.longitude,
                        fecha = Date()
                    )

                    insertEntity(entity)

                }
            }, Looper.getMainLooper())
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    getLocation()
                }
            } else {
                // Permiso denegado, maneja la situación de acuerdo a tus necesidades
            }
        }
    }

    private fun insertEntity(entity: Ubicacion) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                ubicacionDao.insert(entity)
            }
        }

    }

}