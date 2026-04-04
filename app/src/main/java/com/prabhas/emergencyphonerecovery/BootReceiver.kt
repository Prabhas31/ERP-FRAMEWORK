package com.prabhas.emergencyphonerecovery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        // ✅ ONLY source of truth
        if (!StolenModeManager.isStolen(context)) return

        // 📍 Restart live location tracking after reboot
        val serviceIntent =
            Intent(context, LiveLocationService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(
                context,
                serviceIntent
            )
        } else {
            context.startService(serviceIntent)
        }
    }
}
