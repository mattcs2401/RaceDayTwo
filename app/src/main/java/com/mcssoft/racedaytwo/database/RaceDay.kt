package com.mcssoft.racedaytwo.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mcssoft.racedaytwo.entity.database.*
import com.mcssoft.racedaytwo.entity.database.CountryDataDBEntity
import com.mcssoft.racedaytwo.utility.CountryData

@Database(entities =
   [MeetingDBEntity::class,
    RaceDBEntity::class,
    RunnerDBEntity::class,
    SummaryDBEntity::class,
    CountryDataDBEntity::class],
    version = 1,
    exportSchema = false)
abstract class RaceDay : RoomDatabase() {

    internal abstract fun raceDayDao(): IRaceDayDAO
    internal abstract fun firstRunDao(): IFirstRunDAO

    companion object {
        @Volatile
        private var instance: RaceDay? = null

        fun getDatabase(context: Application): RaceDay {
            return instance ?: Room
                .databaseBuilder(context,
                    RaceDay::class.java, "race_day.db")
                .addCallback(object: Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CountryData.insertCountryData(context)
                    }
                })
                .build()
                .also { instance = it }
        }
    }

}

/* Assorted doco:
 https://medium.com/androiddevelopers/7-pro-tips-for-room-fbadea4bfbd1
 https://developer.android.com/training/data-storage/room
 https://medium.com/androiddevelopers/7-pro-tips-for-room-fbadea4bfbd1#829a
 https://proandroiddev.com/sqlite-triggers-android-room-2e7120bb3e3a
*/
