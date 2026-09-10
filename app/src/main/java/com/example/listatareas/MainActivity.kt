package com.example.listatareas

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.content.Intent
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAdd: Button
    private lateinit var btnHistory: Button
    private lateinit var editTask: EditText
    private lateinit var adapter: ItemAdapter
    private val taskList = mutableListOf<Item>()
    private var lastId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 1. Inicializar vistas
        recyclerView = findViewById(R.id.recycler_tasks)
        btnAdd = findViewById(R.id.btn_add)
        btnHistory = findViewById(R.id.btn_view_history)
        editTask = findViewById(R.id.edit_task)

        // 2. Datos iniciales
        val initialTasks = listOf(
            Item(++lastId, "Estudiar para el examen", "09:00", false, 5, "Estudio"),
            Item(++lastId, "Comprar víveres", "11:30", false, 2, "Hogar", quantity = "Lista", price = "$0"),
            Item(++lastId, "Gimnasio", "17:00", false, 4, "Salud")
        )
        taskList.addAll(initialTasks)

        // 3. Configurar el Adapter y el RecyclerView
        adapter = ItemAdapter(taskList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 4. Lógica para abrir el diálogo de añadir tarea
        val openAddDialog = { showAddTaskDialog() }
        btnAdd.setOnClickListener { openAddDialog() }
        editTask.setOnClickListener { openAddDialog() }

        // 5. Lógica para ver el historial
        btnHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Muestra un BottomSheetDialog para crear una nueva tarea o lista.
     */
    private fun showAddTaskDialog() {
        val dialog = BottomSheetDialog(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_add_task, null)

        val editName = dialogView.findViewById<EditText>(R.id.edit_name)
        val editHour = dialogView.findViewById<EditText>(R.id.edit_hour)
        val editQuantity = dialogView.findViewById<EditText>(R.id.edit_quantity)
        val editPrice = dialogView.findViewById<EditText>(R.id.edit_price)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinner_category)
        val btnCreate = dialogView.findViewById<Button>(R.id.btn_create)

        // Configurar el Spinner de categorías
        val categories = arrayOf("Compra", "Trabajo", "Estudio", "Personal", "Salud", "Hogar", "Otros")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = spinnerAdapter
        spinnerCategory.setSelection(categories.indexOf("Personal")) // Por defecto

        // Pre-llenar hora actual
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        editHour.setText(currentTime)

        btnCreate.setOnClickListener {
            val name = editName.text.toString()
            val hour = editHour.text.toString()
            val quantity = editQuantity.text.toString()
            val price = editPrice.text.toString()
            val category = spinnerCategory.selectedItem.toString()

            if (name.isNotEmpty()) {
                val newItem = Item(
                    id = ++lastId,
                    name = name,
                    hora = if (hour.isEmpty()) null else hour,
                    quantity = if (quantity.isEmpty()) null else quantity,
                    price = if (price.isEmpty()) null else price,
                    category = category,
                    isDone = false
                )

                taskList.add(newItem)
                adapter.notifyItemInserted(taskList.size - 1)
                recyclerView.scrollToPosition(taskList.size - 1)
                dialog.dismiss()
            } else {
                editName.error = "El nombre es obligatorio"
            }
        }

        dialog.setContentView(dialogView)
        dialog.show()
    }
}