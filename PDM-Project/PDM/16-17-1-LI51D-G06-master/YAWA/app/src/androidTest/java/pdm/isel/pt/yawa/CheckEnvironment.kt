package pdm.isel.pt.yawa

import android.content.Context
import android.content.pm.PackageManager
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class CheckEnvironment {

    private lateinit var targetContext: Context

    @Before
    fun prepare(){
        //context of the app under test
        targetContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    @Throws(Exception::class)
    fun test_useAppContext() {
        assertEquals(APP_PACKAGE, targetContext.packageName)
    }

    @Test
    fun test_checkPermissions() {
        assertEquals(
                PackageManager.PERMISSION_GRANTED,
                targetContext.packageManager.checkPermission(INTERNET_PERMISSION, APP_PACKAGE)
        )
    }
}
