package com.example.currencyconverter.di

import android.content.Context
import androidx.room.Room
import com.example.currencyconverter.data.dataSource.remote.RatesService
import com.example.currencyconverter.data.dataSource.room.ConverterDatabase
import com.example.currencyconverter.data.dataSource.room.account.dao.AccountDao
import com.example.currencyconverter.data.dataSource.room.transaction.dao.TransactionDao
import com.example.currencyconverter.data.dataSource.remote.RemoteRatesServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ConverterDatabase {
        return Room.databaseBuilder(
            context,
            ConverterDatabase::class.java,
            "converter-db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAccountDao(database: ConverterDatabase): AccountDao {
        return database.accountDao()
    }

    @Provides
    fun provideTransactionDao(database: ConverterDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    @Singleton
    fun provideRatesService(): RatesService {
        return RemoteRatesServiceImpl()
    }
}