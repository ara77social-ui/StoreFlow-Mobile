with open("app/src/main/java/com/example/ui/LoginScreen.kt", "r") as f:
    content = f.read()

new_imports = """
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
"""

if "import androidx.compose.ui.platform.LocalLayoutDirection" not in content:
    content = content.replace("import androidx.compose.ui.platform.LocalContext", "import androidx.compose.ui.platform.LocalContext" + new_imports)

content = content.replace(
    "Box(\n        modifier = Modifier\n            .fillMaxSize()",
    "CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {\n    Box(\n        modifier = Modifier\n            .fillMaxSize()"
)

content = content.replace("            }\n        }\n    }\n}", "            }\n        }\n    }\n}\n}")

with open("app/src/main/java/com/example/ui/LoginScreen.kt", "w") as f:
    f.write(content)
