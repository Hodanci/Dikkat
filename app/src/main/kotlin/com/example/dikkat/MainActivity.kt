package com.example.dikkat

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class MainActivity : AppCompatActivity() {

    private lateinit var speedTextView: TextView
    private lateinit var speedLimitTextView: TextView
    private lateinit var roadTypeSpinner: Spinner
    private lateinit var statusImageView: ImageView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    
    private lateinit var soundAlertManager: SoundAlertManager
    private lateinit var locationManager: SpeedLocationManager
    private val scope = MainScope()
    
    private var currentSpeed = 0f
    private var isMonitoring = false
    private var selectedRoadCategory = SpeedLimitManager.RoadCategory.CITY

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        initializeManagers()
        requestPermissions()
        setupListeners()
    }

    private fun initializeViews() {
        speedTextView = findViewById(R.id.speedTextView)
        speedLimitTextView = findViewById(R.id.speedLimitTextView)
        roadTypeSpinner = findViewById(R.id.roadTypeSpinner)
        statusImageView = findViewById(R.id.statusImageView)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        
        // Spinner adapter setup
        val roadTypes = arrayOf(
            "Şehir İçi (50 km/h)",
            "Çift Yönlü Yol (80 km/h)",
            "Bölünmüş Yol (90 km/h)",
            "Paralı Otoyol (100 km/h)"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roadTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roadTypeSpinner.adapter = adapter
    }

    private fun initializeManagers() {
        soundAlertManager = SoundAlertManager(this)
        
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager = SpeedLocationManager(this, fusedLocationClient) { location ->
            handleLocationUpdate(location)
        }
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val needPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (needPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                needPermissions.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun setupListeners() {
        startButton.setOnClickListener {
            startMonitoring()
        }

        stopButton.setOnClickListener {
            stopMonitoring()
        }

        roadTypeSpinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                selectedRoadCategory = SpeedLimitManager.RoadCategory.values()[position]
                updateSpeedLimitDisplay()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })
    }

    private fun startMonitoring() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.startLocationUpdates()
            isMonitoring = true
            startButton.isEnabled = false
            stopButton.isEnabled = true
        }
    }

    private fun stopMonitoring() {
        locationManager.stopLocationUpdates()
        isMonitoring = false
        startButton.isEnabled = true
        stopButton.isEnabled = false
        speedTextView.text = "0 km/h"
    }

    private fun handleLocationUpdate(location: Location) {
        currentSpeed = location.speed * 3.6f // m/s to km/h

        updateSpeedDisplay()
        checkSpeedLimit()
    }

    private fun updateSpeedDisplay() {
        speedTextView.text = String.format("%.1f km/h", currentSpeed)
    }

    private fun updateSpeedLimitDisplay() {
        val limit = SpeedLimitManager.getCurrentSpeedLimit(selectedRoadCategory)
        speedLimitTextView.text = "Limit: $limit km/h"
    }

    private fun checkSpeedLimit() {
        val isExceeded = SpeedLimitManager.isSpeedExceeded(currentSpeed, selectedRoadCategory)
        val exceededPercentage = SpeedLimitManager.getExceededSpeedPercentage(currentSpeed, selectedRoadCategory)

        if (isExceeded) {
            soundAlertManager.playSpeedExceededAlert()
            statusImageView.setImageResource(android.R.drawable.ic_dialog_alert)
            statusImageView.setColorFilter(android.graphics.Color.RED)
            
            val speedLimit = SpeedLimitManager.getCurrentSpeedLimit(selectedRoadCategory)
            speedLimitTextView.text = "⚠️ UYARI! +${exceededPercentage}% hız yapıyorsunuz!\nLimit: $speedLimit km/h"
        } else if (currentSpeed > SpeedLimitManager.getCurrentSpeedLimit(selectedRoadCategory) * 0.9) {
            statusImageView.setImageResource(android.R.drawable.ic_dialog_info)
            statusImageView.setColorFilter(android.graphics.Color.YELLOW)
            
            val speedLimit = SpeedLimitManager.getCurrentSpeedLimit(selectedRoadCategory)
            speedLimitTextView.text = "Limit: $speedLimit km/h"
        } else {
            statusImageView.setImageResource(android.R.drawable.ic_dialog_info)
            statusImageView.setColorFilter(android.graphics.Color.GREEN)
            
            val speedLimit = SpeedLimitManager.getCurrentSpeedLimit(selectedRoadCategory)
            speedLimitTextView.text = "Limit: $speedLimit km/h"
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            // İzinler verildi
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.stopLocationUpdates()
        soundAlertManager.release()
        scope.cancel()
    }
}