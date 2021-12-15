package com.mcssoft.racedaytwo.entity.mapper

import com.mcssoft.racedaytwo.entity.cache.MeetingCacheEntity
import com.mcssoft.racedaytwo.entity.database.MeetingDBEntity

/*
  Utility class to map between the network entity model MeetingDBEntity, and the domain model
  MeetingCacheEntity.
 */
class MeetingMapper : IMeetingMapper<MeetingDBEntity, MeetingCacheEntity> {
/* Thought about making this a singleton but sure there would be any real benefit. */

    /**
     * Map from the network entity model to the domain model.
     * @param entity: The entity model.
     * @return A domain model entity.
     */
    override fun mapFromMeetingEntity(entity: MeetingDBEntity): MeetingCacheEntity {
        return MeetingCacheEntity().apply {
            id = entity.id
            meetingId = entity.meetingId
            meetingCode = entity.meetingCode
            venueName = entity.venueName
            meetingType = entity.meetingType
            abandoned = entity.abandoned
            hiRaceNo = entity.hiRaceNo
            isExpanded = false                     // specific to the domain model.
            meetingDate = entity.meetingDate
            meetingTime = entity.meetingTime
            trackDesc = entity.trackDesc
            trackCond = entity.trackCond
            weatherDesc = entity.weatherDesc
        }
    }

    /**
     * Map from the domain model to the network entity model.
     * @param domain: The domain model.
     * @return A database model entity.
     */
    override fun mapToMeetingEntity(domain: MeetingCacheEntity): MeetingDBEntity {
        return MeetingDBEntity().apply {
            id = domain.id
            meetingId = domain.meetingId
            meetingCode = domain.meetingCode
            venueName = domain.venueName
            meetingType = domain.meetingType
            abandoned = domain.abandoned
            hiRaceNo = domain.hiRaceNo
            meetingDate = domain.meetingDate
            meetingTime = domain.meetingTime
            trackDesc = domain.trackDesc
            trackCond = domain.trackCond
            weatherDesc = domain.weatherDesc
        }
    }

    /**
     * Map from a list of Xml entity models to a list of domain entity models.
     */
    fun mapFromEntityList(entities: List<MeetingDBEntity>): List<MeetingCacheEntity> {
        return entities.map { entity -> mapFromMeetingEntity(entity) }
    }
}