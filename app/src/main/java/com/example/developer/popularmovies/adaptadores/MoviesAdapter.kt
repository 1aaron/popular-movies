package com.example.developer.popularmovies.adaptadores

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.developer.popularmovies.R
import com.example.developer.popularmovies.fragments.DetailsFragment
import com.example.developer.popularmovies.clases.Movie
import com.example.developer.popularmovies.clases.Statics
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

/**
 * Created by developer on 1/11/18.
 */
class MoviesAdapter(var arrayMovies : ArrayList<Movie>): RecyclerView.Adapter<MoviesAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.rec_item,parent,false)
        var vHolder = ViewHolder(view)
        return vHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = arrayMovies.get(position)
        val imagePath = movie.image
        val completeUrl = Statics.context.resources.getString(R.string.imageBase) + imagePath
        Picasso.with(Statics.context).load(completeUrl)
                .into(holder.myImageView,object : Callback {
                    override fun onSuccess() {
                        Log.e("tag","success picasso")
                    }

                    override fun onError() {
                        holder.myImageView.setImageResource(R.drawable.posterplaceholder)
                    }
                })
        holder.myImageView.setOnClickListener {
            val fragment = DetailsFragment.newInstance(Statics.arrayMovies[position])
            if(Statics.land){
                Statics.activity.supportFragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.content_detail, fragment)
                        .commit()
            }else {
                Statics.activity.supportFragmentManager.beginTransaction()
                        .addToBackStack("DetailsFragment")
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.content_main, fragment)
                        .commit()
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayMovies.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var myImageView: ImageView = itemView.findViewById(R.id.item_img)
    }
}