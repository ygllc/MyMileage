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

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yg.mileage.ui.vehiclesScreen.viewModel.ExchangeRate
import java.util.Date

@Entity(tableName = "exchange_rates")
data class ExchangeRateEntity(
    @PrimaryKey val id: String,
    val baseCurrencyCode: String,
    val targetCurrencyCode: String,
    val rate: Double,
    val year: Int,
    val month: Int,
    val lastUpdated: Date
) {
    fun toExchangeRate(): ExchangeRate {
        return ExchangeRate(
            id = id,
            baseCurrencyCode = baseCurrencyCode,
            targetCurrencyCode = targetCurrencyCode,
            rate = rate,
            year = year,
            month = month,
            lastUpdated = lastUpdated
        )
    }

    companion object {
        fun fromExchangeRate(exchangeRate: ExchangeRate): ExchangeRateEntity {
            return ExchangeRateEntity(
                id = exchangeRate.id,
                baseCurrencyCode = exchangeRate.baseCurrencyCode,
                targetCurrencyCode = exchangeRate.targetCurrencyCode,
                rate = exchangeRate.rate,
                year = exchangeRate.year,
                month = exchangeRate.month,
                lastUpdated = exchangeRate.lastUpdated
            )
        }

        fun generateId(baseCode: String, targetCode: String, year: Int, month: Int): String {
            return "${baseCode}_${targetCode}_${year}_${month}"
        }
    }
}
