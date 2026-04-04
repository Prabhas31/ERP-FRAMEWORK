package com.prabhas.emergencyphonerecovery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class SimulatedPowerOffActivity : AppCompatActivity() {

    private lateinit var passwordInput: EditText
    private lateinit var confirmButton: Button

    private var isFakePoweredOff = false
    private val MAX_ATTEMPTS = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulated_power_off)

        passwordInput = findViewById(R.id.etAppLockPassword)
        confirmButton = findViewById(R.id.btnConfirmShutdown)

        confirmButton.setOnClickListener {

            val enteredPassword = passwordInput.text.toString().trim()
            if (enteredPassword.isEmpty()) {
                passwordInput.error = "Enter App Lock password"
                return@setOnClickListener
            }

            // ✅ Correct password
            if (SecurePrefs.verifyAppLockPassword(this, enteredPassword)) {

                SecurePrefs.resetFailedAttempts(this)
                showFakePowerOffScreen()
                return@setOnClickListener
            }

            // ❌ Wrong password
            SecurePrefs.incrementFailedAttempts(this)
            val attempts = SecurePrefs.getFailedAttempts(this)

            if (attempts >= MAX_ATTEMPTS) {

                // 🔴 REAL stolen mode activation
                StolenModeManager.activate(this)

                startActivity(
                    Intent(this, StolenActivity::class.java)
                        .addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                        )
                )
                finish()
            } else {
                passwordInput.error =
                    "Wrong password. ${MAX_ATTEMPTS - attempts} attempts left"
            }
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    if (StolenModeManager.isStolen(this@SimulatedPowerOffActivity)) {
                        return
                    }

                    if (isFakePoweredOff) {
                        finish()
                        return
                    }

                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            })
    }

    private fun showFakePowerOffScreen() {
        isFakePoweredOff = true
        setContentView(R.layout.activity_fake_powered_off)
    }
}
