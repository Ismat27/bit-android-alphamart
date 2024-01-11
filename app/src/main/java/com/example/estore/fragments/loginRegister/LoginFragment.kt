package com.example.estore.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.estore.R
import com.example.estore.activities.ShoppingActivity
import com.example.estore.databinding.FragmentLoginBinding
import com.example.estore.dialog.ResetPasswordBottomSheetDialog
import com.example.estore.util.Resource
import com.example.estore.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel by viewModels<LoginViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            buttonLoginLogin.setOnClickListener {
                val email = etLoginEmail.text.toString()
                val password = etLoginPassword.text.toString()
                loginUser(email, password)
            }
            tvRegisterPrompt.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            tvForgotPasswordLogin.setOnClickListener {
                val resetDialog = ResetPasswordBottomSheetDialog { email ->
                    startPasswordReset(email)
                }
                resetDialog.show(childFragmentManager, "ResetPasswordDialog")
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.login.collect {
                    if (it is Resource.Loading) {
                        binding.buttonLoginLogin.text = getString(R.string.loading_request)
                        binding.buttonLoginLogin.isEnabled = false
                    } else {
                        binding.buttonLoginLogin.text = getString(R.string.login_app)
                        binding.buttonLoginLogin.isEnabled = validateInputs()
                    }
                    if (it is Resource.Error) {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    if (it is Resource.Success) {
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                }
            }
        }

        observePasswordResetFlow()
    }

    private fun observePasswordResetFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resetPassword.collect {
                    if (it is Resource.Error) {
                        Snackbar.make(
                            requireView(),
                            it.message ?: "Operation failed",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    if (it is Resource.Success) {
                        Snackbar.make(
                            requireView(),
                            it.data ?: "Check your email for steps to reset your password",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        if (validateInputs()) {
            viewModel.loginWithEmailAndPassword(email, password)
            return
        }
        Toast.makeText(requireContext(), "Enter correct email and password", Toast.LENGTH_LONG)
            .show()
    }

    private fun validateInputs(): Boolean {
        val email = binding.etLoginEmail.text.toString().trim()
        val password = binding.etLoginPassword.toString()
        val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return isEmailValid && password.length >= 6
    }

    private fun startPasswordReset(email: String) {
        val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (isEmailValid) {
            viewModel.passwordReset(email)
        } else {
            Toast.makeText(requireContext(), "Enter correct email", Toast.LENGTH_LONG)
                .show()
        }
    }

}