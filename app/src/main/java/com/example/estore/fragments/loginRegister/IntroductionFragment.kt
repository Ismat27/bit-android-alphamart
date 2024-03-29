package com.example.estore.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.estore.R
import com.example.estore.activities.ShoppingActivity
import com.example.estore.databinding.FragmentIntroductionBinding
import com.example.estore.viewmodel.IntroductionViewModel
import com.example.estore.viewmodel.IntroductionViewModel.Companion.ACCOUNT_OPTIONS_FRAGMENT
import com.example.estore.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IntroductionFragment : Fragment(R.layout.fragment_introduction) {

    private lateinit var binding: FragmentIntroductionBinding
    private val viewModel by viewModels<IntroductionViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroductionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            startButton.setOnClickListener {
                viewModel.startButtonClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigate.collect {
                    when (it) {
                        SHOPPING_ACTIVITY -> {
                            Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }

                        ACCOUNT_OPTIONS_FRAGMENT -> {
                            findNavController().navigate(it)
                        }
                    }
                }
            }
        }

    }

}