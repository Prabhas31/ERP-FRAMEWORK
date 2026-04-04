package com.prabhas.emergencyphonerecovery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var manageEmailsButton: Button
    private lateinit var manageRecoveryPasswordButton: Button
    private lateinit var manageAppLockPasswordButton: Button
    private lateinit var physicalKeyButton: Button
    private lateinit var simulatePowerOffButton: Button

    private lateinit var userId: String
    private val PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ FIX
        userId = SecurePrefs.getUserId(this)

        if (StolenModeManager.isStolen(this)) {
            startActivity(
                Intent(this, StolenActivity::class.java)
                    .addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
            )
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        manageEmailsButton = findViewById(R.id.btnManageTrustedEmails)
        manageRecoveryPasswordButton = findViewById(R.id.btnManageRecoveryPassword)
        manageAppLockPasswordButton = findViewById(R.id.btnManageAppLockPassword)
        physicalKeyButton = findViewById(R.id.btnPhysicalRecoveryKey)
        simulatePowerOffButton = findViewById(R.id.btnSimulatePowerOff)

        statusText.text = getString(R.string.status_normal_mode)

        requestRequiredPermissionsIfNeeded()

        manageEmailsButton.setOnClickListener {
            startActivity(
                Intent(this, VerifyRecoveryPasswordActivity::class.java)
                    .putExtra("TARGET", "MANAGE_TRUSTED_EMAILS")
            )
        }

        manageRecoveryPasswordButton.setOnClickListener {
            startActivity(Intent(this, RecoveryPasswordOptionsActivity::class.java))
        }

        physicalKeyButton.setOnClickListener {
            startActivity(
                Intent(this, VerifyRecoveryPasswordActivity::class.java)
                    .putExtra("TARGET", "VIEW_PHYSICAL_KEY")
            )
        }

        simulatePowerOffButton.setOnClickListener {
            startActivity(Intent(this, SimulatedPowerOffActivity::class.java))
        }

        manageAppLockPasswordButton.setOnClickListener {
            startActivity(Intent(this, VerifyAppLockActivity::class.java))
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    moveTaskToBack(true)
                }
            }
        )

        setupDeviceInfoIfNeeded()
    }

    override fun onResume() {
        super.onResume()

        if (StolenModeManager.isStolen(this)) {
            startActivity(
                Intent(this, StolenActivity::class.java)
                    .addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
            )
            finish()
        }
    }

    private fun requestRequiredPermissionsIfNeeded() {

        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun setupDeviceInfoIfNeeded() {

        val deviceRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("device_info")

        deviceRef.get().addOnSuccessListener { snapshot ->

            if (snapshot.child("display_name").exists())
                return@addOnSuccessListener

            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            val phoneModel = "$manufacturer $model"

            val input = EditText(this)

            AlertDialog.Builder(this)
                .setTitle("Owner Name")
                .setMessage("Enter your name")
                .setView(input)
                .setCancelable(false)
                .setPositiveButton("Save") { _, _ ->

                    val ownerName =
                        input.text.toString().trim()
                            .ifEmpty { "Owner" }

                    val data = mapOf(
                        "owner_name" to ownerName,
                        "manufacturer" to manufacturer,
                        "model" to model,
                        "display_name" to "$ownerName – $phoneModel"
                    )

                    deviceRef.setValue(data)
                }
                .show()
        }
    }
}