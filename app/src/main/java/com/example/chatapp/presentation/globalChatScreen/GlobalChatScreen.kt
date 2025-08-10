package com.example.chatapp.presentation.globalChatScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatapp.domain.model.Message
import com.example.chatapp.presentation.components.ChatInput
import com.example.chatapp.presentation.components.MessageBubble
import com.example.chatapp.presentation.components.ReactionPalette
import com.example.chatapp.presentation.components.SuggestionButton
import com.example.chatapp.presentation.components.SuggestionChips
import com.example.chatapp.ui.theme.ChatAppTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalChatScreen(
    navController: NavController,
    viewModel: GlobalChatViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var selectedMessageId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Global Chat", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                    onSuggestionClick = viewModel::useSuggestion
                )
                ChatInput(
                    message = uiState.currentMessage,
                    onMessageChange = viewModel::onMessageChange,
                    onSendClick = viewModel::sendMessage
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
                            modifier = Modifier.animateItem(),
                            uiMessage = uiMessage.copy(shouldShowSenderName = true),
                            isFromCurrentUser = uiMessage.message.senderId == uiState.currentUserId,
                            onLongPress = { msgId -> selectedMessageId = msgId }
                        )
                    }

                    item {
                        val lastMessage = uiState.messages.lastOrNull()
                        if (lastMessage != null && lastMessage.message.senderId != uiState.currentUserId && uiState.suggestedReplies.isEmpty()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                                SuggestionButton(
                                    onClick = viewModel::generateReplySuggestions,
                                    isLoading = uiState.isGeneratingSuggestions,
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            }
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
                        selectedMessageId?.let { viewModel.toggleReaction(it, reaction) }
                        selectedMessageId = null
                    }
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun GlobalChatScreenPreview() {
    val previewUiMessages = listOf(
        UiMessage(Message(senderId = "1", text = "Great! Should we add an emoji feature to the conversation screen?", timestamp = System.currentTimeMillis() - 200000), "Alice"),
        UiMessage(Message(senderId = "2", text = "Good idea, but let's keep it simple at first. Focus on text messages initially.", timestamp = System.currentTimeMillis() - 100000), "You"),
        UiMessage(Message(senderId = "3", text = "Okay, and if we want to test the screen, send me a wireframe.", timestamp = System.currentTimeMillis()), "Bob")
    )

    ChatAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Global Chat") },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            bottomBar = {
                ChatInput(message = "Hello there!", onMessageChange = {}, onSendClick = {})
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(previewUiMessages) { uiMessage ->
                    MessageBubble(
                        uiMessage = uiMessage,
                        isFromCurrentUser = uiMessage.message.senderId == "2",
                        onLongPress = {},
                        modifier = Modifier.navigationBarsPadding() ,
                    )
                }
            }
        }
    }
}