package penna.kotarch.ui.activities

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import penna.kotarch.R
import penna.kotarch.SearchViewModel
import penna.kotarch.getApp
import penna.kotarch.models.Song
import penna.kotarch.models.SongDao

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val songDao = getApp(this).db?.
                songDao()

        insert.setOnClickListener {
            insertSong(songDao)
        }

        songDao?.
                getAllSongs()?.
                subscribeOn(Schedulers.io())?.
                observeOn(AndroidSchedulers.mainThread())?.
                subscribe {
                    var message = ""
                    for (i in it) {
                        message += "\n" + "$i"
                    }
                    toast(message)
                }


        val viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
    }

    private fun insertSong(songDao: SongDao?) {
        Single.fromCallable {
            songDao?.insert(Song(input_id.text.toString(), input_title.text.toString(), input_path.text.toString()))
        }.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe()
    }
}
