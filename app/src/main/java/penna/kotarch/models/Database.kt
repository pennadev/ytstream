package penna.kotarch.models

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 * Created by danpena on 8/15/17.
 */
@Database(
        entities = [
            (Song::class),
            (Playlist::class),
            (PlaylistsSongs::class)
        ],
        version = 2
)

abstract class YtStreamDb : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistsSongsDao(): PlaylistsSongsDao
}