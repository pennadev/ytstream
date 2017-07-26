package penna.kotarch.extractors

import android.content.Context
import android.util.SparseArray
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by danpena on 7/25/17.
 */

class Youtube(ctx: Context) : YouTubeExtractor(ctx) {

    data class YoutubeExtract(val ytFile: SparseArray<YtFile>, val metaData: VideoMeta)


    val extractionSubject: PublishSubject<YoutubeExtract> = PublishSubject.create<YoutubeExtract>()

    override fun onExtractionComplete(p0: SparseArray<YtFile>?, p1: VideoMeta?) {
        when {
            p1 == null -> System.err.println("File was null")
            p0 == null -> System.err.println("Meta was null")
            true -> extractionSubject.onNext(YoutubeExtract(p0, p1))
        }
    }

    fun extract(id: String): Observable<YoutubeExtract> {
        this.extract(id, true, false)
        return extractionSubject
    }

}

