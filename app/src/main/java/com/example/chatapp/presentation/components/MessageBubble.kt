package com.example.chatapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatapp.domain.model.Reaction
import com.example.chatapp.presentation.globalChatScreen.UiMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessageBubble(
    uiMessage: UiMessage,
    isFromCurrentUser: Boolean,
    onLongPress: (String) -> Unit, // Callback with messageId
    modifier: Modifier = Modifier
) {
    val alignment = if (isFromCurrentUser) Alignment.End else Alignment.Start
    val horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start

    val backgroundColor = if (isFromCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isFromCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val bubbleShape = if (isFromCurrentUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = horizontalArrangement
    ) {
        Column(horizontalAlignment = alignment) {
            if (!isFromCurrentUser) {
                Text(
                    text = uiMessage.senderDisplayName,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF696969),
                    modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                )
            }
            Box(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(backgroundColor)
                    .combinedClickable(
                        onClick = { /* Can add single click logic here later */ },
                        onLongClick = { onLongPress(uiMessage.message.messageId) }
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(text = uiMessage.message.text, color = textColor)
            }

            // Display Reactions if they exist
            if (uiMessage.message.reactions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                ReactionsDisplay(reactions = uiMessage.message.reactions)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatTimestamp(uiMessage.message.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ReactionPalette(
    onReactionSelected: (Reaction) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Reaction.entries.forEach { reaction ->
                Text(
                    text = reaction.emoji,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onReactionSelected(reaction) }
                        .padding(6.dp)
                )
            }
        }
    }
}

@Composable
private fun ReactionsDisplay(reactions: Map<String, String>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy((-8).dp), // Overlap emojis slightly
        verticalAlignment = Alignment.CenterVertically
    ) {
        val reactionCounts = reactions.values.groupingBy { it }.eachCount()
        items(items = reactionCounts.entries.toList()) { (reactionKey, count) ->
            val reaction = Reaction.fromKey(reactionKey)
            if (reaction != null) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.8f))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = reaction.emoji, fontSize = 14.sp)
                    if (count > 1) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = count.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}