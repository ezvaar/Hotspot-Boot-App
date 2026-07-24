package com.example.hotspotboot

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(48, 96, 48, 48)

        val info = TextView(this)
        info.text = "This app opens your Hotspot settings automatically every time your phone boots.\n\n" +
                "For it to work reliably, please allow it to ignore battery optimization."
        info.textSize = 16f
        layout.addView(info)

        val button = Button(this)
        button.text = "Disable Battery Optimization"
        button.setPadding(0, 48, 0, 0)
        button.setOnClickListener {
            requestIgnoreBatteryOptimizations()
        }
        layout.addView(button)

        setContentView(layout)
    }

    private fun requestIgnoreBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            val packageName = packageName
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }
    }
}
