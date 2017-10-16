package pdm.isel.pt.yawa.models

import com.fasterxml.jackson.annotation.JsonProperty

data class DetailWeatherDto(val weather : Collection<WeatherDto>, //only with 1 object
                            val main : Main,
                            val wind : Wind,
                            val rain : Rain?,
                            val clouds : Clouds,
                            val snow : Snow?,
                            val sys : Sys)
{

    data class Main(val temp : Float,
                    val pressure : Float,
                    val humidity : Int)

    data class Wind(val speed : Float)

    data class Rain(@JsonProperty("3h") val three_hours : Float?)

    data class Clouds(val all : Int)

    data class Snow(@JsonProperty("3h") val three_hours: Float?)

    data class Sys (val sunrise : Long, //unixTime
                    val sunset : Long, //unixTime
                    val country : String)
}