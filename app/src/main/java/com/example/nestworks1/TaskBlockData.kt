package com.example.nestworks1

data class TaskBlockData(
    val blockId: String,
    var blockTitle: String,
    val tasks: MutableList<ToDoData>
)
{
    constructor() : this("", "", mutableListOf())
}
