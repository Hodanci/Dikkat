package com.example.dikkat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dikkat.R
import com.example.dikkat.models.SavedRoute

class SavedRoutesAdapter(
    private val routes: MutableList<SavedRoute>,
    private val onRoutesItemClicked: (SavedRoute) -> Unit,
    private val onRouteDelete: (SavedRoute) -> Unit,
    private val onRouteShare: (SavedRoute) -> Unit,
    private val onRouteEdit: (SavedRoute) -> Unit
) : RecyclerView.Adapter<SavedRoutesAdapter.RouteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_route, parent, false)
        return RouteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.bind(routes[position])
    }

    override fun getItemCount(): Int = routes.size

    inner class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.routeNameTextView)
        private val distanceTextView: TextView = itemView.findViewById(R.id.routeDistanceTextView)
        private val durationTextView: TextView = itemView.findViewById(R.id.routeDurationTextView)
        private val addressesTextView: TextView = itemView.findViewById(R.id.routeAddressesTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.routeDateTextView)
        private val shareButton: ImageButton = itemView.findViewById(R.id.shareRouteButton)
        private val editButton: ImageButton = itemView.findViewById(R.id.editRouteButton)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteRouteButton)

        fun bind(route: SavedRoute) {
            nameTextView.text = route.name
            distanceTextView.text = "📍 ${route.getFormattedDistance()}"
            durationTextView.text = "⏱️ ${route.getFormattedDuration()}"
            addressesTextView.text = "${route.startAddress}\n➜\n${route.endAddress}"
            dateTextView.text = route.getFormattedDate()

            itemView.setOnClickListener {
                onRoutesItemClicked(route)
            }

            shareButton.setOnClickListener {
                onRouteShare(route)
            }

            editButton.setOnClickListener {
                onRouteEdit(route)
            }

            deleteButton.setOnClickListener {
                onRouteDelete(route)
            }
        }
    }

    fun updateRoutes(newRoutes: List<SavedRoute>) {
        routes.clear()
        routes.addAll(newRoutes)
        notifyDataSetChanged()
    }
}