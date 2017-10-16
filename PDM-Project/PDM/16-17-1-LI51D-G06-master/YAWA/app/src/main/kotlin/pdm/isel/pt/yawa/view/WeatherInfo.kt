package pdm.isel.pt.yawa.view

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import pdm.isel.pt.yawa.models.DetailWeatherDto
import pdm.isel.pt.yawa.utils.WeatherUtils
import java.sql.Time
import java.util.*

data class WeatherInfo(
        val country: String,
        val city: String,
        val description: String,
        val icon: String,
        val temp: Float,
        val min: Float,
        val max: Float,
        val pressure: Float,
        val humidity: Int,
        val windSpeed: Float,
        val sunrise: Time, //real Time
        val sunset: Time, //real Time
        val cloudiness: Int,
        val rain: Float,
        val snow: Float,
        val date: Long) : Parcelable{

    companion object{
        @JvmField @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<WeatherInfo>{
            override fun createFromParcel(source: Parcel) = WeatherInfo(source)
            override fun newArray(size: Int): Array<WeatherInfo?> = arrayOfNulls(size)
        }

        fun dtoToCurrentWeatherInfo(city: String, info: DetailWeatherDto): WeatherInfo {
            Log.v("CurrentWeatherInfo", "map a currentWeatherDto to CurrentWeatherInfo")
            val w = info.weather as ArrayList

            var snow = -1F
            if(info.snow?.three_hours != null)
                snow = info.snow?.three_hours
            var rain = -1F
            if(info.rain?.three_hours != null)
                rain = info.rain?.three_hours

            return WeatherInfo(
                    info.sys.country,
                    city,
                    w[0].description,
                    w[0].icon,
                    info.main.temp,
                    -1F,
                    -1F,
                    info.main.pressure,
                    info.main.humidity,
                    info.wind.speed,
                    WeatherUtils.unixTimeToRealTime(info.sys.sunrise),
                    WeatherUtils.unixTimeToRealTime(info.sys.sunset),
                    info.clouds.all,
                    rain,
                    snow,
                    -1
            )
        }
    }

    //convert date value to long with GetDate() possible and use readLong().
    constructor(source : Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readFloat(),
            source.readFloat(),
            source.readFloat(),
            source.readFloat(),
            source.readInt(),
            source.readFloat(),
            source.readSerializable() as Time,
            source.readSerializable() as Time,
            source.readInt(),
            source.readFloat(),
            source.readFloat(),
            source.readLong()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(country)
        dest.writeString(city)
        dest.writeString(description)
        dest.writeString(icon)
        dest.writeFloat(temp)
        dest.writeFloat(min)
        dest.writeFloat(max)
        dest.writeFloat(pressure)
        dest.writeInt(humidity)
        dest.writeFloat(windSpeed)
        dest.writeSerializable(sunrise)
        dest.writeSerializable(sunset)
        dest.writeInt(cloudiness)
        dest.writeFloat(rain)
        dest.writeFloat(snow)
        dest.writeLong(date)
    }
}