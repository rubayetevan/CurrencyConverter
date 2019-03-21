package com.example.currencyconverter.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface CurrencyVatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrencyVatModel(currencyVatModel: CurrencyVatModel)

    @Query("SELECT * FROM CurrencyVatModel")
    fun getAllCurrencyVatModel(): List<CurrencyVatModel>

    @Query("DELETE FROM CurrencyVatModel")
    fun deleteAllCurrencyVatModel()

    @Query("SELECT name FROM CurrencyVatModel ORDER BY name")
    fun getAllCountryList(): List<String>

    @Query("SELECT * FROM CurrencyVatModel WHERE name=:name")
    fun getRatesByCountryName(name:String): List<CurrencyVatModel>


}