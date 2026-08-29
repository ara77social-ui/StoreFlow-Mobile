with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    c = f.read()

c = c.replace("import androidx.compose.runtime.collectAsStatepackage com.example", "package com.example\n\nimport androidx.compose.runtime.collectAsState\n")

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(c)
