package pdm.isel.pt.yawa.handler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import pdm.isel.pt.yawa.services.WeatherUpdater

class ConnectivityChanged : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.v("YAWA_ConnectChanged", "Connectivity changed")
        if(isNetworkAvailable(context!!)){
            Log.v("YAWA_ConnectChanged", "Network on")
            val serviceIntent = Intent(context, WeatherUpdater::class.java)
            context.startService(serviceIntent)
        }
        else
            Log.v("YAWA_ConnectChanged", "Network off")
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        Log.v("YAWA_ConnectChanged", "check if there is an internet connection")
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

}
