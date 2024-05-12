package it.unipi.masss.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import it.unipi.masss.LocationHandling
import it.unipi.masss.MainActivity
import it.unipi.masss.R
import java.util.Locale
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


class OnAlertReceivedService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(OnAlertReceivedService::class.java.simpleName, remoteMessage.toString())
        if(remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Data Payload: " + remoteMessage.data.toString())

            // check if I sent the alert
            val sharedPreference =  getSharedPreferences("TOKEN",Context.MODE_PRIVATE)
            val token = sharedPreference.getString("token", "defaultValue")
            if(token.equals(remoteMessage.data.get("token"))) return

            // Get location from message payload
            val lat = remoteMessage.data.get("lat")
            val long = remoteMessage.data.get("long")

            // Compute distance
            val location = LocationHandling.getPreciseLocation(this).get()
            if(lat != null && long != null && location != null) {
                val dist = distance(lat.toDouble(), long.toDouble(),
                    location.latitude, location.longitude)
                if (dist <= THRESHOLD) {
                    // it also open maps
                    showNotification("Someone is in danger! Click to locate",
                        lat.toDouble(), long.toDouble())
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        //save the token in a shared preference
        val sharedPreference =  getSharedPreferences("TOKEN",Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("token",token)
        editor.apply()

    }

    // method to show a notification and open google maps to locate the victim
    private fun showNotification(messageBody: String, lat: Double, long: Double) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = "fcm_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Alert!!")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        val channel = NotificationChannel(
            channelId,
            "Channel human readable title",
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        notificationManager.createNotificationChannel(channel)

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())

        // open google maps to show the victim position
        val uri = java.lang.String.format(Locale.ENGLISH, "geo:%f,%f", lat, long)
        val mapsIntent = Intent(Intent.ACTION_VIEW)
        mapsIntent.data = Uri.parse(uri)
        startActivity(intent)
    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(
            deg2rad(lat1)
        ) * cos(deg2rad(lat2)) * cos(deg2rad(theta))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        return (dist)
    }

    private fun deg2rad(deg: Double): Double {
        return (deg * Math.PI / 180.0)
    }

    private fun rad2deg(rad: Double): Double {
        return (rad * 180.0 / Math.PI)
    }

    companion object {
        private const val TAG = "FirebaseNotificationService"
        private const val THRESHOLD = 1.0 // TODO settare
    }
}