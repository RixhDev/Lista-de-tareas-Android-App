package com.example.listatareas

/**
 * Singleton simple para gestionar el historial de tareas eliminadas en memoria.
 */
object HistoryManager {
    val deletedTasks = mutableListOf<Item>()

    fun addTask(item: Item) {
        deletedTasks.add(item)
    }

    fun removeSelected(selectedIds: Set<Int>) {
        deletedTasks.removeAll { it.id in selectedIds }
    }

    fun clearAll() {
        deletedTasks.clear()
    }
}