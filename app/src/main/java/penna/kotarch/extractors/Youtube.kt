package penna.kotarch.extractors

import android.content.Context
import android.util.SparseArray
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import io.reactivex.*
import io.reactivex.rxkotlin.toSingle
import io.reactivex.subjects.PublishSubject
import penna.kotarch.utils.convertToList

/**
 * Created by danpena on 7/25/17.
 */

class Youtube(ctx: Context) : YouTubeExtractor(ctx) {

    data class YoutubeExtract(val ytFile: SparseArray<YtFile>, val metaData: VideoMeta?)


    private val extractionSubject: PublishSubject<YoutubeExtract> = PublishSubject.create<YoutubeExtract>()

    override fun onExtractionComplete(p0: SparseArray<YtFile>?, p1: VideoMeta?) {
        when {
            p1 == null -> System.err.println("File was null")
            p0 == null -> System.err.println("Meta was null")
            else -> extractionSubject.onNext(YoutubeExtract(p0, p1))
        }
    }

    fun extract(id: String): Flowable<YoutubeExtract> {
        this.extract(id, true, false)
        return extractionSubject.toFlowable(BackpressureStrategy.LATEST)
    }
}

data class Stream(val url: String = "", val ext: String = "")

//https://en.wikipedia.org/w/index.php?title=YouTube&oldid=800910021#Quality_and_formats
val itagList = listOf(
        139, // AAC 48kbps
        249, // Opus 48kbps
        250, // Opus 64kbps
        140, // AAC 128kbps
        171, // Vorbis 128kbps
        251  // Opus 160kbps
)


fun getBestStream(ytFile: SparseArray<YtFile>): Stream {
    val list = convertToList(ytFile)

    val maxBy = list.maxBy {
        itagList.indexOf(it.format.itag)
    }

    return Stream(maxBy?.url ?: "", maxBy?.format?.ext ?: "")
}