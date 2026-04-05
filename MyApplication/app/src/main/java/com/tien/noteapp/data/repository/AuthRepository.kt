package com.tien.noteapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tien.noteapp.data.model.User
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun registerUser(email: String, password: String, displayName: String): Result<Unit> = try {
        // Tạo tài khoản Firebase Auth
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()

        val userId = firebaseAuth.currentUser?.uid ?: throw Exception("User ID not found")

        // Lưu thông tin user vào Firestore
        val user = User(
            id = userId,
            email = email,
            displayName = displayName
        )

        firestore.collection("users").document(userId).set(user).await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun loginUser(email: String, password: String): Result<Unit> = try {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun logoutUser() {
        firebaseAuth.signOut()
    }

    fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

    fun isUserAuthenticated(): Boolean = firebaseAuth.currentUser != null

    suspend fun getCurrentUserInfo(): Result<User> = try {
        val userId = firebaseAuth.currentUser?.uid ?: throw Exception("User not authenticated")
        val documentSnapshot = firestore.collection("users").document(userId).get().await()
        val user = documentSnapshot.toObject(User::class.java) ?: throw Exception("User data not found")
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateUserProfile(displayName: String, bio: String): Result<Unit> = try {
        val userId = firebaseAuth.currentUser?.uid ?: throw Exception("User not authenticated")
        val updates = mapOf(
            "displayName" to displayName,
            "bio" to bio
        )
        firestore.collection("users").document(userId).update(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun resetPassword(email: String): Result<Unit> = try {
        firebaseAuth.sendPasswordResetEmail(email)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
