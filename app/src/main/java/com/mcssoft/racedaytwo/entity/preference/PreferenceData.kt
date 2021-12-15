package com.mcssoft.racedaytwo.entity.preference

import android.os.Parcel
import android.os.Parcelable

/**
 * Class that holds the data used to establish the actual values of the user selectable preferences
 * when the preference screen is displayed. Data comes from the datastore preferences for user
 * selectable preferences.
 */
class PreferenceData() : Parcelable {

    // For the 'Use Cache' preference.
    private var _useCache: Boolean = false
    var useCache
        get() = _useCache
        set(value) {
            _useCache = value
        }

    constructor(parcel: Parcel) : this() {
        _useCache = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (_useCache) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<PreferenceData> {
        override fun createFromParcel(parcel: Parcel): PreferenceData {
            return PreferenceData(parcel)
        }

        override fun newArray(size: Int): Array<PreferenceData?> {
            return arrayOfNulls(size)
        }
    }
}