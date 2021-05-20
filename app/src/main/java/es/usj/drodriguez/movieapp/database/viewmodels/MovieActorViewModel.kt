package es.usj.drodriguez.movieapp.database.viewmodels

import androidx.lifecycle.*
import es.usj.drodriguez.movieapp.database.DatabaseRepository
import es.usj.drodriguez.movieapp.database.classes.MovieActor
import kotlinx.coroutines.launch

class MovieActorViewModel (private val repository: DatabaseRepository):ViewModel() {
    val allMoviesActors : LiveData<List<MovieActor>> = repository.allMoviesActors.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(movieActor: MovieActor) = viewModelScope.launch {
        repository.insertMovieActor(movieActor)
    }
    fun delete(movieActor: MovieActor) = viewModelScope.launch {
        repository.deleteMovieActor(movieActor)
    }

    fun getMovies(actorID: Long) = repository.getActorMovies(actorID).asLiveData()
    fun getActors(movieID: Long) = repository.getMovieActors(movieID).asLiveData()
}
class MovieActorViewModelFactory(private val repository: DatabaseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieActorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieActorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}