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
import es.usj.drodriguez.movieapp.database.classes.Genre
import es.usj.drodriguez.movieapp.database.viewmodels.GenreViewModel
import es.usj.drodriguez.movieapp.editors.ActorGenreEditor

class GenreListAdapter(
    private val activity: Activity?,
    private val genreViewModel: GenreViewModel,
    private val viewLifecycleOwner: LifecycleOwner,
    private val editButton: Boolean = true,
    private val onFavorite: ( (currentGenre: Genre) -> Unit)? = null,
    private val onDelete: ( (currentGenre: Genre) -> Unit)? = null,
    private val onCardClick: (cardView: View, currentGenre: Genre) -> Unit = { _, currentGenre: Genre ->
        TODO("startActivity(context ,Intent(context, MovieVisor::class.java).putExtra(MovieVisor.OBJECT, currentMovie), null)")
    }): ListAdapter<Genre, ActorGenreHolder>(GenreComparator) {
    private lateinit var context : Context
    var selectedMovies: List<Long> = emptyList()
    var showOnlyFavorites = false
    var textFiler: String = ""
    private var originalList = emptyList<Genre>()

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorGenreHolder {
        context = parent.context
        return ActorGenreHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ActorGenreHolder, position: Int) {
        val currentGenre = getItem(position)
        holder.cardView.visibility = if(showOnlyFavorites && !currentGenre.favorite) View.GONE else View.VISIBLE
        holder.name.text = currentGenre.name
        genreViewModel.getMovies(currentGenre.id).observe(viewLifecycleOwner) { movies ->
            movies.let {
                holder.movies.text = String.format(
                    context.getString(R.string.tv_actor_genre_item_movies),
                    it.size
                )
            }
        }
        holder.favorite.visibility = if(currentGenre.favorite) View.VISIBLE else View.INVISIBLE
        if (selectedMovies.contains(currentGenre.id)){
            setSelect(true, holder.cardView)
        }else{
            setSelect(false, holder.cardView)
        }
        holder.cardView.setOnClickListener {
            onCardClick.invoke(it,currentGenre)
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
                            if (currentGenre.favorite) {
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
                                        .putExtra(ActorGenreEditor.OBJECT, currentGenre)
                                        .putExtra(ActorGenreEditor.CLASS,ActorGenreEditor.GENRE),
                                    null
                                )
                                true
                            }
                            R.id.btn_set_fav -> {
                                onFavorite?.invoke(currentGenre)
                                true
                            }
                            R.id.btn_delete -> {
                                onDelete?.invoke(currentGenre)
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
    override fun submitList(list: List<Genre>?) {
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
    private fun filterFavorites(list: List<Genre>): List<Genre> {
        val favoriteList = mutableListOf<Genre>()
        list.forEach {
            if (it.favorite){
                favoriteList.add(it)
            }
        }
        return favoriteList
    }
    private fun filterTitles(list: List<Genre>): List<Genre> {
        val filteredTitles = mutableListOf<Genre>()
        list.forEach{
            if (it.name.lowercase().contains(textFiler)){
                filteredTitles.add(it)
            }
        }
        return filteredTitles
    }
    object GenreComparator: DiffUtil.ItemCallback<Genre>(){
        override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean = oldItem == newItem
    }
}
