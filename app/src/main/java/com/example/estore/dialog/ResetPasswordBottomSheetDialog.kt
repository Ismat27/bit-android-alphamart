package com.example.estore.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.estore.R
import com.example.estore.databinding.ResetPasswordDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ResetPasswordBottomSheetDialog(val onClickReset: (String) -> Unit) :
    BottomSheetDialogFragment() {

    private lateinit var binding: ResetPasswordDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomDialog)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.setCanceledOnTouchOutside(false)
        binding = ResetPasswordDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            cancelButton.setOnClickListener { dismiss() }

            resetButton.setOnClickListener {
                val email = etResetEmail.text.toString()
                onClickReset(email)
                dismiss()
            }

        }
    }

}