package com.yaskapp.myapplication.ui.feed

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.yaskapp.myapplication.data.model.PollDto
import com.yaskapp.myapplication.data.model.PollOptionDto

private val CardBackground = Color.White
private val YaskRed = Color(0xFFE53935)
private val LightRed = Color(0xFFFFEBEE)
private val LightGray = Color(0xFFF3F3F3)
private val BorderGray = Color(0xFFE7E7E7)

@Composable
fun PollItem(
    poll: PollDto,
    onVoteClick: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onEditClick: (String, String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var editedQuestion by remember { mutableStateOf(poll.question) }

    val likeTint by animateColorAsState(
        targetValue = if (poll.likedByMe) YaskRed else Color.Gray,
        animationSpec = tween(durationMillis = 250),
        label = "likeTint"
    )

    val likeTextColor by animateColorAsState(
        targetValue = if (poll.likedByMe) YaskRed else Color.Black,
        animationSpec = tween(durationMillis = 250),
        label = "likeTextColor"
    )

    val likeScale by animateFloatAsState(
        targetValue = if (poll.likedByMe) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = 0.45f,
            stiffness = 500f
        ),
        label = "likeScale"
    )

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text("Редактировать опрос")
            },
            text = {
                OutlinedTextField(
                    value = editedQuestion,
                    onValueChange = { editedQuestion = it },
                    label = { Text("Вопрос") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newQuestion = editedQuestion.trim()
                        if (newQuestion.isNotBlank()) {
                            onEditClick(poll.id, newQuestion)
                            showEditDialog = false
                        }
                    }
                ) {
                    Text("Сохранить", color = YaskRed)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditDialog = false }
                ) {
                    Text("Отмена")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = poll.author.displayName.ifBlank { poll.author.username },
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )

                    Text(
                        text = "@${poll.author.username}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                if (poll.isMine) {
                    Row {
                        IconButton(
                            onClick = {
                                editedQuestion = poll.question
                                showEditDialog = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Редактировать",
                                tint = YaskRed
                            )
                        }

                        IconButton(
                            onClick = { onDeleteClick(poll.id) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Удалить",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = poll.question,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(14.dp))

            poll.options.forEach { option ->
                PollOptionItem(
                    option = option,
                    isSelected = poll.userVote == option.id,
                    onClick = { onVoteClick(option.id) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${poll.totalVotes} votes",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onLikeClick(poll.id) }
                ) {
                    Icon(
                        imageVector = if (poll.likedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Likes",
                        tint = likeTint,
                        modifier = Modifier.graphicsLayer {
                            scaleX = likeScale
                            scaleY = likeScale
                        }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = poll.likesCount.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = likeTextColor
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onCommentClick(poll.id) }
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = poll.commentsCount.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun PollOptionItem(
    option: PollOptionDto,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) LightRed else LightGray,
        animationSpec = tween(durationMillis = 250),
        label = "optionBackground"
    )

    val progressColor by animateColorAsState(
        targetValue = if (isSelected) YaskRed else Color(0xFFB8B8B8),
        animationSpec = tween(durationMillis = 250),
        label = "optionProgress"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) YaskRed else Color.Black,
        animationSpec = tween(durationMillis = 250),
        label = "optionText"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = (option.percentage.toFloat() / 100f).coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "optionProgressValue"
    )

    val animatedPadding by animateDpAsState(
        targetValue = if (isSelected) 16.dp else 14.dp,
        animationSpec = tween(durationMillis = 220),
        label = "optionPadding"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(animatedPadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = option.text,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${option.percentage.toInt()}%",
                style = MaterialTheme.typography.labelLarge,
                color = textColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = progressColor,
            trackColor = BorderGray
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Votes: ${option.votesCount}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Нажмите еще раз, чтобы отменить голос",
                style = MaterialTheme.typography.bodySmall,
                color = YaskRed
            )
        }
    }
}



