package com.tien.noteapp.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import coil.compose.AsyncImage
import com.tien.noteapp.data.model.Note
import com.tien.noteapp.ui.state.NoteDetailState
import android.net.Uri
import androidx.compose.ui.res.painterResource
import com.tien.noteapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteDetailState: NoteDetailState,
    isNewNote: Boolean,
    onSaveNote: (Note) -> Unit,
    onBackClick: () -> Unit,
    isAdmin: Boolean = false
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showImagePickerDialog by remember { mutableStateOf(false) }

    // List drawable images
    val drawableImages = listOf(
        "canhan" to R.drawable.canhan,
        "congviec" to R.drawable.congviec
    )

    // Image picker launcher for real gallery
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            imageUrl = uri.toString()
        }
    }

    LaunchedEffect(noteDetailState) {
        if (noteDetailState is NoteDetailState.Success && !isNewNote) {
            val note = noteDetailState.note
            title = note.title
            content = note.content
            imageUrl = note.imageUrl
            selectedImageUri = if (note.imageUrl.isNotEmpty()) Uri.parse(note.imageUrl) else null
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isNewNote) "New Note" else "Edit Note",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF6200EE),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFFAFAFA))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Title Field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedBorderColor = Color(0xFF6200EE)
                    )
                )

                // Content Field
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Note Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 200.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedBorderColor = Color(0xFF6200EE)
                    )
                )

                // Image Picker
                Button(
                    onClick = {
                        showImagePickerDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF6200EE)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        if (selectedImageUri != null) "Change Image" else "Pick Image from Gallery",
                        fontSize = 14.sp
                    )
                }

                // Clear Image Button (if image is selected)
                if (selectedImageUri != null) {
                    Button(
                        onClick = {
                            selectedImageUri = null
                            imageUrl = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(end = 8.dp)
                        )
                        Text("Remove Image", fontSize = 12.sp)
                    }
                }

                // Image Preview
                if (imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                            .padding(bottom = 16.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }

                // Save Button - Only for admin
                if (isAdmin) {
                    Button(
                        onClick = {
                            val note = Note(
                                title = title,
                                content = content,
                                imageUrl = imageUrl
                            )
                            onSaveNote(note)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                        enabled = title.isNotEmpty() && content.isNotEmpty()
                    ) {
                        Text("Save Note", fontSize = 16.sp, color = Color.White)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Read-only mode", fontSize = 14.sp, color = Color(0xFF999999))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Image Picker Dialog
    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = { Text("Choose Image") },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(drawableImages.size) { index ->
                        val (imageName, drawableId) = drawableImages[index]
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Convert drawable resource to Uri
                                    val packageName = "com.tien.noteapp"
                                    val resourceUri = Uri.parse("android.resource://$packageName/$drawableId")
                                    selectedImageUri = resourceUri
                                    imageUrl = resourceUri.toString()
                                    showImagePickerDialog = false
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = drawableId),
                                contentDescription = imageName,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                imageName.uppercase(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF6200EE)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showImagePickerDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                ) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}
