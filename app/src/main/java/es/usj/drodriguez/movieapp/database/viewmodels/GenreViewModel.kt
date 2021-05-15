package es.usj.drodriguez.movieapp.database.viewmodels

import androidx.lifecycle.*
import es.usj.drodriguez.movieapp.database.DatabaseRepository
import es.usj.drodriguez.movieapp.database.classes.Genre
import kotlinx.coroutines.launch

class GenreViewModel(private val repository: DatabaseRepository):ViewModel() {
    val allGenres : LiveData<List<Genre>> = repository.allGenres.asLiveData()
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(genre: Genre) = viewModelScope.launch {
        repository.insertGenre(genre)
    }
    fun setFavorite(id: Int, favorite: Boolean) = viewModelScope.launch {
        repository.setFavoriteGenre(id, favorite)
    }
    fun delete(genre: Genre) = viewModelScope.launch {
        repository.deleteGenre(genre)
    }
}
class GenreViewModelFactory(private val repository: DatabaseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GenreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GenreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}