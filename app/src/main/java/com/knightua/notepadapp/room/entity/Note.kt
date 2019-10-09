package com.knightua.notepadapp.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "date_of_creation") val dateOfCreation: Long?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}