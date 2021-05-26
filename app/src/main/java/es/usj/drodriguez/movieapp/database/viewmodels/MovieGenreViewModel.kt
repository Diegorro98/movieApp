package es.usj.drodriguez.movieapp.database.viewmodels

import androidx.lifecycle.*
import es.usj.drodriguez.movieapp.database.DatabaseRepository
import es.usj.drodriguez.movieapp.database.classes.MovieGenre
import kotlinx.coroutines.launch

class MovieGenreViewModel (private val repository: DatabaseRepository): ViewModel() {
    val allMoviesGenres : LiveData<List<MovieGenre>> = repository.allMoviesGenres.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(movieGenre: MovieGenre) = viewModelScope.launch {
        repository.insertMovieGenre(movieGenre)
    }
    fun delete(movieGenre: MovieGenre) = viewModelScope.launch {
        repository.deleteMovieGenre(movieGenre)
    }
}
class MovieGenreViewModelFactory(private val repository: DatabaseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieGenreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieGenreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}