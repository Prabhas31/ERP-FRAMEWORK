package com.prabhas.emergencyphonerecovery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class AppLockSetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_lock_setup)

        val etPassword = findViewById<EditText>(R.id.etAppLockPassword)
        val etConfirm = findViewById<EditText>(R.id.etConfirmAppLockPassword)
        val btnContinue = findViewById<Button>(R.id.btnContinue)

        btnContinue.setOnClickListener {

            val p1 = etPassword.text.toString()
            val p2 = etConfirm.text.toString()

            if (p1.length < 6) {
                toast("Password must be at least 6 characters")
                return@setOnClickListener
            }

            if (p1 != p2) {
                toast("Passwords do not match")
                return@setOnClickListener
            }

            // 🔐 Store App Lock securely
            SecurePrefs.setAppLockPassword(this, p1)

            // ✅ Mark only App Lock as set
            SecurePrefs.setAppLockSet(this, true)

            toast("App Lock setup complete")

            // 🔄 Go to Recovery Setup (fresh install flow)
            startActivity(
                Intent(this, RecoverySetupActivity::class.java)
            )

            finish()
        }

        // Prevent back navigation during setup
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Block back
                }
            }
        )
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
