package com.example.chatapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.chatapp.domain.model.Reaction
import com.example.chatapp.presentation.globalChatScreen.UiMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageActionsBottomSheet(
    uiMessage: UiMessage,
    currentUserId: String,
    onDismiss: () -> Unit,
    onReply: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onReact: (Reaction) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(bottom = 32.dp)) {
            // Reaction Palette is now part of the bottom sheet
            ReactionPalette(
                onReactionSelected = { reaction ->
                    onReact(reaction)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            ActionItem(icon = Icons.AutoMirrored.Filled.Reply, text = "Reply", onClick = onReply)

            // Only show Edit and Delete for the user's own messages
            if (uiMessage.message.senderId == currentUserId) {
                ActionItem(icon = Icons.Default.Edit, text = "Edit", onClick = onEdit)
                ActionItem(icon = Icons.Default.Delete, text = "Delete", onClick = onDelete)
            }
        }
    }
}

@Composable
private fun ActionItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = text)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text)
    }
}