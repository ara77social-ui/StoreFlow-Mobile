package com.example.data

import kotlinx.coroutines.flow.Flow

class StoreRepository(private val storeDao: StoreDao) {
    val allProducts: Flow<List<ProductEntity>> = storeDao.getAllProducts()
    val allSales: Flow<List<SaleWithItems>> = storeDao.getAllSalesWithItems()
    val allCustomers: Flow<List<CustomerEntity>> = storeDao.getAllCustomers()
    val allLedgers: Flow<List<LedgerEntity>> = storeDao.getAllLedgers()
    val allExpenses: Flow<List<ExpenseEntity>> = storeDao.getAllExpenses()
    val allWastages: Flow<List<WastageEntity>> = storeDao.getAllWastages()

    suspend fun insertProduct(product: ProductEntity) = storeDao.insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = storeDao.updateProduct(product)
    suspend fun deleteProduct(id: String) = storeDao.deleteProductById(id)
    suspend fun getProductById(id: String): ProductEntity? = storeDao.getProductById(id)

    suspend fun insertSaleTransaction(sale: SaleEntity, items: List<SaleItemEntity>, productsToUpdate: List<ProductEntity>, ledger: LedgerEntity?) {
        storeDao.insertSale(sale)
        storeDao.insertSaleItems(items)
        productsToUpdate.forEach { storeDao.updateProduct(it) }
        if (ledger != null) {
            storeDao.insertLedger(ledger)
        }
    }

    suspend fun insertCustomer(customer: CustomerEntity) = storeDao.insertCustomer(customer)
    suspend fun updateCustomer(customer: CustomerEntity) = storeDao.updateCustomer(customer)
    suspend fun deleteCustomer(id: String) = storeDao.deleteCustomerById(id)

    suspend fun insertLedger(ledger: LedgerEntity) = storeDao.insertLedger(ledger)

    suspend fun insertExpense(expense: ExpenseEntity) = storeDao.insertExpense(expense)
    suspend fun updateExpense(expense: ExpenseEntity) = storeDao.updateExpense(expense)
    suspend fun deleteExpense(id: String) = storeDao.deleteExpenseById(id)

    suspend fun insertWastage(wastage: WastageEntity, updatedProduct: ProductEntity) {
        storeDao.insertWastage(wastage)
        storeDao.updateProduct(updatedProduct)
    }
}
