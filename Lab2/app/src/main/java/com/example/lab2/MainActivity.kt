package com.example.lab2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationState = mutableStateOf<Location?>(null)
    private var addressState = mutableStateOf("No address yet")
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                getFreshLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        setContent {
            MaterialTheme {
                LocationDetails(
                    location = locationState.value,
                    address = addressState.value,
                    onGetLocation = { checkAndGetLocation() },
                    onSendSms = { openSms() },
                    onOpenMap = { openMap() }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkAndGetLocation()
    }
    private fun checkAndGetLocation() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {

                if (isLocationEnabled()) {
                    getFreshLocation()
                } else {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            }

            else -> {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private fun getFreshLocation() {

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            3000
        ).build()

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let {
                        locationState.value = it
                        addressState.value = getAddress(it)
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    private fun getAddress(location: Location): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses =
            geocoder.getFromLocation(location.latitude, location.longitude, 1)
        return addresses?.get(0)?.getAddressLine(0) ?: "Not found"
    }

    private fun openSms() {
        val number = "0123456789"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("sms:$number")
            putExtra("sms_body", "My current location:\n${addressState.value}")
        }
        startActivity(intent)
    }

    private fun openMap() {
        locationState.value?.let {
            val uri =
                Uri.parse("geo:${it.latitude},${it.longitude}?q=${it.latitude},${it.longitude}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }
}
@Composable
fun LocationDetails(
    location: Location?,
    address: String,
    onGetLocation: () -> Unit,
    onSendSms: () -> Unit,
    onOpenMap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Current Location")
        Spacer(modifier = Modifier.height(24.dp))
        if (location != null) {
            Text("Latitude: ${location.latitude}")
            Text("Longitude: ${location.longitude}")
            Spacer(modifier = Modifier.height(12.dp))
            Text("Address:")
            Text(address)
        } else {
            Text("Location not available")
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onGetLocation,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get Location")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onSendSms,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send SMS")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onOpenMap,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open Map")
        }
    }
}