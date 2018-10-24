package com.helloworld.googlemaps.googlemapshelloworld

import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.navisens.motiondnaapi.MotionDna
import com.navisens.motiondnaapi.MotionDnaApplication
import com.navisens.motiondnaapi.MotionDnaInterface
import com.navisens.motiondnaapi.MotionDnaPlugin.startMotionDna
import android.content.Intent
import com.google.android.gms.maps.model.*

// Inheriting from GoogleMaps Callbacks and our MotionDnaInterface
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, MotionDnaInterface {

    // MotionDna Permissions value
    private val REQUEST_MDNA_PERMISSIONS = 1
    // Current location Google Marker
    private lateinit var pos_marker : Marker
    // MotionDnaApplication instance
    private lateinit var motionDnaApp : MotionDnaApplication

    // Must do, MotionDna SDK uses ApplicationContext to request permission.
    override fun getAppContext(): Context {
        return applicationContext
    }

    override fun receiveNetworkData(p0: MotionDna?) {
        // Receive location data from network when user is sharing.
    }

    override fun receiveNetworkData(p0: MotionDna.NetworkCode?, p1: MutableMap<String, out Any>?) {
        // Receive custom commands over network to interactive multi user applications.
    }

    // SDK Callback for current location estimate
    override fun receiveMotionDna(p0: MotionDna?) {
        // Current location estimate
        var latlon=LatLng(p0!!.location.globalLocation.latitude, p0!!.location.globalLocation.longitude)
        if (::pos_marker.isInitialized == false) {
            // Initialize marker
            pos_marker = mMap.addMarker(MarkerOptions()
                    .title("Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot)).position(latlon))
            // Set marker to be flat.
            pos_marker.isFlat=true
            // Move camera to initial position
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlon, 15.0.toFloat()));
        }
        //Update location and heading
        pos_marker.position=latlon
        pos_marker.rotation= p0!!.location.heading.toFloat()
    }

    override fun reportError(p0: MotionDna.ErrorCode?, p1: String?) {
        // Error reporting
    }
    // Must do, MotionDna SDK uses PackageManager to request permission.
    override fun getPkgManager(): PackageManager {
        return packageManager
    }

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // Callback for permissions to trigger.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (MotionDnaApplication.checkMotionDnaPermissions(this)){
            // Instantiate MotionDnaApplication instance
            motionDnaApp= MotionDnaApplication(this)
            // Start SDK with the device key you retrieved from www.navisens.com
            motionDnaApp.runMotionDna("YOUR_DEVELOPER_KEY_HERE")
            // Ensure GPS is turned on for Navisens' fusion systems to work
            motionDnaApp.setExternalPositioningState(MotionDna.ExternalPositioningState.HIGH_ACCURACY)
            // Start sensor fusion systems
            motionDnaApp.setLocationNavisens()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        ActivityCompat.requestPermissions(this,MotionDnaApplication.needsRequestingPermissions()
                , REQUEST_MDNA_PERMISSIONS);
    }
}
