package es.usj.drodriguez.movieapp.editors

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import es.usj.drodriguez.movieapp.R
import es.usj.drodriguez.movieapp.database.adapters.MovieListAdapter
import es.usj.drodriguez.movieapp.database.classes.Actor
import es.usj.drodriguez.movieapp.database.classes.Genre
import es.usj.drodriguez.movieapp.database.viewmodels.*
import es.usj.drodriguez.movieapp.databinding.ActivityActorGenreEditorBinding
import es.usj.drodriguez.movieapp.utils.DatabaseApp

class ActorGenreEditor : AppCompatActivity() {
    private var saved = false
    private lateinit var binding: ActivityActorGenreEditorBinding
    private val movieViewModel: MovieViewModel by viewModels { MovieViewModelFactory((application as DatabaseApp).repository) }
    private val movieActorViewModel: MovieActorViewModel by viewModels { MovieActorViewModelFactory((application as DatabaseApp).repository) }
    private val movieGenreViewModel: MovieGenreViewModel by viewModels { MovieGenreViewModelFactory((application as DatabaseApp).repository) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActorGenreEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when(intent?.extras?.getString(CLASS)){
            ACTOR -> {
                val actor = intent?.extras?.getSerializable(OBJECT) as Actor?
                if (actor != null){
                    binding.tbAG.title = getString(R.string.actor_editor_edit_title)
                    binding.etAGName.setText(actor.name, TextView.BufferType.EDITABLE)
                    val adapter = MovieListAdapter(this, editButton = false,
                        onDelete = { currentMovie ->
                            TODO()
                        }
                    )
                    binding.rvAGMovies.adapter = adapter
                    movieActorViewModel.getMovies(actor.id).observe(this) { moviesActors ->
                        moviesActors.let { moviesActorsList ->
                            val moviesID = List(moviesActorsList.size){
                                moviesActorsList[it].movieID
                            }
                            movieViewModel.getByID(moviesID).observe(this) { foundMovies ->
                                foundMovies.let { adapter.submitList(foundMovies)}
                            }
                        }
                    }
                    binding.rvAGMovies.layoutManager = LinearLayoutManager(this)
                } else {
                    binding.tbAG.title = getString(R.string.actor_editor_new_title)
                }
            }

            GENRE -> {
                val genre = intent?.extras?.getSerializable(OBJECT) as Genre?
                if (genre != null) {
                    binding.tbAG.title = getString(R.string.genre_editor_edit_title)
                    binding.etAGName.setText(genre.name, TextView.BufferType.EDITABLE)
                    val adapter = MovieListAdapter(this, editButton = false,
                        onDelete = { currentMovie ->
                           TODO()
                        }
                    )
                    binding.rvAGMovies.adapter = adapter
                    movieGenreViewModel.getMovies(genre.id).observe(this) { moviesGenres ->
                        moviesGenres.let { moviesGenresList ->
                            val moviesID = List(moviesGenresList.size){
                                moviesGenresList[it].movieID
                            }
                            movieViewModel.getByID(moviesID).observe(this) { foundMovies ->
                                foundMovies.let { adapter.submitList(foundMovies)}
                            }
                        }
                    }
                    binding.rvAGMovies.layoutManager = LinearLayoutManager(this)
                } else {
                    binding.tbAG.title = getString(R.string.genre_editor_new_title)
                }

            }
        }
        binding.tbAG.setNavigationOnClickListener {
            finish()
        }
    }

    companion object {
        const val ACTOR = "actor"
        const val GENRE = "genre"
        const val CLASS = "class"
        const val OBJECT = "object"
    }
}