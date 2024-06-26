package it.unipi.masss.firebase

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import it.unipi.masss.ProtectronApplication
import it.unipi.masss.ProtectronApplication.Companion.ALERT_RANGE_THRESHOLD
import it.unipi.masss.ProtectronApplication.Companion.SHARED_PREF
import it.unipi.masss.R
import it.unipi.masss.location.LocationRetriever


class OnAlertReceivedService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Data Payload: " + remoteMessage.data.toString())

            // check if I'm the sender of the alert
            val sharedPreference = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
            val token = sharedPreference.getString("token", "defaultValue")
            if (token.equals(remoteMessage.data["token"])) return

            // Get location from message payload
            val lat = remoteMessage.data["lat"]
            val long = remoteMessage.data["long"]

            // Compute distance
            val personInDangerLocation = Location("") //provider name is unnecessary
            personInDangerLocation.latitude = lat!!.toDouble()
            personInDangerLocation.longitude = long!!.toDouble()

            LocationRetriever.getPreciseLocation(applicationContext).thenApply { location ->
                if(location == null) {
                    Log.d(TAG, "Cannot fetch user precise location")
                }
                else {
                    Log.d(TAG, "Location fetched $location")
                    val dis = location.distanceTo(personInDangerLocation)
                    Log.d(
                        TAG,
                        "Distance from the victim: $dis"
                    )
                    if (dis <= ALERT_RANGE_THRESHOLD) {
                        // it also open maps
                        showNotification("Someone is in danger!",
                            lat.toDouble(), long.toDouble())
                    }
                    else {

                    }
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        //save the token in a shared preference
        val sharedPreference = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("token", token)
        editor.apply()

    }

    // method to show a notification and open google maps to locate the victim
    private fun showNotification(message: String, lat: Double, long: Double) {
        // Creates an Intent that will load the position of the victim
        val mapsIntent = Intent(Intent.ACTION_VIEW,
            Uri.parse("http://maps.google.com/maps?q=${lat},${long}"))
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, mapsIntent, PendingIntent.FLAG_IMMUTABLE)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, ProtectronApplication.CHANNEL_ID)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(message)
            .setContentText("Click to open Google Maps!")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "OnAlertReceivedService"
    }
}