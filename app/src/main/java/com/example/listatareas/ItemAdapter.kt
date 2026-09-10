package com.example.listatareas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.recyclerview.widget.RecyclerView
import android.content.res.ColorStateList
import android.graphics.Color

/**
 * Adapter para gestionar la lista de tareas (Item) en un RecyclerView.
 * Extiende de RecyclerView.Adapter y utiliza un ViewHolder interno.
 */
class ItemAdapter(private val taskList: MutableList<Item>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    /**
     * ViewHolder que mantiene las referencias a las vistas de cada elemento de la lista.
     * Esto evita llamadas repetitivas a findViewById y mejora el rendimiento.
     */
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgIcon: ImageView = view.findViewById(R.id.img_icon)
        val txtName: TextView = view.findViewById(R.id.txt_name)
        val txtHora: TextView = view.findViewById(R.id.txt_hora)
        val txtDetails: TextView = view.findViewById(R.id.txt_compra_details)
        val chkDone: CheckBox = view.findViewById(R.id.chk_done)
        val ratingPriority: RatingBar = view.findViewById(R.id.rating_priority)
    }

    /**
     * Se llama cuando el RecyclerView necesita un nuevo ViewHolder.
     * Infla el diseño XML (item_task.xml) y crea la instancia del ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return ItemViewHolder(view)
    }

    /**
     * Se llama para mostrar los datos en una posición específica.
     * Asigna los valores del objeto Item a los widgets y configura los listeners de interacción.
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = taskList[position]

        // Asignar datos a los widgets
        holder.txtName.text = item.name
        
        // Manejar la hora opcional (null)
        if (item.hora != null) {
            holder.txtHora.text = item.hora
            holder.txtHora.visibility = View.VISIBLE
        } else {
            holder.txtHora.visibility = View.GONE
        }

        // Mostrar cantidad y precio si existen
        val details = mutableListOf<String>()
        if (!item.quantity.isNullOrEmpty()) details.add("Cant: ${item.quantity}")
        if (!item.price.isNullOrEmpty()) details.add("Precio: ${item.price}")

        if (details.isNotEmpty()) {
            holder.txtDetails.text = details.joinToString(" - ")
            holder.txtDetails.visibility = View.VISIBLE
        } else {
            holder.txtDetails.visibility = View.GONE
        }
        
        holder.imgIcon.setImageResource(item.iconRes)
        
        // Evitar que el listener se dispare al asignar el estado inicial
        holder.chkDone.setOnCheckedChangeListener(null)
        holder.chkDone.isChecked = item.isDone
        
        holder.ratingPriority.rating = item.priority.toFloat()
        updateRatingColor(holder.ratingPriority, item.priority)

        // Actualizar el modelo cuando el usuario cambia el CheckBox
        holder.chkDone.setOnCheckedChangeListener { _, isChecked ->
            item.isDone = isChecked
        }

        // Actualizar el modelo cuando el usuario cambia la prioridad en el RatingBar
        holder.ratingPriority.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                val priority = rating.toInt()
                item.priority = priority
                updateRatingColor(holder.ratingPriority, priority)
            }
        }

        // --- Lógica para editar la tarea al hacer clic en el ítem ---
        holder.itemView.setOnClickListener {
            showEditDialog(holder, item, position)
        }
    }

    /**
     * Muestra un BottomSheetDialog con un layout personalizado para editar la tarea.
     */
    private fun showEditDialog(holder: ItemViewHolder, item: Item, position: Int) {
        val context = holder.itemView.context
        // Crear el BottomSheetDialog
        val dialog = BottomSheetDialog(context)
        
        // Inflar el layout personalizado dialog_edit_task.xml
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_edit_task, null)
        
        // Referenciar los widgets del diálogo
        val editName = dialogView.findViewById<EditText>(R.id.edit_name)
        val editHour = dialogView.findViewById<EditText>(R.id.edit_hour)
        val editQuantity = dialogView.findViewById<EditText>(R.id.edit_quantity)
        val editPrice = dialogView.findViewById<EditText>(R.id.edit_price)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinner_category)
        val btnSave = dialogView.findViewById<Button>(R.id.btn_save)
        val btnDelete = dialogView.findViewById<Button>(R.id.btn_delete)

        // Configurar el Spinner de categorías
        val categories = arrayOf("Trabajo", "Estudio", "Personal", "Salud", "Hogar", "Otros")
        val spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = spinnerAdapter

        // Cargar los valores actuales del objeto Item
        editName.setText(item.name)
        editHour.setText(item.hora ?: "")
        editQuantity.setText(item.quantity ?: "")
        editPrice.setText(item.price ?: "")
        
        // Seleccionar la categoría actual en el Spinner
        val categoryIndex = categories.indexOf(item.category)
        if (categoryIndex >= 0) {
            spinnerCategory.setSelection(categoryIndex)
        }

        // Configurar la acción del botón Guardar
        btnSave.setOnClickListener {
            val newName = editName.text.toString()
            val newHour = editHour.text.toString()
            val newQuantity = editQuantity.text.toString()
            val newPrice = editPrice.text.toString()
            val newCategory = spinnerCategory.selectedItem.toString()

            if (newName.isNotEmpty()) {
                // 1. Actualizar los campos del objeto Item
                item.name = newName
                item.hora = if (newHour.isEmpty()) null else newHour
                item.quantity = if (newQuantity.isEmpty()) null else newQuantity
                item.price = if (newPrice.isEmpty()) null else newPrice
                item.category = newCategory

                // 2. Refrescar el ítem específico en el RecyclerView
                notifyItemChanged(position)

                // 3. Cerrar el diálogo
                dialog.dismiss()
            }
        }

        // Configurar la acción del botón Eliminar
        btnDelete.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                val deletedItem = taskList[currentPosition]
                HistoryManager.addTask(deletedItem)
                taskList.removeAt(currentPosition)
                notifyItemRemoved(currentPosition)
                dialog.dismiss()
            }
        }
        
        // Asignar la vista al diálogo y mostrarlo
        dialog.setContentView(dialogView)
        dialog.show()
    }

    /**
     * Actualiza el color de las estrellas basándose en la prioridad.
     */
    private fun updateRatingColor(ratingBar: RatingBar, priority: Int) {
        val color = when (priority) {
            5 -> Color.RED          // Crítica
            4 -> Color.parseColor("#FF6D00") // Alta (Naranja fuerte)
            3 -> Color.YELLOW       // Media
            else -> Color.CYAN      // Baja / Normal
        }
        ratingBar.progressTintList = ColorStateList.valueOf(color)
    }

    /**
     * Devuelve el tamaño total de la lista de datos.
     */
    override fun getItemCount(): Int = taskList.size
}
