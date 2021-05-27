package es.usj.drodriguez.movieapp.database.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Layout
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import es.usj.drodriguez.movieapp.MainActivity
import es.usj.drodriguez.movieapp.R
import es.usj.drodriguez.movieapp.database.DatabaseFetcher
import es.usj.drodriguez.movieapp.database.classes.Movie
import es.usj.drodriguez.movieapp.database.viewmodels.MovieViewModel
import es.usj.drodriguez.movieapp.editors.MovieEditor
import es.usj.drodriguez.movieapp.utils.TextDrawable
import es.usj.drodriguez.movieapp.visors.MovieVisor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


class MovieListAdapter(
    private val activity: Activity,
    private val movieViewModel: MovieViewModel,
    private val editButton: Boolean = true,
    private val onFavorite: ((currentMovie: Movie) -> Unit)? = null,
    private val onDelete: ((currentMovie: Movie) -> Unit)? = null,
    private val onCardClick: (cardView: View, currentMovie: Movie, context: Context) -> Unit = { _, currentMovie, context ->
        startActivity(context ,Intent(context, MovieVisor::class.java).putExtra(MovieVisor.OBJECT_ID, currentMovie.id), null)
    }): ListAdapter<Movie,MovieListAdapter.MovieViewHolder>(MovieComparator) {
    var selectedMovies: List<Long> = emptyList()
    private lateinit var context : Context
    private lateinit var picasso : Picasso.Builder
    var showOnlyFavorites = false
    var textFiler: String = ""
    private var originalList = emptyList<Movie>()
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        context = parent.context
        picasso = Picasso.Builder(context)
        return MovieViewHolder.create(parent)
    }

    override fun onViewRecycled(holder: MovieViewHolder) {
        super.onViewRecycled(holder)
        holder.poster.setImageBitmap(null)
    }
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentMovie = getItem(position)
        holder.cardView.visibility =
            if (showOnlyFavorites && !currentMovie.favorite) View.GONE else View.VISIBLE

        if (currentMovie.posterURL != null && currentMovie.posterURL != Movie.MOVIE_NOT_FOUND) {
            Picasso.get().load(currentMovie.posterURL).into(holder.poster, object: Callback {
                override fun onSuccess() {}
                override fun onError(e: Exception?) {
                    e?.printStackTrace()
                    val errorText = TextDrawable(context)
                    errorText.text = "error"
                    errorText.textAlign = Layout.Alignment.ALIGN_CENTER
                    holder.poster.setImageDrawable(errorText)
                }
            })
        } else if(currentMovie.posterURL == Movie.MOVIE_NOT_FOUND){
            val movieNotFound = TextDrawable(context)
            movieNotFound.text = Movie.MOVIE_NOT_FOUND
            movieNotFound.textAlign = Layout.Alignment.ALIGN_CENTER
            holder.poster.setImageDrawable(movieNotFound)

        }else {
            CoroutineScope(IO + Movie.posterFetcherJob).launch{
                currentMovie.getPosterURLFromOMDbAPI()?.let { posterURL->
                    movieViewModel.setPosterURL(currentMovie, posterURL)
                } ?: movieViewModel.setPosterURL(currentMovie, Movie.MOVIE_NOT_FOUND)
            }
        }
        holder.title.text = currentMovie.title
        holder.description.text = currentMovie.description
        holder.year.text = currentMovie.year.toString()
        holder.runtime.text = String.format(context.getString(R.string.tv_movie_item_runtime),currentMovie.runtime)
        holder.rating.text = currentMovie.rating.toString()
        DrawableCompat.setTint(DrawableCompat.wrap(holder.ratingBackground.drawable), context.getColor(when{
            9 <= currentMovie.rating -> R.color.good_rating
            8 <= currentMovie.rating && currentMovie.rating < 9 -> R.color.mid_good_rating
            6 <= currentMovie.rating && currentMovie.rating < 8 -> R.color.mid_rating
            5 <= currentMovie.rating && currentMovie.rating < 6 -> R.color.mid_bad_rating
            else -> R.color.bad_rating
        }))
        holder.favorite.visibility = if(currentMovie.favorite) View.VISIBLE else View.INVISIBLE
        if (selectedMovies.contains(currentMovie.id)){
            setSelect(true, holder.cardView)
        }else{
            setSelect(false, holder.cardView)
        }
        holder.cardView.setOnClickListener {
            onCardClick.invoke(it,currentMovie, context)
        }
        if (editButton || onFavorite != null || onDelete != null) {
            holder.cardView.setOnLongClickListener {
                if (!it.isSelected) {
                    setSelect(true, it)
                    val callback = object : ActionMode.Callback {
                        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                            MainActivity.contextualToolbar = mode
                            activity.menuInflater.inflate(R.menu.toolbar_main_contextual, menu)
                            if (!editButton) {
                                menu?.getItem(0)?.isVisible = false
                                menu?.getItem(0)?.isEnabled = false
                            }
                            if (onFavorite != null) {
                                if (currentMovie.favorite) {
                                    val drawable = getDrawable(context, R.drawable.ic_baseline_star_border_24)
                                    drawable?.setTint(Color.BLACK)
                                    menu?.getItem(1)?.icon = drawable
                                    menu?.getItem(1)?.title =
                                        context.getString(R.string.title_contextual_rmv_fav)
                                } else {
                                    val drawable = getDrawable(context, R.drawable.ic_baseline_star_24)
                                    drawable?.setTint(Color.BLACK)
                                    menu?.getItem(1)?.icon = drawable
                                    menu?.getItem(1)?.title =
                                        context.getString(R.string.title_contextual_add_fav)
                                }
                            } else {
                                menu?.getItem(1)?.isVisible = false
                                menu?.getItem(1)?.isEnabled = false
                            }
                            if (onDelete == null) {
                                menu?.getItem(2)?.isVisible = false
                                menu?.getItem(2)?.isEnabled = false
                            }
                            return true
                        }

                        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean =
                            false

                        override fun onActionItemClicked(
                            mode: ActionMode?,
                            item: MenuItem?
                        ): Boolean {
                            mode?.finish()
                            return when (item?.itemId) {
                                R.id.btn_tb_contextual_edit -> {
                                    startActivity(
                                        context,
                                        Intent(context, MovieEditor::class.java)
                                            .putExtra(MovieEditor.OBJECT, currentMovie),
                                        null
                                    )
                                    true
                                }
                                R.id.btn_tb_contextual_fav -> {
                                    onFavorite?.invoke(currentMovie)
                                    true
                                }
                                R.id.btn_tb_contextual_delete -> {
                                    onDelete?.invoke(currentMovie)
                                    if(DatabaseFetcher.added.movies.contains(currentMovie)){
                                        DatabaseFetcher.added.movies.remove(currentMovie)
                                    }else{
                                        if (DatabaseFetcher.updated.movies.contains(currentMovie)){
                                            DatabaseFetcher.updated.movies.remove(currentMovie)
                                        }
                                        DatabaseFetcher.deleted.movies.add(currentMovie)
                                    }
                                    true
                                }
                                else -> false
                            }
                        }

                        override fun onDestroyActionMode(mode: ActionMode?) {
                            MainActivity.contextualToolbar = null
                            setSelect(false, it)
                        }
                    }
                    (activity as AppCompatActivity?)?.startSupportActionMode(callback)
                } else {
                    MainActivity.contextualToolbar?.finish()
                }
                return@setOnLongClickListener true
            }
        }
    }
    private fun setSelect(selected: Boolean, cardView: View){
        cardView.isSelected = selected
        cardView.backgroundTintList = if(selected) ColorStateList.valueOf(context.getColor(R.color.selected_item)) else null
    }
    override fun submitList(list: List<Movie>?) {
        var listToSubmit = list
        if (list != null) {
            originalList = list
            when {
                showOnlyFavorites && textFiler.isEmpty()-> listToSubmit = filterFavorites(list)
                showOnlyFavorites && textFiler.isNotEmpty() -> listToSubmit = filterTitles(filterFavorites(list))
                !showOnlyFavorites && textFiler.isNotEmpty() -> listToSubmit = filterTitles(list)
            }
        }
        super.submitList(listToSubmit)
    }
    fun resubmitList(){
        submitList(originalList.toMutableList())
    }
    private fun filterFavorites(list: List<Movie>): List<Movie> {
        val favoriteList = mutableListOf<Movie>()
        list.forEach {
            if (it.favorite){
                favoriteList.add(it)
            }
        }
        return favoriteList
    }
    private fun filterTitles(list: List<Movie>): List<Movie> {
        val filteredTitles = mutableListOf<Movie>()
        list.forEach{
            if (it.title.lowercase().contains(textFiler)){
                filteredTitles.add(it)
            }
        }
        return filteredTitles
    }
    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var title: TextView = itemView.findViewById(R.id.tv_movie_title)
        internal var year: TextView = itemView.findViewById(R.id.tv_movie_year)
        internal var runtime: TextView = itemView.findViewById(R.id.tv_movie_runtime)
        internal var rating: TextView = itemView.findViewById(R.id.tv_movie_rating)
        internal var ratingBackground: ImageView = itemView.findViewById(R.id.im_movie_ratingBackground)
        internal var favorite: ImageView = itemView.findViewById(R.id.iv_movie_item_fav)
        internal var cardView: CardView = itemView.findViewById(R.id.card_view_movie)
        internal var poster: ImageView = itemView.findViewById(R.id.im_movie_poster)
        internal var description:TextView = itemView.findViewById(R.id.tv_movie_description)
        companion object {
            fun create(parent: ViewGroup): MovieViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.movie_item, parent, false)
                return MovieViewHolder(view)
            }
        }
    }
    object MovieComparator: DiffUtil.ItemCallback<Movie>(){
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem
    }
}
