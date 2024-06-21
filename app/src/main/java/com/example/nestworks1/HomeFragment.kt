package com.example.nestworks1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nestworks1.databinding.FragmentHomeBinding
import com.google.firebase.database.FirebaseDatabase

class HomeFragment : Fragment(), TaskBlockAdapter.OnBlockClickListener, AddTodoPopupFragment.OnTaskSaveListener, AddBlockPopupFragment.OnSaveListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskBlockAdapter: TaskBlockAdapter
    private val blockList = mutableListOf<TaskBlockData>()

    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.addBtnHome.setOnClickListener {
            showAddBlockPopup()
        }
    }

    private fun setupRecyclerView() {
        taskBlockAdapter = TaskBlockAdapter(blockList)
        binding.todoRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.todoRecyclerView.adapter = taskBlockAdapter

        taskBlockAdapter.setOnBlockClickListener(this)
    }

    private fun showAddBlockPopup() {
        val addBlockPopupFragment = AddBlockPopupFragment()
        addBlockPopupFragment.setOnSaveListener(this)
        addBlockPopupFragment.show(childFragmentManager, "AddBlockPopupFragment")
    }

    override fun onSave(blockTitle: String) {
        val newBlock = TaskBlockData(
            blockId = System.currentTimeMillis().toString(),
            blockTitle = blockTitle,
            tasks = mutableListOf()
        )
        blockList.add(newBlock)
        taskBlockAdapter.notifyDataSetChanged()
        saveBlockToFirebase(newBlock)
    }

    private fun saveBlockToFirebase(block: TaskBlockData) {
        database.child("blocks").child(block.blockId).setValue(block)
    }

    private fun showAddTaskPopup(blockData: TaskBlockData) {
        val addTodoPopupFragment = AddTodoPopupFragment()
        addTodoPopupFragment.setOnSaveListener(this)
        addTodoPopupFragment.setBlockId(blockData.blockId)
        addTodoPopupFragment.show(childFragmentManager, "AddTodoPopupFragment")
    }

    override fun onAddItemClick(blockData: TaskBlockData) {
        showAddTaskPopup(blockData)
    }

    override fun onBlockEditClick(blockData: TaskBlockData) {
        showEditBlockPopup(blockData)
    }

    private fun showEditBlockPopup(blockData: TaskBlockData) {
        val editBlockPopupFragment = AddBlockPopupFragment()
        editBlockPopupFragment.setExistingBlock(blockData)
        editBlockPopupFragment.setOnSaveListener(object : AddBlockPopupFragment.OnSaveListener {
            override fun onSave(blockTitle: String) {
                blockData.blockTitle = blockTitle
                taskBlockAdapter.notifyDataSetChanged()
                saveBlockToFirebase(blockData)
            }
        })
        editBlockPopupFragment.show(childFragmentManager, "EditBlockPopupFragment")
    }

    override fun onBlockDeleteClick(position: Int) {
        val blockId = blockList[position].blockId
        blockList.removeAt(position)
        taskBlockAdapter.notifyItemRemoved(position)
        deleteBlockFromFirebase(blockId)
    }

    private fun deleteBlockFromFirebase(blockId: String) {
        database.child("blocks").child(blockId).removeValue()
    }

    override fun onTaskSave(blockId: String, taskName: String) {
        val blockIndex = blockList.indexOfFirst { it.blockId == blockId }
        if (blockIndex != -1) {
            val taskId = System.currentTimeMillis().toString()
            val newTask = ToDoData(taskId, taskName)
            blockList[blockIndex].tasks.add(newTask)
            taskBlockAdapter.notifyDataSetChanged()
            saveTaskToFirebase(blockId, newTask)
        }
    }

    private fun saveTaskToFirebase(blockId: String, task: ToDoData) {
        database.child("blocks").child(blockId).child("tasks").child(task.taskId).setValue(task)
    }

    override fun onTaskEditClick(blockData: TaskBlockData, todoData: ToDoData) {
        showEditTaskPopup(blockData, todoData)
    }

    private fun showEditTaskPopup(blockData: TaskBlockData, todoData: ToDoData) {
        val editTodoPopupFragment = AddTodoPopupFragment()
        editTodoPopupFragment.setExistingTask(todoData)
        editTodoPopupFragment.setOnSaveListener(object : AddTodoPopupFragment.OnTaskSaveListener {
            override fun onTaskSave(blockId: String, taskName: String) {
                val blockIndex = blockList.indexOfFirst { it.blockId == blockId }
                if (blockIndex != -1) {
                    val taskId = todoData.taskId
                    val updatedTask = ToDoData(taskId, taskName)
                    blockList[blockIndex].tasks.replaceAll { if (it.taskId == taskId) updatedTask else it }
                    taskBlockAdapter.notifyDataSetChanged()
                    saveTaskToFirebase(blockId, updatedTask)
                }
            }
        })
        editTodoPopupFragment.show(childFragmentManager, "EditTodoPopupFragment")
    }

    override fun onTaskDeleteClick(blockData: TaskBlockData, position: Int) {
        val taskId = blockData.tasks[position].taskId
        blockData.tasks.removeAt(position)
        taskBlockAdapter.notifyDataSetChanged()
        deleteTaskFromFirebase(blockData.blockId, taskId)
    }

    private fun deleteTaskFromFirebase(blockId: String, taskId: String) {
        database.child("blocks").child(blockId).child("tasks").child(taskId).removeValue()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}