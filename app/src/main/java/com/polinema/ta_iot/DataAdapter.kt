package com.polinema.ta_iot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DataAdapter(private var dataList: List<Data>) : RecyclerView.Adapter<DataAdapter.DataViewHolder>() {

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val kadarCo2: TextView = itemView.findViewById(R.id.kadar_co2_value)
        val kelembapan: TextView = itemView.findViewById(R.id.kelembapan_value)
        val skala: TextView = itemView.findViewById(R.id.skala_value)
        val suhu: TextView = itemView.findViewById(R.id.suhu_value)
        val waktu: TextView = itemView.findViewById(R.id.waktu_value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_data, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val data = dataList[position]
        holder.kadarCo2.text = "${data.kadar_co2_value} ppm"
        holder.kelembapan.text = "${data.kelembapan_value}%"
        holder.skala.text = "${data.skala_value}"
        holder.suhu.text = "${data.suhu_value} C"
        holder.waktu.text = "${data.waktu_value}"
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newDataList: List<Data>) {
        dataList = newDataList
        notifyDataSetChanged()
    }
}
