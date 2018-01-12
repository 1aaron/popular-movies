package com.example.developer.popularmovies.fragments

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.developer.popularmovies.R
import com.example.developer.popularmovies.clases.Movie
import com.example.developer.popularmovies.clases.Statics
import com.example.developer.popularmovies.clases.Trailer
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_details.*
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DetailsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailsFragment : Fragment() {


    private var mListener: OnFragmentInteractionListener? = null
    lateinit var pDialog: ProgressDialog
    var arrayVideos = ArrayList<Trailer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_details, container, false)
    }

    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pDialog = ProgressDialog(context)
        pDialog.setMessage(resources.getString(R.string.loading))
        det_movieDate.setText(resources.getString(R.string.date) +  movie.fechaLanzamiento)
        det_language.setText(resources.getString(R.string.language) + movie.language)
        det_movieTitle.setText(movie.title)
        det_rating.rating = movie.votes.toFloat() / 2
        det_txtOverview.setText(movie.overview)

        val completeUrl = resources.getString(R.string.imageBase) + movie.image
        Picasso.with(context).load(completeUrl)
                .into(det_movieImage,object : Callback {
                    override fun onSuccess() {
                    }

                    override fun onError() {
                        det_movieImage.setImageResource(R.drawable.posterplaceholder)
                    }
                })
        getTrailers()
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

    fun getTrailers(){
        pDialog.show()
        val url = resources.getString(R.string.trailers1) + movie.id + resources.getString(R.string.trailers2)
        val stRequest = StringRequest(Request.Method.GET,url, Response.Listener {
            response ->
            val raiz = JSONObject(response)
            if(raiz.has("results")){
                val results = raiz.getJSONArray("results")
                if(results.length() > 0){
                    var i = 0
                    while (i<results.length()){
                        val json = results.getJSONObject(i)
                        val trailer = Trailer(json.getString("key"),json.getString("site"),json.getString("name"))
                        arrayVideos.add(trailer)
                        i++
                    }
                    mostrarTrailers()
                }else{
                    Toast.makeText(context,resources.getString(R.string.noTrailer),Toast.LENGTH_SHORT).show()
                }
            }
            pDialog.dismiss()
        }, Response.ErrorListener {
            error ->
            Log.e("tag","error videos",error)
            Toast.makeText(context,resources.getString(R.string.errorTrailer),Toast.LENGTH_SHORT).show()
            pDialog.dismiss()
        })
        stRequest.setRetryPolicy(DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        Statics.volleyqueue.add(stRequest)
    }

    fun mostrarTrailers(){
        for(trailer in arrayVideos){
            val textView = TextView(context)
            textView.setPadding(20,20,20,20)
            textView.setText("${trailer.name} en ${trailer.site}")
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.youtube,0,0,0)
            textView.setOnClickListener {
                val videoUrl = resources.getString(R.string.urlYoutube) + trailer.key
                startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(videoUrl)))
            }
            det_linearTrailers.addView(textView)
        }
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
        lateinit var movie: Movie
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param movie Movie parameter.
         * @return A new instance of fragment DetailsFragment.
         */
        fun newInstance(movie: Movie): DetailsFragment {
            val fragment = DetailsFragment()
            Companion.movie = movie
            return fragment
        }
    }
}// Required empty public constructor
