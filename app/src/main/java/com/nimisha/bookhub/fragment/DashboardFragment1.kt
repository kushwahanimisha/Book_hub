package com.nimisha.bookhub.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nimisha.bookhub.R
import com.nimisha.bookhub.adapter.DashboardRecyclerAdapter
import com.nimisha.bookhub.model.Book
import com.nimisha.bookhub.util.ConnectionManager
import org.json.JSONException
import java.util.Collections

class DashboardFragment1 : Fragment() {
    lateinit var progressLayout: RelativeLayout
    private lateinit var recyclerDashboard: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: DashboardRecyclerAdapter
    lateinit var progressBar: ProgressBar
    val bookInfoList = ArrayList<Book>()

    var ratingComparator = Comparator<Book> { book1, book2 ->
       if (book1.bookRating.compareTo(book2.bookRating, true) == 0) {
           book1.bookName.compareTo(book2.bookName,true)
       }else{
           book1.bookRating.compareTo(book2.bookRating,true)
       }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard1, container, false)
        setHasOptionsMenu(true)
        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)
        layoutManager = LinearLayoutManager(activity)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE

        // Move the JSON request here, inside the if block
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http:/13.235.250.119/v1/book/fetch_books/"

        val jsonObjectRequest =
            object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                try {
                    progressLayout.visibility = View.GONE

                    val success = it.getBoolean("success")

                    if (success) {
                        val data = it.getJSONArray("data")
                        for (i in 0 until data.length()) {
                            val bookJSONObject = data.getJSONObject(i)
                            val bookObject = Book(
                                bookJSONObject.getString("book_id"),
                                bookJSONObject.getString("name"),
                                bookJSONObject.getString("author"),
                                bookJSONObject.getString("rating"),
                                bookJSONObject.getString("price"),
                                bookJSONObject.getString("image")
                            )
                            bookInfoList.add(bookObject)
                        }
                        // Initialize and set the adapter after parsing the data
                        recyclerAdapter = DashboardRecyclerAdapter(
                            activity as Context,
                            bookInfoList
                        )
                        recyclerDashboard.adapter = recyclerAdapter
                        recyclerDashboard.layoutManager = layoutManager

                    } else {
                        Toast.makeText(
                            activity as Context,
                            "Some error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(
                        activity as Context,
                        "Some unexpected error occured",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }, Response.ErrorListener {
                // Handle network errors here
                if(activity!=null) {
                    Toast.makeText(
                        activity as Context,
                        "Volleyoccurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "3a73388dd4a314"
                    return headers
                }
            }

        queue.add(jsonObjectRequest)


        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_dashboard,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item?.itemId
        if(id==R.id.action_sort){
            Collections.sort(bookInfoList,ratingComparator)
            bookInfoList.reverse()
        }

        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }


}