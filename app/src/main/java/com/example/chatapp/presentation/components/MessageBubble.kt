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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatapp.domain.model.Reaction
import com.example.chatapp.presentation.globalChatScreen.UiMessage
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessageBubble(
    uiMessage: UiMessage,
    isFromCurrentUser: Boolean,
    receiverLastSeenTimestamp: Long,
    onLongPress: (String) -> Unit,
    onStartReply: ((UiMessage) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // This is the single source of truth for the read status
    val isRead = isFromCurrentUser && uiMessage.message.timestamp <= receiverLastSeenTimestamp

    val replyAction = if (onStartReply != null) {
        SwipeAction(
            onSwipe = { onStartReply(uiMessage) },
            icon = {
                Icon(
                    Icons.Default.Reply,
                    contentDescription = "Reply",
                    modifier = Modifier.padding(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            },
            background = MaterialTheme.colorScheme.primary
        )
    } else {
        null
    }

    SwipeableActionsBox(
        modifier = modifier,
        startActions = if (!isFromCurrentUser && replyAction != null) listOf(replyAction) else emptyList(),
        endActions = if (isFromCurrentUser && replyAction != null) listOf(replyAction) else emptyList(),
        swipeThreshold = 80.dp
    ) {
        val bubbleShape = if (isFromCurrentUser) {
            RoundedCornerShape(topStart = 20.dp, topEnd = 4.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
        } else {
            RoundedCornerShape(topStart = 4.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
        }

        val backgroundColor = if (isFromCurrentUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
        val textColor = if (isFromCurrentUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
        val alignment = if (isFromCurrentUser) Alignment.End else Alignment.Start

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalAlignment = alignment
        ) {
            // Display sender's name for group chats
            if (!isFromCurrentUser && uiMessage.shouldShowSenderName) {
                Text(
                    text = uiMessage.senderDisplayName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
                )
            }

            Box(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .clip(bubbleShape)
                    .background(backgroundColor)
                    .combinedClickable(
                        onClick = { /* Handle single click if needed */ },
                        onLongClick = { onLongPress(uiMessage.message.messageId) }
                    )
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {

                    // Display the replied-to message if it exists
                    if (uiMessage.message.repliedToMessageText != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Replying to ${uiMessage.repliedToSenderName ?: "..."}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = uiMessage.message.repliedToMessageText,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    // Main message text
                    Text(text = uiMessage.message.text, color = textColor)
                    Spacer(Modifier.height(4.dp))

                    // Timestamp and receipt indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = formatTimestamp(uiMessage.message.timestamp),
                            style = MaterialTheme.typography.labelMedium,
                            color = textColor.copy(alpha = 0.7f)
                        )
                        // Only show receipt for messages sent by the current user
                        if (isFromCurrentUser) {
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = if (isRead) Icons.Default.DoneAll else Icons.Default.Done,
                                contentDescription = "Message Status",
                                tint = if (isRead) Color.Blue else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Reactions are displayed neatly below the bubble
            if (uiMessage.message.reactions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                ReactionsDisplay(
                    reactions = uiMessage.message.reactions,
                    modifier = Modifier.padding(start = if (isFromCurrentUser) 0.dp else 12.dp)
                )
            }
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
                    fontSize = 28.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onReactionSelected(reaction) }
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun ReactionsDisplay(reactions: Map<String, String>, modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy((-10).dp), // Overlap emojis
        verticalAlignment = Alignment.CenterVertically
    ) {
        val reactionCounts = reactions.values.groupingBy { it }.eachCount()
        items(items = reactionCounts.entries.toList()) { (reactionKey, count) ->
            val reaction = Reaction.fromKey(reactionKey)
            if (reaction != null) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 2.dp,
                    modifier = Modifier.padding(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

