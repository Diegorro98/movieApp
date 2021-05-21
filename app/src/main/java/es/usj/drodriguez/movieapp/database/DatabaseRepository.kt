package es.usj.drodriguez.movieapp.database

import es.usj.drodriguez.movieapp.database.classes.*
import es.usj.drodriguez.movieapp.database.dao.*
import kotlinx.coroutines.flow.Flow

class DatabaseRepository(
    private val movieDao: MovieDao,
    private val genreDao: GenreDao,
    private val actorDao: ActorDao,
    private val movieActorDao: MovieActorDao,
    private val movieGenreDao: MovieGenreDao){

    val allMovies: Flow<List<Movie>> = movieDao.getAll()
    val allGenres: Flow<List<Genre>> = genreDao.getAll()
    val allActors: Flow<List<Actor>> = actorDao.getAll()
    val allMoviesActors: Flow<List<MovieActor>> = movieActorDao.getAll()
    val allMoviesGenres: Flow<List<MovieGenre>> = movieGenreDao.getAll()

    suspend fun getNewMovie() = movieDao.getNew()
    suspend fun insertMovie(movie: Movie) = movieDao.insert(movie)
    suspend fun insertMovies(movies: List<Movie>) = movieDao.insertMultiple(movies)
    suspend fun updateMovie(movie: Movie) = movieDao.update(movie)
    suspend fun updateMovies(movies: List<Movie>) = movieDao.updateAll(movies)
    suspend fun deleteMovie(movie: Movie) = movieDao.delete(movie)
    suspend fun setFavoriteMovie(id: Long, favorite: Boolean) = movieDao.setFavorite(id, favorite)
    fun getFavoriteMovies() = movieDao.getFavorites()
    fun getMovieByID(IDs: List<Long>) = movieDao.getByID(IDs)
    fun getMovieByID(id: Long) = movieDao.getByID(id)

    suspend fun getNewGenre() = genreDao.getNew()
    suspend fun insertGenre(genre: Genre) = genreDao.insert(genre)
    suspend fun insertGenres(genres: List<Genre>) = genreDao.insertMultiple(genres)
    suspend fun updateGenre(genre: Genre) = genreDao.update(genre)
    suspend fun updateGenres(genres: List<Genre>) = genreDao.updateAll(genres)
    suspend fun deleteGenre(genre: Genre) = genreDao.delete(genre)
    suspend fun setFavoriteGenre(id: Long, favorite: Boolean) = genreDao.setFavorite(id, favorite)
    fun getFavoriteGenres() = genreDao.getFavorites()
    fun getGenreByID(IDs: List<Long>) = genreDao.getByID(IDs)
    fun getGenreByID(id: Long) = genreDao.getByID(id)

    suspend fun getNewActor()=actorDao.getNew()
    suspend fun insertActor(actor: Actor) = actorDao.insert(actor)
    suspend fun insertActors(actors: List<Actor>) = actorDao.insertMultiple(actors)
    suspend fun updateActor(actor: Actor) = actorDao.update(actor)
    suspend fun updateActors(actors: List<Actor>) = actorDao.updateAll(actors)
    suspend fun deleteActor(actor: Actor) = actorDao.delete(actor)
    suspend fun setFavoriteActor(id: Long, favorite: Boolean) = actorDao.setFavorite(id, favorite)
    fun getFavoriteActors() = actorDao.getFavorites()
    fun getActorByID(IDs: List<Long>) = actorDao.getByID(IDs)
    fun getActorByID(id: Long) = actorDao.getByID(id)

    suspend fun insertMovieActor(movieActor: MovieActor) = movieActorDao.insert(movieActor)
    suspend fun deleteMovieActor(movieActor: MovieActor) = movieActorDao.delete(movieActor)
    fun getActorMovies(actorID: Long) = movieActorDao.getMovies(actorID)
    fun getMovieActors(movieID: Long) = movieActorDao.getActors(movieID)

    suspend fun insertMovieGenre(movieGenre: MovieGenre) = movieGenreDao.insert(movieGenre)
    suspend fun deleteMovieGenre(movieGenre: MovieGenre) = movieGenreDao.delete(movieGenre)
    fun getGenreMovies(genreID: Long) = movieGenreDao.getMovies(genreID)
    fun getMovieGenres(movieID: Long) = movieGenreDao.getGenres(movieID)
}