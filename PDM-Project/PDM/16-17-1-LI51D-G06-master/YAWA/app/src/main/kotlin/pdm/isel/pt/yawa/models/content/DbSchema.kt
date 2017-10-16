package pdm.isel.pt.yawa.models.content

import android.provider.BaseColumns

object DbSchema {

    val DB_NAME = "yawa.db"
    val DB_VERSION = 1

    val COL_ID = BaseColumns._ID

    val CURRENT_WEATHER_TYPE = 1
    val FORECAST_WEATHER_TYPE = 2

    class Weather {

        companion object {

            val TBL_NAME = "weather"

            const val COLUMN_COUNTRY = "COUNTRY"
            const val COLUMN_CITY = "CITY"
            const val COLUMN_DESCRIPTION = "DESCRIPTION"
            const val COLUMN_ICON = "ICON"
            const val COLUMN_TEMP = "TEMP"
            const val COLUMN_MIN = "MIN"
            const val COLUMN_MAX = "MAX"
            const val COLUMN_PRESSURE = "PRESSURE"
            const val COLUMN_HUMIDITY = "HUMIDITY"
            const val COLUMN_WINDSPEED = "WINDSPEED"
            const val COLUMN_SUNRISE = "SUNRISE"
            const val COLUMN_SUNSET = "SUNSET"
            const val COLUMN_CLOUDINESS = "CLOUDINESS"
            const val COLUMN_RAIN = "RAIN"
            const val COLUMN_SNOW = "SNOW"
            const val COLUMN_DATE = "DATE"
            const val COLUMN_TYPE = "TYPE"

            val DDL_CREATE_TABLE =
                    "CREATE TABLE " + TBL_NAME +
                            "(" +
                            COL_ID + " INTEGER PRIMARY KEY, " +
                            COLUMN_COUNTRY + " TEXT, " +
                            COLUMN_CITY + " TEXT, " +
                            COLUMN_DESCRIPTION + " TEXT, " +
                            COLUMN_ICON + " INTEGER, " +
                            COLUMN_TEMP + " REAL, " +
                            COLUMN_MIN + " REAL, " +
                            COLUMN_MAX + " REAL, " +
                            COLUMN_PRESSURE + " REAL, " +
                            COLUMN_HUMIDITY + " INTEGER, " +
                            COLUMN_WINDSPEED + " REAL, " +
                            COLUMN_SUNRISE + " INTEGER, " +
                            COLUMN_SUNSET + " INTEGER, " +
                            COLUMN_CLOUDINESS + " INTEGER, " +
                            COLUMN_RAIN + " REAL, " +
                            COLUMN_SNOW + " REAL, " +
                            COLUMN_DATE + " INTEGER, " +
                            COLUMN_TYPE + " INTEGER " +
                            ")"

            val DDL_DROP_TABLE = "DROP TABLE IF EXISTS " + TBL_NAME
        }

    }
}