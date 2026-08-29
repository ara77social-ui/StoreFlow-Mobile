import re

def fix_screen(path):
    with open(path, "r") as f:
        code = f.read()

    code = code.replace("GlassCard {", "GlassCard(isDarkMode = isDarkMode) {")
    code = code.replace("GlassCard(modifier =", "GlassCard(isDarkMode = isDarkMode, modifier =")
    code = code.replace("GlassCard(onClick =", "GlassCard(isDarkMode = isDarkMode, onClick =")

    with open(path, "w") as f:
        f.write(code)
    print(path, "updated")

fix_screen("app/src/main/java/com/example/ui/SettingsScreen.kt")
fix_screen("app/src/main/java/com/example/ui/CustomersScreen.kt")
