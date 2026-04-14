package com.tien.noteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tien.noteapp.data.repository.AuthRepository
import com.tien.noteapp.data.repository.NoteRepository
import com.tien.noteapp.navigation.Route
import com.tien.noteapp.ui.screens.*
import com.tien.noteapp.ui.state.NoteDetailState
import com.tien.noteapp.ui.viewmodel.AuthViewModel
import com.tien.noteapp.ui.viewmodel.NoteViewModel
import com.tien.noteapp.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                NoteAppNavigation()
            }
        }
    }
}

@Composable
fun NoteAppNavigation() {
    val navController = rememberNavController()
    val firebaseAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    val authRepository = remember { AuthRepository(firebaseAuth, firestore) }
    val authViewModel = remember { AuthViewModel(authRepository) }

    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    
    // Force recompose when isAuthenticated changes
    val currentUserId = if (isAuthenticated) {
        firebaseAuth.currentUser?.uid ?: ""
    } else {
        ""
    }

    val noteRepository = remember(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            NoteRepository(firestore, currentUserId)
        } else {
            null
        }
    }

    val noteViewModel = remember(noteRepository) {
        if (noteRepository != null) {
            NoteViewModel(noteRepository, authRepository)
        } else {
            null
        }
    }

    // Reset HomeScreen state when userId changes
    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated) {
            // Clear local state when logging out
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated && currentUserId.isNotEmpty()) Route.Home.route else Route.Login.route
    ) {
        // Auth Screens
        composable(Route.Login.route) {
            val loginState by authViewModel.loginState.collectAsState()
            LoginScreen(
                onLoginClick = { email, password ->
                    authViewModel.login(email, password)
                },
                onRegisterClick = {
                    authViewModel.clearLoginState()
                    navController.navigate(Route.Register.route)
                },
                state = loginState,
                onStateChange = { authViewModel.clearLoginState() }
            )

            LaunchedEffect(loginState) {
                val stateString = loginState.toString()
                if (stateString.contains("Success")) {
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                }
            }
        }

        composable(Route.Register.route) {
            val registerState by authViewModel.registerState.collectAsState()
            RegisterScreen(
                onRegisterClick = { email, password, displayName ->
                    authViewModel.register(email, password, displayName)
                },
                onLoginClick = {
                    authViewModel.clearRegisterState()
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Register.route) { inclusive = true }
                    }
                },
                state = registerState,
                onStateChange = { authViewModel.clearRegisterState() }
            )

            LaunchedEffect(registerState) {
                val stateString = registerState.toString()
                if (stateString.contains("Success")) {
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Register.route) { inclusive = true }
                    }
                }
            }
        }

        // App Screens
        composable(Route.Home.route) {
            if (noteViewModel != null) {
                val noteListState by noteViewModel.noteListState.collectAsState()
                val isAdmin by noteViewModel.isAdmin.collectAsState()

                HomeScreen(
                    noteListState = noteListState,
                    onNoteClick = { noteId ->
                        navController.navigate(Route.NoteDetail.createRoute(noteId))
                    },
                    onCreateNoteClick = {
                        navController.navigate(Route.CreateNote.route)
                    },
                    onLogoutClick = {
                        authViewModel.logout()
                        navController.navigate(Route.Login.route) {
                            popUpTo(Route.Home.route) { inclusive = true }
                        }
                    },
                    onSearchChange = { query ->
                        noteViewModel?.searchNotes(query)
                    },
                    onDeleteNote = { noteId ->
                        noteViewModel?.deleteNote(noteId)
                    },
                    onRefreshNotes = {
                        noteViewModel.loadNotes()
                    },
                    isAdmin = isAdmin
                )
            }
        }

        composable(Route.CreateNote.route) {
            if (noteViewModel != null) {
                val isAdmin by noteViewModel.isAdmin.collectAsState()
                
                NoteDetailScreen(
                    noteDetailState = NoteDetailState.Loading,
                    isNewNote = true,
                    onSaveNote = { note ->
                        val newNote = note.copy(userId = currentUserId)
                        noteViewModel.createNote(newNote)
                        navController.popBackStack()
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    isAdmin = isAdmin
                )
            }
        }

        composable(Route.NoteDetail.route) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            if (noteViewModel != null) {
                val noteDetailState by noteViewModel.noteDetailState.collectAsState()
                val isAdmin by noteViewModel.isAdmin.collectAsState()

                LaunchedEffect(Unit) {
                    noteViewModel.loadNoteDetail(noteId)
                }

                NoteDetailScreen(
                    noteDetailState = noteDetailState,
                    isNewNote = false,
                    onSaveNote = { note ->
                        val updatedNote = note.copy(userId = currentUserId, id = noteId)
                        noteViewModel.updateNote(noteId, updatedNote)
                        navController.popBackStack()
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    isAdmin = isAdmin
                )
            }
        }
    }
}