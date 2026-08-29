with open("app/src/main/java/com/example/ui/components/GlassComponents.kt", "r") as f:
    c = f.read()

# Remove the bad appends
c = c.split("import androidx.compose.foundation.layout.Column")[0]

c = c.replace("import androidx.compose.ui.unit.dp\n", "import androidx.compose.ui.unit.dp\nimport androidx.compose.foundation.layout.Column\nimport androidx.compose.foundation.layout.Spacer\nimport androidx.compose.foundation.layout.height\nimport androidx.compose.foundation.layout.padding\nimport androidx.compose.material3.Text\nimport androidx.compose.material3.MaterialTheme\nimport androidx.compose.ui.text.font.FontWeight\nimport androidx.compose.ui.unit.sp\nimport java.util.Locale\n")

c += """
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
"""

with open("app/src/main/java/com/example/ui/components/GlassComponents.kt", "w") as f:
    f.write(c)
