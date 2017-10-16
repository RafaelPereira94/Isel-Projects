package pdm.isel.pt.yawa.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import pdm.isel.pt.yawa.R

class SplashActivity : BaseActivity() {

    override val layoutResId: Int = R.layout.activity_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Log.v("YAWA_SplashActivity", "onCreated called")

        val handler = Handler()
        handler.postDelayed({
            Log.v("YAWA_SplashActivity", "startActivity MainActivity")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 0)

    }

}
