package com.example.estore.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.estore.data.User
import com.example.estore.util.Constants
import com.example.estore.util.Constants.USER_COLLECTION
import com.example.estore.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Uninitialized())

    val register: Flow<Resource<User>> = _register
    fun createAccountWithEmailAndPassword(user: User, password: String) {
        runBlocking {
            _register.emit(Resource.Loading())
        }
        try {
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener { it ->
                    it.user?.let {
                        saveUser(it.uid, user)
                        _register.value = Resource.Success(user)
                    }
                }
                .addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                    Log.e(Constants.REGISTER_TAG, it.localizedMessage ?: "error firebase sign up")
                }
        } catch (e: Exception) {
            viewModelScope.launch {
                _register.emit(Resource.Error("Unable to sign up"))
                Log.e(Constants.REGISTER_TAG, e.localizedMessage ?: "error sign up")
            }
        }

    }

    private fun saveUser(uid: String, user: User) {
        db.collection(USER_COLLECTION)
            .document(uid)
            .set(user)
            .addOnSuccessListener {
//                _register.value = Resource.Success(user)
            }
            .addOnFailureListener {
//                _register.value = Resource.Error(it.message.toString())
            }
    }

    fun resetStatus() {
        _register.value = Resource.Uninitialized()
    }
}