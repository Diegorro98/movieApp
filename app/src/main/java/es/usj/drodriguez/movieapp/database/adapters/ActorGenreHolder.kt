package es.usj.drodriguez.movieapp.database.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import es.usj.drodriguez.movieapp.R

class ActorGenreHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    internal var name: TextView = itemView.findViewById(R.id.tv_actor_genre_name)
    internal var movies: TextView = itemView.findViewById(R.id.tv_actor_genre_movies)
    internal var cardView: CardView = itemView.findViewById(R.id.card_view_actor_genre)
    internal var favorite: ImageView = itemView.findViewById(R.id.iv_actor_genre_item_fav)
    companion object {
        fun create(parent: ViewGroup): ActorGenreHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.actor_genre_item, parent, false)
            return ActorGenreHolder(view)
        }
    }
}