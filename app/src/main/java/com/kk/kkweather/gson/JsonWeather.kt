package com.kk.kkweather.gson

/**
 * Created by xxnfd on 25/03/2018.
 */
import com.google.gson.annotations.SerializedName

data class Basic(@SerializedName("city")
                 val city: String = "",
                 @SerializedName("admin_area")
                 val adminArea: String = "",
                 @SerializedName("tz")
                 val tz: String = "",
                 @SerializedName("update")
                 val update: Update,
                 @SerializedName("location")
                 val location: String = "",
                 @SerializedName("lon")
                 val lon: String = "",
                 @SerializedName("parent_city")
                 val parentCity: String = "",
                 @SerializedName("id")
                 val id: String = "",
                 @SerializedName("cnty")
                 val cnty: String = "",
                 @SerializedName("lat")
                 val lat: String = "",
                 @SerializedName("cid")
                 val cid: String = "")


data class DailyForecastItem(@SerializedName("date")
                             val date: String = "",
                             @SerializedName("tmp")
                             val tmp: Tmp,
                             @SerializedName("cond")
                             val cond: Cond)


data class Cond(@SerializedName("txt_d")
                val txtD: String = "")

data class CondNow(@SerializedName("code")
                   val code: String = "",
                   @SerializedName("txt")
                   val txt: String = "")

data class Comf(@SerializedName("txt")
                val txt: String = "",
                @SerializedName("brf")
                val brf: String = "",
                @SerializedName("type")
                val type: String = "")


data class HeWeatherItem(@SerializedName("now")
                         val now: Now,
                         @SerializedName("suggestion")
                         val suggestion: Suggestion,
                         @SerializedName("aqi")
                         val aqi: Aqi,
                         @SerializedName("update")
                         val update: Update,
                         @SerializedName("basic")
                         val basic: Basic,
                         @SerializedName("daily_forecast")
                         val dailyForecast: List<DailyForecastItem>?,
                         @SerializedName("status")
                         val status: String = "")


data class City(@SerializedName("pm25")
                val pm: String = "",
                @SerializedName("qlty")
                val qlty: String = "",
                @SerializedName("aqi")
                val aqi: String = "")


data class Update(@SerializedName("loc")
                  val loc: String = "",
                  @SerializedName("utc")
                  val utc: String = "")


data class Suggestion(@SerializedName("cw")
                      val cw: Cw,
                      @SerializedName("comf")
                      val comf: Comf,
                      @SerializedName("sport")
                      val sport: Sport)


data class Sport(@SerializedName("txt")
                 val txt: String = "",
                 @SerializedName("brf")
                 val brf: String = "",
                 @SerializedName("type")
                 val type: String = "")


data class Cw(@SerializedName("txt")
              val txt: String = "",
              @SerializedName("brf")
              val brf: String = "",
              @SerializedName("type")
              val type: String = "")


data class JsonWeather(@SerializedName("HeWeather")
                       val heWeather: List<HeWeatherItem>?)


data class Tmp(@SerializedName("min")
               val min: String = "",
               @SerializedName("max")
               val max: String = "")


data class Now(@SerializedName("hum")
               val hum: String = "",
               @SerializedName("vis")
               val vis: String = "",
               @SerializedName("pres")
               val pres: String = "",
               @SerializedName("pcpn")
               val pcpn: String = "",
               @SerializedName("fl")
               val fl: String = "",
               @SerializedName("wind_sc")
               val windSc: String = "",
               @SerializedName("wind_dir")
               val windDir: String = "",
               @SerializedName("cond")
               val cond: CondNow,
               @SerializedName("wind_spd")
               val windSpd: String = "",
               @SerializedName("cloud")
               val cloud: String = "",
               @SerializedName("wind_deg")
               val windDeg: String = "",
               @SerializedName("tmp")
               val tmp: String = "",
               @SerializedName("cond_txt")
               val condTxt: String = "",
               @SerializedName("cond_code")
               val condCode: String = "")


data class Aqi(@SerializedName("city")
               val city: City)