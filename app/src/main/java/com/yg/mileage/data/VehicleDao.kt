package com.yg.mileage.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles WHERE userId = :userId ORDER BY name ASC")
    fun getAllVehiclesForUser(userId: String): Flow<List<VehicleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity)

    @Update
    suspend fun updateVehicle(vehicle: VehicleEntity)

    @Delete
    suspend fun deleteVehicle(vehicle: VehicleEntity)

    @Query("SELECT * FROM vehicles WHERE name = :name AND userId = :userId")
    suspend fun getVehicleByName(name: String, userId: String): VehicleEntity?

    @Query("SELECT * FROM vehicles WHERE id = :id AND userId = :userId")
    suspend fun getVehicleById(id: String, userId: String): VehicleEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM trips WHERE vehicleId = :vehicleId AND userId = :userId LIMIT 1)")
    suspend fun hasTrips(vehicleId: String, userId: String): Boolean

    @Query("DELETE FROM vehicles WHERE userId = :userId")
    suspend fun deleteAllVehiclesForUser(userId: String)
}
