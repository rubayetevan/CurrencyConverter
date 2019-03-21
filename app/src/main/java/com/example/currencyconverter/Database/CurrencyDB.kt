package com.example.currencyconverter.Database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.currencyconverter.Constants.Companion.internal_database_name


@Database(entities = [CurrencyVatModel::class], version = 1, exportSchema = false)
abstract class CurrencyDB:RoomDatabase(){

    abstract fun currencyVatDao(): CurrencyVatDao

    companion object {
        @Volatile
        private var INSTANCE: CurrencyDB? = null

        fun getInstance(context: Context): CurrencyDB =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, CurrencyDB::class.java, internal_database_name).build()
    }


}