package es.usj.drodriguez.movieapp.database.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import es.usj.drodriguez.movieapp.MainActivity
import es.usj.drodriguez.movieapp.R
import es.usj.drodriguez.movieapp.database.classes.Actor
import es.usj.drodriguez.movieapp.database.viewmodels.ActorViewModel
import es.usj.drodriguez.movieapp.editors.ActorGenreEditor

class ActorListAdapter(
    private val activity: Activity?,
    private val actorViewModel: ActorViewModel,
    private val viewLifecycleOwner: LifecycleOwner,
    private val editButton: Boolean = true,
    private val onFavorite: ( (currentActor: Actor) -> Unit)? = null,
    private val onDelete: ( (currentActor: Actor) -> Unit)? = null,
    private val onCardClick: (cardView: View, currentActor: Actor) -> Unit = { _, currentActor: Actor ->
        TODO("startActivity(context ,Intent(context, MovieVisor::class.java).putExtra(MovieVisor.OBJECT, currentMovie), null)")
    }): ListAdapter<Actor, ActorGenreHolder>(ActorComparator) {
    private lateinit var context : Context
    var selectedMovies: List<Long> = emptyList()
    var showOnlyFavorites = false
    var textFiler: String = ""
    private var originalList = emptyList<Actor>()

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorGenreHolder {
        context = parent.context
        return ActorGenreHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ActorGenreHolder, position: Int) {
        val currentActor = getItem(position)
        holder.cardView.visibility = if(showOnlyFavorites && !currentActor.favorite) View.GONE else View.VISIBLE
        holder.name.text = currentActor.name
        actorViewModel.getMovies(currentActor.id).observe(viewLifecycleOwner) { movies ->
            movies.let {
                holder.movies.text = String.format(
                    context.getString(R.string.tv_actor_genre_item_movies),
                    it.size
                )
            }
        }
        holder.favorite.visibility = if(currentActor.favorite) View.VISIBLE else View.INVISIBLE
        if (selectedMovies.contains(currentActor.id)){
            setSelect(true, holder.cardView)
        }else{
            setSelect(false, holder.cardView)
        }
        holder.cardView.setOnClickListener {
            onCardClick.invoke(it,currentActor)
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
                            if (currentActor.favorite) {
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
                                    Intent(context, ActorGenreEditor::class.java)
                                        .putExtra(ActorGenreEditor.OBJECT, currentActor)
                                        .putExtra(ActorGenreEditor.CLASS,ActorGenreEditor.ACTOR),
                                    null
                                )
                                true
                            }
                            R.id.btn_set_fav -> {
                                onFavorite?.invoke(currentActor)
                                true
                            }
                            R.id.btn_delete -> {
                                onDelete?.invoke(currentActor)
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
    private fun setSelect(selected: Boolean, cardView: View){
        cardView.isSelected = selected
        cardView.backgroundTintList = if(selected) ColorStateList.valueOf(context.getColor(R.color.selected_item)) else null
    }
    override fun submitList(list: List<Actor>?) {
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
    private fun filterFavorites(list: List<Actor>): List<Actor> {
        val favoriteList = mutableListOf<Actor>()
        list.forEach {
            if (it.favorite){
                favoriteList.add(it)
            }
        }
        return favoriteList
    }
    private fun filterTitles(list: List<Actor>): List<Actor> {
        val filteredTitles = mutableListOf<Actor>()
        list.forEach{
            if (it.name.lowercase().contains(textFiler)){
                filteredTitles.add(it)
            }
        }
        return filteredTitles
    }
    object ActorComparator: DiffUtil.ItemCallback<Actor>(){
        override fun areItemsTheSame(oldItem: Actor, newItem: Actor): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Actor, newItem: Actor): Boolean = oldItem == newItem
    }
}
