/*
 * MyMileage – Your Smart Vehicle Mileage Tracker
 * Copyright (C) 2026 Yojit Ghadi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.yg.mileage.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {
    @Query("SELECT * FROM exchange_rates WHERE baseCurrencyCode = :baseCurrency ORDER BY year DESC, month DESC")
    fun getExchangeRatesForBase(baseCurrency: String): Flow<List<ExchangeRateEntity>>

    @Query("SELECT * FROM exchange_rates WHERE baseCurrencyCode = :baseCurrency AND targetCurrencyCode = :targetCurrency ORDER BY year DESC, month DESC")
    fun getExchangeRatesForPair(baseCurrency: String, targetCurrency: String): Flow<List<ExchangeRateEntity>>

    @Query("SELECT * FROM exchange_rates WHERE baseCurrencyCode = :baseCurrency AND targetCurrencyCode = :targetCurrency AND year = :year AND month = :month LIMIT 1")
    suspend fun getExchangeRate(baseCurrency: String, targetCurrency: String, year: Int, month: Int): ExchangeRateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRate(exchangeRate: ExchangeRateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRates(exchangeRates: List<ExchangeRateEntity>)

    @Query("DELETE FROM exchange_rates WHERE baseCurrencyCode = :baseCurrency")
    suspend fun deleteExchangeRatesForBase(baseCurrency: String)

    @Query("DELETE FROM exchange_rates")
    suspend fun deleteAllExchangeRates()

    @Query("SELECT COUNT(*) FROM exchange_rates WHERE baseCurrencyCode = :baseCurrency")
    suspend fun getExchangeRateCount(baseCurrency: String): Int
}
