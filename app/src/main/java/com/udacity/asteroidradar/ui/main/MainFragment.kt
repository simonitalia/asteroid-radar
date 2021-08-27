package com.udacity.asteroidradar.ui.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.AsteroidItemBinding
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.models.PictureOfDay

/**
 * This fragment shows asteroids from fetched from Nasa NEO (Near-Earth-Objects) web service.
 */

class MainFragment: Fragment() {

    // lazily initialize MainViewModel using .Factory to pass in application parameter
    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity)  {
            "You can only access the viewModel after onViewCreated()"
        }

        ViewModelProvider(this, MainViewModel.Factory(activity.application)).get(MainViewModel::class.java)
    }

    // recycler adapter to display asteroid items data in ui
    private val adapter = AsteroidRecyclerViewAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // inflate layout using Data Binding, and bind fragment with this ui controller

        val binding: FragmentMainBinding =  DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main,
            container,
            false
        )

        binding.setLifecycleOwner(viewLifecycleOwner)
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

                //update recycler adapter with new asteroid items
                adapter.asteroidItems = it
            }
        })

        viewModel.pictureOfDay?.observe(viewLifecycleOwner, {
            Log.i("MainFragment", "Picture of day object fetched. Media type: ${it.mediaType}")
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }
}

/**
 * RecyclerView Adapter for setting up data binding on the items in the list.
 */
class AsteroidRecyclerViewAdapter: RecyclerView.Adapter<AsteroidItemViewHolder>() {

    // list data
    var asteroidItems: List<Asteroid> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidItemViewHolder {

        val withDataBinding: AsteroidItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            AsteroidItemViewHolder.LAYOUT,
            parent,
            false)
        return AsteroidItemViewHolder(withDataBinding)
    }

    // bridge between asteroid data item and asteroid item view (ui layout)
    override fun onBindViewHolder(holder: AsteroidItemViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.asteroid = asteroidItems[position] // connects to layout item data variable
        }
    }

    override fun getItemCount(): Int {
        return asteroidItems.size
    }
}

/**
 * Bridge between Adapter class and xml ui Views
 * Setting Views with data handled by BindingAdapters
 */
class AsteroidItemViewHolder(val viewDataBinding: AsteroidItemBinding): RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.asteroid_item
    }
}
