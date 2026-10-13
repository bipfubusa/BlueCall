package org.bluecall.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

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

    val status = TextView(this)
    status.text = "Your communication starts here."
    status.textSize = 16f
    status.gravity = Gravity.CENTER

    layout.addView(title)
    layout.addView(description)
    layout.addView(status)

    setContentView(layout)
}

}
