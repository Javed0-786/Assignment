package com.javed.assignment

data class Data(val recent_links: List<Item>, val top_links: List<Item>, val overall_url_chart: List<OverAllUrlChart>, val today_clicks: Int, val top_location: String, val top_source: String)