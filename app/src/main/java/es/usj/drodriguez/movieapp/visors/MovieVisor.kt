package es.usj.drodriguez.movieapp.visors

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import es.usj.drodriguez.movieapp.R
import es.usj.drodriguez.movieapp.database.adapters.ActorListAdapter
import es.usj.drodriguez.movieapp.database.adapters.GenreListAdapter
import es.usj.drodriguez.movieapp.database.classes.Movie
import es.usj.drodriguez.movieapp.database.viewmodels.*
import es.usj.drodriguez.movieapp.databinding.ActivityMovieVisorBinding
import es.usj.drodriguez.movieapp.editors.MovieEditor
import es.usj.drodriguez.movieapp.utils.DatabaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class MovieVisor : AppCompatActivity() {
    private val movieViewModel: MovieViewModel by viewModels { MovieViewModelFactory((application as DatabaseApp).repository) }
    private val actorViewModel: ActorViewModel by viewModels { ActorViewModelFactory((application as DatabaseApp).repository) }
    private val genreViewModel: GenreViewModel by viewModels { GenreViewModelFactory((application as DatabaseApp).repository) }

    private lateinit var binding:ActivityMovieVisorBinding
    private lateinit var picasso : Picasso.Builder
    private lateinit var currentMovie: Movie
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main_contextual, menu)
        if (this::currentMovie.isInitialized){
            setFavoriteItem(currentMovie.favorite,menu?.getItem(1)!!)
        }
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.btn_tb_contextual_edit -> {
            startActivity(Intent(this,MovieEditor::class.java)
                    .putExtra(MovieEditor.OBJECT, currentMovie)
            )
            true
        }
        R.id.btn_tb_contextual_fav -> {
            movieViewModel.setFavorite(currentMovie.id, !currentMovie.favorite)
            true
        }
        R.id.btn_tb_contextual_delete -> {
            movieViewModel.delete(currentMovie)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieVisorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbMovieVisor)
        binding.tbMovieVisor.showOverflowMenu()
        binding.tbMovieVisor.setNavigationOnClickListener {
            finish()
        }
        picasso = Picasso.Builder(this)
        movieViewModel.getByID(intent?.extras?.getLong(ActorGenreVisor.OBJECT_ID)!!).observe(this){movie ->
            if(movie != null){
                CoroutineScope(Main).launch {
                    if (movie.posterURL != null && movie.posterURL != Movie.MOVIE_NOT_FOUND) {
                        Picasso.get().load(movie.posterURL).into(binding.imToolbarPoster, object:Callback{
                            override fun onSuccess() {
                                //ColorUtils.calculateContrast()
                            }
                            override fun onError(e: Exception?) {}
                        })
                    }
                }
                currentMovie = movie
                invalidateOptionsMenu()
                binding.tbMovieVisor.title = movie.title
                binding.tvMovieVisorRating.text = movie.rating.toString()
                DrawableCompat.setTint(
                    DrawableCompat.wrap(binding.imMovieVisorRatingBackground.drawable), getColor(when{
                    9 <= movie.rating -> R.color.good_rating
                    8 <= movie.rating && movie.rating < 9 -> R.color.mid_good_rating
                    6 <= movie.rating && movie.rating < 8 -> R.color.mid_rating
                    5 <= movie.rating && movie.rating < 6 -> R.color.mid_bad_rating
                    else -> R.color.bad_rating
                }))
                binding.tvMovieVisorYear.text = movie.year.toString()
                binding.tvMovieVisorRuntime.text = String.format(getString(R.string.tv_movie_item_runtime),movie.runtime)
                binding.tvMovieVisorDirector.text = movie.director
                binding.tvMovieVisorDescription.text = movie.description
                binding.tvMovieVisorRevenue.text = movie.revenue.toString()
                binding.tvMovieVisorVotes.text = movie.votes.toString()
                val actorsAdapter = ActorListAdapter(this, actorViewModel, this, false)
                movieViewModel.getActors(movie.id).observe(this){actorsInMovie ->
                    actorsInMovie.let { actorsInMovieNotNull->
                        val actorsInList = List(actorsInMovieNotNull.size){
                            actorsInMovieNotNull[it]
                        }
                        actorViewModel.getByID(actorsInList).observe(this){ foundActors ->
                            foundActors.let {actorsAdapter.submitList(it)}
                        }
                    }
                }
                binding.rvMovieVisorActors.adapter = actorsAdapter
                binding.rvMovieVisorActors.layoutManager = LinearLayoutManager(this)
                binding.tvMovieVisorActors.setOnClickListener {
                    if (binding.rvMovieVisorActors.visibility == View.GONE){
                        binding.rvMovieVisorActors.visibility = View.VISIBLE
                        binding.ibMovieVisorShowActors.rotation =  binding.ibMovieVisorShowActors.rotation + 180
                    } else if (binding.rvMovieVisorActors.visibility == View.VISIBLE){
                        binding.rvMovieVisorActors.visibility = View.GONE
                        binding.ibMovieVisorShowActors.rotation =  binding.ibMovieVisorShowActors.rotation - 180
                    }
                }
                binding.tvMovieVisorYear.text = movie.year.toString()
                binding.tvMovieVisorRuntime.text = String.format(getString(R.string.tv_movie_item_runtime),movie.runtime)
                binding.tvMovieVisorDirector.text = movie.director
                binding.tvMovieVisorDescription.text = movie.description
                binding.tvMovieVisorRevenue.text = movie.revenue.toString()
                binding.tvMovieVisorVotes.text = movie.votes.toString()
                val genresAdapter = GenreListAdapter(this, genreViewModel, this, false)
                movieViewModel.getGenres(movie.id).observe(this){genresInMovie ->
                    genresInMovie.let { genresInMovieNotNull->
                        val genresInList = List(genresInMovieNotNull.size){
                            genresInMovieNotNull[it]
                        }
                        genreViewModel.getByID(genresInList).observe(this){ foundGenres ->
                            foundGenres.let {genresAdapter.submitList(it)}
                        }
                    }
                }
                binding.rvMovieVisorGenres.adapter = genresAdapter
                binding.rvMovieVisorGenres.layoutManager = LinearLayoutManager(this)
                binding.tvMovieVisorGenres.setOnClickListener {
                    if (binding.rvMovieVisorGenres.visibility == View.GONE){
                        binding.rvMovieVisorGenres.visibility = View.VISIBLE
                        binding.ibMovieVisorShowGenres.rotation =  binding.ibMovieVisorShowActors.rotation + 180
                    } else if (binding.rvMovieVisorGenres.visibility == View.VISIBLE){
                        binding.rvMovieVisorGenres.visibility = View.GONE
                        binding.ibMovieVisorShowGenres.rotation =  binding.ibMovieVisorShowActors.rotation - 180
                    }
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
        const val OBJECT_ID = "object"
    }
}