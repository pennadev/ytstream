package penna.kotarch

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import io.reactivex.subjects.PublishSubject
import penna.kotarch.extractors.Stream
import penna.kotarch.models.Db
import penna.kotarch.ui.services.MyService
import timber.log.Timber

/**
 * Created by danpena on 8/15/17.
 */

object Bus {
    val playStream = PublishSubject.create<Stream>()
}

class YtApplication : Application() {
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
        database = Room.databaseBuilder(this, Db::class.java, "yt-db").build()
        startService(Intent(this, MyService::class.java))
    }
}

fun getApp(ctx: Context): YtApplication {
    return ctx.applicationContext as YtApplication
}