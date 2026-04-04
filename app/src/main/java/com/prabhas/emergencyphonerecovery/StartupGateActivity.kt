package com.prabhas.emergencyphonerecovery

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class StartupGateActivity : AppCompatActivity() {

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = SecurePrefs.initUserId(this)

        setContentView(R.layout.activity_startup_gate)
        route()
    }

    private fun route() {

        val userRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)

        userRef.get().addOnSuccessListener { snapshot ->

            val recoveryExists =
                snapshot.child("recovery").child("password_hash").exists()

            val trustedEmailsSet =
                snapshot.child("trusted_emails")
                    .child("trusted_emails_set")
                    .getValue(Boolean::class.java) ?: false

            if (recoveryExists &&
                !getSharedPreferences("install_state", MODE_PRIVATE)
                    .getBoolean("trusted_install", false)
            ) {
                startActivity(
                    Intent(this, ReinstallRecoveryVerifyActivity::class.java)
                )
                finish()
                return@addOnSuccessListener
            }

            if (!SecurePrefs.isAppLockSet(this)) {
                startActivity(Intent(this, AppLockSetupActivity::class.java))
                finish()
                return@addOnSuccessListener
            }

            if (!SecurePrefs.isRecoveryPasswordSet(this)) {
                startActivity(Intent(this, RecoverySetupActivity::class.java))
                finish()
                return@addOnSuccessListener
            }

            if (!trustedEmailsSet) {
                startActivity(Intent(this, TrustedEmailSetupActivity::class.java))
                finish()
                return@addOnSuccessListener
            }

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}