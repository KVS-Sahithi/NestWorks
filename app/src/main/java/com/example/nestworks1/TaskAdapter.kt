package com.example.nestworks1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nestworks1.databinding.ItemTaskBinding

class TaskAdapter(private val itemList: MutableList<ToDoData>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(todoData: ToDoData) {
            binding.taskName.text = todoData.taskName



            binding.deleteTask.setOnClickListener {
                listener?.onItemDeleteClick(adapterPosition)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemEditClick(todoData: ToDoData)
        fun onItemDeleteClick(position: Int)
    }
}
