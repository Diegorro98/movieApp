package es.usj.drodriguez.movieapp.database.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.usj.drodriguez.movieapp.R
import es.usj.drodriguez.movieapp.database.classes.Genre
import es.usj.drodriguez.movieapp.utils.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GenreListAdapter: ListAdapter<Genre,GenreListAdapter.GenreViewHolder>(GenreComparator) {
    private lateinit var context : Context
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        context = parent.context
        return GenreViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val currentGenre = getItem(position)
        holder.name.text = currentGenre.name
        CoroutineScope(IO).launch{
            val genreMovies = App().repository.getGenreMovies(currentGenre.id)
            val movies = mutableListOf<String>()
            genreMovies.forEach {
                movies.add(it.title)
            }
            GlobalScope.launch(Dispatchers.Main) {
                holder.movies.text = String.format(
                    context.getString(R.string.tv_actor_genre_item_movies),
                    movies.size
                )
            }
        }
    }

    class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var name: TextView = itemView.findViewById(R.id.tv_actor_genre_name)
        internal var movies: TextView = itemView.findViewById(R.id.tv_actor_genre_movies)
        companion object {
            fun create(parent: ViewGroup): GenreViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.actor_genre_item, parent, false)
                return GenreViewHolder(view)
            }
        }
    }
    object GenreComparator: DiffUtil.ItemCallback<Genre>(){
        override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean = oldItem == newItem

    }
}
