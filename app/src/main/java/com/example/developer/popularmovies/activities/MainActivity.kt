package com.example.developer.popularmovies.activities

import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Point
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.developer.popularmovies.R
import com.example.developer.popularmovies.clases.Movie
import com.example.developer.popularmovies.clases.Statics
import com.example.developer.popularmovies.fragments.DetailsFragment
import com.example.developer.popularmovies.fragments.PortadasFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity(), PortadasFragment.OnFragmentInteractionListener, DetailsFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            Statics.land = false
            content_detail.visibility = View.GONE
        }else {
            Statics.land = true
        }
        //initialize statics variables, and check the devices screen size
        Statics.context = this
        Statics.activity = this
        Statics.volleyqueue = Volley.newRequestQueue(this)
        var point = Point()
        windowManager.defaultDisplay.getRealSize(point)
        Statics.screenWidth = point.x
        Statics.screenHeight = point.y
        //check for permissions
        if(!checkPermission()){
            ActivityCompat.requestPermissions(this,Statics.permissions,Statics.PERMISSION_CHECK)
        }else{
            getMovies()
        }

    }

    /**
     * check if needed permissions are granted
     */
    fun checkPermission() : Boolean{
        for (checks in Statics.permissions){
            if(ContextCompat.checkSelfPermission(this,checks) == PackageManager.PERMISSION_DENIED)
                return false
        }
        return true
    }

    /**
     * create the upper menu
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    /**
     * act according to the chosen element from menu and in this case pop a dialog to choose
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        val filters = arrayOf(resources.getString(R.string.filterPopularity),resources.getString(R.string.filterRated))
        if(id == R.id.menu_filter){
            val builder = AlertDialog.Builder(this)
                    .setSingleChoiceItems(filters,Statics.chosenFilter, { dialogInterface, i ->
                        Statics.chosenFilter = i
                    })
                    .setPositiveButton("Ok", DialogInterface.OnClickListener { dialogInterface, i ->
                        getMovies()
                    })
                    .create()
            builder.setTitle(resources.getString(R.string.filter))
            builder.show()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * check if the user is doing back press when theres nothing in backstack
     */
    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount < 1){
            val dialog = AlertDialog.Builder(this)
                    .setMessage(resources.getString(R.string.getOut))
                    .setPositiveButton("ok", DialogInterface.OnClickListener { dialogInterface, i ->
                        finish()
                    })
                    .setNegativeButton(resources.getString(R.string.cancel),null)
                    .create().show()
        }else
            super.onBackPressed()
    }

    /**
     * connect to the web service and retrieve information of movies checking which filter to apply
     */
    fun getMovies(){
        var url = ""
        if(Statics.chosenFilter == Statics.POPULARITY_FILTER)
            url = resources.getString(R.string.queryPopularity)+"&page=1"
        else
            url = resources.getString(R.string.queryRating)+"&page=1"

        val stringResquest = StringRequest(Request.Method.GET,url, Response.Listener {
            response ->
            val raiz = JSONObject(response)
            Statics.arrayMovies.clear()
            val results = raiz.getJSONArray("results")
            var i = 0
            while (i<results.length()){
                val json: JSONObject = results.getJSONObject(i)
                val movie = Movie(json.getString("id"),json.getString("original_title"),json.getString("poster_path"),json.getString("overview"),
                        json.getString("vote_average"),json.getString("release_date"),json.getString("popularity"),json.getString("original_language"))
                Statics.arrayMovies.add(movie)
                i++
            }
            main_pgBar.visibility = View.GONE
            Statics.pages ++
            inflateFragment()
        }, Response.ErrorListener {
            error ->
            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
            Log.e(Statics.TAG,"NO SE PUDO",error)
        })
        stringResquest.setRetryPolicy(DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        Statics.volleyqueue.add(stringResquest)
    }

    /**
     * inflate fragment when movies available
     */
    fun inflateFragment(){
        if(!Statics.land){
            val fragment = PortadasFragment.newInstance()
            supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out)
                    .replace(R.id.content_main,fragment)
                    .commit()
        }else{
            content_detail.visibility = View.VISIBLE
            val fragment = PortadasFragment.newInstance()
            supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out)
                    .replace(R.id.content_main,fragment)
                    .commit()
        }

    }
}
