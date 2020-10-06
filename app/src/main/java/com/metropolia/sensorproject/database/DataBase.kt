package com.metropolia.sensorproject.database

import android.content.Context
import androidx.room.*
import java.util.*

/**
 * roomSQL database
 * - single table of DayActivity
 * */
@Database( entities = [(DayActivity::class)], version = 1)
@TypeConverters(Converters::class)
abstract class AppDB: RoomDatabase() {
    abstract fun activityDao(): ActivityDao

    companion object {
        private var sInstance: AppDB? = null
        @Synchronized
        fun get(context: Context): AppDB {
            if (sInstance == null) {
                sInstance =
                    Room.databaseBuilder(context.applicationContext,
                        AppDB::class.java, "app.db").build()
            }
            return sInstance!!
        }
    }
}
@Entity
data class DayActivity (
    @PrimaryKey(autoGenerate = true)
    val date: Date,
    val Steps: Int,
    val timer: Long,
    val weather: String?,
    val route: String?,
    val distance: Float,
) {
    val getSteps: String
        get() { return "Steps $Steps"}

    val getDistance: String
        get() { return "Distance: ${distance.toInt()}m"}
}

@Dao
interface ActivityDao {
    @Query("SELECT * FROM dayactivity")
    fun getAll(): MutableList<DayActivity>

    @Query("SELECT * FROM dayactivity ORDER BY date DESC LIMIT :limit")
    fun getLimitedActivities(limit: Int): MutableList<DayActivity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dayActivity: DayActivity)
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}