import re
with open("app/src/main/java/com/example/ui/DashboardScreen.kt", "r") as f:
    content = f.read()
# Let's fix todayProfit closing brace
content = content.replace("saleWithItems.items.sumOf { it.qty * ((it.price) - (it.unitCostAtSale ?: 0.0)) }\n    val lowStockCount", "saleWithItems.items.sumOf { it.qty * ((it.price) - (it.unitCostAtSale ?: 0.0)) }\n    }\n\n    val lowStockCount")
content = content.replace("            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = valueColor)\n        }\n}\n", "            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = valueColor)\n        }\n    }\n}\n")
content = content.replace("            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)\n        }\n}\n", "            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)\n        }\n    }\n}\n")
with open("app/src/main/java/com/example/ui/DashboardScreen.kt", "w") as f:
    f.write(content)
