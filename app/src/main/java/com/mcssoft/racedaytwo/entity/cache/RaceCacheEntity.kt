package com.mcssoft.racedaytwo.entity.cache

import android.os.Parcel
import android.os.Parcelable

/**
 * Class that provides the cache entity for a Race.
 * @note: Parcelable implementation used as a RaceCacheEntity is passed as a navigation argument
 *        from the RacesFragment to the RunnersFragment..
 */
class RaceCacheEntity() : Parcelable {
    var id: Long? = null          // Race DB entity id (row value inserted by Room).
    var raceNo: String = ""       // Race number.
    var raceName: String = ""     // Race name.
    var distance: String = ""     // Race distance.
    var raceTime: String = ""     // Race time (as HH:MM).
    var mtgId: Long? = null       // Meeting id (DB row id).
    var mtgCode: String = ""      // Meeting code.
    var mtgVenue: String = ""     // Meeting venue name.

    //<editor-fold default state="collapsed" desc="Region: Parcel">
    constructor(parcel: Parcel) : this() {
        id = parcel.readValue(Long::class.java.classLoader) as? Long
        mtgId = parcel.readValue(Long::class.java.classLoader) as? Long
        mtgCode = parcel.readString()!!
        mtgVenue = parcel.readString()!!
        raceNo = parcel.readString()!!
        raceName = parcel.readString()!!
        distance = parcel.readString()!!
        raceTime = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeValue(mtgId)
        parcel.writeValue(mtgCode)
        parcel.writeValue(mtgVenue)
        parcel.writeValue(raceNo)
        parcel.writeValue(raceName)
        parcel.writeValue(distance)
        parcel.writeValue(raceTime)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<RaceCacheEntity> {
        override fun createFromParcel(parcel: Parcel): RaceCacheEntity {
            return RaceCacheEntity(parcel)
        }

        override fun newArray(size: Int): Array<RaceCacheEntity?> {
            return arrayOfNulls(size)
        }
    }
    //</editor-fold>
}
