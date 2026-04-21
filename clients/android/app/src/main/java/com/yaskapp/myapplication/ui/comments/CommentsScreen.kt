package com.yaskapp.myapplication.ui.comments

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = YaskRed
                )
            }

            Text(
                text = "Комментарии",
                style = MaterialTheme.typography.titleLarge,
                color = YaskRed
            )
        }

        HorizontalDivider(color = Color(0xFFE7E7E7))

        Crossfade(
            targetState = viewModel.isLoading,
            label = "commentsLoadingCrossfade"
        ) { isLoading ->
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = YaskRed)
                }
            } else {
                if (viewModel.comments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Пока нет комментариев",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = viewModel.comments,
                            key = { it.id }
                        ) { comment ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + expandVertically() + slideInVertically(initialOffsetY = { it / 3 }),
                                exit = fadeOut() + shrinkVertically() + slideOutVertically(targetOffsetY = { it / 3 })
                            ) {
                                CommentItem(comment)
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp)
                .animateContentSize()
        ) {
            AnimatedVisibility(
                visible = viewModel.error != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = viewModel.error ?: "",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (viewModel.error != null) {
                Spacer(modifier = Modifier.padding(top = 6.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewModel.input,
                    onValueChange = viewModel::onInputChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Написать комментарий...") },
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Button(
                    onClick = { viewModel.addComment(pollId) },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = YaskRed),
                    modifier = Modifier.scale(sendButtonScale)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Отправить"
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentItem(comment: CommentDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFFFEBEE), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = comment.author.displayName
                            .ifBlank { comment.author.username }
                            .firstOrNull()
                            ?.uppercase() ?: "?",
                        color = YaskRed,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = comment.author.displayName.ifBlank { comment.author.username },
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Black
                    )

                    Text(
                        text = "@${comment.author.username}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.padding(top = 10.dp))

            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
}