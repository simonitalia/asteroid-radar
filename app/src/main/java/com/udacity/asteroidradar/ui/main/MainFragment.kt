package com.udacity.asteroidradar.ui.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.AsteroidItemBinding
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.repository.AsteroidsRepository

/**
 * This fragment shows asteroids from fetched from Nasa NEO (Near-Earth-Objects) web service.
 */

class MainFragment: Fragment(), AsteroidRecyclerViewAdapterListener { // implement adapter listener

    // lazily initialize MainViewModel using .Factory to pass in application parameter
    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity)  {
            "You can only access the viewModel after onViewCreated()"
        }

        ViewModelProvider(this, MainViewModel.Factory(activity.application)).get(MainViewModel::class.java)
    }

    // recycler adapter to display asteroid items data in ui
    private lateinit var listener: AsteroidRecyclerViewAdapterListener
    private lateinit var adapter: AsteroidAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // inflate layout using Data Binding, and bind fragment with this ui controller

        val binding: FragmentMainBinding =  DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main,
            container,
            false
        )

        listener = this
        adapter = AsteroidAdapter(listener)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        //adapter binding
        binding.asteroidRecycler.adapter = this.adapter

        setHasOptionsMenu(true) //show options menu in action bar
        return binding.root
    }

    /**
     * Called immediately after onCreateView() has returned, and fragment's
     * view hierarchy has been created.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // observer MainViewModel live data changes

        viewModel.asteroids.observe(viewLifecycleOwner, { asteroids ->
            asteroids?.let {

                Log.i("MainFragment.OnViewCreated", "Asteroids successfully loaded from repo: ${it.count()}.")
                adapter.submitList(it)
            }
        })

        viewModel.pictureOfDay?.observe(viewLifecycleOwner, {
            Log.i("MainFragment", "Picture of day object fetched. Media type: ${it.mediaType}")
        })

        //observe the navigation property value to trigger navigation (on tap of selected asteroid)
        viewModel.selectedAsteroid.observe(viewLifecycleOwner, { selectedAsteroid ->
            selectedAsteroid?.let {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.showDetailFragmentComplete()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_today_menu -> viewModel.getLiveData(AsteroidsRepository.AsteroidsFilter.SHOW_TODAY)
            R.id.show_past_week_menu -> viewModel.getLiveData(AsteroidsRepository.AsteroidsFilter.SHOW_PAST_WEEK)
            R.id.show_all_menu -> viewModel.getLiveData()
        }

        return true
    }

    // adapter interface method implementation
    override fun onItemViewPressed(asteroid: Asteroid) {
        viewModel.showDetailFragment(asteroid)
    }
}

/**
 * RecyclerView Adapter for setting up data binding on the items in the list.
 * Interface for communication between adapter and fragment classes
 */
interface AsteroidRecyclerViewAdapterListener {
    fun onItemViewPressed(asteroid: Asteroid)
}
class AsteroidAdapter(
    private val listener: AsteroidRecyclerViewAdapterListener
): ListAdapter<Asteroid, AsteroidAdapter.ViewHolder>(AdapterDiffCallback()) {

    /**
     * Diff Util for managing Data changes
     */
    class AdapterDiffCallback: DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }
    }

    /**
     * Bridge between Adapter class and xml ui Views
     * Setting Views with data handled by BindingAdapters
     */
    class ViewHolder(val viewDataBinding: AsteroidItemBinding): RecyclerView.ViewHolder(viewDataBinding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.asteroid_item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val withDataBinding: AsteroidItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            ViewHolder.LAYOUT,
            parent,
            false)
        return ViewHolder(withDataBinding)
    }

    // bridge between asteroid data item and asteroid item view (ui layout)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val asteroidItem = getItem(position)
        holder.viewDataBinding.also {
            it.asteroid = asteroidItem // connects to layout item data variable
        }

        // pass asteroid item pressed back to listener
        holder.itemView.setOnClickListener {
            listener.onItemViewPressed(asteroidItem)
        }
    }
}