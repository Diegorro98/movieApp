package es.usj.drodriguez.movieapp.database.classes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE


@Entity(
    tableName = MovieActor.TABLE_NAME,
    primaryKeys = [MovieActor.MOVIE_ID, MovieActor.ACTOR_ID],
    foreignKeys =
    [ForeignKey(entity = Movie::class,
            parentColumns = [Movie.ID],
            childColumns = [MovieActor.MOVIE_ID],
            onDelete = CASCADE),
    ForeignKey(entity = Actor::class,
        parentColumns = [Actor.ID],
        childColumns = [MovieActor.ACTOR_ID],
        onDelete = CASCADE)])
class MovieActor (
    @ColumnInfo(name = MOVIE_ID, index = true)var movieID: Int,
    @ColumnInfo(name = ACTOR_ID, index = true)var actorID: Int
) {
    companion object {
        const val TABLE_NAME = "movies_actors"
        const val MOVIE_ID = "movie_id"
        const val ACTOR_ID = "actor_id"
    }
}