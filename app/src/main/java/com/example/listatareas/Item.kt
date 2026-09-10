package com.example.listatareas

class Item (
    val id: Int,
    var name: String,
    var hora: String? = null,
    var isDone: Boolean = false,
    var priority: Int = 0,
    var category: String = "General",
    val iconRes: Int = R.drawable.ic_launcher_foreground,
    var quantity: String? = null,
    var price: String? = null
) {
    override fun toString(): String {
        return "[$id] $name - ${hora ?: "Sin hora"} (Category: $category, Done: $isDone, Priority: $priority, Qty: $quantity, Price: $price)"
    }
}