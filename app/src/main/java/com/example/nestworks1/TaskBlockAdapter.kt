package com.example.nestworks1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nestworks1.databinding.ItemTaskBlockBinding

class TaskBlockAdapter(private val blockList: MutableList<TaskBlockData>) :
    RecyclerView.Adapter<TaskBlockAdapter.TaskBlockViewHolder>() {

    private var listener: OnBlockClickListener? = null

    fun setOnBlockClickListener(listener: OnBlockClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskBlockViewHolder {
        val binding = ItemTaskBlockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskBlockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskBlockViewHolder, position: Int) {
        val currentBlock = blockList[position]
        holder.bind(currentBlock)
    }

    override fun getItemCount(): Int {
        return blockList.size
    }

    inner class TaskBlockViewHolder(private val binding: ItemTaskBlockBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(blockData: TaskBlockData) {
            binding.blockTitle.text = blockData.blockTitle

            // Setup tasks RecyclerView
            val taskAdapter = TaskAdapter(blockData.tasks)
            binding.taskRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.taskRecyclerView.adapter = taskAdapter

            taskAdapter.setOnItemClickListener(object : TaskAdapter.OnItemClickListener {
                override fun onItemEditClick(todoData: ToDoData) {
                    listener?.onTaskEditClick(blockData, todoData)
                }

                override fun onItemDeleteClick(position: Int) {
                    listener?.onTaskDeleteClick(blockData, position)
                }
            })

            binding.editBlock.setOnClickListener {
                listener?.onBlockEditClick(blockData)
            }

            binding.deleteBlock.setOnClickListener {
                listener?.onBlockDeleteClick(adapterPosition)
            }

            binding.addItem.setOnClickListener {
                listener?.onAddItemClick(blockData)
            }
        }
    }

    interface OnBlockClickListener {
        fun onBlockEditClick(blockData: TaskBlockData)
        fun onBlockDeleteClick(position: Int)
        fun onAddItemClick(blockData: TaskBlockData) // New method for adding tasks
        fun onTaskEditClick(blockData: TaskBlockData, todoData: ToDoData)
        fun onTaskDeleteClick(blockData: TaskBlockData, position: Int)
    }
}
