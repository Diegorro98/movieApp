package es.usj.drodriguez.movieapp.database.viewmodels

import androidx.lifecycle.*
import es.usj.drodriguez.movieapp.database.DatabaseRepository
import es.usj.drodriguez.movieapp.database.classes.MovieActor
import kotlinx.coroutines.launch

class MovieActorViewModel (private val repository: DatabaseRepository):ViewModel() {
    val allMoviesActors : LiveData<List<MovieActor>> = repository.allMoviesActors.asLiveData()
    fun insert(movieActor: MovieActor) = viewModelScope.launch {
        repository.insertMovieActor(movieActor)
    }
    fun delete(movieActor: MovieActor) = viewModelScope.launch {
        repository.deleteMovieActor(movieActor)
    }
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