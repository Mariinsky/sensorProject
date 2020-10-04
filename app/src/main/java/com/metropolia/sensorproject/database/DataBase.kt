package com.metropolia.sensorproject.database

import android.content.Context
import androidx.room.*
import com.metropolia.sensorproject.services.Weather
import java.time.LocalDateTime
import java.util.*

@Entity
data class DayActivity (
    @PrimaryKey(autoGenerate = true)
    var date: Date? = null,
    var Steps: Int? = null,
    var timer: Long? = null,
    var weather: String? = null
)

@Dao
interface ActivityDao {
    @Query("SELECT * FROM dayactivity")
    fun getAll(): MutableList<DayActivity>

    @Query("SELECT * FROM dayactivity ORDER BY date DESC LIMIT :limit")
    fun getLimitedActivities(limit: Int): MutableList<DayActivity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dayActivity: DayActivity)
}

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