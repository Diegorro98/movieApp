package es.usj.drodriguez.movieapp.database.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.usj.drodriguez.movieapp.R
import es.usj.drodriguez.movieapp.database.classes.Actor
import es.usj.drodriguez.movieapp.editors.ActorGenreEditor
import es.usj.drodriguez.movieapp.utils.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ActorListAdapter: ListAdapter<Actor,ActorListAdapter.ActorViewHolder>(ActorComparator) {
    private lateinit var context : Context
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorViewHolder {
        context = parent.context
        return ActorViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        val currentActor = getItem(position)
        holder.name.text = currentActor.name
        CoroutineScope(IO).launch{
            val actorMovies = App().repository.getActorsMovies(currentActor.id)
            val movies = mutableListOf<String>()
            actorMovies.forEach {
                movies.add(it.title)
            }
            GlobalScope.launch(Dispatchers.Main) {
                holder.movies.text = String.format(
                    context.getString(R.string.tv_actor_genre_item_movies),
                    movies.size
                )
            }
        }
        holder.cardView.setOnClickListener {
            startActivity(
                context,
                Intent(context, ActorGenreEditor::class.java)
                    .putExtra(ActorGenreEditor.CLASS, ActorGenreEditor.ACTOR)
                    .putExtra(ActorGenreEditor.OBJECT, currentActor),
                null
            )
        }
    }

    class ActorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var name: TextView = itemView.findViewById(R.id.tv_actor_genre_name)
        internal var movies: TextView = itemView.findViewById(R.id.tv_actor_genre_movies)
        internal var cardView: CardView = itemView.findViewById(R.id.card_view_actor_genre)
        companion object {
            fun create(parent: ViewGroup): ActorViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.actor_genre_item, parent, false)
                return ActorViewHolder(view)
            }
        }
    }
    object ActorComparator: DiffUtil.ItemCallback<Actor>(){
        override fun areItemsTheSame(oldItem: Actor, newItem: Actor): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Actor, newItem: Actor): Boolean = oldItem == newItem

    }
}
