package es.usj.drodriguez.movieapp.database.viewmodels

import androidx.lifecycle.*
import es.usj.drodriguez.movieapp.database.DatabaseRepository
import es.usj.drodriguez.movieapp.database.classes.Movie
import kotlinx.coroutines.launch

class MovieViewModel(private val repository: DatabaseRepository):ViewModel() {
    val allMovies : LiveData<List<Movie>> = repository.allMovies.asLiveData()
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(movie: Movie) = viewModelScope.launch {
        repository.insertMovie(movie)
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