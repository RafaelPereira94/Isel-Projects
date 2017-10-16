package pdm.isel.pt.yawa.models.content

import android.content.ContentResolver
import android.net.Uri
import android.provider.BaseColumns

object WeatherContract {

    const val AUTHORITY = "pdm.isel.pt.yawa"

    val CONTENT_URI: Uri? = Uri.parse("content://" + AUTHORITY)

    val MEDIA_BASE_SUBTYPE = "/vnd.weather."

    object Weather : BaseColumns {
        val RESOURCE = "weather"

        val CONTENT_URI = Uri.withAppendedPath(
                WeatherContract.CONTENT_URI,
                RESOURCE)!!

        val CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        MEDIA_BASE_SUBTYPE + RESOURCE

        val CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        MEDIA_BASE_SUBTYPE + RESOURCE

        const val COUNTRY = "COUNTRY"
        const val CITY = "CITY"
        const val DESCRIPTION = "DESCRIPTION"
        const val ICON = "ICON"
        const val TEMP = "TEMP"
        const val MIN = "MIN"
        const val MAX = "MAX"
        const val PRESSURE = "PRESSURE"
        const val HUMIDITY = "HUMIDITY"
        const val WINDSPEED = "WINDSPEED"
        const val SUNRISE = "SUNRISE"
        const val SUNSET = "SUNSET"
        const val CLOUDINESS= "CLOUDINESS"
        const val RAIN = "RAIN"
        const val SNOW = "SNOW"
        const val DATE = "DATE"
        const val TYPE = "TYPE"

        val SELECT_ALL = arrayOf(BaseColumns._ID, COUNTRY, CITY, DESCRIPTION, ICON, TEMP, MIN,
                MAX, PRESSURE, HUMIDITY, WINDSPEED, SUNRISE, SUNSET, CLOUDINESS, RAIN, SNOW, DATE, TYPE)

    }

}
