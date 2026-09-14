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

import android.util.Log
import com.yg.mileage.ui.vehiclesScreen.viewModel.ExchangeRate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

class ExchangeRateApiService {
    companion object {
        private const val TAG = "ExchangeRateApiService"
        private const val BASE_URL = "https://api.frankfurter.dev/v2"

        private val COMMON_CURRENCIES = listOf(
            "USD", "EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "CNY", "INR", "MXN",
            "BRL", "RUB", "KRW", "SGD", "HKD", "NOK", "SEK", "DKK", "NZD", "ZAR",
            "AED", "AFN", "ALL", "AMD", "ANG", "AOA", "ARS", "AWG", "AZN", "BAM",
            "BBD", "BDT", "BGN", "BHD", "BIF", "BMD", "BND", "BOB", "BSD", "BTN",
            "BWP", "BYN", "BZD", "CDF", "CLP", "COP", "CRC", "CUP", "CVE", "CZK",
            "DJF", "DOP", "DZD", "EGP", "ERN", "ETB", "FJD", "FKP", "GEL", "GGP",
            "GHS", "GIP", "GMD", "GNF", "GTQ", "GYD", "HNL", "HRK", "HTG", "HUF",
            "IDR", "ILS", "IMP", "IQD", "IRR", "ISK", "JEP", "JMD", "JOD", "KES",
            "KGS", "KHR", "KMF", "KPW", "KWD", "KYD", "KZT", "LAK", "LBP", "LKR",
            "LRD", "LSL", "LYD", "MAD", "MDL", "MGA", "MKD", "MMK", "MNT", "MOP",
            "MRU", "MUR", "MVR", "MWK", "MYR", "MZN", "NAD", "NGN", "NIO", "NPR",
            "OMR", "PAB", "PEN", "PGK", "PHP", "PKR", "PLN", "PYG", "QAR", "RON",
            "RSD", "RWF", "SAR", "SBD", "SCR", "SDG", "SHP", "SLL", "SOS", "SRD",
            "SSP", "STN", "SYP", "SZL", "THB", "TJS", "TMT", "TND", "TOP", "TRY",
            "TTD", "TWD", "TZS", "UAH", "UGX", "UYU", "UZS", "VES", "VND", "VUV",
            "WST", "XAF", "XCD", "XOF", "XPF", "YER", "ZMW", "ZWL"
        )

        suspend fun fetchExchangeRatesForBaseCurrency(baseCurrencyCode: String, monthsBack: Int = 12): List<ExchangeRate> = withContext(Dispatchers.IO) {
            val exchangeRates = mutableListOf<ExchangeRate>()
            val calendar = Calendar.getInstance()
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            val quotes = COMMON_CURRENCIES.filter { it != baseCurrencyCode }.joinToString(",")
            if (quotes.isEmpty()) return@withContext exchangeRates

            for (i in 0 until monthsBack) {
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH) + 1
                val date = LocalDate.of(year, month, 1)
                val dateStr = date.format(dateFormatter)

                try {
                    val url = URL("$BASE_URL/rates?date=$dateStr&base=$baseCurrencyCode&quotes=$quotes")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 10000
                    connection.readTimeout = 10000

                    val responseCode = connection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response = connection.inputStream.bufferedReader().readText()
                        val jsonArray = JSONArray(response)

                        for (j in 0 until jsonArray.length()) {
                            val rateObj = jsonArray.getJSONObject(j)
                            val quote = rateObj.getString("quote")
                            val rate = rateObj.getDouble("rate")
                            val id = ExchangeRateEntity.generateId(baseCurrencyCode, quote, year, month)
                            exchangeRates.add(
                                ExchangeRate(
                                    id = id,
                                    baseCurrencyCode = baseCurrencyCode,
                                    targetCurrencyCode = quote,
                                    rate = rate,
                                    year = year,
                                    month = month,
                                    lastUpdated = Date()
                                )
                            )
                        }
                    } else {
                        Log.w(TAG, "Failed to fetch rates for $dateStr: HTTP $responseCode")
                    }
                    connection.disconnect()
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching rates for $dateStr", e)
                }

                calendar.add(Calendar.MONTH, -1)
            }

            exchangeRates
        }

        suspend fun fetchLatestExchangeRates(baseCurrencyCode: String): Map<String, Double> = withContext(Dispatchers.IO) {
            val rates = mutableMapOf<String, Double>()
            try {
                val quotes = COMMON_CURRENCIES.filter { it != baseCurrencyCode }.joinToString(",")
                if (quotes.isEmpty()) return@withContext rates

                val url = URL("$BASE_URL/rates?base=$baseCurrencyCode&quotes=$quotes")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val jsonArray = JSONArray(response)

                    for (i in 0 until jsonArray.length()) {
                        val rateObj = jsonArray.getJSONObject(i)
                        val quote = rateObj.getString("quote")
                        val rate = rateObj.getDouble("rate")
                        rates[quote] = rate
                    }
                }
                connection.disconnect()
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching latest rates", e)
            }
            rates
        }
    }
}
