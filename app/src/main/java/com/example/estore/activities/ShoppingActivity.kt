package com.example.estore.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.estore.R
import com.example.estore.databinding.ActivityShoppingBinding

class ShoppingActivity : AppCompatActivity() {

    private val binding by lazy { ActivityShoppingBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentShoppingHost) as NavHostFragment

        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}