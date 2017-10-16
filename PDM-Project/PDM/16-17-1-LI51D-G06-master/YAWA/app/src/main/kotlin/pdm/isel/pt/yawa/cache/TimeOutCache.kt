package pdm.isel.pt.yawa.cache

import android.util.Log
import android.util.LruCache

class TimeOutCache<T> {
    private val MAX = 20
    private val timeOut = 3600000 //1 hour

    //String -> city; T -> object
    private val cache = LruCache<String, T>(MAX)
    //String -> city; Long -> time of object creation
    private val composedKeys = LruCache<String, Long>(MAX)

    fun get(city: String): T?{
        val obj = cache[city]

        if(obj == null){
            Log.v("YAWA_timeoutCache", "object not fetched from cache")
            return obj
        }
        if((System.currentTimeMillis() - composedKeys[city]) > timeOut){
            Log.v("YAWA_timeoutCache", "object in cache timed out, has been removed")
            cache.remove(city)
            composedKeys.remove(city)
            return null
        }
        Log.v("YAWA_timeoutCache", "object fetched from cache")
        return cache[city]
    }

    fun put(city: String, weather: T) {
        Log.v("YAWA_timeoutCache", "object was put in cache")
        composedKeys.put(city, System.currentTimeMillis())
        cache.put(city, weather)
    }

    fun remove(city: String){
        cache.remove(city)
        composedKeys.remove(city)
    }
}