package com.yaskapp.myapplication.ui.profile

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yaskapp.myapplication.ui.feed.PollItem
import com.yaskapp.myapplication.ui.viewmodel.ProfileViewModel

private val ScreenBg = Color(0xFFF7F7F7)
private val YaskRed = Color(0xFFE53935)

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = remember { ProfileViewModel() }

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    when {
        viewModel.isLoading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(ScreenBg),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = YaskRed)
            }
        }

        viewModel.error != null -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(ScreenBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ошибка: ${viewModel.error}",
                    color = Color.Red
                )
            }
        }

        viewModel.profile != null -> {
            val profile = viewModel.profile!!

            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(ScreenBg),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(82.dp)
                                    .background(Color(0xFFFFEBEE), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile avatar",
                                    tint = YaskRed,
                                    modifier = Modifier.size(42.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = profile.displayName,
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.Black
                            )

                            Text(
                                text = "@${profile.username}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )

                            if (profile.bio.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = profile.bio,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.DarkGray
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem("Polls", profile.pollsCount)
                                StatItem("Followers", profile.followersCount)
                                StatItem("Following", profile.followingCount)
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Мои опросы",
                            style = MaterialTheme.typography.titleLarge,
                            color = YaskRed
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFE7E7E7))
                }

                items(viewModel.polls) { poll ->
                    PollItem(
                        poll = poll,
                        onVoteClick = {},
                        onLikeClick = {},
                        onCommentClick = {},
                        onEditClick = { _, _ -> },
                        onDeleteClick = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = YaskRed
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

