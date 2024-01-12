package com.example.estore.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.estore.R
import com.example.estore.util.Constants.INTRO_BUTTON_KEY
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val sp: SharedPreferences,
    firebaseAuth: FirebaseAuth
) :
    ViewModel() {

    private val _navigate = MutableStateFlow(0)
    val navigate: StateFlow<Int> = _navigate

    companion object {
        const val SHOPPING_ACTIVITY = 23
        val ACCOUNT_OPTIONS_FRAGMENT =
            R.id.action_introductionFragment_to_accountOptionsFragment
    }

    init {
        val isButtonClicked = sp.getBoolean(INTRO_BUTTON_KEY, false)
        val user = firebaseAuth.currentUser
        if (user != null) {
            viewModelScope.launch {
                _navigate.emit(SHOPPING_ACTIVITY)
            }
        } else if (isButtonClicked) {
            viewModelScope.launch { _navigate.emit(ACCOUNT_OPTIONS_FRAGMENT) }
        } else {
            Unit
        }

    }

    fun startButtonClick() {
        with(sp.edit()) {
            putBoolean(INTRO_BUTTON_KEY, true)
            apply()
        }
        viewModelScope.launch { _navigate.emit(ACCOUNT_OPTIONS_FRAGMENT) }
    }

}