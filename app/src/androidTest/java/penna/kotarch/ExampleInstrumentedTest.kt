package penna.kotarch

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import io.reactivex.Observable
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import penna.kotarch.extractors.Youtube
import penna.kotarch.utils.YoutubeSearch
import penna.kotarch.utils.convertToList

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("penna.kotarch", appContext.packageName)
        val ext = Youtube(appContext)
        val extract = ext.extract("wcbtofyeLfA")
        val blockingFirst = extract.blockingFirst()
        val list = convertToList(blockingFirst.ytFile)
        list.forEach { it -> System.out.println(it.url) }
        System.out.println(list.size)

        val s = YoutubeSearch()

        val blockingFirst1 = Observable.fromCallable { s.search("argatu") }.blockingSubscribe {
            System.out.println("HELO??!")
            System.out.println(it)
        }
    }
}
