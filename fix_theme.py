import re

# 1. Update GlassComponents.kt
with open("app/src/main/java/com/example/ui/components/GlassComponents.kt", "r") as f:
    glass_code = f.read()

new_app_bg = """
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
"""

new_glass_card = """
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
"""

glass_code = re.sub(r"@Composable\nfun AppBackground.*?\n    }\n}", new_app_bg, glass_code, flags=re.DOTALL)
glass_code = re.sub(r"@Composable\nfun GlassCard.*?\n    Box\(modifier = finalModifier, content = content\)\n}", new_glass_card, glass_code, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/components/GlassComponents.kt", "w") as f:
    f.write(glass_code)

print("GlassComponents.kt updated")
