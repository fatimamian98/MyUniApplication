package com.example.myapplication.service

import android.Manifest
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.myapplication.R


const val CHANNEL_DEFAULT_CHANNEL_ID = "CHANNEL_DEFAULT_CHANNEL_ID"
const val ONGOING_NOTIFICATION_ID = 10

open class LocationService : Service() {
    private var locationManager: LocationManager? = null
    private var listener: MyLocationListener? = null
    var previousBestLocation: Location? = null
    var mIntervalLocation = 0
    var intent: Intent? = null
    var counter = 0
    override fun onCreate() {
        super.onCreate()
        intent = Intent(BROADCAST_ACTION)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()

            val notification: Notification =
                NotificationCompat.Builder(this, CHANNEL_DEFAULT_CHANNEL_ID)
                    .setContentTitle(getText(R.string.notification_title))
                    .setSmallIcon(R.drawable.ic_location_24dp)
                    .build()

            // Notification ID cannot be 0.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    ONGOING_NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                )
            } else {
                startForeground(ONGOING_NOTIFICATION_ID, notification)
            }
        } else {

        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_DEFAULT_CHANNEL_ID,
                CHANNEL_DEFAULT_CHANNEL_ID,
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onStart(intent: Intent?, startId: Int) {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        listener = MyLocationListener()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager!!.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER, TWO_MINUTES.toLong(), 0f,
            listener!!
        )
        locationManager!!.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, TWO_MINUTES.toLong(), 0f,
            listener!!
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    protected fun isBetterLocation(location: Location, currentBestLocation: Location?): Boolean {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true
        }

        // Check whether the new location fix is newer or older
        val timeDelta = location.time - currentBestLocation.time
        val isSignificantlyNewer = timeDelta > TWO_MINUTES
        val isSignificantlyOlder = timeDelta < -TWO_MINUTES
        val isNewer = timeDelta > 0

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false
        }

        // Check whether the new location fix is more or less accurate
        val accuracyDelta = (location.accuracy - currentBestLocation.accuracy).toInt()
        val isLessAccurate = accuracyDelta > 0
        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > 200

        // Check if the old and new location are from the same provider
        val isFromSameProvider = isSameProvider(
            location.provider,
            currentBestLocation.provider
        )

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true
        } else if (isNewer && !isLessAccurate) {
            return true
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true
        }
        return false
    }

    /** Checks whether two providers are the same  */
    private fun isSameProvider(provider1: String?, provider2: String?): Boolean {
        return if (provider1 == null) {
            provider2 == null
        } else provider1 == provider2
    }

    override fun onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy()
        Log.v("STOP_SERVICE", "DONE")
        locationManager!!.removeUpdates(listener!!)
    }

    //public static Thread performOnBackgroundThread(final Runnable runnable) {
    fun performOnBackgroundThread(runnable: Runnable): Thread {
        val t: Thread = object : Thread() {
            override fun run() {
                try {
                    runnable.run()
                } finally {
                }
            }
        }
        t.start()
        return t
    }

    inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(loc: Location) {
            Toast.makeText(this@LocationService, "Location changed", Toast.LENGTH_SHORT).show()
            Log.i("*********", "Location changed")
            if (isBetterLocation(loc, previousBestLocation)) {

                Log.i("****latitude*****", "Location: ${loc.latitude}")
                Log.i("****longitude*****", "longitude: ${loc.longitude}")

                Toast.makeText(
                    this@LocationService,
                    "Location: ${loc.latitude}, longitude: ${loc.longitude}",
                    Toast.LENGTH_SHORT
                ).show()

                intent!!.putExtra("Latitude", loc.latitude.toString())
                intent!!.putExtra("Longitude", loc.longitude.toString())
                intent!!.putExtra("Provider", loc.provider)
                sendBroadcast(intent)

                //Log.d("LOCATION", "LON:" + String.valueOf(loc.getLongitude()) + " LAT:" + String.valueOf(loc.getLatitude()));
                //Toast.makeText( getApplicationContext(), "LON:" + String.valueOf(loc.getLongitude()) + " LAT" + String.valueOf(loc.getLatitude()), Toast.LENGTH_SHORT ).show();
            }
        }

        override fun onProviderDisabled(provider: String) {
            Toast.makeText(applicationContext, "Gps Disabled", Toast.LENGTH_SHORT).show()
        }

        override fun onProviderEnabled(provider: String) {
            Toast.makeText(applicationContext, "Gps Enabled", Toast.LENGTH_SHORT).show()
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }

    companion object {
        const val BROADCAST_ACTION = "BroadcastLocationFastium"
        private const val TWO_MINUTES = 1000 * 60 * 2

        fun isMyServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
            val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            return false
        }
    }
}