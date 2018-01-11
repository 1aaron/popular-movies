package com.example.developer.popularmovies.controllers

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

import com.example.developer.popularmovies.R
import com.example.developer.popularmovies.models.Movie
import com.example.developer.popularmovies.models.Statics
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_portadas.*
import com.squareup.picasso.NetworkPolicy
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PortadasFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PortadasFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PortadasFragment : Fragment() {


    private var mListener: OnFragmentInteractionListener? = null
    lateinit var adapter: MoviesAdapter
    lateinit var gridLayoutManager: GridLayoutManager
    var loading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_portadas, container, false)
    }

    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        por_recycler.setHasFixedSize(true)
        gridLayoutManager = GridLayoutManager(context,2)
        por_recycler.layoutManager = gridLayoutManager
        adapter = MoviesAdapter(Statics.arrayMovies)
        por_recycler.adapter = adapter
        por_recycler.setOnScrollListener(object : RecyclerView.OnScrollListener(){
            var ydy = 0
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                ydy = dy
                Log.e("item pos: ",gridLayoutManager.findLastVisibleItemPosition().toString())
                Log.e("count: ",gridLayoutManager.itemCount.toString())
                if(gridLayoutManager.findLastVisibleItemPosition() == gridLayoutManager.itemCount - 1 && !loading){
                    getMovies()
                }
            }
        })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    fun getMovies(){
        if(Statics.pages > 1000){
            Toast.makeText(context,resources.getString(R.string.listEnd),Toast.LENGTH_SHORT).show()
            return
        }
        loading = true
        por_loader.visibility = View.VISIBLE
        var url = ""
        if(Statics.chosenFilter == Statics.POPULARITY_FILTER)
            url = resources.getString(R.string.queryPopularity)+"&page=${Statics.pages}"
        else
            url = resources.getString(R.string.queryRating)+"&page=${Statics.pages}"

        val stringResquest = StringRequest(Request.Method.GET,url, Response.Listener {
            response ->
            val raiz = JSONObject(response)
            val results = raiz.getJSONArray("results")
            var i = 0
            while (i<results.length()){
                val json: JSONObject = results.getJSONObject(i)
                val movie = Movie(json.getString("id"),json.getString("title"),json.getString("poster_path"),json.getString("overview"),
                        json.getString("vote_average"),json.getString("release_date"),json.getString("popularity"))
                Statics.arrayMovies.add(movie)
                i++
            }
            adapter.notifyDataSetChanged()
            loading = false
            Statics.pages ++
            por_loader.visibility = View.GONE
        }, Response.ErrorListener {
            error ->
            loading = false
            Toast.makeText(context,"Error", Toast.LENGTH_SHORT).show()
            Log.e(Statics.TAG,"NO SE PUDO",error)
            por_loader.visibility = View.GONE
        })
        stringResquest.setRetryPolicy(DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        Statics.volleyqueue.add(stringResquest)
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment PortadasFragment.
         */
        fun newInstance(): PortadasFragment {
            val fragment = PortadasFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    inner class MoviesAdapter(var arrayMovies : ArrayList<Movie>): RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.rec_item,parent,false)
            var vHolder = ViewHolder(view)
            return vHolder
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val movie = arrayMovies.get(position)
            val imagePath = movie.image
            val completeUrl = resources.getString(R.string.imageBase) + imagePath
            Picasso.with(context).load(completeUrl)
                    .into(holder.myImageView,object : Callback {
                        override fun onSuccess() {
                            Log.e("tag","success picasso")
                        }

                        override fun onError() {
                            holder.myImageView.setImageResource(R.drawable.posterplaceholder)
                        }
                    })
            //holder.myImageView.setImageURI(uri)
        }

        override fun getItemCount(): Int {
            return arrayMovies.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            var myImageView: ImageView = itemView.findViewById(R.id.item_img)
        }
    }
}
