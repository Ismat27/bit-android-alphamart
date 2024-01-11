package com.example.estore.viewmodel

import androidx.lifecycle.ViewModel
import com.example.estore.data.User
import com.example.estore.util.Constants.USER_COLLECTION
import com.example.estore.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
        firebaseAuth.createUserWithEmailAndPassword(user.email, password)
            .addOnSuccessListener { it ->
                it.user?.let {
                    saveUser(it.uid, user)
                    _register.value = Resource.Success(user)
                }
            }
            .addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
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