package com.javed.assignment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager2
    lateinit var tabLayout: TabLayout
    private val items = mutableListOf<OverAllUrlChart>()
    private val apiUrl = "https://api.inopenapp.com/api/v1/dashboardNew"
    private lateinit var lineChart: LineChart
    private val bearerToken = "your_bearer_token_here"
    private lateinit var todayClicks: TextView
    private lateinit var location: TextView
    private lateinit var topSource: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchDashboardData()
        setupViewPager()

        lineChart = findViewById(R.id.lineChart)
        if(items.isEmpty())
            plotGraph(getMockData())  // Use the mock data for plotting initially
        else {
            val data = items.associate { it.toString().slice(0..2) to it.clicks }
            plotGraph(data)
        }
    }

    private fun setupViewPager() {
        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)

        todayClicks = findViewById(R.id.clickcount)
        location = findViewById(R.id.location)
        topSource = findViewById(R.id.topSource)

        viewPager.adapter = FragmentPageAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Top Links"
                1 -> tab.text = "Recent Links"
            }
        }.attach()
    }

    private fun fetchDashboardData() {
        val requestQueue = Volley.newRequestQueue(this)
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET, apiUrl, null,
            Response.Listener { response ->
                try {
                    val apiResponse = Gson().fromJson(response.toString(), ApiResponse::class.java)
                    items.clear()
                    items.addAll(apiResponse.data.overall_url_chart)
                    Log.d("All Items", items.toString())

                    // Update the chart with actual data
                    val data = apiResponse.data.overall_url_chart.associate { it.toString().slice(0..2) to it.clicks }

                    todayClicks.text = apiResponse.data.today_clicks.toString()
                    location.text = apiResponse.data.top_location
                    topSource.text = apiResponse.data.top_source
                    plotGraph(data)
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

    private fun getMockData(): Map<String, Int> {
        return mapOf(
            "00:00" to 0,
            "01:00" to 0,
            "02:00" to 0,
            "03:00" to 0,
            "04:00" to 0,
            "05:00" to 0,
            "06:00" to 0,
            "07:00" to 1,
            "08:00" to 0,
            "09:00" to 0,
            "10:00" to 0,
            "11:00" to 0,
            "12:00" to 2,
            "13:00" to 0,
            "14:00" to 1,
            "15:00" to 0,
            "16:00" to 1,
            "17:00" to 0,
            "18:00" to 0,
            "19:00" to 0,
            "20:00" to 0,
            "21:00" to 0,
            "22:00" to 0,
            "23:00" to 0
        )
    }

    private fun plotGraph(data: Map<String, Int>) {
        val entries = ArrayList<Entry>()
        var index = 0f
        for ((key, value) in data) {
            entries.add(Entry(index, value.toFloat()))
            index += 1f
        }

        val lineDataSet = LineDataSet(entries, "Clicks Over Time")
        lineDataSet.color = Color.BLUE
        lineDataSet.valueTextColor = Color.BLACK
        lineDataSet.lineWidth = 2f
        lineDataSet.setDrawCircles(true)
        lineDataSet.setDrawCircleHole(true)
        lineDataSet.setCircleColor(Color.RED)
        lineDataSet.circleRadius = 5f

        val lineData = LineData(lineDataSet)
        lineChart.data = lineData
        lineChart.isDoubleTapToZoomEnabled = false

        val description = Description()
        description.text = "Time of Day"
        lineChart.description = description

        lineChart.xAxis.valueFormatter = MyXAxisFormatter()
        lineChart.invalidate() // refresh
    }

    class MyXAxisFormatter : ValueFormatter() {
        private val hours = arrayOf(
            "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00",
            "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00",
            "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"
        )
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return hours.getOrNull(value.toInt()) ?: value.toString()
        }
    }

    fun openWhatsappChat(view: View) {
        val formattedNumber = "9663908504".replace("+", "").replace(" ", "")
        val url = "https://api.whatsapp.com/send?phone=$formattedNumber"

        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            intent.setPackage("com.whatsapp")
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            // Handle the case where WhatsApp is not installed
            Toast.makeText(this, "WhatsApp is not installed on this device", Toast.LENGTH_SHORT).show()
        }
    }

    fun openInBrowser(view: View) {
        val url = "https://openinapp.com/faq"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}
