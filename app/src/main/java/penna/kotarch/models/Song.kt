package penna.kotarch.models

import android.arch.persistence.room.*
import io.reactivex.Flowable

/**
 * Created by danpena on 8/15/17.
 */

@Entity
data class Song(
        @PrimaryKey(autoGenerate = false)
        var id: String,
        var title: String,
        var storagePath: String
) {
    constructor() : this("", "", "")
}

@Dao
interface SongDao {
    @Query("SELECT * FROM song")
    fun getAllSongs(): Flowable<List<Song>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(song: Song)
}