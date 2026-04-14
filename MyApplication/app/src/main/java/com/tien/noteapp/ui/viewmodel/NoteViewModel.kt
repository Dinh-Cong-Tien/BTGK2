package com.tien.noteapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tien.noteapp.data.model.Note
import com.tien.noteapp.data.repository.AuthRepository
import com.tien.noteapp.data.repository.NoteRepository
import com.tien.noteapp.ui.state.NoteDetailState
import com.tien.noteapp.ui.state.NoteListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteViewModel(
    private val noteRepository: NoteRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _noteListState = MutableStateFlow<NoteListState>(NoteListState.Loading)
    val noteListState = _noteListState.asStateFlow()

    private val _noteDetailState = MutableStateFlow<NoteDetailState>(NoteDetailState.Loading)
    val noteDetailState = _noteDetailState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin = _isAdmin.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    init {
        loadUserRole()
        loadNotes()
    }

    fun loadUserRole() {
        viewModelScope.launch {
            try {
                val role = authRepository.getUserRole()
                _isAdmin.value = role == "admin"
            } catch (e: Exception) {
                _isAdmin.value = false
            }
        }
    }

    fun loadNotes() {
        viewModelScope.launch {
            try {
                noteRepository.getNotes().collect { notes ->
                    _noteListState.value = NoteListState.Success(notes)
                }
            } catch (e: Exception) {
                _noteListState.value = NoteListState.Error(e.message ?: "Error loading notes")
            }
        }
    }

    fun searchNotes(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            try {
                noteRepository.searchNotes(query).collect { notes ->
                    _noteListState.value = NoteListState.Success(notes)
                }
            } catch (e: Exception) {
                _noteListState.value = NoteListState.Error(e.message ?: "Error searching notes")
            }
        }
    }

    fun loadNoteDetail(noteId: String) {
        viewModelScope.launch {
            try {
                _noteDetailState.value = NoteDetailState.Loading
                val result = noteRepository.getNoteById(noteId)
                _noteDetailState.value = if (result.isSuccess) {
                    NoteDetailState.Success(result.getOrNull() ?: Note())
                } else {
                    NoteDetailState.Error(result.exceptionOrNull()?.message ?: "Error loading note")
                }
            } catch (e: Exception) {
                _noteDetailState.value = NoteDetailState.Error(e.message ?: "Error loading note")
            }
        }
    }

    fun createNote(note: Note) {
        viewModelScope.launch {
            try {
                noteRepository.createNote(note)
                loadNotes()
            } catch (e: Exception) {
                _noteDetailState.value = NoteDetailState.Error(e.message ?: "Error creating note")
            }
        }
    }

    fun updateNote(noteId: String, note: Note) {
        viewModelScope.launch {
            try {
                noteRepository.updateNote(noteId, note)
                loadNotes()
            } catch (e: Exception) {
                _noteDetailState.value = NoteDetailState.Error(e.message ?: "Error updating note")
            }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            try {
                noteRepository.deleteNote(noteId)
                loadNotes()
            } catch (e: Exception) {
                _noteDetailState.value = NoteDetailState.Error(e.message ?: "Error deleting note")
            }
        }
    }
}
