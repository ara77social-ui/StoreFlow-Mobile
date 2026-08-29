import re
with open("app/src/main/java/com/example/ui/ProductsScreen.kt", "r") as f:
    c = f.read()
# Let's replace the whole line:
c = re.sub(r'viewModel\.addProduct.*?emoji\)\)', 'viewModel.addProduct(com.example.data.ProductEntity(name = name, price = price, cost = cost ?: 0.0, stock = stock, emoji = emoji, category = null, packUnit = null, packSize = null, packPrice = null, createdAt = java.util.Date().toString()))', c)
with open("app/src/main/java/com/example/ui/ProductsScreen.kt", "w") as f:
    f.write(c)
