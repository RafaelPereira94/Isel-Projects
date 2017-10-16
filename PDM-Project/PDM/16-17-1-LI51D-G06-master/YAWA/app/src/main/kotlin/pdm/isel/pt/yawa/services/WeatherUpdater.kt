package pdm.isel.pt.yawa.services

import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import pdm.isel.pt.yawa.R
import pdm.isel.pt.yawa.YawaApplication
import pdm.isel.pt.yawa.models.content.DbSchema
import pdm.isel.pt.yawa.models.content.WeatherContract

class WeatherUpdater : ListService(){
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.v("YAWA_WeatherUpdater", "Weather updater service called")
        if(!canDownload())
            return Service.START_REDELIVER_INTENT

        val sharedPreferences = getSharedPreferences(getString(R.string.shared_preference), Context.MODE_PRIVATE)
        val refreshTime = sharedPreferences.getInt(getString(R.string.shared_preference_key_data_refresh), 0)
        if(System.currentTimeMillis() - fetchTime < refreshTime / 2)
            return Service.START_REDELIVER_INTENT

        val city = sharedPreferences.getString(getString(R.string.shared_preference_key_favorite_city),"Lisbon")

        val app = application as YawaApplication

        contentResolver.delete(WeatherContract.Weather.CONTENT_URI,"TYPE="+DbSchema.CURRENT_WEATHER_TYPE,null)
        app.fetchCurrentWeather(city!!, {
            weather ->
            Log.v("YAWA_WeatherUpdater", "WeatherInfo new request")
            val res = ContentValues()
            res.put(DbSchema.Weather.COLUMN_COUNTRY, weather.country)
            res.put(DbSchema.Weather.COLUMN_CITY, weather.city)
            res.put(DbSchema.Weather.COLUMN_DESCRIPTION, weather.description)
            res.put(DbSchema.Weather.COLUMN_ICON, weather.icon)
            res.put(DbSchema.Weather.COLUMN_TEMP, weather.temp)
            res.put(DbSchema.Weather.COLUMN_MIN, weather.min)
            res.put(DbSchema.Weather.COLUMN_MAX, weather.max)
            res.put(DbSchema.Weather.COLUMN_PRESSURE, weather.pressure)
            res.put(DbSchema.Weather.COLUMN_HUMIDITY, weather.humidity)
            res.put(DbSchema.Weather.COLUMN_WINDSPEED, weather.windSpeed)
            res.put(DbSchema.Weather.COLUMN_SUNRISE, weather.sunrise.time)
            res.put(DbSchema.Weather.COLUMN_SUNSET, weather.sunset.time)
            res.put(DbSchema.Weather.COLUMN_CLOUDINESS, weather.cloudiness)
            res.put(DbSchema.Weather.COLUMN_RAIN, weather.rain)
            res.put(DbSchema.Weather.COLUMN_SNOW, weather.snow)
            res.put(DbSchema.Weather.COLUMN_DATE, weather.date)
            res.put(DbSchema.Weather.COLUMN_TYPE, DbSchema.CURRENT_WEATHER_TYPE)

            contentResolver.insert(WeatherContract.Weather.CONTENT_URI, res)
        })

        fetchTime = System.currentTimeMillis()
        return Service.START_REDELIVER_INTENT
    }
}
