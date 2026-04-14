package com.tien.noteapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.tien.noteapp.data.model.Note
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NoteRepository(
    private val firestore: FirebaseFirestore,
    private val currentUserId: String
) {

    // Lấy danh sách ghi chú theo thời gian (real-time) - Tất cả notes
    fun getNotes(): Flow<List<Note>> = callbackFlow {
        val listener = firestore.collection("notes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val notes = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Note::class.java)?.copy(id = doc.id)
                }?.sortedByDescending { it.title } ?: emptyList()
                
                trySend(notes)
            }

        awaitClose { listener.remove() }
    }

    // Tìm kiếm ghi chú - Tất cả notes
    fun searchNotes(query: String): Flow<List<Note>> = callbackFlow {
        val listener = firestore.collection("notes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val notes = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Note::class.java)?.copy(id = doc.id)
                }?.filter { 
                    it.title.contains(query, ignoreCase = true) || 
                    it.content.contains(query, ignoreCase = true)
                }?.sortedByDescending { it.title } ?: emptyList()
                
                trySend(notes)
            }

        awaitClose { listener.remove() }
    }

    // Lấy một ghi chú cụ thể
    suspend fun getNoteById(noteId: String): Result<Note> = try {
        val document = firestore.collection("notes").document(noteId).get().await()
        val note = document.toObject(Note::class.java)?.copy(id = document.id)
            ?: throw Exception("Note not found")
        Result.success(note)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Tạo ghi chú mới
    suspend fun createNote(note: Note): Result<String> = try {
        val noteWithUserId = note.copy(userId = currentUserId)
        val response = firestore.collection("notes").add(noteWithUserId).await()
        Result.success(response.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Cập nhật ghi chú
    suspend fun updateNote(noteId: String, note: Note): Result<Unit> = try {
        val noteWithUserId = note.copy(userId = currentUserId)
        firestore.collection("notes").document(noteId).set(noteWithUserId).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Xóa ghi chú
    suspend fun deleteNote(noteId: String): Result<Unit> = try {
        firestore.collection("notes").document(noteId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
