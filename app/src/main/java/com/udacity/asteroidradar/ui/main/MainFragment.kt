package com.udacity.asteroidradar.ui.main

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.models.Asteroid
import kotlinx.android.synthetic.main.asteroid_item.view.*

/**
 * This fragment shows asteroids from fetched from Nasa NEO (Near-Earth-Objects) web service.
 */

class MainFragment: Fragment() {

    // lazily initialize MainViewModel using .Factory to pass in application parameter
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(activity!!.application)).get(MainViewModel::class.java)
    }

    // recycler adapter to convert asteroid items to ui
    private val adapter = RecyclerAdapter()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //Inflate layout using Data Binding
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.asteroidRecycler.adapter = adapter

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

        //monitor changes to MainViewModel live data
        viewModel.asteroids.observe(viewLifecycleOwner, Observer<List<Asteroid>> { asteroids ->
            asteroids?.apply {

                Log.i("MainFragment", "Asteroids successfully loaded from repo: ${asteroids.count()}")

                //update recylcer adapter with new asteroid items
                adapter?.asteroidItems = asteroids
            }
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
class RecyclerAdapter: RecyclerView.Adapter<RecyclerAdapter.AsteroidHolder>() {

    // list data
    var asteroidItems = listOf<Asteroid>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        //inflate layout xml (passing in filename of xml)
        val inflatedView = layoutInflater
            .inflate(R.layout.asteroid_item, parent, false)
        return AsteroidHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: AsteroidHolder, position: Int) {
        val asteroidItem = asteroidItems[position]
        holder.bindAsteroid(asteroidItem)
    }

    override fun getItemCount(): Int {
        return asteroidItems.size
    }

    /**
     * Bridge between Adapter class and xml ui Views
     */
    class AsteroidHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {

        // item reference
        private var asteroid: Asteroid? = null

        init {
            itemView.setOnClickListener(this)
        }

        // bind asteroid item property values with ui views
        fun bindAsteroid(asteroid: Asteroid) {
            this.asteroid = asteroid
            itemView.code_name.text = asteroid.codename
            itemView.close_approach_date.text = asteroid.closeApproachDate

            var statusImage: Drawable? = when (asteroid.isPotentiallyHazardous) {

                true -> {
                    itemView.context?.resources?.let {
                        ResourcesCompat.getDrawable(it, R.drawable.ic_status_potentially_hazardous, null)
                    }
                }

                false -> {
                    itemView.context?.resources?.let {
                        ResourcesCompat.getDrawable(it, R.drawable.ic_status_normal, null)
                    }
                }
            }

            itemView.status_image.setImageDrawable(statusImage)
        }

        override fun onClick(v: View?) {

        }
    }
}
