package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.UUID

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String?,
    val price: Double,
    val cost: Double,
    val stock: Int,
    val emoji: String,
    val packUnit: String?,
    val packSize: Int?,
    val packPrice: Double?,
    val createdAt: String
)

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val date: String,
    val datetime: String,
    val subtotal: Double,
    val discount: Double,
    val finalTotal: Double,
    val customerId: String?
)

@Entity(
    tableName = "sale_items",
    foreignKeys = [
        ForeignKey(
            entity = SaleEntity::class,
            parentColumns = ["id"],
            childColumns = ["saleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["saleId"])]
)
data class SaleItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val saleId: String,
    val productId: String,
    val name: String,
    val price: Double,
    val qty: Int,
    val unitCostAtSale: Double?,
    val unit: String,
    val packSize: Int?,
    val packUnit: String?
)

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phone: String?,
    val note: String?,
    val createdAt: String
)

@Entity(
    tableName = "ledgers",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["customerId"])]
)
data class LedgerEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val customerId: String,
    val type: String, // "debt" or "payment"
    val amount: Double,
    val note: String?,
    val date: String,
    val dueDate: String?
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String?,
    val amount: Double,
    val date: String,
    val note: String?
)

@Entity(tableName = "wastages")
data class WastageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productId: String,
    val productName: String,
    val qty: Int,
    val type: String,
    val note: String?,
    val costAtTime: Double,
    val date: String
)
