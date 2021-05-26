package es.usj.drodriguez.movieapp.database.viewmodels

import androidx.lifecycle.*
import es.usj.drodriguez.movieapp.database.DatabaseRepository
import es.usj.drodriguez.movieapp.database.classes.Actor
import kotlinx.coroutines.launch

class ActorViewModel(private val repository: DatabaseRepository):ViewModel() {
    val allActors : LiveData<List<Actor>> = repository.allActors.asLiveData()
    suspend fun getNew() = repository.getNewActor()
    fun insert(actor: Actor) = viewModelScope.launch {
        repository.insertActor(actor)
    }
    suspend fun insertReturn(actor: Actor) = repository.insertActor(actor)
    fun setFavorite(id: Long, favorite: Boolean) = viewModelScope.launch {
        repository.setFavoriteActor(id, favorite)
    }
    fun delete(actor: Actor) = viewModelScope.launch {
        repository.deleteActor(actor)
    }
    fun update(actor: Actor) = viewModelScope.launch {
        repository.updateActor(actor)
    }
    fun getByID(IDs: List<Long>) = repository.getActorByID(IDs).asLiveData()
    fun getFavorites() = repository.getFavoriteActors().asLiveData()
    fun getMovies(actorID: Long) = repository.getActorMovies(actorID).asLiveData()
    suspend fun getByNameNoFlow(name: String) = repository.getActorByNameNoFlow(name)
}
class ActorViewModelFactory(private val repository: DatabaseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}