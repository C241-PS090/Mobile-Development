package com.example.myapplication.ViewModel

import HistoryPredictResponse
import Prediction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class AdapterHistory(private val list: List<Prediction>) : RecyclerView.Adapter<AdapterHistory.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.riwayat, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = list[position]
        holder.rvClass.text = currentItem.classType
        holder.rvTanggal.text = currentItem.createdAt
        holder.rvConfidence.text = currentItem.confidence.toString()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvClass: TextView = itemView.findViewById(R.id.Class)
        val rvTanggal: TextView = itemView.findViewById(R.id.tanggal)
        val rvConfidence: TextView = itemView.findViewById(R.id.confidence)
    }
}
