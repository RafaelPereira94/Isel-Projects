package pdm.isel.pt.yawa.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.toolbox.NetworkImageView
import kotlinx.android.synthetic.main.list_item_forecast.view.*
import pdm.isel.pt.yawa.R
import pdm.isel.pt.yawa.YawaApplication
import pdm.isel.pt.yawa.utils.WeatherUtils
import java.text.SimpleDateFormat

class TempAdapter(context: Context, layout: Int) : ArrayAdapter<WeatherInfo>(context, layout) {

    private val inflater: LayoutInflater? = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private inner class Holder {
        internal lateinit var textView: TextView
        internal lateinit var icon: ImageView
    }

    fun setData(data: List<WeatherInfo>){
        this.addAll(data)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        Log.v("TempAdapter", "set views")
        val holder = Holder()
        val rowView = inflater!!.inflate(R.layout.list_item_forecast, null)
        println(rowView)

        holder.textView = rowView?.list_item_forecast_date as TextView
        (holder.textView).text = SimpleDateFormat("dd/MM/yyyy").format(WeatherUtils.unixTimeToDate(this.getItem(position).date))

        holder.textView = rowView?.list_item_forecast_tempMin as TextView
        (holder.textView).text = "${this.getItem(position).min} ${context.getString(R.string.weather_temperatureUnit)}"

        holder.textView = rowView?.findViewById(R.id.list_item_forecast_tempMax) as TextView
        (holder.textView).text = "${this.getItem(position).max} ${context.getString(R.string.weather_temperatureUnit)}"

        holder.icon = rowView?.findViewById(R.id.list_item_forecast_imageView) as NetworkImageView

        fetchAndSetIcon(this.getItem(position).icon, holder.icon as NetworkImageView)

        return rowView
    }

    private fun fetchAndSetIcon(icon: String, img: NetworkImageView) {
        Log.v("TempAdapter", "fecth icon")
        img.setImageUrl(
                "${context.getString(R.string.api_icon_uri)}$icon${context.getString(R.string.api_icon_extension)}",
                (context.applicationContext as YawaApplication).imageLoader
        )
    }
}