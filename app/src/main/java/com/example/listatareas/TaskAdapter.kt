package com.example.listatareas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val taskList: List<Item>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txt_name)
        val txtHora: TextView = view.findViewById(R.id.txt_hora)
        val chkDone: CheckBox = view.findViewById(R.id.chk_done)
        val ratingPriority: RatingBar = view.findViewById(R.id.rating_priority)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.txtName.text = task.name
        holder.txtHora.text = task.hora
        holder.chkDone.isChecked = task.isDone
        holder.ratingPriority.rating = task.priority.toFloat()
    }

    override fun getItemCount(): Int = taskList.size
}
