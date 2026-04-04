package com.prabhas.emergencyphonerecovery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RecoveryPasswordOptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovery_password_options)

        val btnUsingPassword =
            findViewById<Button>(R.id.btnUsingRecoveryPassword)

        val btnUsingPhysicalKey =
            findViewById<Button>(R.id.btnUsingPhysicalKey)

        // 🔐 Reset using recovery password
        btnUsingPassword.setOnClickListener {
            startActivity(
                Intent(this, VerifyRecoveryPasswordActivity::class.java)
                    .putExtra("TARGET", "RESET_RECOVERY_PASSWORD")
            )
            finish() // 🔑 CRITICAL: remove options from back stack
        }

        // 🔑 Reset using physical key
        btnUsingPhysicalKey.setOnClickListener {
            startActivity(
                Intent(this, PhysicalKeyVerifyActivity::class.java)
                    .putExtra("TARGET", "RESET_RECOVERY_PASSWORD")
            )
            finish() // 🔑 CRITICAL: remove options from back stack
        }
    }
}
// git test