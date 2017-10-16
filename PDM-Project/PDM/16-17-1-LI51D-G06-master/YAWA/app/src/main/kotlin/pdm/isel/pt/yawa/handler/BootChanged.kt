package pdm.isel.pt.yawa.handler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import pdm.isel.pt.yawa.YawaApplication

class BootChanged : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        (context!!.applicationContext as YawaApplication).initAlarms()
    }
}
