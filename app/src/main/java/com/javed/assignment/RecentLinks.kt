package com.javed.assignment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

class RecentLinks : Fragment() {
    private lateinit var adapter: RVAdapter
    private val items = mutableListOf<Item>()
    private val apiUrl = "https://api.inopenapp.com/api/v1/dashboardNew"
    private val bearerToken = "your_bearer_token_here"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recent_links, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = RVAdapter(items)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        fetchDashboardData()

        return view
    }

    private fun fetchDashboardData() {
        val requestQueue = Volley.newRequestQueue(context)
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET, apiUrl, null,
            Response.Listener { response ->
                try {
                    val apiResponse = Gson().fromJson(response.toString(), ApiResponse::class.java)
                    items.clear()
                    items.addAll(apiResponse.data.recent_links)
                    Log.d("All Items", items.toString())
                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("All Items", e.toString())
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                Log.d("All Items", error.toString())
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $bearerToken"
                return headers
            }
        }

        requestQueue.add(jsonObjectRequest)
    }
}