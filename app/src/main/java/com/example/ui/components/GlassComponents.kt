package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.util.Locale

fun Modifier.frostedGlass(
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundAlpha: Float = 0.05f,
    borderAlpha: Float = 0.1f
): Modifier = this
    .background(Color.White.copy(alpha = backgroundAlpha), shape)
    .border(1.dp, Color.White.copy(alpha = borderAlpha), shape)
    .clip(shape)


@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "scale"
    )
    
    val bgAlpha = if (isDarkMode) 0.08f else 0.6f
    val borderAlpha = if (isDarkMode) 0.08f else 0.55f

    val baseModifier = modifier
        .scale(scale)
        .frostedGlass(RoundedCornerShape(24.dp), backgroundAlpha = bgAlpha, borderAlpha = borderAlpha)
        
    val finalModifier = if (onClick != null) {
        baseModifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    } else {
        baseModifier
    }
    
    Box(modifier = finalModifier, content = content)
}



@Composable
fun AppBackground(isDarkMode: Boolean = true, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val bgColor = if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF8FAFC)
                val c1 = if (isDarkMode) Color(0xFF6366F1).copy(alpha = 0.2f) else Color(0xFF3B82F6).copy(alpha = 0.15f)
                val c2 = if (isDarkMode) Color(0xFFEC4899).copy(alpha = 0.2f) else Color(0xFF8B5CF6).copy(alpha = 0.1f)
                drawRect(bgColor)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(c1, Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = size.width * 0.8f
                    ),
                    center = Offset(0f, 0f),
                    radius = size.width * 0.8f
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(c2, Color.Transparent),
                        center = Offset(size.width, size.height),
                        radius = size.width * 0.8f
                    ),
                    center = Offset(size.width, size.height),
                    radius = size.width * 0.8f
                )
            }
    ) {
        content()
    }
}




@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier, valueColor: Color) {
    GlassCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

fun formatCurrency(amount: Double): String {
    return String.format(Locale.US, "%,.0f Toman", amount)
}
