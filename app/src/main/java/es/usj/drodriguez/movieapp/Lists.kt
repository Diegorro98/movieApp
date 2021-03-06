package es.usj.drodriguez.movieapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import es.usj.drodriguez.movieapp.database.adapters.ActorListAdapter
import es.usj.drodriguez.movieapp.database.adapters.GenreListAdapter
import es.usj.drodriguez.movieapp.database.adapters.MovieListAdapter
import es.usj.drodriguez.movieapp.database.viewmodels.*
import es.usj.drodriguez.movieapp.databinding.FragmentListsBinding
import es.usj.drodriguez.movieapp.utils.DatabaseApp

/**
 * A simple [Fragment] subclass.
 * Use the [Lists.newInstance] factory method to
 * create an instance of this fragment.
 */
class Lists : Fragment() {
    private var type: String? = null
    private val movieViewModel: MovieViewModel by viewModels { MovieViewModelFactory((activity?.application as DatabaseApp).repository) }
    private val actorViewModel: ActorViewModel by viewModels { ActorViewModelFactory((activity?.application as DatabaseApp).repository) }
    private val genreViewModel: GenreViewModel by viewModels { GenreViewModelFactory((activity?.application as DatabaseApp).repository) }

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
        if (activity!= null) {
            when (type) {
                MOVIES -> {
                    val adapter = MovieListAdapter(requireActivity(),movieViewModel,
                        onFavorite = { currentMovie ->
                            movieViewModel.setFavorite(currentMovie.id, !currentMovie.favorite)
                        },
                        onDelete = { currentMovie ->
                            movieViewModel.delete(currentMovie)
                        }
                    )
                    binding.rvItems.adapter = adapter
                    movieViewModel.allMovies.observe(viewLifecycleOwner) { movies ->
                        movies.let { adapter.submitList(it) }
                    }
                    onlyFavs.observe(viewLifecycleOwner){ onlyFavsValue ->
                        onlyFavsValue.let {
                            adapter.showOnlyFavorites = it
                            adapter.resubmitList()
                        }
                    }
                    textFilter.observe(viewLifecycleOwner){ text ->
                        text.let {
                            adapter.textFiler = it
                            adapter.resubmitList()
                        }
                    }
                }
                ACTORS -> {
                    val adapter = ActorListAdapter(requireActivity(), actorViewModel, this,
                        onFavorite = { currentActor ->
                            actorViewModel.setFavorite(currentActor.id, !currentActor.favorite)
                        },
                        onDelete = { currentActor ->
                            actorViewModel.delete(currentActor)
                        })
                    binding.rvItems.adapter = adapter
                    actorViewModel.allActors.observe(viewLifecycleOwner) { actors ->
                        actors.let { adapter.submitList(it) }
                    }
                    onlyFavs.observe(viewLifecycleOwner){ onlyFavsValue ->
                        onlyFavsValue.let {
                            adapter.showOnlyFavorites = it
                            adapter.resubmitList()
                        }
                    }
                    textFilter.observe(viewLifecycleOwner){ text ->
                        text.let {
                            adapter.textFiler = it
                            adapter.resubmitList()
                        }
                    }
                }
                GENRES -> {
                    val adapter = GenreListAdapter(requireActivity(), genreViewModel, this,
                        onFavorite = { currentGenre ->
                            genreViewModel.setFavorite(currentGenre.id, !currentGenre.favorite)
                        },
                        onDelete = { currentGenre ->
                            genreViewModel.delete(currentGenre)
                        })
                    binding.rvItems.adapter = adapter
                    genreViewModel.allGenres.observe(viewLifecycleOwner) { genres ->
                        genres.let { adapter.submitList(it) }
                    }
                    onlyFavs.observe(viewLifecycleOwner){ onlyFavsValue ->
                        onlyFavsValue.let {
                            adapter.showOnlyFavorites = it
                            adapter.resubmitList()
                        }
                    }
                    textFilter.observe(viewLifecycleOwner){ text ->
                        text.let {
                            adapter.textFiler = it
                            adapter.resubmitList()
                        }
                    }
                }
            }

            binding.rvItems.layoutManager = LinearLayoutManager(context)
        }
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
        var onlyFavs: MutableLiveData<Boolean> = MutableLiveData(false)
        var textFilter: MutableLiveData<String> = MutableLiveData("")
    }
}