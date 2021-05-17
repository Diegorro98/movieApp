package es.usj.drodriguez.movieapp.database.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import es.usj.drodriguez.movieapp.MainActivity
import es.usj.drodriguez.movieapp.R
import es.usj.drodriguez.movieapp.database.classes.Actor
import es.usj.drodriguez.movieapp.database.viewmodels.ActorViewModel
import es.usj.drodriguez.movieapp.editors.ActorGenreEditor
import es.usj.drodriguez.movieapp.utils.DatabaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class ActorListAdapter(private val activity: Activity?, private val actorViewModel: ActorViewModel): ListAdapter<Actor, ActorGenreHolder>(ActorComparator) {
    private lateinit var context : Context
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorGenreHolder {
        context = parent.context
        return ActorGenreHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ActorGenreHolder, position: Int) {
        val currentActor = getItem(position)
        holder.name.text = currentActor.name
        /*CoroutineScope(IO).launch{
            //TODO integrate flow
            val movies = DatabaseApp().repository.getActorMovies(currentActor.id).toList()
            GlobalScope.launch(Dispatchers.Main) {
                holder.movies.text = String.format(
                    context.getString(R.string.tv_actor_genre_item_movies),
                    movies.size
                )
            }
        }*/
        holder.favorite.visibility = if(currentActor.favorite) View.VISIBLE else View.INVISIBLE
        holder.cardView.setOnClickListener {
            TODO("startActivity(context ,Intent(context, ActorGenreVisor::class.java).putExtra(ActorGenreVisor.OBJECT, currentMovie), null)")

        }
        holder.cardView.setOnLongClickListener {
            if (!it.isSelected) {
                holder.cardView.backgroundTintList =  ColorStateList.valueOf(context.getColor(R.color.selected_item))
                it.isSelected = true
                val callback = object : ActionMode.Callback {
                    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                        MainActivity.contextualToolbar = mode
                        activity?.menuInflater?.inflate(R.menu.toolbar_main_contextual, menu)
                        if (currentActor.favorite){
                            menu?.getItem(1)?.icon = getDrawable(context,R.drawable.ic_baseline_star_border_24)
                            menu?.getItem(1)?.title = context.getString(R.string.title_contextual_rmv_fav)
                        }else{
                            menu?.getItem(1)?.icon = getDrawable(context,R.drawable.ic_baseline_star_24)
                            menu?.getItem(1)?.title = context.getString(R.string.title_contextual_add_fav)
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
                                    Intent(context, ActorGenreEditor::class.java)
                                        .putExtra(ActorGenreEditor.OBJECT, currentActor)
                                        .putExtra(ActorGenreEditor.CLASS,ActorGenreEditor.ACTOR),
                                    null
                                )
                                true
                            }
                            R.id.btn_set_fav -> {
                                actorViewModel.setFavorite(currentActor.id, !currentActor.favorite)
                                true
                            }
                            R.id.btn_delete -> {
                                actorViewModel.delete(currentActor)
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
    object ActorComparator: DiffUtil.ItemCallback<Actor>(){
        override fun areItemsTheSame(oldItem: Actor, newItem: Actor): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Actor, newItem: Actor): Boolean = oldItem == newItem
    }
}
