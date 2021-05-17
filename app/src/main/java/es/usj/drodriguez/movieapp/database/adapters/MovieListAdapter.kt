package es.usj.drodriguez.movieapp.database.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
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
import es.usj.drodriguez.movieapp.MainActivity
import es.usj.drodriguez.movieapp.R
import es.usj.drodriguez.movieapp.database.classes.Movie
import es.usj.drodriguez.movieapp.editors.MovieEditor
import es.usj.drodriguez.movieapp.utils.DatabaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class MovieListAdapter(
    private val activity: Activity?,
    //private val movieViewModel: MovieViewModel, todo delete
    private val editButton: Boolean = true,
    private val onFavorite: ( (currentMovie: Movie) -> Unit)? = null,
    private val onDelete: ( (currentMovie: Movie) -> Unit)? = null
    ): ListAdapter<Movie,MovieListAdapter.MovieViewHolder>(MovieComparator) {
    private lateinit var context : Context
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        context = parent.context
        return MovieViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentMovie = getItem(position)
        holder.title.text = currentMovie.title
        holder.year.text = currentMovie.year.toString()
        /*CoroutineScope(Dispatchers.IO).launch{
            val text = DatabaseApp().repository.getMovieGenres(currentMovie.id).toList().joinToString()
            println("genres: $text")
            holder.genre.text = text
        }*/
        holder.runtime.text = String.format(context.getString(R.string.tv_movie_item_runtime),currentMovie.runtime)
        holder.rating.text = currentMovie.rating.toString()
        DrawableCompat.setTint(DrawableCompat.wrap(holder.ratingBackground.drawable), context.getColor(when{
            8 <= currentMovie.rating -> R.color.good_rating
            6 <= currentMovie.rating && currentMovie.rating < 8 -> R.color.mid_rating
            5 <= currentMovie.rating && currentMovie.rating < 6 -> R.color.mid_bad_rating
            else -> R.color.bad_rating
        }))
        holder.favorite.visibility = if(currentMovie.favorite) View.VISIBLE else View.INVISIBLE
        holder.cardView.setOnClickListener {
            TODO("startActivity(context ,Intent(context, MovieVisor::class.java).putExtra(MovieVisor.OBJECT, currentMovie), null)")
        }
        holder.cardView.setOnLongClickListener {
            if (!it.isSelected) {
                it.isSelected = true
                holder.cardView.backgroundTintList =  ColorStateList.valueOf(context.getColor(R.color.selected_item))
                val callback = object : ActionMode.Callback {
                    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                        MainActivity.contextualToolbar = mode
                        activity?.menuInflater?.inflate(R.menu.toolbar_main_contextual, menu)
                        if (!editButton){
                            menu?.getItem(0)?.isVisible = false
                            menu?.getItem(0)?.isEnabled = false
                        }
                        if (onFavorite != null) {
                            if (currentMovie.favorite) {
                                menu?.getItem(1)?.icon = getDrawable(context, R.drawable.ic_baseline_star_border_24)
                                menu?.getItem(1)?.title = context.getString(R.string.title_contextual_rmv_fav)
                            } else {
                                menu?.getItem(1)?.icon = getDrawable(context, R.drawable.ic_baseline_star_24)
                                menu?.getItem(1)?.title = context.getString(R.string.title_contextual_add_fav)
                            }
                        } else {
                            menu?.getItem(1)?.isVisible = false
                            menu?.getItem(1)?.isEnabled = false
                        }
                        if (onDelete == null){
                            menu?.getItem(2)?.isVisible = false
                            menu?.getItem(2)?.isEnabled = false
                        }
                        return true
                    }

                    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean =
                        false

                    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                        mode?.finish()
                        return when (item?.itemId) {
                            R.id.btn_edit -> {
                                startActivity(
                                    context,
                                    Intent(context, MovieEditor::class.java)
                                        .putExtra(MovieEditor.OBJECT, currentMovie),
                                    null
                                )
                                true
                            }
                            R.id.btn_set_fav -> {
                                onFavorite?.invoke(currentMovie)
                                //movieViewModel.setFavorite(currentMovie.id, !currentMovie.favorite) TODO delete
                                true
                            }
                            R.id.btn_delete -> {
                                //movieViewModel.delete(currentMovie) TODO delete
                                true
                            }
                            else -> false
                        }
                    }

                    override fun onDestroyActionMode(mode: ActionMode?) {
                        MainActivity.contextualToolbar = null
                        it.isSelected = false
                        holder.cardView.backgroundTintList = null
                    }
                }
                (activity as AppCompatActivity?)?.startSupportActionMode(callback)
            } else {
                MainActivity.contextualToolbar?.finish()
            }
            return@setOnLongClickListener true
        }
    }

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var title: TextView = itemView.findViewById(R.id.tv_movie_title)
        internal var year: TextView = itemView.findViewById(R.id.tv_movie_year)
        internal var genre: TextView = itemView.findViewById(R.id.tv_movie_genre)
        internal var runtime: TextView = itemView.findViewById(R.id.tv_movie_runtime)
        internal var rating: TextView = itemView.findViewById(R.id.tv_movie_rating)
        internal var ratingBackground: ImageView = itemView.findViewById(R.id.im_movie_ratingBackground)
        internal var favorite: ImageView = itemView.findViewById(R.id.iv_movie_item_fav)
        internal var cardView: CardView = itemView.findViewById(R.id.card_view_movie)
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
