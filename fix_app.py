import re

with open("app/src/main/java/com/example/ui/StoreFlowApp.kt", "r") as f:
    code = f.read()

code = code.replace("AppBackground {", "AppBackground(isDarkMode = isDarkMode) {")

with open("app/src/main/java/com/example/ui/StoreFlowApp.kt", "w") as f:
    f.write(code)

print("StoreFlowApp.kt updated")
