package penna.kotarch.utils

import android.content.Context
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Okio
import java.io.File

/**
 * Created by danpena on 8/11/17.
 */

val DOWNLOAD_CHUNK_SIZE = 2048L

val client: OkHttpClient = OkHttpClient.Builder().build()

fun downloadToExternal(ctx: Context, url: String, fileName: String): Observable<File> {
    val externalLocation = ctx.getExternalFilesDir(null)
    return downloadToFile(url, File("$externalLocation${File.pathSeparator}$fileName"))
}

fun downloadToFile(url: String, file: File): Observable<File> {
    return Observable.fromCallable {
        val request = Request.Builder().url(url).build()
        val body = client.newCall(request).execute().body()
        val source = body!!.source()
        val contentLength = body.contentLength()
        val sink = Okio.buffer(Okio.sink(file))

        var totalRead = 0L
        var read: Long
        do {
            read = source.read(sink.buffer(), DOWNLOAD_CHUNK_SIZE)
            totalRead += read
            val progress = (totalRead * 100 / contentLength).toInt()
//            publishProgress(progress)
        } while (read != -1L)
        sink.writeAll(source)
        sink.flush()
        sink.close()
        file
    }
}

