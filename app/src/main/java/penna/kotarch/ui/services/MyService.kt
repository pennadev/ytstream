package penna.kotarch.ui.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.devbrackets.android.exomedia.AudioPlayer
import com.github.ajalt.timberkt.d
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import penna.kotarch.Bus
import penna.kotarch.R
import penna.kotarch.extractors.Stream
import penna.kotarch.ui.activities.SearchActivity
import penna.kotarch.utils.downloadToExternal


object ServiceSubjects {
    val PlaySubject = PublishSubject.create<PlayInfo>()
}

class PlayInfo {

}


class MyService() : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    private val mBinder = LocalBinder()

    var playBusSubscription: Disposable? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val showTaskIntent = Intent(applicationContext, SearchActivity::class.java)
        showTaskIntent.action = Intent.ACTION_MAIN
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        mediaPlayer = AudioPlayer(this)

        playBusSubscription = Bus.playStream.subscribe(this::play)

        val contentIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Configure the notification channel.
            NotificationChannel("yt_stream", "My Notifications", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Channel description"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(false)
                vibrationPattern = null
                notificationManager.createNotificationChannel(this)
            }
        }

        val notification = NotificationCompat.Builder(applicationContext, "yt_stream")
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Playing")
                .setSmallIcon(R.drawable.notification_bg_normal)
                .setWhen(System.currentTimeMillis())
                .setVibrate(null)
                .setContentIntent(contentIntent)
                .build()
        startForeground(1337, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    class LocalBinder : Binder()

    private var mediaPlayer: AudioPlayer? = null

    var downloadSub: Disposable? = null

    private var isPlaying: Boolean = false

    fun play(stream: Stream) {
        d { "Playing" }
        if (isPlaying) {
            mediaPlayer?.stopPlayback()
            mediaPlayer?.reset()
        }

        mediaPlayer?.setOnCompletionListener {
            mediaPlayer?.reset()
            isPlaying = false
        }

        mediaPlayer?.setOnPreparedListener {
            mediaPlayer?.start()
        }

        mediaPlayer?.setDataSource(Uri.parse(stream.url))
        d { stream.url }
        mediaPlayer?.prepareAsync()


        isPlaying = true
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadSub?.dispose()
        playBusSubscription?.dispose()
        mediaPlayer?.release()
    }


}

