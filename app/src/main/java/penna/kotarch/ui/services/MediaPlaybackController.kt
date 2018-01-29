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
    var currentState = PlayingState()


    fun observePlayState(): Flowable<PlayingState> {
        return playStream
                .doOnNext {
                    currentState = it
                }
                .toFlowable(BackpressureStrategy.LATEST).share()
    }

    fun togglePause() {
        playStream.onNext(currentState.copy(playing = !currentState.playing))
    }

    fun stop() {
        playStream.onNext(currentState.copy(playing = false))
    }

    fun enqueSong() {

    }

    fun startPlayingSong(song: Song) {
        playStream.onNext(PlayingState(
                playing = true,
                currentSong = song
        ))
    }

    fun extractAndPlay(context: Context, id: String) {
        //TODO: Get rid of this context dependency nonsense
        var disposable: Disposable? = null
        disposable = Youtube(context).extract(id)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({
                    startPlayingSong(songFromYtExtract(it))
                    disposable?.dispose()
                }, {
                    it.printStackTrace()
                    disposable?.dispose()
                })
    }
}

fun songFromYtExtract(it: Youtube.YoutubeExtract): Song {
    return Song(songId = it.metaData?.videoId ?: "", title = it.metaData?.title ?: "", url = getBestStream(it.ytFile).url)

}

data class PlayingState(
        var playing: Boolean = false,
        var currentSong: Song? = null
)
