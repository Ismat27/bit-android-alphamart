package com.example.estore.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.estore.R
import com.example.estore.data.User
import com.example.estore.databinding.FragmentRegisterBinding
import com.example.estore.util.Resource
import com.example.estore.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


const val REGISTER_TAG = "RegisterTag"

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun completeRegister() {
        with(binding) {
            buttonRegister.isEnabled = true
            etFirstName.setText("")
            etLastName.setText("")
            etRegisterEmail.setText("")
            etRegisterPassword.setText("")
        }
        viewModel.resetStatus()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            buttonRegister.setOnClickListener {
                val user =
                    User(
                        etFirstName.text.toString().trim(),
                        etLastName.text.toString().trim(),
                        etRegisterEmail.text.toString().trim(),
                    )
                val password = etRegisterPassword.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password)

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.register.collect {
                    when (it) {
                        is Resource.Error -> {
                            Log.d(REGISTER_TAG, it.message ?: "Error registering")
                        }

                        is Resource.Loading -> {}

                        is Resource.Success -> {
                            Log.d(REGISTER_TAG, it.data.toString())
                            completeRegister()
                        }

                        is Resource.Uninitialized -> {}
                    }
                    if (it is Resource.Loading) {
                        binding.buttonRegister.text = getString(R.string.loading_request)
                        binding.buttonRegister.isEnabled = false
                    } else {
                        binding.buttonRegister.isEnabled = true
                        binding.buttonRegister.text = getString(R.string.register_app)
                    }
                }
            }
        }
    }
}