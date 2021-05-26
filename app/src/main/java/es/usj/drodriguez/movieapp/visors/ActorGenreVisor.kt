package es.usj.drodriguez.movieapp.visors

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import es.usj.drodriguez.movieapp.R
import es.usj.drodriguez.movieapp.database.adapters.MovieListAdapter
import es.usj.drodriguez.movieapp.database.classes.Actor
import es.usj.drodriguez.movieapp.database.classes.Genre
import es.usj.drodriguez.movieapp.database.viewmodels.*
import es.usj.drodriguez.movieapp.databinding.ActivityActorGenreVisorBinding
import es.usj.drodriguez.movieapp.editors.ActorGenreEditor
import es.usj.drodriguez.movieapp.utils.DatabaseApp

class ActorGenreVisor : AppCompatActivity() {
    private val movieViewModel: MovieViewModel by viewModels { MovieViewModelFactory((application as DatabaseApp).repository) }
    private lateinit var binding:ActivityActorGenreVisorBinding
    private lateinit var editClass: String
    private lateinit var favorite: () -> Boolean
    private lateinit var editCall: () -> Unit
    private lateinit var delete: () -> Unit
    private lateinit var currentActor: Actor
    private lateinit var currentGenre: Genre
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main_contextual, menu)
        when{
            this::currentActor.isInitialized -> setFavoriteItem(currentActor.favorite,menu?.getItem(1)!!)
            this::currentGenre.isInitialized -> setFavoriteItem(currentGenre.favorite,menu?.getItem(1)!!)
        }
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.btn_tb_contextual_edit -> {
            val intent = Intent(this, ActorGenreEditor::class.java)
            when{
                this::currentActor.isInitialized -> intent.putExtra(ActorGenreEditor.OBJECT, currentActor)
                    .putExtra(ActorGenreEditor.CLASS, ActorGenreEditor.ACTOR)
                    .putExtra(ActorGenreEditor.FAVORITE, currentActor.favorite)
                this::currentGenre.isInitialized -> intent.putExtra(ActorGenreEditor.OBJECT, currentGenre)
                    .putExtra(ActorGenreEditor.CLASS, ActorGenreEditor.GENRE)
                    .putExtra(ActorGenreEditor.FAVORITE, currentGenre.favorite)
            }
            startActivity(intent)
            true
        }
        R.id.btn_tb_contextual_fav -> {
            val favorite = favorite.invoke()
            setFavoriteItem(favorite, item)
            true
        }
        R.id.btn_tb_contextual_delete -> {
            delete.invoke()
            finish()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActorGenreVisorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        editClass = intent?.extras?.getString(CLASS).toString()
        setSupportActionBar(binding.tbAGVisor)
        binding.tbAGVisor.showOverflowMenu()
        binding.tbAGVisor.setNavigationOnClickListener {
            finish()
        }
        when(editClass) {
            ACTOR -> {
                val actorViewModel: ActorViewModel by viewModels { ActorViewModelFactory((application as DatabaseApp).repository) }
                actorViewModel.getByID(intent?.extras?.getLong(OBJECT_ID)!!).observe(this){ actor ->
                    if(actor != null){
                        currentActor = actor
                        invalidateOptionsMenu()
                        supportActionBar?.title = currentActor.name
                    }
                }
                val adapter = MovieListAdapter(this, movieViewModel, false)
                actorViewModel.getMovies(intent?.extras?.getLong(OBJECT_ID)!!).observe(this){ moviesActors ->
                    moviesActors.let { moviesActorsList ->
                        val moviesInList = List(moviesActorsList.size){
                            moviesActorsList[it]
                        }
                        movieViewModel.getByID(moviesInList).observe(this) { foundMovies ->
                            foundMovies.let { adapter.submitList(it)}
                        }
                    }
                }
                binding.rvAGItems.adapter = adapter
                binding.rvAGItems.layoutManager = LinearLayoutManager(this)
                favorite = {
                    actorViewModel.setFavorite(currentActor.id, !currentActor.favorite)
                    !currentActor.favorite
                }
                editCall = {
                    startActivity(Intent(this, ActorGenreEditor::class.java)
                        .putExtra(ActorGenreEditor.OBJECT, currentActor)
                        .putExtra(ActorGenreEditor.CLASS, ActorGenreEditor.ACTOR))
                }
                delete = {
                    actorViewModel.delete(currentActor)
                }
            }
            GENRE -> {
                val genreViewModel: GenreViewModel by viewModels { GenreViewModelFactory((application as DatabaseApp).repository) }
                genreViewModel.getByID(intent?.extras?.getLong(OBJECT_ID)!!).observe(this){ genre ->
                    if(genre != null) {
                        currentGenre = genre
                        invalidateOptionsMenu()
                        supportActionBar?.title = currentGenre.name
                    }
                }
                val adapter = MovieListAdapter(this, movieViewModel, false)
                genreViewModel.getMovies(intent?.extras?.getLong(OBJECT_ID)!!).observe(this){ moviesActors ->
                    moviesActors.let { moviesActorsList ->
                        val moviesInList = List(moviesActorsList.size){
                            moviesActorsList[it]
                        }
                        movieViewModel.getByID(moviesInList).observe(this) { foundMovies ->
                            foundMovies.let { adapter.submitList(it)}
                        }
                    }
                }
                binding.rvAGItems.adapter = adapter
                binding.rvAGItems.layoutManager = LinearLayoutManager(this)
                favorite = {
                    genreViewModel.setFavorite(currentGenre.id, !currentGenre.favorite)
                    !currentGenre.favorite
                }
                editCall = {
                    startActivity(Intent(this, ActorGenreEditor::class.java)
                        .putExtra(ActorGenreEditor.OBJECT, currentGenre)
                        .putExtra(ActorGenreEditor.CLASS, ActorGenreEditor.GENRE))
                }
                delete = {
                    genreViewModel.delete(currentGenre)
                }
            }
        }
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
        const val OBJECT_ID = "object"
    }
}