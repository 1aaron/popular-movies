package com.example.developer.popularmovies.fragments

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
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

import com.example.developer.popularmovies.R
import com.example.developer.popularmovies.adaptadores.MoviesAdapter
import com.example.developer.popularmovies.clases.Movie
import com.example.developer.popularmovies.clases.Statics
import kotlinx.android.synthetic.main.fragment_portadas.*
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
        if(Statics.screenWidth > 720 && !Statics.land){
            gridLayoutManager = GridLayoutManager(context,3)

        }else{
            gridLayoutManager = GridLayoutManager(context,2)
        }
        por_recycler.layoutManager = gridLayoutManager
        adapter = MoviesAdapter(Statics.arrayMovies)
        por_recycler.adapter = adapter
        if(Statics.land){
            val fragment = DetailsFragment.newInstance(Statics.arrayMovies[0])
            Statics.activity.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.content_detail, fragment)
                    .commit()
        }
        por_recycler.setOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
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
                val movie = Movie(json.getString("id"),json.getString("original_title"),json.getString("poster_path"),json.getString("overview"),
                        json.getString("vote_average"),json.getString("release_date"),json.getString("popularity"),json.getString("original_language"))
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
}
