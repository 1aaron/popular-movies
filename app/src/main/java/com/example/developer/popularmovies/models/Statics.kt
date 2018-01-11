package com.example.developer.popularmovies.models

import android.Manifest
import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * Created by developer on 1/10/18.
 */
class Statics{
    companion object {
        val PERMISSION_CHECK = 100
        var permissions = arrayOf(Manifest.permission.INTERNET)
        lateinit var volleyqueue:RequestQueue
        lateinit var context: Context
        val TAG = "ERROR NETWORK"
        val POPULARITY_FILTER = 0
        val RATING_FILTER = 1
        var arrayMovies = ArrayList<Movie>()
    }
}