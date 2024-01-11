package com.example.estore.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.estore.util.Constants.LOGIN_TAG
import com.example.estore.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    fun loginWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            _login.emit(Resource.Loading())
        }
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        viewModelScope.launch {
                            val user = firebaseAuth.currentUser
                            _login.emit(Resource.Success(user!!))
                        }
                    } else {
                        viewModelScope.launch {
                            _login.emit(Resource.Error("Authentication Failed"))
                        }
                        Log.e(LOGIN_TAG, it.exception?.localizedMessage ?: "firebase error log in")
                    }
                }
        } catch (e: Exception) {
            viewModelScope.launch {
                _login.emit(Resource.Error("Unable to log in"))
                Log.e(LOGIN_TAG, e.localizedMessage ?: "error log in")
            }
        }
    }

    fun passwordReset(email: String) {
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }
        try {
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        _resetPassword.emit(
                            Resource.Success(
                                "Password reset link " +
                                        "has been sent to your email"
                            )
                        )
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _resetPassword.emit(Resource.Error("Incorrect credentials"))
                    }
                }
        } catch (e: Exception) {
            Log.e("PasswordReset", e.localizedMessage ?: "unable to process password reset")
            viewModelScope.launch {
                _resetPassword.emit(Resource.Error("unable to process password reset"))
            }
        }
    }
}