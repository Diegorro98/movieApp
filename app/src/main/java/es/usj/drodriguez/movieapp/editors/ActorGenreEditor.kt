package es.usj.drodriguez.movieapp.editors

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import es.usj.drodriguez.movieapp.R
import es.usj.drodriguez.movieapp.database.classes.Actor
import es.usj.drodriguez.movieapp.database.classes.Genre
import es.usj.drodriguez.movieapp.databinding.ActivityActorGenreEditorBinding

class ActorGenreEditor : AppCompatActivity() {
    private var saved = false
    private lateinit var binding: ActivityActorGenreEditorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActorGenreEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when(intent?.extras?.getString(CLASS)){
            ACTOR -> {
                val actor = intent?.extras?.getSerializable(OBJECT) as Actor?
                binding.toolbarEditor.title = if(actor == null) getString(R.string.actor_editor_new_title) else getString(R.string.actor_editor_edit_title)
            }
            GENRE -> {
                val genre = intent?.extras?.getSerializable(OBJECT) as Genre?
                binding.toolbarEditor.title = if(genre == null) getString(R.string.genre_editor_new_title) else getString(R.string.genre_editor_edit_title)
            }
        }
        binding.toolbarEditor.setNavigationOnClickListener {
            finish()
        }
    }

    companion object {
        const val ACTOR = "actor"
        const val GENRE = "genre"
        const val CLASS = "class"
        const val OBJECT = "object"
    }
}