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

import android.content.Context
import android.util.Log
import com.yg.mileage.auth.DriveService
import com.yg.mileage.ui.tripsScreen.viewModels.Trip
import com.yg.mileage.ui.vehiclesScreen.viewModel.Currency
import com.yg.mileage.ui.vehiclesScreen.viewModel.ExchangeRate
import com.yg.mileage.ui.vehiclesScreen.viewModel.FuelPrice
import com.yg.mileage.ui.vehiclesScreen.viewModel.FuelType
import com.yg.mileage.ui.vehiclesScreen.viewModel.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class Repository(
    private val database: AppDatabase,
    private val driveService: DriveService
) {
    private val vehicleDao = database.vehicleDao()
    private val tripDao = database.tripDao()
    private val tripGroupDao = database.tripGroupDao()
    private val currencyDao = database.currencyDao()
    private val fuelPriceDao = database.fuelPriceDao()
    private val exchangeRateDao = database.exchangeRateDao()

    // --- VEHICLE ---
    fun getAllVehicles(userId: String): Flow<List<Vehicle>> =
        vehicleDao.getAllVehiclesForUser(userId).map { it.map { e -> e.toVehicle() } }

    fun getVehicleIdsWithTrips(userId: String): Flow<Set<String>> =
        vehicleDao.getVehicleIdsWithTrips(userId).map { it.toSet() }

    suspend fun addVehicle(vehicle: Vehicle, userId: String) {
        vehicleDao.insertVehicle(VehicleEntity.fromVehicle(vehicle, userId))
    }

    suspend fun updateVehicle(vehicle: Vehicle, userId: String) {
        vehicleDao.updateVehicle(VehicleEntity.fromVehicle(vehicle, userId))
    }

    suspend fun deleteVehicle(vehicleId: String, userId: String) {
        val v = vehicleDao.getVehicleById(vehicleId, userId)
        v?.let { vehicleDao.deleteVehicle(it) }
    }

    suspend fun canDeleteVehicle(vehicleId: String, userId: String): Boolean {
        return !vehicleDao.hasTrips(vehicleId, userId)
    }

    // --- TRIPS ---
    fun getAllTrips(userId: String): Flow<List<Trip>> =
        tripDao.getAllTripsForUser(userId).map { it.toTripList() }

    suspend fun addTrip(trip: Trip, userId: String) {
        val tripEntity = TripEntity.fromTrip(trip, userId)
        Log.d("Repository", "Attempting to insert tripEntity into DAO: $tripEntity")
        try {
            tripDao.insertTrip(tripEntity)
            Log.d("Repository", "tripDao.insertTrip called successfully for id: ${tripEntity.id}")
        } catch (e: Exception) {
            Log.e("Repository", "Error inserting tripEntity into DAO for id: ${tripEntity.id}", e)
            throw e
        }
    }

    suspend fun updateTrip(trip: Trip, userId: String) {
        val tripEntity = TripEntity.fromTrip(trip, userId)
        Log.d("Repository", "Attempting to update tripEntity in DAO: $tripEntity")
        try {
            tripDao.updateTrip(tripEntity)
            Log.d("Repository", "tripDao.updateTrip called successfully for id: ${tripEntity.id}")
        } catch (e: Exception) {
            Log.e("Repository", "Error updating tripEntity in DAO for id: ${tripEntity.id}", e)
            throw e
        }
    }

    suspend fun deleteTrip(tripId: String, userId: String) {
        val trip = tripDao.getTripById(tripId, userId)
        trip?.let { tripDao.deleteTrip(it) }
    }

    // --- CURRENCY ---
    fun getAllCurrencies(): Flow<List<Currency>> =
        currencyDao.getAllCurrencies().map { it.map { e -> e.toCurrency() } }

    suspend fun getDefaultCurrency(): Currency? =
        currencyDao.getDefaultCurrency()?.toCurrency()

    suspend fun addCurrency(currency: Currency) {
        currencyDao.insertCurrency(CurrencyEntity.fromCurrency(currency))
    }

    suspend fun updateCurrency(currency: Currency) {
        currencyDao.updateCurrency(CurrencyEntity.fromCurrency(currency))
    }

    suspend fun deleteCurrency(currency: Currency) {
        currencyDao.deleteCurrency(CurrencyEntity.fromCurrency(currency))
    }

    suspend fun setDefaultCurrency(currencyId: String) {
        currencyDao.clearDefaultCurrencies()
        currencyDao.setDefaultCurrency(currencyId)
    }

    // --- FUEL PRICES ---
    fun getAllActiveFuelPrices(): Flow<List<FuelPrice>> =
        fuelPriceDao.getAllActiveFuelPrices().map { it.map { e -> e.toFuelPrice() } }

    suspend fun getLatestFuelPrice(fuelType: FuelType): FuelPrice? =
        fuelPriceDao.getLatestFuelPrice(fuelType)?.toFuelPrice()

    suspend fun addFuelPrice(fuelPrice: FuelPrice) {
        fuelPriceDao.deactivateFuelPrices(fuelPrice.fuelType)
        fuelPriceDao.insertFuelPrice(FuelPriceEntity.fromFuelPrice(fuelPrice))
    }

    suspend fun updateFuelPrice(fuelPrice: FuelPrice) {
        fuelPriceDao.updateFuelPrice(FuelPriceEntity.fromFuelPrice(fuelPrice))
    }

    suspend fun deleteFuelPrice(fuelPrice: FuelPrice) {
        fuelPriceDao.deleteFuelPrice(FuelPriceEntity.fromFuelPrice(fuelPrice))
    }

    // --- TRIP GROUPS ---
    fun getAllTripGroups(userId: String): Flow<List<TripGroupEntity>> {
        return tripGroupDao.getAllTripGroups(userId)
    }

    suspend fun addTripGroup(tripGroup: TripGroupEntity) {
        tripGroupDao.insertTripGroup(tripGroup)
    }

    suspend fun updateTripGroup(tripGroup: TripGroupEntity) {
        tripGroupDao.updateTripGroup(tripGroup)
    }

    suspend fun deleteTripGroup(tripGroup: TripGroupEntity) {
        tripGroupDao.deleteTripGroup(tripGroup)
    }

    suspend fun saveTripGroup(tripGroup: TripGroupEntity) {
        tripGroupDao.insertTripGroup(tripGroup)
    }

    // --- Optional: Google Drive Backup ---
    suspend fun backupTripsToDrive(userId: String, accountEmail: String): Boolean {
        return try {
            val allTrips = getAllTrips(userId).first()
            driveService.saveTripsToDrive(accountEmail, allTrips)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun restoreFromDrive(userId: String, accountEmail: String): Boolean {
        return try {
            val backupData = driveService.retrieveAllDataFromDrive(accountEmail)
            backupData.vehicles?.forEach { vehicle ->
                addVehicle(vehicle, userId)
            }
            backupData.trips?.forEach { trip ->
                addTrip(trip, userId)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    // --- EXCHANGE RATES ---
    fun getExchangeRatesForBase(baseCurrencyCode: String): Flow<List<ExchangeRate>> =
        exchangeRateDao.getExchangeRatesForBase(baseCurrencyCode).map { it.map { e -> e.toExchangeRate() } }

    fun getExchangeRatesForPair(baseCurrencyCode: String, targetCurrencyCode: String): Flow<List<ExchangeRate>> =
        exchangeRateDao.getExchangeRatesForPair(baseCurrencyCode, targetCurrencyCode).map { it.map { e -> e.toExchangeRate() } }

    suspend fun getExchangeRate(baseCurrencyCode: String, targetCurrencyCode: String, year: Int, month: Int): ExchangeRate? =
        exchangeRateDao.getExchangeRate(baseCurrencyCode, targetCurrencyCode, year, month)?.toExchangeRate()

    suspend fun saveExchangeRate(exchangeRate: ExchangeRate) {
        exchangeRateDao.insertExchangeRate(ExchangeRateEntity.fromExchangeRate(exchangeRate))
    }

    suspend fun saveExchangeRates(exchangeRates: List<ExchangeRate>) {
        exchangeRateDao.insertExchangeRates(exchangeRates.map { ExchangeRateEntity.fromExchangeRate(it) })
    }

    suspend fun deleteExchangeRatesForBase(baseCurrencyCode: String) {
        exchangeRateDao.deleteExchangeRatesForBase(baseCurrencyCode)
    }

    suspend fun deleteAllExchangeRates() {
        exchangeRateDao.deleteAllExchangeRates()
    }

    suspend fun getExchangeRateCount(baseCurrencyCode: String): Int {
        return exchangeRateDao.getExchangeRateCount(baseCurrencyCode)
    }

    suspend fun syncExchangeRatesFromApi(baseCurrencyCode: String): Boolean {
        return try {
            val existingCount = exchangeRateDao.getExchangeRateCount(baseCurrencyCode)
            if (existingCount == 0) {
                val rates = ExchangeRateApiService.fetchExchangeRatesForBaseCurrency(baseCurrencyCode, 12)
                if (rates.isNotEmpty()) {
                    exchangeRateDao.insertExchangeRates(rates.map { ExchangeRateEntity.fromExchangeRate(it) })
                    Log.d("Repository", "Synced ${rates.size} exchange rates for $baseCurrencyCode")
                }
                true
            } else {
                Log.d("Repository", "Exchange rates already exist for $baseCurrencyCode, skipping sync")
                true
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error syncing exchange rates", e)
            false
        }
    }

    suspend fun updateExchangeRatesFromApi(baseCurrencyCode: String): Boolean {
        return try {
            deleteExchangeRatesForBase(baseCurrencyCode)
            val rates = ExchangeRateApiService.fetchExchangeRatesForBaseCurrency(baseCurrencyCode, 12)
            if (rates.isNotEmpty()) {
                exchangeRateDao.insertExchangeRates(rates.map { ExchangeRateEntity.fromExchangeRate(it) })
                Log.d("Repository", "Updated ${rates.size} exchange rates for $baseCurrencyCode")
            }
            true
        } catch (e: Exception) {
            Log.e("Repository", "Error updating exchange rates", e)
            false
        }
    }

    companion object {
        fun getRepository(context: Context): Repository {
            val database = AppDatabase.getDatabase(context)
            val driveService = DriveService(context)
            return Repository(database, driveService)
        }
    }
}
