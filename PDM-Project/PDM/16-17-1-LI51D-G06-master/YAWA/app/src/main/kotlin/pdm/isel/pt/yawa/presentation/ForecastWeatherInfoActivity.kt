package pdm.isel.pt.yawa.presentation

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_forecast_weather_info.*
import pdm.isel.pt.yawa.R
import pdm.isel.pt.yawa.view.ForecastWeatherInfo
import pdm.isel.pt.yawa.view.TempAdapter
import pdm.isel.pt.yawa.view.WeatherInfo

class ForecastWeatherInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast_weather_info)
        Log.v("ForecastWeatherInfoAct", "onCreated called")
    }

    override fun onStart() {
        super.onStart()
        Log.v("ForecastWeatherInfoAct", "onStart called")
        Log.v("ForecastWeatherInfoAct", "get forecastWeather from intent")

        var forecastWeather = intent.getParcelableExtra<ForecastWeatherInfo>("forecast_weather")
        val i = Intent(this, WeatherInfoActivity::class.java)
        i.putExtra("city", forecastWeather.name)
        setView(forecastWeather)
    }

    private fun setView(forecastWeather: ForecastWeatherInfo) {
        Log.v("ForecastWeatherInfoAct", "initialized adapter")
        activity_forecast_weather_listView.adapter = TempAdapter(
                this,
                R.layout.activity_forecast_weather_info
        )
        val i = Intent(this, WeatherInfoActivity::class.java)
        i.putExtra("city", forecastWeather.name)

        Log.v("ForecastWeatherInfoAct", "set listItem views")
        Log.v("ForecastWeatherInfoAct", "startActivity WeatherActivity")
        activity_forecast_weather_listView.setOnItemClickListener { adapterView, view, position, id ->
            val weatherInfo = activity_forecast_weather_listView.getItemAtPosition(position) as WeatherInfo
            i.putExtra("weather", weatherInfo)
            startActivity(i)
        }

        (activity_forecast_weather_listView.adapter as TempAdapter).setData(forecastWeather.list)
    }
}
