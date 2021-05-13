package es.usj.drodriguez.movieapp.editors

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import es.usj.drodriguez.movieapp.R
import es.usj.drodriguez.movieapp.database.classes.Movie
import es.usj.drodriguez.movieapp.databinding.ActivityMovieEditorBinding

class MovieEditor : AppCompatActivity() {
    private var saved = false
    private lateinit var binding: ActivityMovieEditorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val movie = intent?.extras?.getSerializable(MOVIE_OBJECT) as Movie?
        if (movie == null){
            binding.toolbarEditor.title = getString(R.string.editor_new_mode_title)
        } else {
            binding.toolbarEditor.title = getString(R.string.editor_edit_mode_title)
        }
        binding.toolbarEditor.setNavigationOnClickListener {
            finish()
        }
    }

    companion object {
        const val MOVIE_OBJECT = "Movie object"
    }
}