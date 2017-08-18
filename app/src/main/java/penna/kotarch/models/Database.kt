package penna.kotarch.models

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 * Created by danpena on 8/15/17.
 */
@Database(entities = arrayOf(Song::class), version = 1)
abstract class Db : RoomDatabase() {
    abstract fun songDao(): SongDao
}