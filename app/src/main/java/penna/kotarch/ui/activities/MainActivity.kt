package penna.kotarch.ui.activities

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.api.services.youtube.model.Playlist
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import penna.kotarch.R
import penna.kotarch.SearchViewModel
import penna.kotarch.getApp
import penna.kotarch.models.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val songDao = getApp(this).db!!.songDao()
        val playlistDao = getApp(this).db!!.playlistDao()
        val playlistSongDao = getApp(this).db!!.playlistsSongsDao()

        insert.setOnClickListener {
            insertSong(songDao)
        }

        insertPlaylist.setOnClickListener {
            insertPlaylist(playlistDao)
        }


        songDao.getAllSongs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {

                })

        playlistDao.getAllPlaylists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    var message = ""
                    for (i in it) {
                        message += "\n" + "$i"
                    }
                    toast(message)
                }

        playlistSongDao.getSongsForPlaylist("playlist1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(toastSongs())


        val viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
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
                    getApp(this).db!!.playlistsSongsDao().insert(PlaylistsSongs(
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
