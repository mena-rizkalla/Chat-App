package com.example.chatapp.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun TypingIndicator() {
    val dot1 by rememberInfiniteTransition(label = "dot1").animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = keyframes { durationMillis = 1200; 0.0f at 0; 1.0f at 300; 1.0f at 800; 0.0f at 1200 }, repeatMode = RepeatMode.Restart))
    val dot2 by rememberInfiniteTransition(label = "dot2").animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = keyframes { durationMillis = 1200; 0.0f at 150; 1.0f at 450; 1.0f at 950; 0.0f at 1200 }, repeatMode = RepeatMode.Restart))
    val dot3 by rememberInfiniteTransition(label = "dot3").animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = keyframes { durationMillis = 1200; 0.0f at 300; 1.0f at 600; 1.0f at 1100; 0.0f at 1200 }, repeatMode = RepeatMode.Restart))

    Row(
        modifier = Modifier.padding(start = 20.dp, bottom = 4.dp, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val dotSize = 8.dp
        val dotColor = MaterialTheme.colorScheme.onSurfaceVariant
        Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor.copy(alpha = dot1)))
        Spacer(modifier = Modifier.width(4.dp))
        Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor.copy(alpha = dot2)))
        Spacer(modifier = Modifier.width(4.dp))
        Box(modifier = Modifier.size(dotSize).clip(CircleShape).background(dotColor.copy(alpha = dot3)))
    }
}