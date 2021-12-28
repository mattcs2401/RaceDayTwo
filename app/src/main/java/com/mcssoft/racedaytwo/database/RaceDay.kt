package com.mcssoft.racedaytwo.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mcssoft.racedaytwo.entity.database.MeetingDBEntity
import com.mcssoft.racedaytwo.entity.database.SummaryDBEntity
import com.mcssoft.racedaytwo.entity.database.RaceDBEntity
import com.mcssoft.racedaytwo.entity.database.RunnerDBEntity

@Database(entities =
   [MeetingDBEntity::class,
    RaceDBEntity::class,
    RunnerDBEntity::class,
    SummaryDBEntity::class],
    version = 1,
    exportSchema = false)
abstract class RaceDay : RoomDatabase() {
// https://developer.android.com/training/data-storage/room
// https://medium.com/androiddevelopers/7-pro-tips-for-room-fbadea4bfbd1#829a
// https://proandroiddev.com/sqlite-triggers-android-room-2e7120bb3e3a

    internal abstract fun raceDayDao(): IRaceDayDAO

    companion object {
        @Volatile
        private var instance: RaceDay? = null

        fun getDatabase(context: Application): RaceDay {
            return instance ?: Room
                .databaseBuilder(context,
                    RaceDay::class.java, "race_day.db")
//                .addCallback(db_callback)
                .build()
                .also { instance = it }
        }

//        private val db_callback = object : RoomDatabase.Callback() {
//            override fun onCreate(db: SupportSQLiteDatabase) {
//                super.onCreate(db)
//                db.execSQL("TBA")
//            }
//        }
    }
}