package com.example.ui.components
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
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
    val infiniteTransition = rememberInfiniteTransition(label = "bg_anim")
    
    val phase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase1"
    )

    val phase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase2"
    )

    val phase3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase3"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                // Light mode colors based on Bloom Field gradient (#E2E2E2 base + #1B9FFE + #4AC9FF)
                val bgColor = if (isDarkMode) Color(0xFF0A0F1C) else Color(0xFFE2E2E2)
                
                // Color 1: #1B9FFE (Blue)
                val color1 = if (isDarkMode) Color(0xFF0D4F88).copy(alpha = 0.5f) else Color(0xFF1B9FFE).copy(alpha = 0.45f)
                // Color 2: #1B9FFE (Blue)
                val color2 = if (isDarkMode) Color(0xFF1565C0).copy(alpha = 0.4f) else Color(0xFF1B9FFE).copy(alpha = 0.35f)
                // Color 3: #4AC9FF (Light Blue)
                val color3 = if (isDarkMode) Color(0xFF00796B).copy(alpha = 0.4f) else Color(0xFF4AC9FF).copy(alpha = 0.5f)

                drawRect(bgColor)
                
                val w = size.width
                val h = size.height
                val radius = max(w, h) * 0.7f

                // Center points oscillating using sine and cosine for smooth looping
                // Approximating the React component's positions (35% 65%, 48% 20%, 80% 88%)
                val cx1 = w * 0.35f + cos(phase1) * w * 0.15f
                val cy1 = h * 0.65f + sin(phase1) * h * 0.15f

                val cx2 = w * 0.48f + sin(phase2) * w * 0.2f
                val cy2 = h * 0.20f + cos(phase2) * h * 0.15f
                
                val cx3 = w * 0.80f + cos(phase3) * w * 0.15f
                val cy3 = h * 0.88f + sin(phase3) * h * 0.15f

                // Draw circles with radial gradient
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color1, Color.Transparent),
                        center = Offset(cx1, cy1),
                        radius = radius
                    ),
                    center = Offset(cx1, cy1),
                    radius = radius
                )
                
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color2, Color.Transparent),
                        center = Offset(cx2, cy2),
                        radius = radius * 1.2f
                    ),
                    center = Offset(cx2, cy2),
                    radius = radius * 1.2f
                )
                
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color3, Color.Transparent),
                        center = Offset(cx3, cy3),
                        radius = radius * 0.9f
                    ),
                    center = Offset(cx3, cy3),
                    radius = radius * 0.9f
                )
                
                // Overlay for light mode to mimic the (226,226,226) radial gradient glow
                if (!isDarkMode) {
                    val cx4 = w * 0.67f
                    val cy4 = h * 0.45f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFE2E2E2).copy(alpha = 0.6f), Color.Transparent),
                            center = Offset(cx4, cy4),
                            radius = radius * 1.3f
                        ),
                        center = Offset(cx4, cy4),
                        radius = radius * 1.3f
                    )
                }
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
