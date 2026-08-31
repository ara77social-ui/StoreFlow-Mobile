package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StoreViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: StoreRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = StoreRepository(database.storeDao())
    }

    val products: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sales: StateFlow<List<SaleWithItems>> = repository.allSales
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customers: StateFlow<List<CustomerEntity>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ledgers: StateFlow<List<LedgerEntity>> = repository.allLedgers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wastages: StateFlow<List<WastageEntity>> = repository.allWastages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart State: Map of productId -> qty
    private val _cart = MutableStateFlow<Map<String, Int>>(emptyMap())
    val cart: StateFlow<Map<String, Int>> = _cart

    fun addToCart(productId: String, qty: Int = 1) {
        val current = _cart.value.toMutableMap()
        val existing = current[productId] ?: 0
        current[productId] = existing + qty
        _cart.value = current
    }

    fun updateCartQty(productId: String, qty: Int) {
        val current = _cart.value.toMutableMap()
        if (qty <= 0) {
            current.remove(productId)
        } else {
            current[productId] = qty
        }
        _cart.value = current
    }

    fun clearCart() {
        _cart.value = emptyMap()
    }

    fun checkout(discount: Double, customerId: String?, onCredit: Boolean) {
        viewModelScope.launch {
            val cartItems = _cart.value
            if (cartItems.isEmpty()) return@launch

            val productsList = products.value
            val saleItems = mutableListOf<SaleItemEntity>()
            val productsToUpdate = mutableListOf<ProductEntity>()
            var subtotal = 0.0

            val saleId = java.util.UUID.randomUUID().toString()
            val dfDate = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val dfDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            val now = Date()
            val dateStr = dfDate.format(now)
            val datetimeStr = dfDateTime.format(now)

            cartItems.forEach { (productId, qty) ->
                val p = productsList.find { it.id == productId }
                if (p != null) {
                    val price = p.price
                    subtotal += price * qty
                    saleItems.add(
                        SaleItemEntity(
                            saleId = saleId,
                            productId = productId,
                            name = p.name,
                            price = price,
                            qty = qty,
                            unitCostAtSale = p.cost,
                            unit = "piece",
                            packSize = p.packSize,
                            packUnit = p.packUnit
                        )
                    )
                    productsToUpdate.add(p.copy(stock = Math.max(0, p.stock - qty)))
                }
            }

            val finalTotal = subtotal - discount
            val sale = SaleEntity(
                id = saleId,
                date = dateStr,
                datetime = datetimeStr,
                subtotal = subtotal,
                discount = discount,
                finalTotal = finalTotal,
                customerId = customerId
            )

            val ledger = if (onCredit && customerId != null) {
                LedgerEntity(
                    customerId = customerId,
                    type = "debt",
                    amount = finalTotal,
                    note = "Sale Invoice",
                    date = datetimeStr,
                    dueDate = null
                )
            } else null

            repository.insertSaleTransaction(sale, saleItems, productsToUpdate, ledger)
            clearCart()
        }
    }

    // Settings
    private val _storeName = MutableStateFlow("StoreFlow")
    val storeName: StateFlow<String> = _storeName

    private val _ownerName = MutableStateFlow("Owner")
    val ownerName: StateFlow<String> = _ownerName
    
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode
    
    private val _biometricEnabled = MutableStateFlow(
        application.getSharedPreferences("store_prefs", android.content.Context.MODE_PRIVATE).getBoolean("biometric_enabled", false)
    )
    val biometricEnabled: StateFlow<Boolean> = _biometricEnabled

    fun setStoreName(name: String) { _storeName.value = name }
    fun setOwnerName(name: String) { _ownerName.value = name }
    fun setDarkMode(enabled: Boolean) { _isDarkMode.value = enabled }
    fun setBiometric(enabled: Boolean) { 
        _biometricEnabled.value = enabled 
        val prefs = getApplication<Application>().getSharedPreferences("store_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().putBoolean("biometric_enabled", enabled).commit()
    }

    // Repository operations
    fun addProduct(product: ProductEntity) = viewModelScope.launch { repository.insertProduct(product) }
    fun updateProduct(product: ProductEntity) = viewModelScope.launch { repository.updateProduct(product) }
    fun deleteProduct(id: String) = viewModelScope.launch { repository.deleteProduct(id) }

    fun addCustomer(customer: CustomerEntity) = viewModelScope.launch { repository.insertCustomer(customer) }
    fun updateCustomer(customer: CustomerEntity) = viewModelScope.launch { repository.updateCustomer(customer) }
    fun deleteCustomer(id: String) = viewModelScope.launch { repository.deleteCustomer(id) }

    fun addExpense(expense: ExpenseEntity) = viewModelScope.launch { repository.insertExpense(expense) }
    fun updateExpense(expense: ExpenseEntity) = viewModelScope.launch { repository.updateExpense(expense) }
    fun deleteExpense(id: String) = viewModelScope.launch { repository.deleteExpense(id) }

    fun addLedger(ledger: LedgerEntity) = viewModelScope.launch { repository.insertLedger(ledger) }
}
