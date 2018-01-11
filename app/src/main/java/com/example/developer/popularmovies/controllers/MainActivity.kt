package com.example.developer.popularmovies.controllers

import android.Manifest
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
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
import com.example.developer.popularmovies.models.Movie
import com.example.developer.popularmovies.models.Statics
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity(), PortadasFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Statics.context = this
        Statics.volleyqueue = Volley.newRequestQueue(this)
        if(!checkPermission()){
            ActivityCompat.requestPermissions(this,Statics.permissions,Statics.PERMISSION_CHECK)
        }else{
            getMovies()
        }

    }

    fun checkPermission() : Boolean{
        for (checks in Statics.permissions){
            if(ContextCompat.checkSelfPermission(this,checks) == PackageManager.PERMISSION_DENIED)
                return false
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        val filters = arrayOf(resources.getString(R.string.filterPopularity),resources.getString(R.string.filterRated))
        if(id == R.id.menu_filter){
            val builder = AlertDialog.Builder(this)
                    .setSingleChoiceItems(filters,0, { dialogInterface, i ->
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

    fun getMovies(){
        //todo: validate which url
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
                val movie = Movie(json.getString("id"),json.getString("title"),json.getString("poster_path"),json.getString("overview"),
                        json.getString("vote_average"),json.getString("release_date"),json.getString("popularity"))
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
    fun inflateFragment(){
        val fragment = PortadasFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out)
                .replace(R.id.content_main,fragment)
                .commit()
    }
}
