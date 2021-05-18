package es.usj.drodriguez.movieapp.editors

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import es.usj.drodriguez.movieapp.R
import es.usj.drodriguez.movieapp.database.DatabaseFetcher
import es.usj.drodriguez.movieapp.database.adapters.MovieListAdapter
import es.usj.drodriguez.movieapp.database.classes.Actor
import es.usj.drodriguez.movieapp.database.classes.Genre
import es.usj.drodriguez.movieapp.database.classes.MovieActor
import es.usj.drodriguez.movieapp.database.classes.MovieGenre
import es.usj.drodriguez.movieapp.database.viewmodels.*
import es.usj.drodriguez.movieapp.databinding.ActivityActorGenreEditorBinding
import es.usj.drodriguez.movieapp.utils.DatabaseApp

class ActorGenreEditor : AppCompatActivity() {
    private lateinit var binding: ActivityActorGenreEditorBinding
    private val movieViewModel: MovieViewModel by viewModels { MovieViewModelFactory((application as DatabaseApp).repository) }
    private val movieActorViewModel: MovieActorViewModel by viewModels { MovieActorViewModelFactory((application as DatabaseApp).repository) }
    private val movieGenreViewModel: MovieGenreViewModel by viewModels { MovieGenreViewModelFactory((application as DatabaseApp).repository) }
    private val actorViewModel: ActorViewModel by viewModels { ActorViewModelFactory((application as DatabaseApp).repository) }
    private val genreViewModel: GenreViewModel by viewModels { GenreViewModelFactory((application as DatabaseApp).repository) }
    private lateinit var editClass: String

    private lateinit var save: () -> Unit
    private lateinit var delete: () -> Unit
    private lateinit var favorite: () -> Unit

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_editor, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.btn_editor_delete -> {
            delete.invoke()
            finish()
            true
        }
        R.id.btn_editor_fav -> {
            favorite.invoke()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActorGenreEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        editClass = intent?.extras?.getString(CLASS).toString()
        setSupportActionBar(binding.tbAG)
        binding.tbAG.showOverflowMenu()
        binding.tbAG.setNavigationOnClickListener {
            finish()
        }
        when(editClass){
            ACTOR -> {
                val actor = intent?.extras?.getSerializable(OBJECT) as Actor?
                if (actor != null){
                    save = {
                        actor.name = binding.etAGName.text.toString().trim()
                        actorViewModel.update(actor)
                    }
                    delete = {
                        actorViewModel.delete(actor)
                    }
                    favorite = {
                        actorViewModel.setFavorite(actor.id, !actor.favorite)
                    }
                    binding.tbAG.title = getString(R.string.actor_editor_edit_title)
                    binding.etAGName.setText(actor.name, TextView.BufferType.EDITABLE)
                    val adapter = MovieListAdapter(this, editButton = false,
                        onDelete = { currentMovie ->
                            movieActorViewModel.delete(MovieActor(currentMovie.id, actor.id))
                            if (currentMovie.id !in DatabaseFetcher.Companion.Updates.movies){
                                DatabaseFetcher.Companion.Updates.movies.add(currentMovie.id)
                            }
                        }
                    )
                    binding.ibAGAdd.setOnClickListener {
                        startActivity(Intent(this, ItemPicker::class.java)
                            .putExtra(ItemPicker.PICKED, ItemPicker.MOVIE)
                            .putExtra(ItemPicker.PICKER, ItemPicker.ACTOR)
                            .putExtra(ItemPicker.OBJECT_ID, actor.id)
                        )
                    }
                    binding.rvAGMovies.adapter = adapter
                    movieActorViewModel.getMovies(actor.id).observe(this) { moviesActors ->
                        moviesActors.let { moviesActorsList ->
                            val moviesID = List(moviesActorsList.size){
                                moviesActorsList[it]
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
                    save =  {
                        genre.name = binding.etAGName.text.toString().trim()
                        genreViewModel.update(genre)
                    }
                    delete = {
                        genreViewModel.delete(genre)
                    }
                    favorite = {
                        genreViewModel.setFavorite(genre.id, !genre.favorite)
                    }
                    binding.tbAG.title = getString(R.string.actor_editor_edit_title)
                    binding.etAGName.setText(genre.name, TextView.BufferType.EDITABLE)
                    val adapter = MovieListAdapter(this, editButton = false,
                        onDelete = { currentMovie ->
                            movieGenreViewModel.delete(MovieGenre(currentMovie.id, genre.id))
                            if (currentMovie.id !in DatabaseFetcher.Companion.Updates.movies){
                                DatabaseFetcher.Companion.Updates.movies.add(currentMovie.id)
                            }
                        }
                    )
                    binding.ibAGAdd.setOnClickListener {
                        startActivity(Intent(this, ItemPicker::class.java)
                            .putExtra(ItemPicker.PICKED, ItemPicker.MOVIE)
                            .putExtra(ItemPicker.PICKER, ItemPicker.GENRE)
                            .putExtra(ItemPicker.OBJECT_ID, genre.id)
                        )
                    }
                    binding.rvAGMovies.adapter = adapter
                    movieGenreViewModel.getMovies(genre.id).observe(this) { moviesGenres ->
                        moviesGenres.let { moviesGenresList ->
                            val moviesID = List(moviesGenresList.size){
                                moviesGenresList[it]
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
        }
        binding.tbAG.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        save.invoke()
        super.onDestroy()
    }
    companion object {
        const val ACTOR = "actor"
        const val GENRE = "genre"
        const val CLASS = "class"
        const val OBJECT = "object"
    }
}