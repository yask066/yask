package com.yaskapp.myapplication.ui.comments

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yaskapp.myapplication.data.model.CommentDto
import com.yaskapp.myapplication.ui.viewmodel.CommentsViewModel

private val YaskRed = Color(0xFFE53935)
private val ScreenBg = Color(0xFFF7F7F7)

@Composable
fun CommentsScreen(
    pollId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = remember { CommentsViewModel() }

    LaunchedEffect(pollId) {
        viewModel.loadComments(pollId)
    }

    val sendButtonScale by animateFloatAsState(
        targetValue = if (viewModel.input.isNotBlank()) 1f else 0.92f,
        label = "sendButtonScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBg)
            .imePadding()
    ) {

        // 🔝 HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = YaskRed)
            }

            Text(
                text = "Комментарии",
                style = MaterialTheme.typography.titleLarge,
                color = YaskRed
            )
        }

        HorizontalDivider()

        // 📄 LIST
        Crossfade(targetState = viewModel.isLoading, label = "") { loading ->
            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = YaskRed)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(viewModel.comments, key = { it.id }) { comment ->
                        CommentItem(
                            comment = comment,
                            onEditClick = { id, newText ->
                                viewModel.updateComment(id, newText, pollId)
                            },
                            onDeleteClick = { id ->
                                viewModel.deleteComment(id, pollId)
                            }
                        )
                    }
                }
            }
        }

        // ✏️ INPUT
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = viewModel.input,
                onValueChange = viewModel::onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Написать комментарий...") }
            )

            Spacer(modifier = Modifier.width(6.dp))

            Button(
                onClick = { viewModel.addComment(pollId) },
                modifier = Modifier.scale(sendButtonScale),
                colors = ButtonDefaults.buttonColors(containerColor = YaskRed)
            ) {
                Icon(Icons.Default.Send, contentDescription = null)
            }
        }
    }
}

@Composable
private fun CommentItem(
    comment: CommentDto,
    onEditClick: (String, String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var editedText by remember { mutableStateOf(comment.content) }

    // ✏️ EDIT DIALOG
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Редактировать") },
            text = {
                OutlinedTextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editedText.isNotBlank()) {
                            onEditClick(comment.id, editedText)
                            showEditDialog = false
                        }
                    }
                ) {
                    Text("Сохранить", color = YaskRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // 👤 AVATAR
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFFFEBEE), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = comment.author.displayName.first().uppercase(),
                        color = YaskRed
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(comment.author.displayName)
                    Text(
                        "@${comment.author.username}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // 🔥 ACTIONS
                if (comment.isMine) {
                    IconButton(onClick = {
                        editedText = comment.content
                        showEditDialog = true
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                    }
                }

                if (comment.canDelete) {
                    IconButton(onClick = {
                        onDeleteClick(comment.id)
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(comment.content)
        }
    }
}