package pdm.isel.pt.yawa.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v4.app.NotificationCompat
import pdm.isel.pt.yawa.R
import pdm.isel.pt.yawa.YawaApplication
import pdm.isel.pt.yawa.presentation.CreditsActivity
import pdm.isel.pt.yawa.presentation.MainActivity

abstract class ListService : Service() {

    var fetchTime : Long = 0

    companion object{
        private var notificationId = 1
    }

    protected fun canDownload(): Boolean {
        val app = application as YawaApplication
        val onlyByWifi = app.sharedPreferences.getBoolean(getString(R.string.shared_preference_key_wi_fi), true)
        if(!onlyByWifi)
            return true
        return onlyByWifi && isWifi()

    }

    protected fun isWifi(): Boolean {
        val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connManager.activeNetworkInfo
        return (info != null && info.isConnected && info.type == ConnectivityManager.TYPE_WIFI )
    }

    protected fun notifyWeatherUpdate(resultIntent: Intent, city: String){
        val builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_template_icon_bg)
                .setContentTitle(application.getString(R.string.notification_title))
                .setContentText(application.getString(R.string.notification_text) + city)
                .setContentIntent(
                        PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
        .setAutoCancel(true)

        var nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(notificationId++, builder.build())
    }
}
