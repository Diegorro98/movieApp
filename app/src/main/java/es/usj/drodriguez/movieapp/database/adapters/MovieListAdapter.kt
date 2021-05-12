package es.usj.drodriguez.movieapp.database.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.usj.drodriguez.movieapp.R
import es.usj.drodriguez.movieapp.database.classes.Movie
import es.usj.drodriguez.movieapp.utils.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MovieListAdapter: ListAdapter<Movie,MovieListAdapter.MovieViewHolder>(MovieComparator) {
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
        CoroutineScope(IO).launch{
            val genres = mutableListOf<String>()
            currentMovie.genres.forEach {
                genres.add(App().repository.getGenreByID(it).name)
                GlobalScope.launch(Dispatchers.Main) {
                    holder.genre.text = genres.joinToString()
                }
            }
        }
        holder.runtime.text = String.format(context.getString(R.string.tv_movie_item_runtime),currentMovie.runtime)
        holder.rating.text = currentMovie.rating.toString()
        DrawableCompat.setTint(DrawableCompat.wrap(holder.ratingBackground.drawable), context.getColor(when{
            8 <= currentMovie.rating -> R.color.good_rating
            6 <= currentMovie.rating && currentMovie.rating < 8 -> R.color.mid_rating
            5 <= currentMovie.rating && currentMovie.rating < 6 -> R.color.mid_bad_rating
            else -> R.color.bad_rating
        }))
    }

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var title: TextView = itemView.findViewById(R.id.tv_movie_title)
        internal var year: TextView = itemView.findViewById(R.id.tv_movie_year)
        internal var genre: TextView = itemView.findViewById(R.id.tv_movie_genre)
        internal var runtime: TextView = itemView.findViewById(R.id.tv_movie_runtime)
        internal var rating: TextView = itemView.findViewById(R.id.tv_movie_rating)
        internal var ratingBackground: ImageView = itemView.findViewById(R.id.im_movie_ratingBackground)
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
