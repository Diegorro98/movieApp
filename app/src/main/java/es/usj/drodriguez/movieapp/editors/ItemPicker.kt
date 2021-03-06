package es.usj.drodriguez.movieapp.editors

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import es.usj.drodriguez.movieapp.database.DatabaseFetcher
import es.usj.drodriguez.movieapp.database.adapters.ActorListAdapter
import es.usj.drodriguez.movieapp.database.adapters.GenreListAdapter
import es.usj.drodriguez.movieapp.database.adapters.MovieListAdapter
import es.usj.drodriguez.movieapp.database.classes.MovieActor
import es.usj.drodriguez.movieapp.database.classes.MovieGenre
import es.usj.drodriguez.movieapp.database.viewmodels.*
import es.usj.drodriguez.movieapp.databinding.ActivityItemPickerBinding
import es.usj.drodriguez.movieapp.utils.DatabaseApp

class ItemPicker : AppCompatActivity() {
    private val movieViewModel: MovieViewModel by viewModels { MovieViewModelFactory((this.application as DatabaseApp).repository) }
    private val actorViewModel: ActorViewModel by viewModels { ActorViewModelFactory((this.application as DatabaseApp).repository) }
    private val genreViewModel: GenreViewModel by viewModels { GenreViewModelFactory((this.application as DatabaseApp).repository) }
    private val movieActorViewModel: MovieActorViewModel by viewModels { MovieActorViewModelFactory((this.application as DatabaseApp).repository) }
    private val movieGenreViewModel: MovieGenreViewModel by viewModels { MovieGenreViewModelFactory((this.application as DatabaseApp).repository) }
    private lateinit var binding: ActivityItemPickerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val objectID = intent?.extras?.getLong(OBJECT_ID)!!
        when (intent?.extras?.getString(PICKED)) {
            MOVIE -> {
                val picker = intent?.extras?.getString(PICKER).toString()
                val adapter = MovieListAdapter(this, movieViewModel,false,
                    onCardClick = { cardView, currentMovie, _ ->
                        DatabaseFetcher.updated.movies.add(currentMovie)
                        if (cardView.isSelected) {
                            when (picker) {
                                ACTOR -> movieActorViewModel.delete(MovieActor(currentMovie.id, objectID))
                                GENRE ->  movieGenreViewModel.delete(MovieGenre(currentMovie.id, objectID))
                            }
                        } else {
                            when (picker) {
                                ACTOR -> movieActorViewModel.insert(MovieActor(currentMovie.id, objectID))
                                GENRE ->  movieGenreViewModel.insert(MovieGenre(currentMovie.id, objectID))
                        }
                    }
                })
                when(picker){
                    ACTOR -> actorViewModel.getMovies(objectID).observe(this){
                        adapter.selectedMovies = it
                        adapter.notifyDataSetChanged()
                    }
                    GENRE -> genreViewModel.getMovies(objectID).observe(this) {
                        adapter.selectedMovies = it
                        adapter.notifyDataSetChanged()
                    }
                }
                binding.rvPicker.adapter = adapter
                movieViewModel.allMovies.observe(this) { movies ->
                    movies.let { adapter.submitList(it) }
                }
                binding.rvPicker.layoutManager = LinearLayoutManager(this)
            }
            ACTOR -> {
                val adapter = ActorListAdapter(this, actorViewModel, this, false,
                onCardClick = {cardView, currentActor, _ ->
                    if (cardView.isSelected) {
                        movieActorViewModel.delete(MovieActor(objectID, currentActor.id))
                    } else {
                        movieActorViewModel.insert(MovieActor(objectID, currentActor.id))
                    }
                })
                movieViewModel.getActors(objectID).observe(this){
                    adapter.selectedMovies = it
                    adapter.notifyDataSetChanged()
                }
                binding.rvPicker.adapter = adapter
                actorViewModel.allActors.observe(this) { movies ->
                    movies.let { adapter.submitList(it) }
                }
                binding.rvPicker.layoutManager = LinearLayoutManager(this)
            }
            GENRE -> {
                val adapter = GenreListAdapter(this, genreViewModel, this, false,
                    onCardClick = {cardView, currentActor, _ ->
                        if (cardView.isSelected) {
                            movieGenreViewModel.delete(MovieGenre(objectID, currentActor.id))
                        } else {
                            movieGenreViewModel.insert(MovieGenre(objectID, currentActor.id))
                        }
                    })
                movieViewModel.getGenres(objectID).observe(this){
                    adapter.selectedMovies = it
                    adapter.notifyDataSetChanged()
                }
                binding.rvPicker.adapter = adapter
                genreViewModel.allGenres.observe(this) { movies ->
                    movies.let { adapter.submitList(it) }
                }
                binding.rvPicker.layoutManager = LinearLayoutManager(this)
            }
        }
        binding.tbPicker.setNavigationOnClickListener {
            finish()
        }
    }
    companion object{
        const val PICKED = "picked"
        const val PICKER = "picker"
        const val MOVIE = "movie"
        const val ACTOR = "actor"
        const val GENRE = "genre"
        const val OBJECT_ID = "object id"
    }
}