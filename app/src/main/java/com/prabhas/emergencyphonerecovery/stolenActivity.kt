package com.prabhas.emergencyphonerecovery

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class StolenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Prevent screenshots & screen recording
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        setContentView(R.layout.activity_stolen)

        // 🔑 RECOVER OWNERSHIP BUTTON (FIX)
        val recoverBtn = findViewById<Button>(R.id.btnRecover)
        recoverBtn.setOnClickListener {

            // Mark recovery flow started (prevents re-trigger loops)
            StolenModeManager.startRecovery(this)

            // Open recovery password verification
            startActivity(
                Intent(this, VerifyRecoveryPasswordActivity::class.java)
            )
        }

        // Block BACK button completely
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // DO NOTHING
                }
            }
        )

        enableImmersiveMode()
        startScreenPinning()
    }

    override fun onResume() {
        super.onResume()

        enableImmersiveMode()
        startScreenPinning()
    }

    private fun enableImmersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.apply {
                hide(
                    WindowInsets.Type.statusBars() or
                            WindowInsets.Type.navigationBars()
                )
                systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

    private fun startScreenPinning() {
        val activityManager =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!activityManager.isInLockTaskMode) {
                startLockTask()
            }
        } else {
            startLockTask()
        }
    }
}
