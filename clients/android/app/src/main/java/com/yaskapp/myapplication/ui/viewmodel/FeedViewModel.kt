package com.yaskapp.myapplication.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaskapp.myapplication.data.model.PollDto
import com.yaskapp.myapplication.data.model.PollOptionDto
import com.yaskapp.myapplication.data.repository.PollsRepository
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {

    private val repository = PollsRepository()

    var polls by mutableStateOf<List<PollDto>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun loadFeed() {
        viewModelScope.launch {
            try {
                isLoading = true
                error = null
                polls = repository.getFeed()
            } catch (e: Exception) {
                error = e.message ?: "Unknown error"
            } finally {
                isLoading = false
            }
        }
    }

    fun vote(optionId: String) {
        val oldPolls = polls

        polls = polls.map { poll ->
            val clickedOption = poll.options.find { it.id == optionId } ?: return@map poll
            val currentVote = poll.userVote
            val currentTotalVotes = poll.totalVotes

            val newUserVote =
                if (currentVote == optionId) null else optionId

            val newTotalVotes = when {
                currentVote == null -> currentTotalVotes + 1
                currentVote == optionId -> (currentTotalVotes - 1).coerceAtLeast(0)
                else -> currentTotalVotes
            }

            val updatedOptions = poll.options.map { option ->
                val newVotesCount = when {
                    currentVote == null && option.id == optionId ->
                        option.votesCount + 1

                    currentVote == optionId && option.id == optionId ->
                        (option.votesCount - 1).coerceAtLeast(0)

                    currentVote != null && currentVote != optionId && option.id == currentVote ->
                        (option.votesCount - 1).coerceAtLeast(0)

                    currentVote != null && currentVote != optionId && option.id == optionId ->
                        option.votesCount + 1

                    else -> option.votesCount
                }

                val newPercentage =
                    if (newTotalVotes == 0) 0.0
                    else (newVotesCount.toDouble() * 100.0) / newTotalVotes.toDouble()

                option.copy(
                    votesCount = newVotesCount,
                    percentage = newPercentage
                )
            }

            poll.copy(
                userVote = newUserVote,
                totalVotes = newTotalVotes,
                options = updatedOptions
            )
        }

        viewModelScope.launch {
            try {
                repository.vote(optionId)
            } catch (e: Exception) {
                polls = oldPolls
                error = e.message ?: "Vote failed"
            }
        }
    }

    fun toggleLike(pollId: String) {
        val oldPolls = polls

        polls = polls.map { poll ->
            if (poll.id != pollId) return@map poll

            val liked = !poll.likedByMe
            val likesCount = if (liked) poll.likesCount + 1 else (poll.likesCount - 1).coerceAtLeast(0)

            poll.copy(
                likedByMe = liked,
                likesCount = likesCount
            )
        }

        viewModelScope.launch {
            try {
                repository.toggleLike(pollId)
            } catch (e: Exception) {
                polls = oldPolls
                error = e.message ?: "Like failed"
            }
        }
    }
}
