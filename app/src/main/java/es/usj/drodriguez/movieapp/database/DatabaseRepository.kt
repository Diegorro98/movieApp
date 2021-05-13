package es.usj.drodriguez.movieapp.database

import es.usj.drodriguez.movieapp.database.classes.Actor
import es.usj.drodriguez.movieapp.database.classes.Genre
import es.usj.drodriguez.movieapp.database.classes.Movie
import es.usj.drodriguez.movieapp.database.dao.ActorDao
import es.usj.drodriguez.movieapp.database.dao.GenreDao
import es.usj.drodriguez.movieapp.database.dao.MovieDao
import kotlinx.coroutines.flow.Flow

class DatabaseRepository(
    private val movieDao: MovieDao,
    private val genreDao: GenreDao,
    private val actorDao: ActorDao
    ){
        val allMovies: Flow<List<Movie>> = movieDao.getAll()
        val allGenres: Flow<List<Genre>> = genreDao.getAll()
        val allActors: Flow<List<Actor>> = actorDao.getAll()
       /* constructor(context: Context){
            val database = Database.getDatabase(context)
            if (database != null) {
                movieDao = database.movieDao()
                genreDao = database.genreDao()
                actorDao = database.actorDao()
            }
            allMovies = movieDao.getAll()
            allGenres = genreDao.getAll()
            allActors = actorDao.getAll()
        }*/
        suspend fun insertMovie(movie: Movie) = movieDao.insert(movie)
        suspend fun insertMovies(movies: List<Movie>) = movieDao.insertMultiple(movies)
        suspend fun updateMovies(movie: Movie) = movieDao.update(movie)
        suspend fun updateAllMovies(movies: List<Movie>) = movieDao.updateAll(movies)
        suspend fun deleteMovie(movie: Movie) = movieDao.delete(movie)
        //fun getAllMovies(): Flow<List<Movie>> = movieDao.getAll()
        fun getMovieByID(id: Int): Movie = movieDao.getByID(id)

        fun getActorsMovies(actorID: Int): List<Movie> = movieDao.getActorMovies("%$actorID%")
        fun getGenreMovies(genreID: Int): List<Movie> =movieDao.getGenreMovies("%$genreID%")

        suspend fun insertGenre(genre: Genre) = genreDao.insert(genre)
        suspend fun insertGenres(genres: List<Genre>) = genreDao.insertMultiple(genres)
        suspend fun updateGenres(genre: Genre) = genreDao.update(genre)
        suspend fun updateAllGenres(genres: List<Genre>) = genreDao.updateAll(genres)
        suspend fun deleteGenre(genre: Genre) = genreDao.delete(genre)
        //fun getAllGenres(): Flow<List<Genre>> = genreDao.getAll()
        fun getGenreByID(id: Int): Genre = genreDao.getByID(id)

        suspend fun insertActor(actor: Actor) = actorDao.insert(actor)
        suspend fun insertActors(actors: List<Actor>) = actorDao.insertMultiple(actors)
        suspend fun updateActors(actor: Actor) = actorDao.update(actor)
        suspend fun updateAllActors(actors: List<Actor>) = actorDao.updateAll(actors)
        suspend fun deleteActor(actor: Actor) = actorDao.delete(actor)
        //fun getAllActors(): Flow<List<Actor>> = actorDao.getAll()
        fun getActorByID(id: Int): Actor = actorDao.getByID(id)
}