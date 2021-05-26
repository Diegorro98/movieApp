package es.usj.drodriguez.movieapp.editors

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import es.usj.drodriguez.movieapp.R
import es.usj.drodriguez.movieapp.database.DatabaseFetcher
import es.usj.drodriguez.movieapp.database.adapters.ActorListAdapter
import es.usj.drodriguez.movieapp.database.adapters.GenreListAdapter
import es.usj.drodriguez.movieapp.database.classes.*
import es.usj.drodriguez.movieapp.database.viewmodels.*
import es.usj.drodriguez.movieapp.databinding.ActivityMovieEditorBinding
import es.usj.drodriguez.movieapp.utils.DatabaseApp
import es.usj.drodriguez.movieapp.utils.TextDrawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class MovieEditor : AppCompatActivity() {
    private lateinit var binding: ActivityMovieEditorBinding
    private val movieViewModel: MovieViewModel by viewModels { MovieViewModelFactory((application as DatabaseApp).repository) }
    private val movieActorViewModel: MovieActorViewModel by viewModels { MovieActorViewModelFactory((application as DatabaseApp).repository) }
    private val movieGenreViewModel: MovieGenreViewModel by viewModels { MovieGenreViewModelFactory((application as DatabaseApp).repository) }
    private val actorViewModel: ActorViewModel by viewModels { ActorViewModelFactory((application as DatabaseApp).repository) }
    private val genreViewModel: GenreViewModel by viewModels { GenreViewModelFactory((application as DatabaseApp).repository) }
    private lateinit var currentMovie: Movie
    private var new = false
    private var saved = false
    private var originalActors: List<Long>? = null
    private var originalGenres: List<Long>? = null
    private var actorsInList: List<Long> = emptyList()
    private var genresInList: List<Long> = emptyList()
    private lateinit var picasso : Picasso.Builder
    private var validRating = false

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_editor, menu)
        if (this::currentMovie.isInitialized){
            setFavoriteItem(currentMovie.favorite,menu?.getItem(1)!!)
        }
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.btn_editor_autocomplete -> {
            currentMovie.title = binding.etMovieEditorTitle.text.toString().trim()
            CoroutineScope(IO + Movie.posterFetcherJob).launch{
                val obtainedMovie = currentMovie.askAPI()
                if (obtainedMovie != null){
                    CoroutineScope(Main).launch {
                        loadPoster(obtainedMovie.poster)
                        val thisYear: Int = Calendar.getInstance().get(Calendar.YEAR)
                        binding.etMovieEditorTitle.setText(obtainedMovie.title, TextView.BufferType.EDITABLE)
                        if(obtainedMovie.year in Movie.MIN_YEAR..thisYear) {
                            binding.spinnerMovieEditorYear.setSelection(obtainedMovie.year - Movie.MIN_YEAR)
                        }
                        val runtime = obtainedMovie.runtime.substringBefore(' ',"")
                        binding.etMovieEditorRuntime.setText(runtime, TextView.BufferType.EDITABLE)
                        binding.etMovieEditorDirector.setText(obtainedMovie.director, TextView.BufferType.EDITABLE)
                        binding.etMovieEditorDescription.setText(obtainedMovie.plot, TextView.BufferType.EDITABLE)
                        val revenue = obtainedMovie.boxOffice.replace(",","").replace("$","").toFloat()
                        binding.etMovieEditorRevenue.setText(revenue.div(1000000F).toString(), TextView.BufferType.EDITABLE)
                        binding.etMovieEditorVotes.setText(obtainedMovie.votes.replace(",",""), TextView.BufferType.EDITABLE)
                        binding.etMovieEditorRating.setText(obtainedMovie.rating, TextView.BufferType.EDITABLE)
                    }
                    obtainedMovie.actors.split(',').forEach { actorName->
                        val actorID: Long = try {
                            actorViewModel.getByNameNoFlow(actorName).id
                        }catch (e: java.lang.Exception){
                            actorViewModel.insertReturn(Actor(0,actorName,false))
                        }
                        movieActorViewModel.insert(MovieActor(currentMovie.id, actorID))
                    }
                    obtainedMovie.genres.split(',').forEach { genreName->
                        val genreID: Long = try {
                            genreViewModel.getByNameNoFlow(genreName).id
                        }catch (e: java.lang.Exception){
                            genreViewModel.insertReturn(Genre(0,genreName,false))
                        }
                        movieGenreViewModel.insert(MovieGenre(currentMovie.id, genreID))
                    }
                }
            }
            true
        }
        R.id.btn_editor_fav -> {
            currentMovie.favorite = !currentMovie.favorite
            setFavoriteItem(currentMovie.favorite, item)
            true
        }
        R.id.btn_editor_save -> {
            currentMovie.title = binding.etMovieEditorTitle.text.toString().trim()
            currentMovie.description = binding.etMovieEditorDescription.text.toString().trim()
            currentMovie.director = binding.etMovieEditorDirector.text.toString().trim()
            currentMovie.year = Movie.MIN_YEAR + binding.spinnerMovieEditorYear.selectedItemPosition
            currentMovie.runtime = binding.etMovieEditorRuntime.text.toString().toIntOrNull()?:currentMovie.runtime
            if(validRating) {currentMovie.rating = binding.etMovieEditorRating.text.toString().toFloat()}
            currentMovie.votes = binding.etMovieEditorVotes.text.toString().toIntOrNull()?:currentMovie.votes
            currentMovie.revenue = binding.etMovieEditorRevenue.text.toString().toFloatOrNull()?:currentMovie.revenue
            movieViewModel.update(currentMovie)
            saved = true
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbMovieEditor)
        binding.tbMovieEditor.showOverflowMenu()
        binding.tbMovieEditor.setNavigationOnClickListener {
            finish()
        }
        picasso = Picasso.Builder(this)
        new = intent?.extras?.getBoolean(ActorGenreEditor.NEW, false) == true
        supportActionBar?.title = if(intent?.extras?.getBoolean(NEW, false) == true) getString(R.string.movie_editor_new_title) else getString(R.string.movie_editor_edit_title)
        val years = ArrayList<String>()
        val thisYear: Int = Calendar.getInstance().get(Calendar.YEAR)
        for (i in Movie.MIN_YEAR..thisYear) {
            years.add(i.toString())
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        binding.spinnerMovieEditorYear.adapter = adapter
        currentMovie = (intent?.extras?.getSerializable(OBJECT) as Movie?)!!
        invalidateOptionsMenu()
        binding.imMovieEditorPoster.setOnClickListener {
            CoroutineScope(IO + Movie.posterFetcherJob).launch{
                currentMovie.posterURL = currentMovie.getPosterURLFromOMDbAPI() ?: Movie.MOVIE_NOT_FOUND
                loadPoster(currentMovie.posterURL)
            }
        }
        loadPoster(currentMovie.posterURL)
        binding.etMovieEditorTitle.setText(currentMovie.title, TextView.BufferType.EDITABLE)
        if(currentMovie.year in Movie.MIN_YEAR..thisYear) {
            binding.spinnerMovieEditorYear.setSelection(currentMovie.year - Movie.MIN_YEAR)
        }
        if (currentMovie.runtime != -1){
            binding.etMovieEditorRuntime.setText(currentMovie.runtime.toString(), TextView.BufferType.EDITABLE)
        }
        binding.etMovieEditorDirector.setText(currentMovie.director, TextView.BufferType.EDITABLE)
        binding.etMovieEditorDescription.setText(currentMovie.description, TextView.BufferType.EDITABLE)
        if (currentMovie.revenue != -1F){
            binding.etMovieEditorRevenue.setText(currentMovie.revenue.toString(), TextView.BufferType.EDITABLE)
        }
        if (currentMovie.votes != -1){
            binding.etMovieEditorVotes.setText(currentMovie.votes.toString(), TextView.BufferType.EDITABLE)
        }
        if (currentMovie.rating != -1F){
            binding.etMovieEditorRating.setText(currentMovie.rating.toString(), TextView.BufferType.EDITABLE)
        }
        val actorsAdapter = ActorListAdapter(this, actorViewModel, this, false,
            onDelete = { deletedActor->
                movieActorViewModel.delete(MovieActor(currentMovie.id, deletedActor.id))
                if (currentMovie.id !in DatabaseFetcher.Companion.Updates.movies){
                    DatabaseFetcher.Companion.Updates.movies.add(currentMovie.id)
                }
            }
        )
        movieViewModel.getActors(currentMovie.id).observe(this){ actorsInMovie ->
            actorsInMovie.let { actorsInMovieNotNull->
                actorsInList = List(actorsInMovieNotNull.size){
                    actorsInMovieNotNull[it]
                }
                if(originalActors == null){
                    originalActors = actorsInList
                }
                actorViewModel.getByID(actorsInList).observe(this){ foundActors ->
                    foundActors.let {actorsAdapter.submitList(it)}
                }
            }
        }
        binding.rvMovieEditorActors.adapter = actorsAdapter
        binding.rvMovieEditorActors.layoutManager = LinearLayoutManager(this)
        binding.ibMovieEditorSelectActors.setOnClickListener {
            startActivity(
                Intent(this, ItemPicker::class.java)
                .putExtra(ItemPicker.PICKED, ItemPicker.ACTOR)
                .putExtra(ItemPicker.PICKER, ItemPicker.MOVIE)
                .putExtra(ItemPicker.OBJECT_ID, currentMovie.id)
            )
        }
        val genresAdapter = GenreListAdapter(this, genreViewModel, this, false,
            onDelete = { deletedGenre->
                movieGenreViewModel.delete(MovieGenre(currentMovie.id, deletedGenre.id))
                if (currentMovie.id !in DatabaseFetcher.Companion.Updates.movies){
                    DatabaseFetcher.Companion.Updates.movies.add(currentMovie.id)
                }
            }
        )
        movieViewModel.getGenres(currentMovie.id).observe(this){ genresInMovie ->
            genresInMovie.let { genresInMovieNotNull->
                genresInList = List(genresInMovieNotNull.size){
                    genresInMovieNotNull[it]
                }
                if(originalGenres == null){
                    originalGenres = genresInList
                }
                genreViewModel.getByID(genresInList).observe(this){ foundGenres ->
                    foundGenres.let {genresAdapter.submitList(it)}
                }
            }
        }
        binding.rvMovieEditorGenres.adapter = genresAdapter
        binding.rvMovieEditorGenres.layoutManager = LinearLayoutManager(this)
        binding.ibMovieEditorSelectGenres.setOnClickListener {
            startActivity(
                Intent(this, ItemPicker::class.java)
                    .putExtra(ItemPicker.PICKED, ItemPicker.GENRE)
                    .putExtra(ItemPicker.PICKER, ItemPicker.MOVIE)
                    .putExtra(ItemPicker.OBJECT_ID, currentMovie.id)
            )
        }
        binding.tvMovieEditorActors.setOnClickListener {
            if (binding.rvMovieEditorActors.visibility == View.GONE){
                binding.rvMovieEditorActors.visibility = View.VISIBLE
                binding.ibMovieEditorSelectActors.visibility = View.VISIBLE
                binding.ibMovieEditorShowActors.rotation =  binding.ibMovieEditorShowActors.rotation + 180
            } else if (binding.rvMovieEditorActors.visibility == View.VISIBLE){
                binding.rvMovieEditorActors.visibility = View.GONE
                binding.ibMovieEditorSelectActors.visibility = View.GONE
                binding.ibMovieEditorShowActors.rotation =  binding.ibMovieEditorShowActors.rotation - 180
            }
        }
        binding.tvMovieEditorGenres.setOnClickListener {
            if (binding.rvMovieEditorGenres.visibility == View.GONE){
                binding.rvMovieEditorGenres.visibility = View.VISIBLE
                binding.ibMovieEditorSelectGenres.visibility = View.VISIBLE
                binding.ibMovieEditorShowGenres.rotation =  binding.ibMovieEditorShowGenres.rotation + 180
            } else if (binding.rvMovieEditorGenres.visibility == View.VISIBLE){
                binding.rvMovieEditorGenres.visibility = View.GONE
                binding.ibMovieEditorSelectGenres.visibility = View.GONE
                binding.ibMovieEditorShowGenres.rotation =  binding.ibMovieEditorShowGenres.rotation - 180
            }
        }
        binding.etMovieEditorRating.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.isNullOrEmpty()){
                    validRating = s.toString().trim().toFloat() in 0F..10F
                    binding.tvMovieEditorRatingLimits.visibility = if(!validRating) View.VISIBLE else View.GONE
                }
            }
        })
    }

    override fun finish() {
        if(!saved){
            originalActors?.forEach {
                if(!actorsInList.contains(it)){
                    movieActorViewModel.insert(MovieActor(currentMovie.id, it))
                }
            }
            actorsInList.forEach {
                if(originalActors?.contains(it) == false){
                    movieActorViewModel.delete(MovieActor(currentMovie.id, it))
                }
            }
            originalGenres?.forEach {
                if(!genresInList.contains(it)){
                    movieActorViewModel.insert(MovieActor(currentMovie.id, it))
                }
            }
            genresInList.forEach {
                if(originalGenres?.contains(it) == false){
                    movieActorViewModel.delete(MovieActor(currentMovie.id, it))
                }
            }
        }
        super.finish()
    }
    private fun loadPoster(posterURL: String? = null){
        CoroutineScope(Main).launch {
            if (posterURL != null && posterURL != Movie.MOVIE_NOT_FOUND) {
                Picasso.get().load(posterURL).into(binding.imMovieEditorPoster, object: Callback {
                    override fun onSuccess() {}
                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        val errorText = TextDrawable(this@MovieEditor)
                        errorText.text = "error"
                        errorText.textAlign = Layout.Alignment.ALIGN_CENTER
                        binding.imMovieEditorPoster.setImageDrawable(errorText)
                    }
                })
            } else if(posterURL == Movie.MOVIE_NOT_FOUND){
                val movieNotFound = TextDrawable(this@MovieEditor)
                movieNotFound.text = Movie.MOVIE_NOT_FOUND
                movieNotFound.textAlign = Layout.Alignment.ALIGN_CENTER
                binding.imMovieEditorPoster.setImageDrawable(movieNotFound)
            } else {
                val movieNotFound = TextDrawable(this@MovieEditor)
                movieNotFound.text = "Press to search"
                movieNotFound.textAlign = Layout.Alignment.ALIGN_CENTER
                binding.imMovieEditorPoster.setImageDrawable(movieNotFound)
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
        const val NEW = "New?"
        const val OBJECT = "Movie object"
    }
}