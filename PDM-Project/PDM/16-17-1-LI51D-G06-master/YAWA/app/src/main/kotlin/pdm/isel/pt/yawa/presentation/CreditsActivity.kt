package pdm.isel.pt.yawa.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_credits.*
import pdm.isel.pt.yawa.R

class CreditsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)

        Log.v("YAWA_CreditsActivity", "onCreate called")

        author1_image.setOnClickListener {
            Log.v("YAWA_CreditsActivity", "author1 image clicked")
            Log.v("YAWA_CreditsActivity", "open browser to site")
            val url = Uri.parse(resources.getString(R.string.credits_data_source_gitURL_authors1))
            startActivity(Intent(Intent.ACTION_VIEW, url))
        }

        author2_image.setOnClickListener {
            Log.v("YAWA_CreditsActivity", "author2 image clicked")
            Log.v("YAWA_CreditsActivity", "open browser to site")
            val url = Uri.parse(resources.getString(R.string.credits_data_source_gitURL_authors2))
            startActivity(Intent(Intent.ACTION_VIEW, url))
        }

        author3_image.setOnClickListener {
            Log.v("YAWA_CreditsActivity", "author3 image clicked")
            Log.v("YAWA_CreditsActivity", "open browser to site")
            val url = Uri.parse(resources.getString(R.string.credits_data_source_gitURL_authors3))
            startActivity(Intent(Intent.ACTION_VIEW, url))
        }

        open_weather_map_image.setOnClickListener {
            Log.v("YAWA_CreditsActivity", "api image clicked")
            Log.v("YAWA_CreditsActivity", "open browser to site")
            val url = Uri.parse(resources.getString(R.string.credits_data_source_open_weather))
            startActivity(Intent(Intent.ACTION_VIEW, url))
        }

    }
}
