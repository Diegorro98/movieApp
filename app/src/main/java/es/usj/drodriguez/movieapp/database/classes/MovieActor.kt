package es.usj.drodriguez.movieapp.database.classes

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = MovieActor.TABLE_NAME, primaryKeys = [MovieActor.MOVIE_ID, MovieActor.ACTOR_ID])
class MovieActor (
    @ColumnInfo(name = MOVIE_ID)var movieID: Int,
    @ColumnInfo(name = ACTOR_ID)var actorID: Int
) {
    companion object {
        const val TABLE_NAME = "movies_actors"
        const val MOVIE_ID = "movie_id"
        const val ACTOR_ID = "actor_id"
    }
}