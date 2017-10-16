package pdm.isel.pt.yawa.services

import android.app.AlarmManager
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
import pdm.isel.pt.yawa.presentation.ForecastWeatherInfoActivity
import pdm.isel.pt.yawa.view.WeatherInfo
import java.util.*

class WeatherForecastUpdater : ListService(){
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.v("YAWA_WeatherForeUpdater", "WeatherForecast updater service called")
        if(!canDownload())
            return Service.START_REDELIVER_INTENT

        val sharedPreferences = getSharedPreferences(getString(R.string.shared_preference), Context.MODE_PRIVATE)
        val refreshTime = AlarmManager.INTERVAL_DAY
        if(System.currentTimeMillis() - fetchTime < refreshTime / 2)
            return Service.START_REDELIVER_INTENT

        val city = sharedPreferences.getString(getString(R.string.shared_preference_key_favorite_city),"Lisbon")

        val app = application as YawaApplication

        val weatherInfoList = ArrayList<WeatherInfo>(5)

        contentResolver.delete(WeatherContract.Weather.CONTENT_URI,"TYPE="+DbSchema.FORECAST_WEATHER_TYPE,null)

        app.fetchForecastWeather(city!!, {
            weather ->
            fetchTime =  Date().time
            Log.v("YAWA_WeatherForeUpdater", "WeatherForecast new request")
            for (idx in weather.list.indices){
                weatherInfoList.add(weather.list[idx])
            }
            weatherInfoList.forEach { w ->
                val res = ContentValues()
                res.put(DbSchema.Weather.COLUMN_COUNTRY, w.country)
                res.put(DbSchema.Weather.COLUMN_CITY, w.city)
                res.put(DbSchema.Weather.COLUMN_DESCRIPTION, w.description)
                res.put(DbSchema.Weather.COLUMN_ICON, w.icon)
                res.put(DbSchema.Weather.COLUMN_TEMP, w.temp)
                res.put(DbSchema.Weather.COLUMN_MIN, w.min)
                res.put(DbSchema.Weather.COLUMN_MAX, w.max)
                res.put(DbSchema.Weather.COLUMN_PRESSURE, w.pressure)
                res.put(DbSchema.Weather.COLUMN_HUMIDITY, w.humidity)
                res.put(DbSchema.Weather.COLUMN_WINDSPEED, w.windSpeed)
                res.put(DbSchema.Weather.COLUMN_SUNRISE, w.sunrise.time)
                res.put(DbSchema.Weather.COLUMN_SUNSET, w.sunset.time)
                res.put(DbSchema.Weather.COLUMN_CLOUDINESS, w.cloudiness)
                res.put(DbSchema.Weather.COLUMN_RAIN, w.rain)
                res.put(DbSchema.Weather.COLUMN_SNOW, w.snow)
                res.put(DbSchema.Weather.COLUMN_DATE, w.date)
                res.put(DbSchema.Weather.COLUMN_TYPE, DbSchema.FORECAST_WEATHER_TYPE)

                contentResolver.insert(WeatherContract.Weather.CONTENT_URI, res)
            }

            //se o user quer notifications
            if(app.sharedPreferences.getBoolean(getString(R.string.shared_preference_key_notification), true)){
                Log.v("YAWA_WeatherForeUpdater", "WeatherForecast new notification")

                val resultIntent = Intent(this, ForecastWeatherInfoActivity::class.java)
                        .putExtra("forecast_weather", weather)

                notifyWeatherUpdate(resultIntent, city)
            }
        })

        fetchTime = System.currentTimeMillis()
        return Service.START_REDELIVER_INTENT
    }
}
