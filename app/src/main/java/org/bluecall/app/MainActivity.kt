package org.bluecall.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val layout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
        setPadding(32, 32, 32, 32)
        setBackgroundColor(Color.WHITE)
    }

    val title = TextView(this).apply {
        text = "BlueCall"
        textSize = 34f
        setTextColor(Color.rgb(25, 75, 180))
        gravity = Gravity.CENTER
    }

    val subtitle = TextView(this).apply {
        text = "Offline Bluetooth Voice Calling"
        textSize = 16f
        setTextColor(Color.DKGRAY)
        gravity = Gravity.CENTER
    }

    val status = TextView(this).apply {
        text = "Ready to connect"
        textSize = 16f
        gravity = Gravity.CENTER
        setPadding(0, 40, 0, 24)
    }

    val findButton = Button(this).apply {
        text = "Find Nearby Phones"
        setOnClickListener {
            status.text = "Bluetooth discovery will be added next."
        }
    }

    layout.addView(
        title,
        LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    )

    layout.addView(
        subtitle,
        LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    )

    layout.addView(
        status,
        LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    )

    layout.addView(
        findButton,
        LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    )

    setContentView(layout)
}

}
