package com.yaskapp.myapplication.ui.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yaskapp.myapplication.R
import com.yaskapp.myapplication.ui.viewmodel.FeedViewModel

private val FeedBackground = Color(0xFFF7F7F7)
private val YaskRed = Color(0xFFE53935)
private val LightRed = Color(0xFFFFEBEE)
private val BorderGray = Color(0xFFE7E7E7)

@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    modifier: Modifier = Modifier,
    onCommentClick: (String) -> Unit,
    onCreateClick: ()-> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FeedBackground)
    ) {
        FeedHeader(onCreateClick = onCreateClick)

        when {
            viewModel.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = YaskRed)
                }
            }

            viewModel.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ошибка: ${viewModel.error}",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            viewModel.polls.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Пока нет опросов",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 14.dp,
                        bottom = 20.dp
                    )
                ) {
                    items(viewModel.polls) { poll ->
                        PollItem(
                            poll = poll,
                            onVoteClick = { optionId ->
                                viewModel.vote(optionId)
                            },
                            onLikeClick = { pollId ->
                                viewModel.toggleLike(pollId)
                            },
                            onCommentClick = { pollId ->
                                onCommentClick(pollId)
                            },
                            onEditClick = { pollId, newQuestion ->
                                viewModel.updatePoll(pollId, newQuestion)
                            },
                            onDeleteClick = { pollId ->
                                viewModel.deletePoll(pollId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedHeader(onCreateClick: () -> Unit) {
    val searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsNone,
                contentDescription = "Notifications",
                tint = YaskRed,
                modifier = Modifier.size(26.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Yask logo",
                modifier = Modifier.size(46.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Yask",
                style = MaterialTheme.typography.headlineSmall,
                color = YaskRed
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = {},
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("Search polls...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray
                    )
                },
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YaskRed,
                    unfocusedBorderColor = BorderGray
                )
            )

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = onCreateClick,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = YaskRed),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Create")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FeedChip("Trending", selected = true)
            FeedChip("New", selected = false)
            FeedChip("Hot", selected = false)
            FeedChip("Following", selected = false)
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 12.dp),
            color = BorderGray
        )
    }
}

@Composable
private fun FeedChip(
    text: String,
    selected: Boolean
) {
    AssistChip(
        onClick = { },
        label = { Text(text) },
        shape = RoundedCornerShape(18.dp),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) LightRed else Color.White,
            labelColor = if (selected) YaskRed else Color.DarkGray
        ),
        border = AssistChipDefaults.assistChipBorder(
            enabled = true,
            borderColor = if (selected) YaskRed else BorderGray
        )
    )
}
