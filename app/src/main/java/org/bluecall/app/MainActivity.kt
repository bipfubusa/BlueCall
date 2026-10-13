package org.bluecall.app

import android.Manifest
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.content.pm.PackageManager
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var statusText: TextView
    private val permissionRequestCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(32, 32, 32, 32)
        layout.setBackgroundColor(Color.WHITE)

        val title = TextView(this)
        title.text = "BlueCall"
        title.textSize = 32f
        title.setTextColor(Color.BLUE)
        title.gravity = Gravity.CENTER

        val description = TextView(this)
        description.text = "Offline Bluetooth Voice Calling"
        description.textSize = 18f
        description.gravity = Gravity.CENTER
        description.setPadding(0, 24, 0, 24)

        statusText = TextView(this)
        statusText.textSize = 16f
        statusText.gravity = Gravity.CENTER
        statusText.setPadding(0, 16, 0, 24)

        val permissionButton = Button(this)
        permissionButton.text = "Grant Permissions"
        permissionButton.setOnClickListener {
            requestBlueCallPermissions()
        }

        layout.addView(title)
        layout.addView(description)
        layout.addView(statusText)
        layout.addView(permissionButton)

        setContentView(layout)
        updateStatus()
    }

    private fun requestBlueCallPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.RECORD_AUDIO
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        }

        val missingPermissions = permissions.filter {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isEmpty()) {
            updateStatus()
        } else {
            requestPermissions(
                missingPermissions.toTypedArray(),
                permissionRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == permissionRequestCode) {
            updateStatus()
        }
    }

    private fun updateStatus() {
        val microphoneGranted =
            checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED

        val bluetoothGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) ==
                    PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) ==
                    PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

        statusText.text = """
            Microphone: ${if (microphoneGranted) "Granted" else "Not granted"}
            Bluetooth permissions: ${if (bluetoothGranted) "Granted" else "Not granted"}

            Device discovery and voice calling are not implemented yet.
        """.trimIndent()
    }
}
