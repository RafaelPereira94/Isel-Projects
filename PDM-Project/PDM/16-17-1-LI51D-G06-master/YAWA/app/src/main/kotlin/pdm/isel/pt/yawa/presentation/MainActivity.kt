package pdm.isel.pt.yawa.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import pdm.isel.pt.yawa.R
import pdm.isel.pt.yawa.YawaApplication
import pdm.isel.pt.yawa.models.content.DbSchema
import pdm.isel.pt.yawa.models.content.WeatherContract
import pdm.isel.pt.yawa.utils.WeatherUtils
import pdm.isel.pt.yawa.view.ForecastWeatherInfo
import pdm.isel.pt.yawa.view.WeatherInfo
import java.util.*

class MainActivity : BaseActivity() {

    override val layoutResId: Int = R.layout.activity_main

    override val actionBarId: Int? = R.id.toolbar

    override val actionBarMenuResId: Int? = R.menu.action_bar_activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        Log.v("YAWA_MainActivity", "onCreated called")
        val app = application as YawaApplication

        currentWeather.setOnClickListener {
            Log.v("YAWA_MainActivity", "current weather button clicked")
            val city = insertCity.text.toString()
            app.fetchCurrentWeather(city, {
                weather ->
                    app.currentTimeOutCache.put(city, weather)
                    val intent = Intent(this, WeatherInfoActivity::class.java)
                    intent.putExtra("weather", weather)
                    startActivity(intent)
            })
        }

        forecastWeather.setOnClickListener {
            Log.v("YAWA_MainActivity", "forecast weather button clicked")
            val city = insertCity.text.toString()
            app.fetchForecastWeather(city, {
                weather ->
                    app.forecastTimeOutCache.put(city, weather)
                    val intent = Intent(this, ForecastWeatherInfoActivity::class.java)
                    intent.putExtra("forecast_weather", weather)
                    startActivity(intent)
            })
        }

        favoriteCityCurr.setOnClickListener{
            Log.v("YAWA_MainActivity", "favorite city button clicked")
            val cursor = contentResolver.query(
                    WeatherContract.Weather.CONTENT_URI,
                    // select * from WeatherContract.Weather.SELECT_ALL
                    WeatherContract.Weather.SELECT_ALL,
                    // where DbSchema.COL_ID + "=?"
                    "TYPE=?",
                    // todos os ? são inseridos neste parametro
                    arrayOf(DbSchema.CURRENT_WEATHER_TYPE.toString()),
                    null)

            if(cursor.moveToNext()){
                val weatherInfo = WeatherInfo(
                        cursor.getString(cursor.getColumnIndex("COUNTRY")),
                        cursor.getString(cursor.getColumnIndex("CITY")),
                        cursor.getString(cursor.getColumnIndex("DESCRIPTION")),
                        cursor.getString(cursor.getColumnIndex("ICON")),
                        cursor.getFloat(cursor.getColumnIndex("TEMP")),
                        cursor.getFloat(cursor.getColumnIndex("MIN")),
                        cursor.getFloat(cursor.getColumnIndex("MAX")),
                        cursor.getFloat(cursor.getColumnIndex("PRESSURE")),
                        cursor.getInt(cursor.getColumnIndex("HUMIDITY")),
                        cursor.getFloat(cursor.getColumnIndex("WINDSPEED")),
                        WeatherUtils.unixTimeToRealTime(cursor.getLong(cursor.getColumnIndex("SUNRISE"))),
                        WeatherUtils.unixTimeToRealTime(cursor.getLong(cursor.getColumnIndex("SUNSET"))),
                        cursor.getInt(cursor.getColumnIndex("CLOUDINESS")),
                        cursor.getFloat(cursor.getColumnIndex("RAIN")),
                        cursor.getFloat(cursor.getColumnIndex("SNOW")),
                        cursor.getLong(cursor.getColumnIndex("DATE"))
                )
                val intent = Intent(this, WeatherInfoActivity::class.java).putExtra("weather", weatherInfo)
                startActivity(intent)
            }

        }

        favoriteCityForec.setOnClickListener{
            Log.v("YAWA_MainActivity", "favorite city button clicked")
            val cursor = contentResolver.query(
                    WeatherContract.Weather.CONTENT_URI,
                    // select * from WeatherContract.Weather.SELECT_ALL
                    WeatherContract.Weather.SELECT_ALL,
                    // where DbSchema.COL_ID + "=?"
                    "TYPE=?",
                    // todos os ? são inseridos neste parametro
                    arrayOf(DbSchema.FORECAST_WEATHER_TYPE.toString()),
                    null)

            val list = ArrayList<WeatherInfo>(5)

            while(cursor.moveToNext()){
               list.add(
                       WeatherInfo(
                        cursor.getString(cursor.getColumnIndex("COUNTRY")),
                        cursor.getString(cursor.getColumnIndex("CITY")),
                        cursor.getString(cursor.getColumnIndex("DESCRIPTION")),
                        cursor.getString(cursor.getColumnIndex("ICON")),
                        cursor.getFloat(cursor.getColumnIndex("TEMP")),
                        cursor.getFloat(cursor.getColumnIndex("MIN")),
                        cursor.getFloat(cursor.getColumnIndex("MAX")),
                        cursor.getFloat(cursor.getColumnIndex("PRESSURE")),
                        cursor.getInt(cursor.getColumnIndex("HUMIDITY")),
                        cursor.getFloat(cursor.getColumnIndex("WINDSPEED")),
                        WeatherUtils.unixTimeToRealTime(cursor.getLong(cursor.getColumnIndex("SUNRISE"))),
                        WeatherUtils.unixTimeToRealTime(cursor.getLong(cursor.getColumnIndex("SUNSET"))),
                        cursor.getInt(cursor.getColumnIndex("CLOUDINESS")),
                        cursor.getFloat(cursor.getColumnIndex("RAIN")),
                        cursor.getFloat(cursor.getColumnIndex("SNOW")),
                        cursor.getLong(cursor.getColumnIndex("DATE"))
                )
               )
            }

            val forecast = ForecastWeatherInfo("iii","iii", list)
            val intent = Intent(this, ForecastWeatherInfoActivity::class.java).putExtra("forecast_weather", forecast)
            startActivity(intent)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.action_credits -> {
            startActivity(Intent(this, CreditsActivity::class.java))
            true
        }

        R.id.action_preferences -> {
            startActivity(Intent(this, PreferencesActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}