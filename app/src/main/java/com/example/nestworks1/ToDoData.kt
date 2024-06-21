package com.example.nestworks1

data class ToDoData(
    val taskId: String,
    val taskName: String
)
{
    constructor() : this("", "")
}
