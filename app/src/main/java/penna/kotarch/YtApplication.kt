package penna.kotarch

import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import penna.kotarch.models.YtStreamDb
import penna.kotarch.ui.services.MediaPlaybackController
import penna.kotarch.ui.services.StreamingService
import timber.log.Timber

/**
 * Created by danpena on 8/15/17.
 */


class YtApplication : MultiDexApplication() {

    val db: YtStreamDb by lazy {
        Room.databaseBuilder(this, YtStreamDb::class.java, "yt-db")
                .fallbackToDestructiveMigration() //TODO handle migrations
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

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
