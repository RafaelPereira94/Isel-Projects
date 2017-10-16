package pdm.isel.pt.yawa.presentation

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_shared_preferences.*
import pdm.isel.pt.yawa.R
import pdm.isel.pt.yawa.YawaApplication

class PreferencesActivity : BaseActivity(){

    override val layoutResId: Int = R.layout.activity_shared_preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResId)

        var adapter = ArrayAdapter.createFromResource(this,R.array.shared_preference_refresh_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        shared_preference_data_refresh_answer.adapter = adapter

        adapter = ArrayAdapter.createFromResource(this,R.array.shared_preference_economic_mode_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        shared_preference_economic_mode_answer.adapter = adapter

        adapter = ArrayAdapter.createFromResource(this,R.array.shared_preference_notification_hour_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        shared_preference_notification_hour_answer.adapter = adapter

        val app = application as YawaApplication

        shared_preference_data_refresh_answer.setSelection(app.sharedPreferences.getInt(getString(R.string.shared_preference_key_data_refresh), 0))
        shared_preference_economic_mode_answer.setSelection(app.sharedPreferences.getInt(getString(R.string.shared_preference_key_economic_mode), 0))
        shared_preference_notification_hour_answer.setSelection(app.sharedPreferences.getInt(getString(R.string.shared_preference_key_notification_hour),0))
        shared_preference_favorite_city_answer.setText(app.sharedPreferences.getString(getString(R.string.shared_preference_key_favorite_city),"Lisbon"))
        if(app.sharedPreferences.getBoolean(getString(R.string.shared_preference_key_notification),true))
            shared_preference_notification.toggle()
        if(app.sharedPreferences.getBoolean(getString(R.string.shared_preference_key_wi_fi),true))
            shared_preference_wifi.toggle()


        shared_preference_saveButton.setOnClickListener {
            val refresh = shared_preference_data_refresh_answer.selectedItemPosition.toString()
            Log.v("refresh = ",refresh)
            app.sharedPreferences.edit()
                    .putInt(getString(R.string.shared_preference_key_data_refresh),refresh.toInt())
                    .apply()

            val economic = shared_preference_economic_mode_answer.selectedItemPosition.toString()
            Log.v("economic = ",economic)
            app.sharedPreferences.edit()
                    .putInt(getString(R.string.shared_preference_key_economic_mode),economic.toInt())
                    .apply()

            val notification_hour = shared_preference_notification_hour_answer.selectedItemPosition.toString()
            Log.v("MyNotification hour = ",notification_hour)
            app.sharedPreferences.edit()
                    .putInt(getString(R.string.shared_preference_key_notification_hour),notification_hour.toInt())
                    .apply()

            val favoriteCity = shared_preference_favorite_city_answer.text.toString()
            Log.v("Favorite city = ",favoriteCity)
            app.sharedPreferences.edit()
                    .putString(getString(R.string.shared_preference_key_favorite_city),favoriteCity)
                    .apply()

            val checkBox_Wifi = shared_preference_wifi.isChecked
            Log.v("Wi-fi = ",checkBox_Wifi.toString())
            app.sharedPreferences.edit()
                    .putBoolean(getString(R.string.shared_preference_key_wi_fi),checkBox_Wifi)
                    .apply()

            val checkBox_notification = shared_preference_notification.isChecked
            Log.v("MyNotification = ",checkBox_notification.toString())
            app.sharedPreferences.edit()
                    .putBoolean(getString(R.string.shared_preference_key_notification),checkBox_notification)
                    .apply()
        }
    }

}