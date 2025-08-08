package com.example.chatapp.presentation.chatScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatapp.presentation.components.ChatInput
import com.example.chatapp.presentation.components.MessageBubble
import com.example.chatapp.presentation.components.ReactionPalette
import com.example.chatapp.presentation.globalChatScreen.UiMessage
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    receiverId: String,
    receiverName: String,
    viewModel: ChatViewModel = koinViewModel(parameters = { parametersOf(receiverId) })
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
                title = { Text("Chat with $receiverName") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black.copy(alpha = 0.8f)
                )
            )
        },
        bottomBar = {
            ChatInput(
                message = uiState.currentMessage,
                onMessageChange = viewModel::onMessageChange,
                onSendClick = viewModel::sendMessage
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(uiState.messages) { message ->
                        MessageBubble(
                            uiMessage = UiMessage(message, receiverName),
                            isFromCurrentUser = message.senderId == uiState.currentUserId,
                            onLongPress = { msgId -> selectedMessageId = msgId }
                        )
                    }
                }
            }

            // Show Reaction Palette when a message is selected
            if (selectedMessageId != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp), // Adjust padding to avoid overlap with chat input
                    contentAlignment = Alignment.BottomCenter
                ) {
                    ReactionPalette(
                        onReactionSelected = { reaction ->
                            viewModel.toggleReaction(selectedMessageId!!, reaction)
                            selectedMessageId = null // Hide palette after selection
                        }
                    )
                }
            }
        }
    }
}