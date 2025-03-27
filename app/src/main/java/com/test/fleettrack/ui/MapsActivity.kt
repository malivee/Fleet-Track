package com.test.fleettrack.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.test.fleettrack.R
import com.test.fleettrack.data.ServiceHandler
import com.test.fleettrack.data.VehicleStatusEntity
import com.test.fleettrack.data.ViewModelFactory
import com.test.fleettrack.databinding.ActivityMapsBinding
import kotlin.text.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var isTracking = false
    private var isEngineStarted = false
    private var isDoorClosed = false
    private var speedValue: Double = 0.0

    private val id = System.currentTimeMillis()

    private var lastLocation: Location? = null
    private var lastUpdateTime: Long = 0

    private val viewModel: MapsViewModel by viewModels { ViewModelFactory.getInstance(this) }


    private lateinit var workManager: WorkManager

    private var allLatLng = ArrayList<LatLng>()

    private var boundsBuilder = LatLngBounds.Builder()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }

        })

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        workManager = WorkManager.getInstance(this)

    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        setMapStyle()

        getMyCurrentLocation()

        createLocationRequest()


        binding.cardMap.btnStart.setOnClickListener {
            setUpWorkManager(id, isEngineStarted, isDoorClosed)
            if (!isTracking) {
                updateTrackingStatus(true)
                createLocationCallback()
                startLocationUpdate()
                binding.cardMap.btnBack.visibility = View.VISIBLE

            } else {
                updateTrackingStatus(false)
                stopLocationUpdate()
                binding.cardMap.btnBack.visibility = View.GONE

            }
        }



        binding.cardMap.btnToogleEngine.setOnClickListener {
            isEngineStarted = !isEngineStarted
            if (isEngineStarted) {
                binding.cardMap.tvEngineStatus.text = getString(R.string.on)
                binding.cardMap.tvEngineStatus.setTextColor(getColor(R.color.green))
            } else {
                binding.cardMap.tvEngineStatus.text = getString(R.string.off)
                binding.cardMap.tvEngineStatus.setTextColor(getColor(R.color.red))
            }
        }

        binding.cardMap.btnToogleDoor.setOnClickListener {
            isDoorClosed = !isDoorClosed
            if (isDoorClosed) {
                binding.cardMap.tvDoorStatus.text = getString(R.string.closed)
                binding.cardMap.tvDoorStatus.setTextColor(getColor(R.color.green))
            } else {
                binding.cardMap.tvDoorStatus.text = getString(R.string.open)
                binding.cardMap.tvDoorStatus.setTextColor(getColor(R.color.red))
            }
        }

        binding.cardMap.btnBack.setOnClickListener {
            viewModel.insertStatus(
                VehicleStatusEntity(
                    id = id,
                    engineStatus = isEngineStarted,
                    doorStatus = isDoorClosed,
                    speed = speedValue
                )
            )
            val intent = Intent(this@MapsActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setUpWorkManager(id: Long, isEngineStart: Boolean, isDoorClose: Boolean) {
        val data = Data.Builder()
            .putLong(ServiceHandler.STATUS_ID, id)
            .putBoolean(ServiceHandler.STATUS_DOOR, isDoorClose)
            .putBoolean(ServiceHandler.STATUS_ENGINE, isEngineStart)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(ServiceHandler::class.java)
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        workManager.enqueue(oneTimeWorkRequest)

    }

    private fun stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        binding.cardMap.tvSpeedStatus.text = "0"
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = java.util.concurrent.TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = java.util.concurrent.TimeUnit.SECONDS.toMillis(1)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                    if (it != null) {
                        markMyLocation(it)
                    } else {
                        Toast.makeText(
                            this@MapsActivity,
                            "Location is not found, try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun startLocationUpdate() {
        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "Error: " + e.message)
        }
    }

    private fun updateTrackingStatus(newStatus: Boolean) {
        isTracking = newStatus
        if (isTracking) {
            binding.cardMap.btnStart.text = getString(R.string.stop_moving)
        } else {
            binding.cardMap.btnStart.text = getString(R.string.start_moving)
        }
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    Log.d(TAG, "onLocationResult: " + location.latitude + "," + location.longitude)
                    val lastLatLng = LatLng(location.latitude, location.longitude)

                    allLatLng.add(lastLatLng)
                    mMap.addPolyline(
                        PolylineOptions()
                            .color(Color.CYAN)
                            .width(10F)
                            .addAll(allLatLng)
                    )

                    boundsBuilder.include(lastLatLng)
                    val bounds: LatLngBounds = boundsBuilder.build()
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64))


                    if (lastLocation != null) {
                        val timeDiff = (System.currentTimeMillis() - lastUpdateTime) / 1000.0
                        val distance = lastLocation!!.distanceTo(location)
                        val speed = if (timeDiff > 0) distance / timeDiff else 0.0

                        val speedKmph = speed * 3.6

                        speedValue = speedKmph

                        runOnUiThread {
                            binding.cardMap.tvSpeedStatus.text =
                                String.format("%.2f kmph", speedKmph)
                        }
                    }
                    lastLocation = location
                    lastUpdateTime = System.currentTimeMillis()
                }

            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun getMyCurrentLocation() {
        mMap.isMyLocationEnabled = true

        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                mMap.setOnMyLocationButtonClickListener {
                    markMyLocation(it)
                    true
                }
                mMap.setOnMyLocationClickListener {
                    markMyLocation(it)
                }
                markMyLocation(it)
            } else {
                Toast.makeText(this, "Cannot Access Location, please try again", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    }

    private fun markMyLocation(loc: Location) {
        val location = LatLng(loc.latitude, loc.longitude)

        mMap.addMarker(
            MarkerOptions().apply {
                position(location)
                title("Fleet")
            }
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLng(location))

    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this@MapsActivity,
                    R.raw.map_style
                )
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, e.message.toString())
        }
    }


    companion object {
        private const val TAG = "MapsActivity"
    }
}