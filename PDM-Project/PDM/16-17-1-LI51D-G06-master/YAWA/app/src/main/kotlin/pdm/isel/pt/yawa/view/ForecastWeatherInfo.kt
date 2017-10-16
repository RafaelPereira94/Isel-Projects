package pdm.isel.pt.yawa.view

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import pdm.isel.pt.yawa.models.ForecastWeatherDto
import java.sql.Time
import java.util.*

data class ForecastWeatherInfo(val name: String,
                               val country: String,
                               val list: List<WeatherInfo>) : Parcelable {

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<ForecastWeatherInfo> = object : Parcelable.Creator<ForecastWeatherInfo> {
            override fun createFromParcel(source: Parcel): ForecastWeatherInfo = ForecastWeatherInfo(source)
            override fun newArray(size: Int): Array<ForecastWeatherInfo?> = arrayOfNulls(size)
        }

        fun dtoToForecastWeather(info: ForecastWeatherDto): ForecastWeatherInfo {
            Log.v("ForecastWeatherInfo", "map a forecastWeatherDto to forecastWeatherInfo")

            val weatherListDto = info.list as ArrayList
            val weatherList = ArrayList<WeatherInfo>()

            for (i in weatherListDto.indices) {
                val w = weatherListDto[i].weather as ArrayList

                var snow = -1F
                if(weatherListDto[i].snow != null)
                    snow = weatherListDto[i].snow as Float
                var rain = -1F
                if(weatherListDto[i].rain != null)
                    rain = weatherListDto[i].rain as Float
                weatherList.add(
                        WeatherInfo(
                                "",
                                "",
                                w[0].description,
                                w[0].icon,
                                weatherListDto[i].temp.day,
                                weatherListDto[i].temp.min,
                                weatherListDto[i].temp.max,
                                weatherListDto[i].pressure,
                                weatherListDto[i].humidity,
                                weatherListDto[i].speed,
                                Time(-1),
                                Time(-1),
                                weatherListDto[i].clouds,
                                rain,
                                snow,
                                weatherListDto[i].dt
                        )
                )
            }
            return ForecastWeatherInfo(
                    info.city.name,
                    info.city.country,
                    weatherList
            )
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.createTypedArrayList(WeatherInfo.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(country)
        dest.writeTypedList(list)
    }
}
