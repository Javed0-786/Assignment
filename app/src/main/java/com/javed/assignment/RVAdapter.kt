package com.javed.assignment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RVAdapter(private val items: List<Item>) : RecyclerView.Adapter<RVAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val title: TextView = itemView.findViewById(R.id.title)
        val count: TextView = itemView.findViewById(R.id.count)
        val date: TextView = itemView.findViewById(R.id.date)
        val weblink: TextView = itemView.findViewById(R.id.weblink)
        val copyIcon: ImageView = itemView.findViewById(R.id.copyIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.count.text = item.total_clicks.toString()
        holder.date.text = item.created_at
        holder.weblink.text = item.web_link


        Glide.with(holder.image.context)
            .load(item.original_image)
            .into(holder.image)

        holder.copyIcon.setOnClickListener {
            val clipboard = holder.itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Link", item.web_link)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(holder.itemView.context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = items.size
}
