package com.prabhas.emergencyphonerecovery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*

class LiveLocationViewerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    private var deviceMarker: Marker? = null
    private var accuracyCircle: Circle? = null

    private val userId = "owner_001"

    private var deviceName = "Device"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_live_location_viewer)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map)
                    as SupportMapFragment

        mapFragment.getMapAsync(this)

        fetchDeviceName()
    }

    override fun onMapReady(map: GoogleMap) {

        googleMap = map

        startListeningLocation()
    }

    private fun fetchDeviceName() {

        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("device_info")
            .child("display_name")
            .get()
            .addOnSuccessListener {

                val name = it.getValue(String::class.java)

                if (!name.isNullOrEmpty()) {

                    deviceName = name
                }
            }
    }

    private fun startListeningLocation() {

        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("live_location")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val lat =
                        snapshot.child("lat")
                            .getValue(Double::class.java)
                            ?: return

                    val lng =
                        snapshot.child("lng")
                            .getValue(Double::class.java)
                            ?: return

                    val accuracy =
                        snapshot.child("accuracy")
                            .getValue(Float::class.java)
                            ?: 0f

                    updateMarker(LatLng(lat, lng), accuracy)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun updateMarker(position: LatLng, accuracy: Float) {

        if (deviceMarker == null) {

            deviceMarker =
                googleMap.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(deviceName)
                        .icon(
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_RED
                            )
                        )
                )

            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(position, 17f)
            )
        }
        else {

            deviceMarker!!.position = position
        }

        if (accuracyCircle == null) {

            accuracyCircle =
                googleMap.addCircle(
                    CircleOptions()
                        .center(position)
                        .radius(accuracy.toDouble())
                        .strokeWidth(2f)
                        .strokeColor(0x55FF0000)
                        .fillColor(0x22FF0000)
                )
        }
        else {

            accuracyCircle!!.center = position
            accuracyCircle!!.radius = accuracy.toDouble()
        }
    }
}
// git test