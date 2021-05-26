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
    private lateinit var currentActor: Actor
    private lateinit var currentGenre: Genre
    private var new = false
    private var saved = false
    private lateinit var save: () -> Unit
    private lateinit var delete: () -> Unit
    private lateinit var favorite: () -> Boolean
    private var originalMovies: List<Long>? = null
    private var moviesInList: List<Long> = emptyList()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_editor, menu)
        when{
            this::currentActor.isInitialized -> setFavoriteItem(currentActor.favorite,menu?.getItem(1)!!)
            this::currentGenre.isInitialized -> setFavoriteItem(currentGenre.favorite,menu?.getItem(1)!!)
        }
        menu?.getItem(0)?.isVisible = false
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.btn_editor_fav -> {
            val favorite = favorite.invoke()
            setFavoriteItem(favorite, item)
            true
        }
        R.id.btn_editor_save -> {
            save.invoke()
            saved = true
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActorGenreEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbAGEditor)
        binding.tbAGEditor.showOverflowMenu()
        binding.tbAGEditor.setNavigationOnClickListener {
            finish()
        }
        new = intent?.extras?.getBoolean(NEW, false) == true
        when(intent?.extras?.getString(CLASS).toString()){
            ACTOR -> {
                val actorViewModel: ActorViewModel by viewModels { ActorViewModelFactory((application as DatabaseApp).repository) }
                supportActionBar?.title = if (new) getString(R.string.actor_editor_new_title) else getString(R.string.actor_editor_edit_title)
                currentActor = intent?.extras?.getSerializable(OBJECT) as Actor
                currentActor.favorite = intent?.extras?.getBoolean(FAVORITE) == true
                invalidateOptionsMenu()
                save = {
                    currentActor.name = binding.etAGName.text.toString().trim()
                    actorViewModel.update(currentActor)
                }
                delete = {actorViewModel.delete(currentActor)}
                favorite = {
                    currentActor.favorite = !currentActor.favorite
                    currentActor.favorite
                }
                binding.etAGName.setText(currentActor.name, TextView.BufferType.EDITABLE)
                val adapter = MovieListAdapter(this, movieViewModel, false,
                    onDelete = { deletedMovie ->
                        movieActorViewModel.delete(MovieActor(deletedMovie.id, currentActor.id))
                        if (deletedMovie.id !in DatabaseFetcher.Companion.Updates.movies){
                            DatabaseFetcher.Companion.Updates.movies.add(deletedMovie.id)
                        }
                    }
                )
                actorViewModel.getMovies(currentActor.id).observe(this) { moviesActors ->
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
                binding.rvAGEditorItems.adapter = adapter
                binding.rvAGEditorItems.layoutManager = LinearLayoutManager(this)
                binding.ibAGAdd.setOnClickListener {
                    startActivity(Intent(this, ItemPicker::class.java)
                        .putExtra(ItemPicker.PICKED, ItemPicker.MOVIE)
                        .putExtra(ItemPicker.PICKER, ItemPicker.ACTOR)
                        .putExtra(ItemPicker.OBJECT_ID, currentActor.id)
                    )
                }
            }

            GENRE -> {
                val genreViewModel: GenreViewModel by viewModels { GenreViewModelFactory((application as DatabaseApp).repository) }
                supportActionBar?.title = if (intent?.extras?.getBoolean(NEW, false) == true) getString(R.string.actor_editor_new_title) else getString(R.string.genre_editor_edit_title)
                currentGenre = intent?.extras?.getSerializable(OBJECT) as Genre
                currentGenre.favorite = intent?.extras?.getBoolean(FAVORITE) == true
                invalidateOptionsMenu()
                save =  {
                    currentGenre.name = binding.etAGName.text.toString().trim()
                    genreViewModel.update(currentGenre)
                }
                delete = {
                    genreViewModel.delete(currentGenre)
                }
                favorite = {
                    currentGenre.favorite = !currentGenre.favorite
                    currentGenre.favorite
                }
                binding.etAGName.setText(currentGenre.name, TextView.BufferType.EDITABLE)
                val adapter = MovieListAdapter(this, movieViewModel, false,
                    onDelete = { currentMovie ->
                        movieGenreViewModel.delete(MovieGenre(currentMovie.id, currentGenre.id))
                        if (currentMovie.id !in DatabaseFetcher.Companion.Updates.movies){
                            DatabaseFetcher.Companion.Updates.movies.add(currentMovie.id)
                        }
                    }
                )
                genreViewModel.getMovies(currentGenre.id).observe(this) { moviesGenres ->
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
                binding.rvAGEditorItems.adapter = adapter
                binding.rvAGEditorItems.layoutManager = LinearLayoutManager(this)
                binding.ibAGAdd.setOnClickListener {
                    startActivity(Intent(this, ItemPicker::class.java)
                        .putExtra(ItemPicker.PICKED, ItemPicker.MOVIE)
                        .putExtra(ItemPicker.PICKER, ItemPicker.GENRE)
                        .putExtra(ItemPicker.OBJECT_ID, currentGenre.id)
                    )
                }
            }
        }
    }

    override fun finish() {
        if(!saved) {
            originalMovies?.forEach {
                if (!moviesInList.contains(it)) {
                    when{
                        this::currentActor.isInitialized -> movieActorViewModel.insert(MovieActor(it, currentActor.id))
                        this::currentGenre.isInitialized -> movieGenreViewModel.insert(MovieGenre(it, currentGenre.id))
                    }

                }
            }
            moviesInList.forEach {
                if (originalMovies?.contains(it) == false) {
                    when{
                        this::currentActor.isInitialized -> movieActorViewModel.delete(MovieActor(it, currentActor.id))
                        this::currentGenre.isInitialized -> movieGenreViewModel.delete(MovieGenre(it, currentGenre.id))
                    }
                }
            }
        }
        super.finish()
    }
    private fun setFavoriteItem(favorite: Boolean, item: MenuItem){
        val favIcon = if(favorite) ContextCompat.getDrawable(this,R.drawable.ic_baseline_star_24) else ContextCompat.getDrawable(this,R.drawable.ic_baseline_star_border_24)
        favIcon?.setTint(getColor(R.color.Toolbar_Primary))
        item.icon = favIcon
        item.title = getString(if(favorite)R.string.title_contextual_rmv_fav else R.string.title_contextual_add_fav)
    }
    companion object {
        const val ACTOR = "actor"
        const val GENRE = "genre"
        const val CLASS = "class"
        const val OBJECT = "object"
        const val FAVORITE = "bugged favorite :)"
        const val NEW = "new?"
    }
}