package net.cassiolandim.kittychallenge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteDatabaseModel (
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "imageId") val imageId: String
)