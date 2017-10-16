package pdm.isel.pt.yawa

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import pdm.isel.pt.yawa.cache.BitmapCache
import pdm.isel.pt.yawa.cache.TimeOutCache
import pdm.isel.pt.yawa.comms.GetRequest
import pdm.isel.pt.yawa.models.DetailWeatherDto
import pdm.isel.pt.yawa.models.ForecastWeatherDto
import pdm.isel.pt.yawa.services.WeatherForecastUpdater
import pdm.isel.pt.yawa.services.WeatherUpdater
import pdm.isel.pt.yawa.view.ForecastWeatherInfo
import pdm.isel.pt.yawa.view.WeatherInfo
import java.util.*

class YawaApplication : Application() {

    lateinit var requestQueue: RequestQueue
    lateinit var imageLoader: ImageLoader
    lateinit var currentTimeOutCache: TimeOutCache<WeatherInfo>
    lateinit var forecastTimeOutCache: TimeOutCache<ForecastWeatherInfo>
    lateinit var alarmManager: AlarmManager

    lateinit var sharedPreferences : SharedPreferences

    override fun onCreate() {
        super.onCreate()
        Log.v("YAWA_YawaApplication", "onCreated called")
        requestQueue = Volley.newRequestQueue(this)
        imageLoader = ImageLoader(requestQueue, BitmapCache())
        currentTimeOutCache = TimeOutCache()
        forecastTimeOutCache = TimeOutCache()

        sharedPreferences = getSharedPreferences(getString(R.string.shared_preference), Context.MODE_PRIVATE)

        alarmManager = (getSystemService(ALARM_SERVICE) as AlarmManager)
        initAlarms()

        Log.v("YAWA_YawaApplication", "requestQueue, imageLoader and caches initialized")
    }

    fun initAlarms(){
        val dataRefresh = sharedPreferences.getInt(getString(R.string.shared_preference_key_data_refresh), 0)

        val notifId = sharedPreferences.getInt(getString(R.string.shared_preference_key_notification_hour), 0)
        val notificationHour  = resources.getStringArray(R.array.shared_preference_notification_hour_array)[notifId].toInt()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, notificationHour)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                0,
                AlarmManager.INTERVAL_DAY,
                PendingIntent.getService(
                        this,
                        1,
                        Intent(this, WeatherForecastUpdater::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT
                )
        )

        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                0,
                AlarmManager.INTERVAL_HOUR * dataRefresh,
                PendingIntent.getService(
                        this,
                        1,
                        Intent(this, WeatherUpdater::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT
                )
        )
    }


    fun fetchCurrentWeather(city: String, function: (WeatherInfo) -> Unit) {
        if(!cityIsEmpty(city)){
            val city = parseCity(city)
            var weather = currentTimeOutCache.get(city)
            if(weather == null){
                Log.v("YAWA_MainActivity", "currentWeather object is not in cache")
                Log.v("YAWA_MainActivity", "add request for a new currentWeather")
                val uri = configureUri(city, resources.getString(R.string.api_currentWeather_config_path))
                Log.v("YAWA_MainActivity", "currentWeather configure uri: $uri")
                requestQueue.add(GetRequest(
                        uri,
                        DetailWeatherDto::class.java,
                        {info ->
                            Log.v("YAWA_MainActivity", "request for a new currentWeather returned successfully")
                            weather = WeatherInfo.dtoToCurrentWeatherInfo(city, info)
                            function(weather!!)
                        },
                        { error ->
                            handleError(error)
                        }
                ))
            }
            else
                function(weather!!)
        }
    }

    fun fetchForecastWeather(city: String, function: (ForecastWeatherInfo) -> Unit) {
        if(!cityIsEmpty(city)){
            val city = parseCity(city)
            var weather = forecastTimeOutCache.get(city)
            if(weather == null){
                Log.v("YAWA_MainActivity", "forecastWeather object is not in cache")
                Log.v("YAWA_MainActivity", "add request for a new forecastWeather")
                val uri = configureUri(city, resources.getString(R.string.api_forecastWeather_config_path))
                Log.v("YAWA_MainActivity", "forecastWeather configure uri: $uri")
                requestQueue.add(GetRequest(
                        uri,
                        ForecastWeatherDto::class.java,
                        {info ->
                            Log.v("YAWA_MainActivity", "request for a new forecastWeather returned successfully")
                            weather = ForecastWeatherInfo.dtoToForecastWeather(info)
                            function(weather!!)
                        },
                        { error -> handleError(error) }
                ))
            }
            else
                function(weather!!)
        }
    }

    private fun parseCity(city: String): String{
        return city.replace(" ", "")
    }

    private fun cityIsEmpty(city: String): Boolean{
        if(city.isEmpty()){
            Log.v("YAWA_MainActivity", "editText - city is empty")
            Toast.makeText(this, resources.getString(R.string.mainActivity_insertCity), Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }

    private fun handleError(error: VolleyError) {
        if(error.networkResponse == null)
            return
        val statusCode = error.networkResponse.statusCode
        Log.v("YAWA_MainActivity", "request for a new currentWeather returned with error -> $statusCode")
        when(statusCode / 100){
            5 -> Toast.makeText(this, "Server error $statusCode" , Toast.LENGTH_SHORT).show()
        }
    }

    private fun configureUri(city: String, configPath: String): String {
        return resources.getString(R.string.api_base_uri) +
                "$configPath=$city&${resources.getString(R.string.api_query_units)}" +
                "&${resources.getString(R.string.api_query_count)}&${resources.getString(R.string.api_query_language)}" +
                "&${resources.getString(R.string.api_key_name)}=${resources.getString(R.string.api_key_value)}"
    }
}
