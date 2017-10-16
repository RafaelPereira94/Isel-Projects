package pdm.isel.pt.yawa.cache

import android.graphics.Bitmap
import android.util.Log
import com.android.volley.toolbox.ImageLoader
import java.util.*


class BitmapCache : ImageLoader.ImageCache {
    private val NUMBER_OF_ICONS = 18

    //String -> URL ; Bitmap -> ICON
    private val cache = HashMap<String, Bitmap>(NUMBER_OF_ICONS)

    override fun getBitmap(url: String): Bitmap?{
        Log.v("BitmapCache", "bitmap fetched from cache")
        return cache[url]
    }

    override fun putBitmap(url: String, bitmap: Bitmap) {
        Log.v("BitmapCache", "bitmap was put in cache")
        cache.put(url, bitmap)
    }
}
