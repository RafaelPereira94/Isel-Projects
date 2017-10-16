package pdm.isel.pt.yawa.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_weather.*
import pdm.isel.pt.yawa.R
import pdm.isel.pt.yawa.YawaApplication
import pdm.isel.pt.yawa.view.WeatherInfo
import java.util.*

class WeatherInfoActivity : BaseActivity() {

    override val layoutResId: Int = R.layout.activity_weather

    override val actionBarId: Int? = R.id.toolbar

    override val actionBarMenuResId: Int? = R.menu.action_bar_activity_weather_info

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("YAWA_WeatherInfoAct", "onCreated called")
    }

    override fun onStart() {
        super.onStart()
        Log.v("YAWA_WeatherInfoAct", "onStart called")
        Log.v("YAWA_WeatherInfoAct", "get currentWeather from intent")

        var weather = intent.getParcelableExtra<WeatherInfo>("weather")
        activity_weather_views_layout.removeAllViews()
        createTextViews(setView(weather))
        fetchAndSetIcon(weather.icon)
    }

    private fun setView(w: WeatherInfo): ArrayList<String> {

        var weatherList = ArrayList<String>()

        if(w.country != "" && w.city != "")
            weatherList.add(w.country + "," + w.city)
        weatherList.add(w.description)
        weatherList.add(resources.getString(R.string.activity_weather_temperatureLabel) +
                w.temp +
                resources.getString(R.string.weather_temperatureUnit))

        if(w.min != -1F)
            weatherList.add(
                    resources.getString(R.string.activity_weather_tempMinLabel) +
                    w.min +
                    resources.getString(R.string.weather_temperatureUnit)
            )
        if(w.max != -1F)
            weatherList.add(
                    resources.getString(R.string.activity_weather_tempMaxLabel) +
                    w.max +
                    resources.getString(R.string.weather_temperatureUnit)
            )
        weatherList.add(
                resources.getString(R.string.activity_weather_pressureLabel) +
                w.pressure +
                resources.getString(R.string.weather_pressureUnit)
        )
        weatherList.add(
                resources.getString(R.string.activity_weather_humidityLabel) +
                w.humidity +
                resources.getString(R.string.weather_humidityUnit)
        )
        weatherList.add(
                resources.getString(R.string.activity_weather_windSpeedLabel) +
                w.windSpeed +
                resources.getString(R.string.weather_windSpeedUnit)
        )
        if(w.sunrise != null)
            weatherList.add(
                    resources.getString(R.string.activity_weather_sunriseLabel) +
                    w.sunrise
            )
        if(w.sunset != null)
            weatherList.add(
                    resources.getString(R.string.activity_weather_sunsetLabel) +
                    w.sunset
            )
        weatherList.add(
                resources.getString(R.string.activity_weather_cloudsLabel) +
                w.cloudiness +
                resources.getString(R.string.weather_cloudsUnit)
        )
        if(w.rain != -1F)
            weatherList.add(
                    resources.getString(R.string.activity_weather_rainLabel) +
                    w.rain +
                    resources.getString(R.string.weather_rainUnit)
            )
        if(w.snow != -1F)
            weatherList.add(
                    resources.getString(R.string.activity_weather_snowLabel) +
                    w.snow +
                    resources.getString(R.string.weather_snowUnit)
            )
        return weatherList
    }

    private fun createTextViews(list: List<String>){
        val layout = activity_weather_views_layout
        layout.orientation = LinearLayout.VERTICAL

        list.forEach { str ->
            val tv = TextView(this)
            tv.text = str
            tv.textSize = 25F
            layout.addView(tv)
        }
    }
    private fun fetchAndSetIcon(icon: String) {
        Log.v("YAWA_WeatherInfoAct", "fetch icon")
        activity_weather_icon.setImageUrl(
                "${resources.getString(R.string.api_icon_uri)}$icon${resources.getString(R.string.api_icon_extension)}",
                (application as YawaApplication).imageLoader
        )
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.action_refresh -> {
            val app = application as YawaApplication

            var currWeather = intent.getParcelableExtra<WeatherInfo>("weather")

            Log.v("YAWA_WeatherInfoAct", "refresh button clicked")
            val city = currWeather.city
            Log.v("YAWA_WeatherInfoAct", "weather info removed from cache")
            app.currentTimeOutCache.remove(city)
            app.fetchCurrentWeather(city, {
                weather ->
                app.currentTimeOutCache.put(city, weather)
                activity_weather_views_layout.removeAllViews()
                createTextViews(setView(weather))
                fetchAndSetIcon(weather.icon)
            })
            true
        }

        R.id.action_credits -> {
            startActivity(Intent(this, CreditsActivity::class.java))
            true
        }

        R.id.action_home -> {
            startActivity(Intent(this, MainActivity::class.java))
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
}
