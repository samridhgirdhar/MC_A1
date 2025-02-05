package com.example.xml_version

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StopsAdapter(
    private var stopsList: List<Stop>,
    private var isMiles: Boolean = false
) : RecyclerView.Adapter<StopsAdapter.StopViewHolder>() {

    inner class StopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStopName: TextView = itemView.findViewById(R.id.tvStopName)
        val tvDistance: TextView = itemView.findViewById(R.id.tvDistance)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvVisa: TextView = itemView.findViewById(R.id.tvVisaRequirement)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stop, parent, false)
        return StopViewHolder(view)
    }

    override fun onBindViewHolder(holder: StopViewHolder, position: Int) {
        val stop = stopsList[position]
        holder.tvStopName.text = stop.name

        // Convert distance if needed
        val distanceText = if (isMiles) {
            // 1 km = 0.621371 miles
            String.format("%.2f miles", stop.distanceKm * 0.621371)
        } else {
            "${stop.distanceKm} km"
        }
        holder.tvDistance.text = "Distance: $distanceText"

        holder.tvTime.text = "Time: ${stop.timeMinutes} minutes"
        holder.tvVisa.text = "Transit Visa Required: ${if (stop.transitVisaRequired) "Yes" else "No"}"
    }

    override fun getItemCount(): Int = stopsList.size

    fun updateData(newStops: List<Stop>, isMiles: Boolean) {
        this.stopsList = newStops
        this.isMiles = isMiles
        notifyDataSetChanged()
    }
}
