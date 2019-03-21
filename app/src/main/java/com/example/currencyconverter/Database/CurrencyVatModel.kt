package com.example.currencyconverter.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "CurrencyVatModel", indices = [(Index(value = ["country_code"], unique = true))])
data class CurrencyVatModel(
    @ColumnInfo(name = "name")
    var name: String?="",
    @ColumnInfo(name = "code")
    var code: String?="",
    @ColumnInfo(name = "country_code")
    var country_code: String? = "",
    @ColumnInfo(name = "effective_from")
    var effective_from: String?="",
    @ColumnInfo(name = "parking")
    var parking: Double?=0.0,
    @ColumnInfo(name = "super_reduced")
    var super_reduced: Double?=0.0,
    @ColumnInfo(name = "reduced")
    var reduced: Double?=0.0,
    @ColumnInfo(name = "reduced1")
    var reduced1: Double?=0.0,
    @ColumnInfo(name = "reduced2")
    var reduced2: Double?=0.0,
    @ColumnInfo(name = "standard")
    var standard: Double?=0.0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}