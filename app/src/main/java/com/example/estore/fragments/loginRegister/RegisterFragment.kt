package com.example.estore.fragments.loginRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.estore.R
import com.example.estore.data.User
import com.example.estore.databinding.FragmentRegisterBinding
import com.example.estore.util.Resource
import com.example.estore.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        Toast.makeText(
            requireContext(),
            "Sign up successful\nProceed to Login",
            Toast.LENGTH_LONG
        ).show()
        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
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

            tvLoginPrompt.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }

            etFirstName.doOnTextChanged { _, _, _, _ ->
                validateInputFields()
            }
            etLastName.doOnTextChanged { _, _, _, _ ->
                validateInputFields()
            }
            etRegisterEmail.doOnTextChanged { _, _, _, _ ->
                validateInputFields()
            }
            etRegisterPassword.doOnTextChanged { _, _, _, _ ->
                validateInputFields()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.register.collect {
                    when (it) {
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }

                        is Resource.Success -> {
                            completeRegister()
                        }

                        else -> {}
                    }
                    if (it is Resource.Loading) {
                        binding.buttonRegister.text = getString(R.string.loading_request)
                        binding.buttonRegister.isEnabled = false
                    } else {
                        binding.buttonRegister.isEnabled = validateInputFields()
                        binding.buttonRegister.text = getString(R.string.register_app)
                    }
                }
            }
        }
    }

    private fun validateInputFields(): Boolean {
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val email = binding.etRegisterEmail.text.toString()
        val password = binding.etRegisterPassword.text.toString()

        val isPasswordValid = password.isNotBlank()
        val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isFirstNameValid = firstName.isNotBlank()
        val isLastNameValid = lastName.isNotBlank()

        val isInputValid = isEmailValid && isPasswordValid && isFirstNameValid && isLastNameValid

        binding.buttonRegister.isEnabled = isInputValid

        return isInputValid
    }
}