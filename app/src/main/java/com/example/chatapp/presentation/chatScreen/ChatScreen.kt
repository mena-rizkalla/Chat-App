package com.example.chatapp.presentation.chatScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatapp.presentation.components.ChatInput
import com.example.chatapp.presentation.components.MessageActionsBottomSheet
import com.example.chatapp.presentation.components.MessageBubble
import com.example.chatapp.presentation.components.ReactionPalette
import com.example.chatapp.presentation.components.ReplyPreview
import com.example.chatapp.presentation.components.SuggestionButton
import com.example.chatapp.presentation.components.SuggestionChips
import com.example.chatapp.presentation.components.TypingIndicator
import com.example.chatapp.presentation.globalChatScreen.UiMessage
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    receiverId: String,
    receiverName: String,
    viewModel: ChatViewModel = koinViewModel(parameters = { parametersOf(receiverId,receiverName) })
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var selectedMessageId by remember { mutableStateOf<String?>(null) }
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(15000L) // Update every 15 seconds
            currentTime = System.currentTimeMillis()
        }
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is ChatEvent.NavigateBack -> navController.popBackStack()
            }
        }
    }

    uiState.messageForAction?.let {
        MessageActionsBottomSheet(
            uiMessage = it,
            currentUserId = uiState.currentUserId,
            onDismiss = { viewModel.onAction(ChatAction.DismissMessageActions) },
            onReply = { viewModel.onAction(ChatAction.StartReply(it)) },
            onEdit = { viewModel.onAction(ChatAction.StartEdit) },
            onDelete = { viewModel.onAction(ChatAction.StartDelete) },
            onReact = { reaction ->
                viewModel.onAction(ChatAction.ToggleReaction(it.message.messageId, reaction))
                viewModel.onAction(ChatAction.DismissMessageActions)
            }
        )
    }

    if (uiState.showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ChatAction.CancelDelete) },
            title = { Text("Delete Message") },
            text = { Text("Are you sure you want to permanently delete this message?") },
            confirmButton = {
                Button(
                    onClick = { viewModel.onAction(ChatAction.ConfirmDelete) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(ChatAction.CancelDelete) }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = receiverName.firstOrNull()?.toString() ?: "U", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(receiverName, style = MaterialTheme.typography.titleMedium)
                            // --- Active Status Logic ---
                            val lastSeen = uiState.receiverLastSeenTimestamp
                            val statusText = formatLastSeen(lastSeen, currentTime)
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (statusText == "Active now") Color(0xFF00C853) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onAction(ChatAction.NavigateBack)  }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Column {
                SuggestionChips(
                    suggestions = uiState.suggestedReplies,
                    onSuggestionClick = { viewModel.onAction(ChatAction.UseSuggestion(it)) }
                )
                ReplyPreview(
                    uiMessage = uiState.replyingToMessage,
                    onCancelReply = { viewModel.onAction(ChatAction.CancelReply) }
                )
                ChatInput(
                    message = uiState.currentMessage,
                    onMessageChange = { viewModel.onAction(ChatAction.OnMessageChange(it)) },
                    onSendClick = {
                        if (uiState.editingMessage != null) {
                            viewModel.onAction(ChatAction.ConfirmEdit)
                        } else {
                            viewModel.onAction(ChatAction.SendMessage)
                        }
                    },
                    isEditing = uiState.editingMessage != null,
                    onCancelEdit = { viewModel.onAction(ChatAction.CancelEdit) }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(uiState.messages, key = { it.message.messageId }) { uiMessage ->
                        MessageBubble(
                            uiMessage =uiMessage,
                            isFromCurrentUser = uiMessage.message.senderId == uiState.currentUserId,
                            receiverLastSeenTimestamp = uiState.receiverLastSeenTimestamp,
                            onLongPress = { viewModel.onAction(ChatAction.OnMessageLongPress(uiMessage)) },
                            onStartReply = { viewModel.onAction(ChatAction.StartReply(uiMessage)) }
                        )
                    }

                    item {
                        // Show Suggest Reply button under the last received message
                        val lastMessage = uiState.messages.lastOrNull()
                        if (lastMessage != null && lastMessage.message.senderId != uiState.currentUserId && uiState.suggestedReplies.isEmpty()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                                SuggestionButton(
                                    onClick = { viewModel.onAction(ChatAction.GenerateReplySuggestions) },
                                    isLoading = uiState.isGeneratingSuggestions,
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            }
                        }
                    }

                    item {
                        AnimatedVisibility(visible = uiState.isOtherUserTyping) {
                            TypingIndicator()
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = selectedMessageId != null,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = slideOutVertically { it / 2 } + fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                ReactionPalette(
                    onReactionSelected = { reaction ->
                        selectedMessageId?.let { viewModel.onAction(ChatAction.ToggleReaction(it, reaction))}
                        selectedMessageId = null
                    }
                )
            }
        }
    }
}

private fun formatLastSeen(timestamp: Long, currentTime: Long): String {
    if (timestamp == 0L) return ""
    val diff = currentTime - timestamp

    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        minutes < 1 -> "Active now"
        minutes < 60 -> "Active $minutes minutes ago"
        hours < 24 -> "Active $hours hours ago"
        else -> "Active $days days ago"
    }
}