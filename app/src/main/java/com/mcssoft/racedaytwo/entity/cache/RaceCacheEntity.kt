package com.mcssoft.racedaytwo.entity.cache

class RaceCacheEntity {
    var id: Long? = null          // value inserted by Room.

    var mtgId: String = ""        // foreign key for meeting_details table.

    var raceNo: String = ""       //
    var raceTime: String = ""     // YYYY-MM-DDTHH:MM:SS - Note 1.
    var raceName: String = ""     //
    var distance: String = ""     //
    var trackDesc: String = ""    // e.g. Soft
    var trackRating: String = ""  // e.g. 7
}
