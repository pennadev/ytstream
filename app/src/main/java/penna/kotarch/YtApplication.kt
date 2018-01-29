package penna.kotarch

import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import io.reactivex.subjects.PublishSubject
import penna.kotarch.extractors.Stream
import penna.kotarch.models.Db
import penna.kotarch.ui.services.MediaPlaybackController
import penna.kotarch.ui.services.PlayingState
import penna.kotarch.ui.services.StreamingService
import timber.log.Timber

/**
 * Created by danpena on 8/15/17.
 */


class YtApplication : MultiDexApplication() {
    companion object {
        var database: Db? = null
    }

    val db: Db?
        get() {
            return database
        }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        database = Room.databaseBuilder(this, Db::class.java, "yt-db")
                .fallbackToDestructiveMigration() //TODO handle migrations
                .build()

        startMusicService()

        Stetho.initializeWithDefaults(this);

    }

    private fun startMusicService() {
        val intent = Intent(this, StreamingService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    val playBackController: MediaPlaybackController by lazy { MediaPlaybackController() }
}

fun getApp(ctx: Context): YtApplication {
    return ctx.applicationContext as YtApplication
}
