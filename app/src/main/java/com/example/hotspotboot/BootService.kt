package com.example.hotspotboot

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat

class BootService : Service() {

    private val channelId = "hotspot_boot_channel"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        // Intent that opens the tethering / hotspot settings screen.
        val settingsIntent = Intent("android.settings.TETHER_SETTINGS")
        settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(
            this, 0, settingsIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Hotspot on Boot")
            .setContentText("Tap to turn on your hotspot")
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        startForeground(1, notification)

        // Also try to launch the settings screen directly so it's one tap, not two.
        try {
            startActivity(settingsIntent)
        } catch (e: Exception) {
            // Fallback to general wireless settings if TETHER_SETTINGS isn't available on this OEM.
            val fallback = Intent(Settings.ACTION_WIRELESS_SETTINGS)
            fallback.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(fallback)
        }

        stopSelf()
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Hotspot Boot Service",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
