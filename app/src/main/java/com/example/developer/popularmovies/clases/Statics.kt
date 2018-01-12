package com.example.developer.popularmovies.clases

import android.Manifest
import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.android.volley.RequestQueue

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
        var pages = 1
        var chosenFilter = 0
        var screenWidth = 0
        var screenHeight = 0
        lateinit var activity: AppCompatActivity
        var land = false
    }
}