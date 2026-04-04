package com.prabhas.emergencyphonerecovery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class ReinstallRecoveryVerifyActivity : AppCompatActivity() {

    private lateinit var recoveryInput: EditText
    private lateinit var verifyButton: Button

    private val userId = "owner_001"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reinstall_recovery_verify)

        recoveryInput = findViewById(R.id.etRecoveryPassword)
        verifyButton = findViewById(R.id.btnVerifyRecovery)

        verifyButton.setOnClickListener {

            val enteredPassword = recoveryInput.text.toString().trim()

            if (enteredPassword.isEmpty()) {
                recoveryInput.error = "Enter recovery password"
                return@setOnClickListener
            }

            verifyRecoveryFromFirebase(enteredPassword)
        }
    }

    private fun verifyRecoveryFromFirebase(input: String) {

        val hash = HashUtils.sha256(input)

        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("recovery")
            .child("password_hash")
            .get()
            .addOnSuccessListener { snapshot ->

                val storedHash = snapshot.getValue(String::class.java)

                if (storedHash == null) {
                    Toast.makeText(
                        this,
                        "No recovery password found. Setup required.",
                        Toast.LENGTH_LONG
                    ).show()

                    startActivity(
                        Intent(this, RecoverySetupActivity::class.java)
                    )
                    finish()
                    return@addOnSuccessListener
                }

                if (storedHash == hash) {

                    // mark this install trusted
                    getSharedPreferences("install_state", MODE_PRIVATE)
                        .edit()
                        .putBoolean("trusted_install", true)
                        .apply()

                    SecurePrefs.setRecoveryPasswordSet(this, true)

                    Toast.makeText(
                        this,
                        "Recovery verified. Please set App Lock.",
                        Toast.LENGTH_LONG
                    ).show()

                    startActivity(
                        Intent(this, AppLockSetupActivity::class.java)
                    )
                    finish()

                } else {
                    recoveryInput.error = "Incorrect recovery password"
                }
            }
    }
}
