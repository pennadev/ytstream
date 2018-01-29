package penna.kotarch.models

import android.arch.persistence.room.*
import io.reactivex.Flowable

/**
 * Created by danpena on 8/15/17.
 */

@Entity(indices = [Index(value = ["songId"])])
data class Song(
        @PrimaryKey(autoGenerate = false)
        var songId: String = "",
        var title: String = "",
        var url: String = ""
)

@Entity
data class Playlist(
        @PrimaryKey(autoGenerate = false)
        var playlistId: String = ""
)

@Entity(
        tableName = "playlist_song_join",
        primaryKeys = ["songId", "playlistId"],
        foreignKeys = [
            ForeignKey(entity = Song::class,
                    parentColumns = ["songId"],
                    childColumns = ["songId"]),
            ForeignKey(entity = Playlist::class,
                    parentColumns = ["playlistId"],
                    childColumns = ["playlistId"])
        ]
)

data class PlaylistsSongs(var songId: String = "",
                          var playlistId: String = "")

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(playlist: Playlist)

    @Query("SELECT * FROM playlist")
    fun getAllPlaylists(): Flowable<List<Playlist>>
}


@Dao
interface SongDao {
    @Query("SELECT * FROM song")
    fun getAllSongs(): Flowable<List<Song>>

    @Query("SELECT * FROM song WHERE songId == :songId")
    fun getSongById(songId: String): Flowable<Song>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(song: Song)
}

@Dao
interface PlaylistsSongsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(playlistsSongs: PlaylistsSongs)

    @Query("""
            SELECT * FROM song
            INNER JOIN playlist_song_join
            ON song.songId == playlist_song_join.songId
            WHERE playlist_song_join.playlistId == :playlistId
            """)
    fun getSongsForPlaylist(playlistId: String): Flowable<List<Song>>
}