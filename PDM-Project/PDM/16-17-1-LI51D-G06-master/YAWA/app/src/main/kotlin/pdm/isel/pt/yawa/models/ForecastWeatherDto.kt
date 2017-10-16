package pdm.isel.pt.yawa.models

data class ForecastWeatherDto(val city : City,
                              val list : Collection<List>){

    data class City(val name : String,
                    val country : String)

    data class List(val temp : Temp,
                    val pressure : Float,
                    val humidity : Int,
                    val weather : Collection<WeatherDto>,
                    val speed : Float,
                    val clouds : Int,
                    val rain : Float?,
                    val snow : Float?,
                    val dt : Long)

    data class Temp(val day : Float,
                    val min : Float,
                    val max : Float)

}