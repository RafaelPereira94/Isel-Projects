package pdm.isel.pt.yawa.utils

import java.sql.Time
import java.util.*

class WeatherUtils{

    companion object {
        fun unixTimeToRealTime(unixTime: Long): Time{
            val date = unixTimeToDate(unixTime)
            return Time(date.hours, date.minutes, date.seconds)
        }

        fun unixTimeToDate(unixTime: Long): Date{
            val date = Date()
            date.time = unixTime * 1000
            return date
        }
    }
}
