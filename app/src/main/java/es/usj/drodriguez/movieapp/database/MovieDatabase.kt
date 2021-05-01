package es.usj.drodriguez.movieapp.database

import android.content.Context
import androidx.room.*


@Database(entities = [Movie::class, Actor::class, Genre::class], version = 1, exportSchema = false)
@TypeConverters(IntListConverter::class, FileConverter::class)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun actorDao(): ActorDao
    abstract fun genreDao(): GenreDao

    companion object {
        private var INSTANCE: MovieDatabase? = null
        fun getDatabase(context: Context?): MovieDatabase? {
            return INSTANCE ?: synchronized(this) {
                val instance = context?.let {
                    Room.databaseBuilder(
                        it.applicationContext,
                        MovieDatabase::class.java,
                        "movie_database"
                    ).build()
                }
                INSTANCE = instance
                // return instance
                instance
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}