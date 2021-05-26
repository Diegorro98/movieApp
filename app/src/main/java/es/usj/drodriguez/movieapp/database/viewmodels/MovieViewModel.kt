package es.usj.drodriguez.movieapp.database.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import es.usj.drodriguez.movieapp.database.DatabaseRepository
import es.usj.drodriguez.movieapp.database.classes.Movie
import kotlinx.coroutines.launch

class MovieViewModel(private val repository: DatabaseRepository):ViewModel() {
    val allMovies = repository.allMovies.asLiveData()
    suspend fun getNew() = repository.getNewMovie()
    fun insert(movie: Movie) = viewModelScope.launch {
        repository.insertMovie(movie)
    }
    fun setFavorite(id: Long, favorite: Boolean) = viewModelScope.launch {
        repository.setFavoriteMovie(id, favorite)
    }
    fun delete(movie: Movie) = viewModelScope.launch {
        repository.deleteMovie(movie)
    }
    fun update(movie: Movie) = viewModelScope.launch {
        repository.updateMovie(movie)
    }
    fun getByID(ID: Long) = repository.getMovieByID(ID).asLiveData()
    fun getByID(IDs: List<Long>) = repository.getMovieByID(IDs).asLiveData()
    fun getFavorites() = repository.getFavoriteMovies().asLiveData()
    fun getActors(movieID: Long) = repository.getMovieActors(movieID).asLiveData()
    fun getGenres(movieID: Long) = repository.getMovieGenres(movieID).asLiveData()
    fun setPosterURL(movie: Movie, posterURL: String) = viewModelScope.launch {
            repository.setMoviePoster(movie.id, posterURL)
    }
}

class MovieViewModelFactory(private val repository: DatabaseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}