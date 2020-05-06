package net.cassiolandim.kittychallenge.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoriteDatabaseModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg entities: FavoriteDatabaseModel)

    @Delete
    suspend fun delete(vararg entities: FavoriteDatabaseModel)

    @Query("DELETE FROM favorites where 1 = 1")
    suspend fun deleteAll()

    @Transaction
    suspend fun deleteAllAndInsertAll(entities: List<FavoriteDatabaseModel>) {
        deleteAll()
        insertAll(*entities.toTypedArray())
    }

    @Query("SELECT * FROM favorites")
    suspend fun findAll(): List<FavoriteDatabaseModel>
}