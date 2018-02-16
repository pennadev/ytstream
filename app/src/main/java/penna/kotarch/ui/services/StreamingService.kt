package penna.kotarch.ui.services

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.widget.RemoteViews
import com.devbrackets.android.exomedia.AudioPlayer
import com.github.ajalt.timberkt.d
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import penna.kotarch.R
import penna.kotarch.getApp
import penna.kotarch.models.Song
import penna.kotarch.ui.activities.SearchActivity


class StreamingService() : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    private val mBinder = LocalBinder()

    var playBusSubscription: Disposable? = null


    companion object Constants {

        const val INTENT_ACTION = "YTSTREAM"

        const val PLAY_COMMAND = 1
        const val PAUSE_COMMAND = 2
        const val STOP_COMMAND = 3
        const val NEXT_COMMAND = 4
        const val PREVIOUS_COMMAND = 5

        const val NOTIFICATION_ID = 1337
        const val CHANNEL_ID = "ytstream"
    }

    private fun registerBroadcastReceiver() {
        val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) =
                    handleBroadcastCommand(intent?.extras?.getInt("command") ?: -1)
        }

        registerReceiver(broadcastReceiver, IntentFilter(INTENT_ACTION))
    }

    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val contentIntent: PendingIntent by lazy {
        val showTaskIntent = Intent(applicationContext, SearchActivity::class.java)
        showTaskIntent.action = Intent.ACTION_MAIN
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        PendingIntent.getActivity(
                applicationContext,
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun handleBroadcastCommand(command: Int) {
        when (command) {
            PLAY_COMMAND -> {

            }
        }
    }


    private fun createNotificationView(playingState: PlayingState): RemoteViews {

        fun clickPendingIntent(command: Int): PendingIntent {
            val intent = Intent(INTENT_ACTION)
            intent.putExtra("command", command)
            return PendingIntent.getBroadcast(this, command, intent, PendingIntent.FLAG_IMMUTABLE)
        }


        val remote = RemoteViews(packageName, R.layout.notification)
        remote.setTextViewText(R.id.title, playingState.currentSong?.title)
        remote.setOnClickPendingIntent(R.id.next_btn, clickPendingIntent(NEXT_COMMAND))
        remote.setOnClickPendingIntent(R.id.play_btn, clickPendingIntent(PLAY_COMMAND))
        remote.setOnClickPendingIntent(R.id.previous_btn, clickPendingIntent(PREVIOUS_COMMAND))
        return remote
    }

    private val mediaPlaybackController: MediaPlaybackController by lazy { getApp(this).playBackController }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerBroadcastReceiver()

        playBusSubscription = mediaPlaybackController.observePlayState().observeOn(AndroidSchedulers.mainThread()).subscribe(this::handleStateChanged)

        val notification = buildPersistentNotification(PlayingState())
        startForeground(NOTIFICATION_ID, notification)

        return super.onStartCommand(intent, flags, startId)
    }


    private fun play(url: String) {
        d { "Playing" }
        if (isPlaying) {
            mediaPlayer.stopPlayback()
            mediaPlayer.reset()
        }

        mediaPlayer.setOnCompletionListener {
            mediaPlayer.reset()
            isPlaying = false
        }

        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
        }

        mediaPlayer.setDataSource(Uri.parse(url))
        d { url }
        mediaPlayer.prepareAsync()


        isPlaying = true
    }

    var currentSong: Song? = Song()
    private fun handleStateChanged(playingState: PlayingState) {
        if (playingState.playing && playingState.currentSong?.equals(currentSong) == false) {
            notificationManager.notify(Constants.NOTIFICATION_ID, buildNotification(playingState))
            playingState.currentSong?.url?.let { play(it) }
        }
    }

    private fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }


    private fun buildPersistentNotification(playingState: PlayingState): Notification? {

        //handle oreo notification api
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(CHANNEL_ID, "YtStream", NotificationManager.IMPORTANCE_LOW).apply {
                description = "YtStream app"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(false)
                vibrationPattern = LongArray(0)
                notificationManager.createNotificationChannel(this)
            }
        }

        return buildNotification(playingState)
    }

    private fun buildNotification(playingState: PlayingState): Notification? {
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setCustomContentView(createNotificationView(playingState))
                .setPriority(Notification.PRIORITY_LOW)
                .setContentTitle(getString(R.string.app_name))
                .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
                .setSmallIcon(R.drawable.ic_play_circle_notif)
                .setWhen(System.currentTimeMillis())
                .setVibrate(LongArray(0))
                .setContentIntent(contentIntent)
                .build()
    }


    class LocalBinder : Binder()

    private val mediaPlayer: AudioPlayer by lazy { AudioPlayer(this) }

    var downloadSub: Disposable? = null

    private var isPlaying: Boolean = false


    override fun onDestroy() {
        super.onDestroy()
        downloadSub?.dispose()
        playBusSubscription?.dispose()
        mediaPlayer.release()
    }

}
