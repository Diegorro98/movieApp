package es.usj.drodriguez.movieapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import es.usj.drodriguez.movieapp.databinding.ActivityMainBinding
import es.usj.drodriguez.movieapp.databinding.ActivityMovieEditorBinding

class MovieEditor : AppCompatActivity() {
    private lateinit var editorMode :String
    private var saved = false
    private lateinit var binding: ActivityMovieEditorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_editor)
        binding = ActivityMovieEditorBinding.inflate(layoutInflater)
        editorMode = intent?.extras?.get(EDITOR_MODE).toString()

        binding.toolbarEditor.setNavigationOnClickListener {
            if((editorMode == MODE_EDIT || editorMode == MODE_NEW) && !saved){
                Toast.makeText(this,"Exiting without saving", Toast.LENGTH_SHORT).show()
            }
            finish()
        }

        binding.toolbarEditor.title = when(editorMode){
            MODE_EDIT -> {
                getString(R.string.editor_edit_mode_title)
            }
            MODE_NEW -> {
                getString(R.string.editor_new_mode_title)
            }
            MODE_SEE -> {
                intent?.extras?.get(MOVIE_NAME).toString()
            }
            else -> ""
        }
    }

    companion object {
        const val MODE_EDIT = "Edit mode"
        const val MODE_NEW = "New mode"
        const val MODE_SEE = "See mode"
        const val EDITOR_MODE = "Editor mode"
        const val MOVIE_NAME = "Movie name"
    }
}