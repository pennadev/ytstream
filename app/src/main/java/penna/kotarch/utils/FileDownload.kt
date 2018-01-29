package penna.kotarch.utils

import android.content.Context
import com.github.ajalt.timberkt.d
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Okio
import java.io.File

/**
 * Created by danpena on 8/11/17.
 */

val client: OkHttpClient = OkHttpClient.Builder().build()

fun downloadToExternal(ctx: Context, url: String, fileName: String): Observable<File> {
    val externalLocation = ctx.getExternalFilesDir(null)
    return downloadToFile(url, File("$externalLocation${File.separator}$fileName")).observeOn(Schedulers.io()).subscribeOn(Schedulers.io())
}

fun downloadToFile(url: String, file: File): Observable<File> {
    return Observable.fromCallable {
        d { "Starting download " }
        val request = Request.Builder().url(url).build()
        val body = client.newCall(request).execute().body()
        val contentLength = body?.contentLength()
        val sink = Okio.buffer(Okio.sink(file))
        val totalRead = if (body != null) sink.writeAll(body.source()) else 0
        d { "Written $totalRead bytes. Content length: $contentLength" }
        sink.flush()
        sink.close()
        file
    }
}

