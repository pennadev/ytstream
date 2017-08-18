package penna.kotarch.ui.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.github.piasy.audioprocessor.AudioProcessor
import com.github.piasy.rxandroidaudio.StreamAudioPlayer
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


class MyService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        val mediaPlayer = MediaPlayer()
        return mBinder
    }

    private val mBinder = LocalBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val showTaskIntent = Intent(applicationContext, SearchActivity::class.java)
        showTaskIntent.action = Intent.ACTION_MAIN
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        Bus.playStream.subscribe(this::play)

        val contentIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = Notification.Builder(applicationContext)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Playing")
                .setSmallIcon(R.drawable.notification_bg_normal)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .build()
        startForeground(1337, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    inner class LocalBinder : Binder() {
        fun getService(): MyService {
            return this@MyService
        }
    }

    val BUFFER_SIZE = 2048

    val mStreamAudioPlayer = StreamAudioPlayer.getInstance();
    val mAudioProcessor = AudioProcessor(BUFFER_SIZE);
    val mBuffer = ByteArray(BUFFER_SIZE)

    val mediaPlayer = MediaPlayer()

    fun play(stream: Stream) {
        downloadToExternal(getApplication(), stream.url, "Test" + "." + stream.ext).subscribe {
            mediaPlayer.setDataSource(it.absolutePath)
            mediaPlayer.prepare()
            mediaPlayer.start()
        }
    }

}

