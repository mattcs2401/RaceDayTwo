package com.mcssoft.racedaytwo.events

/**
 * Generic class that implements a ResultMessage type Event.
 * @param res: A value representing the result of the event.
 * @param msg: An optional message (or blank).
 */
class EventResultMessage(private val res: Int, private var msg: String = "") {

    val result: Int
        get() = res

    val message: String
        get() = msg

    // TBA - other fields as req.
}
