package com.example.nestworks1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.nestworks1.databinding.FragmentAddBlockPopupBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddBlockPopupFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddBlockPopupBinding? = null
    private val binding get() = _binding!!

    private var onSaveListener: OnSaveListener? = null
    private var existingBlock: TaskBlockData? = null

    fun setOnSaveListener(listener: OnSaveListener) {
        this.onSaveListener = listener
    }

    fun setExistingBlock(block: TaskBlockData) {
        this.existingBlock = block
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBlockPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (existingBlock != null) {
            binding.blockTitleEt.setText(existingBlock!!.blockTitle)
        }

        binding.blockNextBtn.setOnClickListener {
            val blockTitle = binding.blockTitleEt.text.toString()
            if (blockTitle.isNotEmpty()) {
                onSaveListener?.onSave(blockTitle)
                dismiss()
            } else {
                Toast.makeText(context, "Please enter a block title", Toast.LENGTH_SHORT).show()
            }
        }

        binding.blockClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface OnSaveListener {
        fun onSave(blockTitle: String)
    }
}
