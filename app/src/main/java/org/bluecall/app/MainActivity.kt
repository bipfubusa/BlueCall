package org.bluecall.app

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var statusText: TextView
    private lateinit var devicesText: TextView
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private var receiverRegistered = false
    private val permissionRequestCode = 100
    private val enableBluetoothRequestCode = 101

    private val discoveredDevices = linkedSetOf<String>()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE,
                                BluetoothDevice::class.java
                            )
                        } else {
                            @Suppress("DEPRECATION")
                            intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE
                            )
                        }

                    if (device != null) {
                        val name = try {
                            device.name ?: "Unknown device"
                        } catch (_: SecurityException) {
                            "Name unavailable"
                        }

                        val address = try {
                            device.address
                        } catch (_: SecurityException) {
                            "Address unavailable"
                        }

                        runOnUiThread {
                            discoveredDevices.add("$name\n$address")
                            devicesText.text =
                                discoveredDevices.joinToString("\n\n")
                            statusText.text = "Discovery running"
                        }
                    }
                }

                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    statusText.text = "Discovery finished"
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val manager = getSystemService(Context.BLUETOOTH_SERVICE)
                as BluetoothManager
        bluetoothAdapter = manager.adapter

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(Color.WHITE)
        }

        val title = TextView(this).apply {
            text = "BlueCall"
            textSize = 30f
            setTextColor(Color.BLUE)
            gravity = Gravity.CENTER
        }

        val description = TextView(this).apply {
            text = "Offline Bluetooth Voice Calling"
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 16)
        }

        statusText = TextView(this).apply {
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 16)
        }

        val scanButton = Button(this).apply {
            text = "Discover Devices"
            setOnClickListener {
                checkPermissionsAndDiscover()
            }
        }

        val stopButton = Button(this).apply {
            text = "Stop Discovery"
            setOnClickListener {
                stopDiscovery()
            }
        }

        val devicesTitle = TextView(this).apply {
            text = "Nearby devices"
            textSize = 20f
            setTextColor(Color.BLACK)
            setPadding(0, 16, 0, 8)
        }

        devicesText = TextView(this).apply {
            text = "No devices discovered yet."
            textSize = 16f
            setTextColor(Color.DKGRAY)
            setPadding(8, 8, 8, 8)
        }

        val scrollView = ScrollView(this)
        scrollView.addView(devicesText)

        layout.addView(title)
        layout.addView(description)
        layout.addView(statusText)
        layout.addView(scanButton)
        layout.addView(stopButton)
        layout.addView(devicesTitle)
        layout.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(layout)

        if (bluetoothAdapter == null) {
            statusText.text = "Bluetooth is not supported on this device."
        } else {
            statusText.text = "Ready to discover devices"
        }
    }

    private fun checkPermissionsAndDiscover() {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        val missing = permissions.filter {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            requestPermissions(
                missing.toTypedArray(),
                permissionRequestCode
            )
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            startActivityForResult(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                enableBluetoothRequestCode
            )
            return
        }

        startDiscovery()
    }

    private fun startDiscovery() {
        try {
            if (bluetoothAdapter.isDiscovering) {
                bluetoothAdapter.cancelDiscovery()
            }

            registerReceiverIfNeeded()
            discoveredDevices.clear()
            devicesText.text = "Searching..."
            statusText.text = "Starting discovery..."

            val started = bluetoothAdapter.startDiscovery()

            statusText.text = if (started) {
                "Discovery running..."
            } else {
                "Could not start discovery. Try again."
            }
        } catch (_: SecurityException) {
            statusText.text = "Bluetooth permission denied."
        }
    }

    private fun registerReceiverIfNeeded() {
        if (receiverRegistered) return

        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filter, Context.RECE
