package com.yg.mileage.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yg.mileage.Trip
import kotlinx.coroutines.flow.Flow

data class TripWithVehicle(
    @Embedded
    val trip: TripEntity,
    @Embedded(prefix = "v_")
    val vehicle: VehicleEntity
)

@Dao
interface TripDao {
    @Query("""
        SELECT 
            trips.*,
            vehicles.id AS v_id,
            vehicles.userId AS v_userId,
            vehicles.name AS v_name,
            vehicles.fuelType AS v_fuelType,
            vehicles.createdAt AS v_createdAt,
            vehicles.updatedAt AS v_updatedAt
        FROM trips 
        INNER JOIN vehicles ON trips.vehicleId = vehicles.id 
        WHERE trips.userId = :userId ORDER BY trips.updatedAt DESC
        """)
    fun getAllTripsForUser(userId: String): Flow<List<TripWithVehicle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("SELECT * FROM trips WHERE id = :tripId AND userId = :userId")
    suspend fun getTripById(tripId: String, userId: String): TripEntity?

    @Query("DELETE FROM trips WHERE vehicleId = :vehicleId AND userId = :userId")
    suspend fun deleteTripsByVehicleId(vehicleId: String, userId: String)

    @Query("DELETE FROM trips WHERE userId = :userId")
    suspend fun deleteAllTripsForUser(userId: String)
}

fun List<TripWithVehicle>.toTripList(): List<Trip> {
    return this.map { (trip, vehicle) ->
        Trip(
            id = trip.id,
            vehicleId = trip.vehicleId,
            vehicleName = vehicle.name,
            startMileage = trip.startMileage,
            endMileage = trip.endMileage,
            fuelFilled = trip.fuelFilled,
            tripDistance = trip.tripDistance,
            fuelEfficiency = trip.fuelEfficiency,
            fuelCost = trip.fuelCost,
            fuelPricePerUnit = trip.fuelPricePerUnit,
            currencyId = trip.currencyId,
            status = trip.status,
            createdAt = trip.createdAt,
            updatedAt = trip.updatedAt
        )
    }
}