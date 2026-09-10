package com.example.listatareas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(private val taskList: List<Item>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    val selectedItems = mutableSetOf<Int>()

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txt_name)
        val txtDetails: TextView = view.findViewById(R.id.txt_details)
        val chkSelect: CheckBox = view.findViewById(R.id.chk_select)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_task, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = taskList[position]
        holder.txtName.text = item.name
        
        val details = mutableListOf<String>()
        if (item.hora != null) details.add(item.hora!!)
        if (!item.quantity.isNullOrEmpty()) details.add("Cant: ${item.quantity}")
        if (!item.price.isNullOrEmpty()) details.add("Precio: ${item.price}")
        
        holder.txtDetails.text = if (details.isNotEmpty()) details.joinToString(" - ") else "Sin detalles"
        
        holder.chkSelect.setOnCheckedChangeListener(null)
        holder.chkSelect.isChecked = selectedItems.contains(item.id)
        
        holder.chkSelect.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedItems.add(item.id)
            } else {
                selectedItems.remove(item.id)
            }
        }
    }

    override fun getItemCount(): Int = taskList.size
}