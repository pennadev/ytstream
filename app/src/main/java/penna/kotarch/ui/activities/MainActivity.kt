package penna.kotarch.ui.activities

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import penna.kotarch.R
import penna.kotarch.SearchViewModel
import penna.kotarch.getApp
import penna.kotarch.models.*

fun Disposable.addDisposable(compoisteDisposable: CompositeDisposable) {
    compoisteDisposable.add(this)
}

class MainActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)

    }

    private val songDao: SongDao
        get() {
            return getApp(this).db.songDao()
        }

    private val playlistDao: PlaylistDao
        get() {
            return getApp(this).db.playlistDao()
        }

    private val playlistsSongsDao: PlaylistsSongsDao
        get() {
            return getApp(this).db.playlistsSongsDao()
        }

    override fun onResume() {
        super.onResume()

        songDao.getAllSongs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {

                }).addTo(onPauseDispose)

        playlistDao.getAllPlaylists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    var message = ""
                    for (i in it) {
                        message += "\n" + "$i"
                    }
                    toast(message)
                }.addTo(onPauseDispose)

        playlistsSongsDao.getSongsForPlaylist("playlist1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(toastSongs()).addTo(onPauseDispose)

        insert.setOnClickListener {
            insertSong(songDao)
        }

        insertPlaylist.setOnClickListener {
            insertPlaylist(playlistDao)
        }
    }

    private fun toastSongs(): (List<Song>) -> Unit {
        return {
            var message = ""
            for (i in it) {
                message += "\n" + "$i"
            }
            toast(message)
        }
    }

    private fun insertSong(songDao: SongDao) {
        fun insertLambda() = songDao
                .insert(Song(input_id.text.toString(),
                        input_title.text.toString(),
                        input_path.text.toString()))

        Single.fromCallable(::insertLambda)
                .map {
                    getApp(this).db.playlistsSongsDao().insert(PlaylistsSongs(
                            input_id.text.toString(),
                            "playlist1"
                    ))
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }


    private fun insertPlaylist(playlistDao: PlaylistDao) {
        fun insertPlaylistInt() = playlistDao.insert(Playlist(input_playlist.text.toString()))

        Single.fromCallable(::insertPlaylistInt)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()

    }

}
