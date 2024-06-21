package com.example.nestworks1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.nestworks1.databinding.FragmentAddTodoPopupBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddTodoPopupFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddTodoPopupBinding? = null
    private val binding get() = _binding!!

    private var onTaskSaveListener: OnTaskSaveListener? = null
    private var blockId: String? = null
    private var existingTask: ToDoData? = null

    fun setOnSaveListener(listener: OnTaskSaveListener) {
        this.onTaskSaveListener = listener
    }

    fun setBlockId(blockId: String) {
        this.blockId = blockId
    }

    fun setExistingTask(task: ToDoData) {
        this.existingTask = task
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTodoPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (existingTask != null) {
            binding.todoEt.setText(existingTask!!.taskName)
        }

        binding.todoNextBtn.setOnClickListener {
            val taskName = binding.todoEt.text.toString()
            if (taskName.isNotEmpty()) {
                blockId?.let {
                    onTaskSaveListener?.onTaskSave(it, taskName)
                }
                dismiss()
            } else {
                Toast.makeText(context, "Please enter a task name", Toast.LENGTH_SHORT).show()
            }
        }

        binding.todoClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface OnTaskSaveListener {
        fun onTaskSave(blockId: String, taskName: String)
    }
}
