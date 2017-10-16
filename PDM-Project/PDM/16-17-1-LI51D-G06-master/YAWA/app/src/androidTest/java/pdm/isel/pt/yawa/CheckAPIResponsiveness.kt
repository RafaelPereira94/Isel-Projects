package pdm.isel.pt.yawa
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.core.deps.guava.base.Strings
import android.support.test.runner.AndroidJUnit4
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import pdm.isel.pt.yawa.comms.GetRequest
import pdm.isel.pt.yawa.models.DetailWeatherDto
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.fail

@RunWith(AndroidJUnit4::class)
class CheckAPIResponsiveness{

    private lateinit var requestQueue: RequestQueue

    // Synchronization between test harness thread and callbacks thread
    private lateinit var latch: CountDownLatch
    private var error: AssertionError? = null

    private fun waitForCompletion() {
        try {
            if (latch.await(60, TimeUnit.SECONDS)) {
                if (error != null)
                    throw error as AssertionError
            } else {
                fail("Test harness thread timeout while waiting for completion")
            }
        } catch (_: InterruptedException) {
            fail("Test harness thread was interrupted")
        }
    }

    private fun executeAndPublishResult(assertions: () -> Unit ) {
        try {
            assertions()
        } catch (error: AssertionError) {
            this.error = error
        } finally {
            latch.countDown()
        }
    }

    @Before
    fun prepare() {
        // Preparing Volley's request queue
        requestQueue = Volley.newRequestQueue(InstrumentationRegistry.getTargetContext())
        requestQueue.cache.clear()
        // Preparing test harness thread synchronization artifacts
        latch = CountDownLatch(1)
        error = null
    }

    @Test
    fun test_checkAPIResponsiveness() {

        requestQueue.add(
                StringRequest(
                        Request.Method.GET,
                        WEATHER_URL,
                        { response -> executeAndPublishResult { assertFalse(Strings.isNullOrEmpty(response)) } },
                        { error -> executeAndPublishResult { assertNotNull(error.networkResponse) } }
                )
        )

        waitForCompletion()
    }

    @Test
    fun test_successfulResponseParsing() {
        requestQueue.add(
                GetRequest(
                    WEATHER_URL, DetailWeatherDto::class.java,
                    { weather -> executeAndPublishResult { assertNotNull(weather) } },
                    { error -> executeAndPublishResult { fail() } }
                )
        )
        waitForCompletion()
    }
}