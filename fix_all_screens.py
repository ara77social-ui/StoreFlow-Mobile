import re
import os

for root, _, files in os.walk("app/src/main/java/com/example/ui"):
    for file in files:
        if file.endswith("Screen.kt") and file not in ["DashboardScreen.kt", "SettingsScreen.kt", "CustomersScreen.kt"]:
            path = os.path.join(root, file)
            with open(path, "r") as f:
                code = f.read()

            if "GlassCard" in code and "isDarkMode" not in code:
                # Add isDarkMode declaration at the beginning of the Screen Composable
                code = re.sub(
                    r"(@Composable\nfun .*?\{)", 
                    r"\1\n    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle(initialValue = false)", 
                    code, 
                    count=1
                )
                
                # Import collectAsStateWithLifecycle if not present
                if "import androidx.lifecycle.compose.collectAsStateWithLifecycle" not in code:
                    code = code.replace("import androidx.compose.runtime.*", "import androidx.compose.runtime.*\nimport androidx.lifecycle.compose.collectAsStateWithLifecycle")

                code = code.replace("GlassCard {", "GlassCard(isDarkMode = isDarkMode) {")
                code = code.replace("GlassCard(modifier =", "GlassCard(isDarkMode = isDarkMode, modifier =")
                code = code.replace("GlassCard(onClick =", "GlassCard(isDarkMode = isDarkMode, onClick =")

                with open(path, "w") as f:
                    f.write(code)
                print(path, "updated")
