package com.yaskapp.myapplication.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.yaskapp.myapplication.data.repository.PollsRepository
import com.yaskapp.myapplication.ui.comments.CommentsScreen
import com.yaskapp.myapplication.ui.create.CreatePollScreen
import com.yaskapp.myapplication.ui.feed.FeedScreen
import com.yaskapp.myapplication.ui.profile.ProfileScreen
import com.yaskapp.myapplication.ui.viewmodel.FeedViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScaffold() {
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var previousScreen by remember { mutableStateOf(AppScreen.HOME) }
    var selectedPollId by remember { mutableStateOf<String?>(null) }

    val feedViewModel = remember { FeedViewModel() }
    val repository = remember { PollsRepository() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        feedViewModel.loadFeed()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentScreen != AppScreen.COMMENTS) {
                BottomNavigationBar(
                    currentScreen = currentScreen,
                    onScreenSelected = { screen ->
                        previousScreen = currentScreen
                        currentScreen = screen

                        if (screen == AppScreen.HOME) {
                            scope.launch {
                                feedViewModel.loadFeed()
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->

        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                val goingToComments =
                    targetState == AppScreen.COMMENTS && initialState != AppScreen.COMMENTS

                val goingBackFromComments =
                    initialState == AppScreen.COMMENTS && targetState != AppScreen.COMMENTS

                when {
                    goingToComments -> {
                        slideInHorizontally(initialOffsetX = { it }) + fadeIn() togetherWith
                                slideOutHorizontally(targetOffsetX = { -it / 4 }) + fadeOut()
                    }

                    goingBackFromComments -> {
                        slideInHorizontally(initialOffsetX = { -it / 4 }) + fadeIn() togetherWith
                                slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                    }

                    else -> {
                        fadeIn() togetherWith fadeOut()
                    }
                }
            },
            label = "main_navigation_animation"
        ) { screen ->

            when (screen) {
                AppScreen.HOME -> {
                    FeedScreen(
                        viewModel = feedViewModel,
                        modifier = Modifier.padding(innerPadding),
                        onCommentClick = { pollId ->
                            selectedPollId = pollId
                            previousScreen = currentScreen
                            currentScreen = AppScreen.COMMENTS
                        },
                        onCreateClick = {
                            previousScreen = currentScreen
                            currentScreen = AppScreen.CREATE
                        }
                    )
                }

                AppScreen.TRENDING -> {
                    PlaceholderScreen(
                        text = "Trending screen",
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                AppScreen.CREATE -> {
                    CreatePollScreen(
                        modifier = Modifier.padding(innerPadding),
                        onCreateClick = { question, options ->
                            scope.launch {
                                try {
                                    repository.createPoll(question, options)
                                    feedViewModel.loadFeed()
                                    previousScreen = currentScreen
                                    currentScreen = AppScreen.HOME
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    )
                }

                AppScreen.NOTIFICATIONS -> {
                    PlaceholderScreen(
                        text = "Notifications screen",
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                AppScreen.PROFILE -> {
                    ProfileScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                AppScreen.COMMENTS -> {
                    selectedPollId?.let { pollId ->
                        CommentsScreen(
                            pollId = pollId,
                            onBackClick = {
                                previousScreen = currentScreen
                                currentScreen = AppScreen.HOME
                                scope.launch {
                                    feedViewModel.loadFeed()
                                }
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.Gray
        )
    }
}