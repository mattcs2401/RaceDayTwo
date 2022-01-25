package com.mcssoft.racedaytwo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.mcssoft.racedaytwo.entity.database.CountryDataDBEntity

@Dao
interface IFirstRunDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCountryData(data: List<CountryDataDBEntity>)
}