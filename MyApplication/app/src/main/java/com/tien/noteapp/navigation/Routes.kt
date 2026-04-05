package com.tien.noteapp.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

sealed class Route(val route: String) {
    object Login : Route("login")
    object Register : Route("register")
    object Home : Route("home")
    object NoteDetail : Route("note_detail/{noteId}") {
        fun createRoute(noteId: String) = "note_detail/$noteId"
    }
    object CreateNote : Route("create_note")
}

fun NavGraphBuilder.authGraph(
    onLoginSuccess: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    navController: NavController
) {
    // Auth screens will be added
}

fun NavGraphBuilder.appGraph(
    onLogoutClick: (NavController) -> Unit,
    navController: NavController
) {
    // App screens will be added
}
