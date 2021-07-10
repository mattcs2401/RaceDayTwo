package com.mcssoft.racedaytwo.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mcssoft.racedaytwo.entity.database.MeetingDBEntity
import com.mcssoft.racedaytwo.entity.database.RaceDBEntity

@Database(entities = [MeetingDBEntity::class, RaceDBEntity::class],
    version = 1, exportSchema = false)
abstract class RaceDay : RoomDatabase() {

    internal abstract fun raceDayDetailsDao(): IRaceDayDAO

    companion object {
        @Volatile
        private var instance: RaceDay? = null

        fun getDatabase(context: Application): RaceDay {
            // https://proandroiddev.com/sqlite-triggers-android-room-2e7120bb3e3a
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