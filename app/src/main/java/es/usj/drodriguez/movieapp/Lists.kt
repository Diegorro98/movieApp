package es.usj.drodriguez.movieapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import es.usj.drodriguez.movieapp.database.DatabaseFetcher
import es.usj.drodriguez.movieapp.database.adapters.*
import es.usj.drodriguez.movieapp.database.viewmodels.*
import es.usj.drodriguez.movieapp.databinding.FragmentListsBinding
import es.usj.drodriguez.movieapp.utils.App

/**
 * A simple [Fragment] subclass.
 * Use the [Lists.newInstance] factory method to
 * create an instance of this fragment.
 */
class Lists : Fragment() {
    private var type: String? = null
    private val movieViewModel: MovieViewModel by viewModels { MovieViewModelFactory((activity?.application as App).repository) }
    private val actorViewModel: ActorViewModel by viewModels { ActorViewModelFactory((activity?.application as App).repository) }
    private val genreViewModel: GenreViewModel by viewModels { GenreViewModelFactory((activity?.application as App).repository) }

    private var _binding : FragmentListsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getString(TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentListsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listRefresh.setOnRefreshListener {
            this.context?.let {
                DatabaseFetcher.fetch(it, onFinish = {
                    binding.listRefresh.isRefreshing = false
                })
            }
        }
        when(type){
            MOVIES -> {
                val adapter = MovieListAdapter()
                binding.recyclerView.adapter = adapter
                movieViewModel.allMovies.observe(viewLifecycleOwner) { movies ->
                    movies.let { adapter.submitList(it) }
                }
            }
            ACTORS ->{
                val adapter = ActorListAdapter()
                binding.recyclerView.adapter = adapter
                actorViewModel.allActors.observe(viewLifecycleOwner) { actors ->
                    actors.let { adapter.submitList(it) }
                }
            }
            GENRES ->{
                val adapter = GenreListAdapter()
                binding.recyclerView.adapter = adapter
                genreViewModel.allGenres.observe(viewLifecycleOwner) { genres ->
                    genres.let { adapter.submitList(it) }
                }
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

    }
    companion object {
        /**
         * Method to create a new instance of
         * this fragment depending on the type of List you want to create.
         *
         * @param type Specify the type of list you want to generate.
         * @return A new instance of fragment List.
         */
        @JvmStatic
        fun newInstance(type: String) =
            Lists().apply {
                arguments = Bundle().apply {
                    putString(TYPE, type)
                }
            }
        private const val TYPE = "type"
        const val MOVIES = "movies"
        const val ACTORS = "actors"
        const val GENRES = "genres"

    }
}