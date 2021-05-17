package es.usj.drodriguez.movieapp.database

import es.usj.drodriguez.movieapp.database.classes.*
import es.usj.drodriguez.movieapp.database.dao.*
import kotlinx.coroutines.flow.Flow

class DatabaseRepository(
    private val movieDao: MovieDao,
    private val genreDao: GenreDao,
    private val actorDao: ActorDao,
    private val movieActorDao: MovieActorDao,
    private val movieGenreDao: MovieGenreDao
    ){
        val allMovies: Flow<List<Movie>> = movieDao.getAll()
        val allGenres: Flow<List<Genre>> = genreDao.getAll()
        val allActors: Flow<List<Actor>> = actorDao.getAll()
        val allMoviesActors: Flow<List<MovieActor>> = movieActorDao.getAll()
        val allMoviesGenres: Flow<List<MovieGenre>> = movieGenreDao.getAll()

        suspend fun insertMovie(movie: Movie) = movieDao.insert(movie)
        suspend fun insertMovies(movies: List<Movie>) = movieDao.insertMultiple(movies)
        suspend fun updateMovies(movie: Movie) = movieDao.update(movie)
        suspend fun updateAllMovies(movies: List<Movie>) = movieDao.updateAll(movies)
        suspend fun deleteMovie(movie: Movie) = movieDao.delete(movie)
        suspend fun setFavoriteMovie(id: Int, favorite: Boolean) = movieDao.setFavorite(id, favorite)
        fun getByID(moviesID: List<Int>) = movieDao.getByID(moviesID)
        fun getMovieByID(id: Int): Movie = movieDao.getByID(id)

        suspend fun insertGenre(genre: Genre) = genreDao.insert(genre)
        suspend fun insertGenres(genres: List<Genre>) = genreDao.insertMultiple(genres)
        suspend fun updateGenres(genre: Genre) = genreDao.update(genre)
        suspend fun updateAllGenres(genres: List<Genre>) = genreDao.updateAll(genres)
        suspend fun deleteGenre(genre: Genre) = genreDao.delete(genre)
        suspend fun setFavoriteGenre(id: Int, favorite: Boolean) = genreDao.setFavorite(id, favorite)
        fun getGenreByID(id: Int): Genre = genreDao.getByID(id)


        suspend fun insertActor(actor: Actor) = actorDao.insert(actor)
        suspend fun insertActors(actors: List<Actor>) = actorDao.insertMultiple(actors)
        suspend fun updateActors(actor: Actor) = actorDao.update(actor)
        suspend fun updateAllActors(actors: List<Actor>) = actorDao.updateAll(actors)
        suspend fun deleteActor(actor: Actor) = actorDao.delete(actor)
        suspend fun setFavoriteActor(id: Int, favorite: Boolean) = actorDao.setFavorite(id, favorite)
        fun getActorByID(id: Int): Actor = actorDao.getByID(id)

        suspend fun insertMovieActor(movieActor: MovieActor) = movieActorDao.insert(movieActor)
        suspend fun deleteMovieActor(movieActor: MovieActor) = movieActorDao.delete(movieActor)
        fun getActorMovies(actorID: Int): Flow<List<MovieActor>> = movieActorDao.getMovies(actorID)
        fun getMovieActors(movieID: Int): Flow<List<MovieActor>> = movieActorDao.getActors(movieID)

        suspend fun insertMovieGenre(movieGenre: MovieGenre) = movieGenreDao.insert(movieGenre)
        suspend fun deleteMovieGenre(movieGenre: MovieGenre) = movieGenreDao.delete(movieGenre)
        fun getGenreMovies(genreID: Int): Flow<List<MovieGenre>> = movieGenreDao.getMovies(genreID)
        fun getMovieGenres(movieID: Int): Flow<List<MovieGenre>> = movieGenreDao.getGenres(movieID)

}