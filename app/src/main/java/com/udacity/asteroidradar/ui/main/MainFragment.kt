package com.udacity.asteroidradar.ui.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.bindAsteroidStatusImage
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.models.Asteroid
import kotlinx.android.synthetic.main.asteroid_item.view.*

/**
 * This fragment shows asteroids from fetched from Nasa NEO (Near-Earth-Objects) web service.
 */

class MainFragment: Fragment() {

    private lateinit var binding: FragmentMainBinding

    // lazily initialize MainViewModel using .Factory to pass in application parameter
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(activity!!.application)).get(MainViewModel::class.java)
    }

    // recycler adapter to convert asteroid items to ui
    private val adapter = AsteroidRecyclerAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //Inflate layout using Data Binding, and bind fragment with this ui controller
        binding = FragmentMainBinding.inflate(inflater)
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

        //observer MainViewModel live data changes

        viewModel.asteroids.observe(viewLifecycleOwner, { asteroids ->
            asteroids?.let {

                Log.i("MainFragment.OnViewCreated", "Asteroids successfully loaded from repo: ${it.count()}.")

                //update recycler adapter with new asteroid items
                adapter?.asteroidItems = it
            }
        })

        viewModel.pictureOfDay.observe(viewLifecycleOwner, {
            it?.let {

                val mediaType = Constants.MediaType.valueOf(it.mediaType)
                when (mediaType) {
                    Constants.MediaType.IMAGE -> Picasso.with(view.context).load(it.url)
                        .into(binding.activityMainImageOfTheDay)
                    else -> binding.activityMainImageOfTheDay.setImageResource(R.drawable.ic_broken_image)
                }
            }.run {
                binding.activityMainImageOfTheDay.setImageResource(R.drawable.ic_connection_error)
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
class AsteroidRecyclerAdapter: RecyclerView.Adapter<AsteroidRecyclerAdapter.AsteroidItemViewHolder>() {

    // list data
    var asteroidItems = listOf<Asteroid>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        //inflate layout xml (passing in filename of xml)
        val asteroidItemView = layoutInflater
            .inflate(R.layout.asteroid_item, parent, false)
        return AsteroidItemViewHolder(asteroidItemView)
    }

    // bridge between asteroid data item and asteroid item view (ui layout)
    override fun onBindViewHolder(holder: AsteroidItemViewHolder, position: Int) {
        val asteroid = asteroidItems[position]
        holder.bindItem(asteroid)
    }

    override fun getItemCount(): Int {
        return asteroidItems.size
    }

    /**
     * Bridge between Adapter class and xml ui Views
     */
    class AsteroidItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {

        // item reference
        private var asteroid: Asteroid? = null

        init {
            itemView.setOnClickListener(this)
        }

        // bind asteroid item property values with ui views
        fun bindItem(asteroid: Asteroid) {
            this.asteroid = asteroid
            itemView.code_name.text = asteroid.codename
            itemView.close_approach_date.text = asteroid.closeApproachDate
            bindAsteroidStatusImage(itemView.status_image, asteroid.isPotentiallyHazardous)
        }

        override fun onClick(v: View?) {

        }
    }
}
