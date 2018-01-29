package penna.kotarch.ui.services


import android.content.Context
import at.huber.youtubeExtractor.YtFile
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import penna.kotarch.extractors.Stream
import penna.kotarch.extractors.Youtube
import penna.kotarch.extractors.getBestStream
import penna.kotarch.models.Song

//acts as a reducer, tacking playback events and outputs state
//handles presistance of playback state
class MediaPlaybackController {
    private val playStream: PublishSubject<PlayingState> = PublishSubject.create()

    fun observePlayState(): Flowable<PlayingState> {
        return playStream.toFlowable(BackpressureStrategy.LATEST)
    }

    fun togglePause() {

    }

    fun stop() {

    }

    fun enqueSong() {

    }

    fun startPlayingSong(song: Song) {

    }

    fun extractAndPlay(context: Context, id: String) {
        //TODO: Get rid of this context dependency nonsense
        var disposable: Disposable? = null
        disposable = Youtube(context).extract(id)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({
                    disposable?.dispose()


                    startPlayingSong(getBestStream(it.ytFile))
                }, {
                    disposable?.dispose()
                })

    }

}

fun songFromYtExtract(it: Youtube.YoutubeExtract) {

}

data class PlayingState(
        var playing: Boolean = false,
        var title: String = "",
        var stream: Stream? = null,
        var currentSong: Song? = null
)
