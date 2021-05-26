package es.usj.drodriguez.movieapp.editors

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private var new = false
    private var saved = false
    private lateinit var save: () -> Unit
    private lateinit var delete: () -> Unit
    private lateinit var favorite: () -> Boolean
    private var objectID: Long = -1L
    private var originalMovies: List<Long>? = null
    private var moviesInList: List<Long> = emptyList()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_editor, menu)
        menu?.getItem(0)?.isVisible = false
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.btn_editor_fav -> {
            val favorite = favorite.invoke()
            val favIcon = if(favorite) ContextCompat.getDrawable(this,R.drawable.ic_baseline_star_24) else ContextCompat.getDrawable(this,R.drawable.ic_baseline_star_border_24)
            favIcon?.setTint(getColor(R.color.Toolbar_Primary))
            item.icon = favIcon
            item.title = getString(if(favorite)R.string.title_contextual_rmv_fav else R.string.title_contextual_add_fav)
            true
        }
        R.id.btn_editor_save -> {
            save.invoke()
            saved = true
            finish()
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
        new = intent?.extras?.getBoolean(NEW, false) == true
        when(editClass){
            ACTOR -> {
                supportActionBar?.title = if (new) getString(R.string.actor_editor_new_title) else getString(R.string.actor_editor_edit_title)
                val actor = intent?.extras?.getSerializable(OBJECT) as Actor
                objectID = actor.id
                save = {
                    actor.name = binding.etAGName.text.toString().trim()
                    actorViewModel.update(actor)
                }
                delete = {actorViewModel.delete(actor)}
                favorite = {
                    actor.favorite = !actor.favorite
                    actor.favorite
                }
                binding.etAGName.setText(actor.name, TextView.BufferType.EDITABLE)
                val adapter = MovieListAdapter(this, movieViewModel, false,
                    onDelete = { deletedMovie ->
                        movieActorViewModel.delete(MovieActor(deletedMovie.id, actor.id))
                        if (deletedMovie.id !in DatabaseFetcher.Companion.Updates.movies){
                            DatabaseFetcher.Companion.Updates.movies.add(deletedMovie.id)
                        }
                    }
                )
                val movies =actorViewModel.getMovies(actor.id)
                originalMovies = movies.value?: emptyList()
                movies.observe(this) { moviesActors ->
                    moviesActors.let { moviesActorsList ->
                        moviesInList = List(moviesActorsList.size){
                            moviesActorsList[it]
                        }
                        if(originalMovies == null){
                            originalMovies = moviesInList
                        }
                        movieViewModel.getByID(moviesInList).observe(this) { foundMovies ->
                            foundMovies.let { adapter.submitList(it)}
                        }
                    }
                }
                binding.rvItems.adapter = adapter
                binding.rvItems.layoutManager = LinearLayoutManager(this)
                binding.ibAGAdd.setOnClickListener {
                    startActivity(Intent(this, ItemPicker::class.java)
                        .putExtra(ItemPicker.PICKED, ItemPicker.MOVIE)
                        .putExtra(ItemPicker.PICKER, ItemPicker.ACTOR)
                        .putExtra(ItemPicker.OBJECT_ID, actor.id)
                    )
                }
            }

            GENRE -> {
                supportActionBar?.title = if (intent?.extras?.getBoolean(NEW, false) == true) getString(R.string.actor_editor_new_title) else getString(R.string.genre_editor_edit_title)
                val genre = intent?.extras?.getSerializable(OBJECT) as Genre
                objectID = genre.id
                save =  {
                    genre.name = binding.etAGName.text.toString().trim()
                    genreViewModel.update(genre)
                }
                delete = {
                    genreViewModel.delete(genre)
                }
                favorite = {
                    genre.favorite = !genre.favorite
                    genre.favorite
                }
                binding.etAGName.setText(genre.name, TextView.BufferType.EDITABLE)
                val adapter = MovieListAdapter(this, movieViewModel, false,
                    onDelete = { currentMovie ->
                        movieGenreViewModel.delete(MovieGenre(currentMovie.id, genre.id))
                        if (currentMovie.id !in DatabaseFetcher.Companion.Updates.movies){
                            DatabaseFetcher.Companion.Updates.movies.add(currentMovie.id)
                        }
                    }
                )
                val movies = genreViewModel.getMovies(genre.id)
                originalMovies = movies.value?: emptyList()
                movies.observe(this) { moviesGenres ->
                    moviesGenres.let { moviesGenresList ->
                        moviesInList = List(moviesGenresList.size){
                            moviesGenresList[it]
                        }
                        if(originalMovies == null){
                            originalMovies = moviesInList
                        }
                        movieViewModel.getByID(moviesInList).observe(this) { foundMovies ->
                            foundMovies.let { adapter.submitList(foundMovies)}
                        }
                    }
                }
                binding.rvItems.adapter = adapter
                binding.rvItems.layoutManager = LinearLayoutManager(this)
                binding.ibAGAdd.setOnClickListener {
                    startActivity(Intent(this, ItemPicker::class.java)
                        .putExtra(ItemPicker.PICKED, ItemPicker.MOVIE)
                        .putExtra(ItemPicker.PICKER, ItemPicker.GENRE)
                        .putExtra(ItemPicker.OBJECT_ID, genre.id)
                    )
                }
            }
        }
    }

    override fun finish() {
        if(!saved && objectID != -1L) {
            originalMovies?.forEach {
                if (!moviesInList.contains(it)) {
                    when(editClass){
                        ACTOR -> movieActorViewModel.insert(MovieActor(it, objectID))
                        GENRE -> movieGenreViewModel.insert(MovieGenre(it, objectID))
                    }

                }
            }
            moviesInList.forEach {
                if (originalMovies?.contains(it) == false) {
                    when(editClass){
                        ACTOR -> movieActorViewModel.delete(MovieActor(it, objectID))
                        GENRE -> movieGenreViewModel.delete(MovieGenre(it, objectID))
                    }
                }
            }
        }
        super.finish()
    }
    companion object {
        const val ACTOR = "actor"
        const val GENRE = "genre"
        const val CLASS = "class"
        const val OBJECT = "object"
        const val NEW = "new?"
    }
}