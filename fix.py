with open("app/src/main/java/com/example/ui/CartScreen.kt", "r") as f:
    c = f.read()
c = c.replace("import androidx.compose.ui.unit.sp\npackage", "package")
c = c.replace("package com.example.ui\n", "package com.example.ui\n\nimport androidx.compose.ui.unit.sp\n")
with open("app/src/main/java/com/example/ui/CartScreen.kt", "w") as f:
    f.write(c)
