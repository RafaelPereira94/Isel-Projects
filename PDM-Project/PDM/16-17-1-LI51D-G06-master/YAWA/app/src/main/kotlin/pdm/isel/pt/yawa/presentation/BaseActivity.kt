package pdm.isel.pt.yawa.presentation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import pdm.isel.pt.yawa.R

abstract class BaseActivity : AppCompatActivity() {

    protected abstract val layoutResId: Int

    protected open val actionBarId: Int? = null

    protected open val actionBarMenuResId: Int? = null

    private fun initContents() {
        setContentView(layoutResId)
        actionBarId?.let {
            setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initContents()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        actionBarMenuResId?.let {
            menuInflater.inflate(it, menu)
            return true
        }

        return super.onCreateOptionsMenu(menu)
    }
}
