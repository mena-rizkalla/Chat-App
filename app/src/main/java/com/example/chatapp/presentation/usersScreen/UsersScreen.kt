package com.example.chatapp.presentation.usersScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chatapp.R
import com.example.chatapp.presentation.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun UsersScreen(navController: NavController, viewModel: UsersViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    UsersScreenContent(
        state = uiState,
        onGoToGlobalChatClick = {
            navController.navigate(Screen.GlobalChatScreen.route)
        },
        onGoToAiChatClick = {
            navController.navigate(Screen.AiChatScreen.route)
        },
        onChatClick = { receiverName, receiverId ->
            navController.navigate(
                Screen.ChatScreen.createRoute(
                    receiverName = receiverName,
                    receiverId = receiverId
                )
            )
        }
    )
}

@Composable
private fun UsersScreenContent(
    state: UsersState,
    onChatClick: (receiverName: String, receiverId: String) -> Unit,
    onGoToGlobalChatClick: () -> Unit,
    onGoToAiChatClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 8.dp, start = 20.dp),
                    text = "Hey There ${if (state.currentUser.displayName != "") state.currentUser.displayName else ""} :)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 32.sp,
                    color = Color(0xFF1D1D1D)
                )

                Icon(
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .clickable {
                            onGoToAiChatClick()
                        },
                    painter = painterResource(R.drawable.baseline_adb_24),
                    contentDescription = "Text with Gemini",
                    tint = Color.Unspecified
                )

                Icon(
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .clickable {
                        onGoToGlobalChatClick()
                    },
                    painter = painterResource(R.drawable.messages_2),
                    contentDescription = "Global Chat",
                    tint = Color.Unspecified
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(bottom = 43.dp)
            )
        }
        items(state.users) { user ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, start = 24.dp)
                    .clickable {
                        onChatClick(
                            user.displayName,
                            user.uid
                        )
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(70.dp)
                        .paint(
                            painter = painterResource(R.drawable.img_empty_user_pic),
                            contentScale = ContentScale.Crop
                        )
                )
                Column {
                    Text(
                        modifier = Modifier.padding(start = 23.dp),
                        text = user.email,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color(0xFF696969)
                    )
                    Text(
                        modifier = Modifier.padding(start = 23.dp),
                        text = user.displayName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        color = Color(0xFF1D1D1D)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun UsersScreenPreview() {
    UsersScreenContent(
        state = UsersState(),
        onChatClick = { _, _ -> },
        onGoToAiChatClick = {},
        onGoToGlobalChatClick = {}
    )
}

