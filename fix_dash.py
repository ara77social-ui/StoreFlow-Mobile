import re

with open("app/src/main/java/com/example/ui/DashboardScreen.kt", "r") as f:
    code = f.read()

code = code.replace("GlassCard(onClick =", "GlassCard(isDarkMode = isDarkMode, onClick =")
code = code.replace("GlassCard(modifier =", "GlassCard(isDarkMode = isDarkMode, modifier =")
code = code.replace("val bgAlpha = if (isDarkMode) 0.76f else 0.78f", "val bgAlpha = if (isDarkMode) 0.15f else 0.6f\n    val borderAlpha = if (isDarkMode) 0.08f else 0.55f")
code = code.replace("frostedGlass(RoundedCornerShape(20.dp), backgroundAlpha = bgAlpha)", "frostedGlass(RoundedCornerShape(20.dp), backgroundAlpha = bgAlpha, borderAlpha = borderAlpha)")

with open("app/src/main/java/com/example/ui/DashboardScreen.kt", "w") as f:
    f.write(code)

print("DashboardScreen.kt updated")
