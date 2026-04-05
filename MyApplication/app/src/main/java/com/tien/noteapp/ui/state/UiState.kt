package com.tien.noteapp.ui.state

import com.tien.noteapp.data.model.Note

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}

sealed class NoteListState {
    object Loading : NoteListState()
    data class Success(val notes: List<Note>) : NoteListState()
    data class Error(val message: String) : NoteListState()
}

sealed class NoteDetailState {
    object Loading : NoteDetailState()
    data class Success(val note: Note) : NoteDetailState()
    data class Error(val message: String) : NoteDetailState()
}
