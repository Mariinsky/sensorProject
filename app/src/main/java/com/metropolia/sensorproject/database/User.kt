package com.metropolia.sensorproject.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User  (
    @PrimaryKey(autoGenerate = true)
    var uid: Long,
    var name: String,
    var weight: Int,
    var height: Int,
    var goal: Int,
    ) {
    override fun toString(): String = "$name : $weight kg $height cm $goal steps"
}