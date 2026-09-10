package com.example.listatareas

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private lateinit var btnDeleteSelected: Button
    private lateinit var btnClearAll: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.recycler_history)
        btnDeleteSelected = findViewById(R.id.btn_delete_selected)
        btnClearAll = findViewById(R.id.btn_clear_all)

        adapter = HistoryAdapter(HistoryManager.deletedTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnDeleteSelected.setOnClickListener {
            if (adapter.selectedItems.isEmpty()) {
                Toast.makeText(this, "Selecciona tareas para borrar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            HistoryManager.removeSelected(adapter.selectedItems)
            adapter.selectedItems.clear()
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Selección eliminada", Toast.LENGTH_SHORT).show()
        }

        btnClearAll.setOnClickListener {
            if (HistoryManager.deletedTasks.isEmpty()) return@setOnClickListener
            
            HistoryManager.clearAll()
            adapter.selectedItems.clear()
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Historial vaciado", Toast.LENGTH_SHORT).show()
        }
    }
}