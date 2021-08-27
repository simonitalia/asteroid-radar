package com.udacity.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.udacity.asteroidradar.models.PictureOfDay
import com.udacity.asteroidradar.ui.main.MainViewModel

/**
 * General purpose bindings.
 */

@BindingAdapter("bindText")
fun bindTextView(textView: TextView, text: String) {
    textView.text = text
}

/**
 * fragment_detail bindings
 */

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}


/**
 * fragment_main bindings .
 */

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("neoApiStatus")
fun bindStatus(progressBarView: ProgressBar, apiStatus: MainViewModel.NeoApiStatus) {
    progressBarView.visibility = if (apiStatus == MainViewModel.NeoApiStatus.LOADING) View.VISIBLE else View.GONE
}

@BindingAdapter("pictureOfDayImage")
fun bindPictureOfDayImage(imageView: ImageView, pictureOfDay: PictureOfDay?) {

    pictureOfDay?.let {
        if (it.mediaType == "image") Glide.with(imageView.context).load(pictureOfDay.url)
            .into(imageView)

    }.run {
        imageView.setImageResource(R.drawable.ic_connection_error)
    }
}