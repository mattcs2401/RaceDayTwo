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
        val raceMeetingCacheEntity = MeetingCacheEntity()
        raceMeetingCacheEntity.id = entity.id
        raceMeetingCacheEntity.mtgId = entity.mtgId
        raceMeetingCacheEntity.meetingCode = entity.meetingCode
        raceMeetingCacheEntity.venueName = entity.venueName
        raceMeetingCacheEntity.meetingType = entity.meetingType
        raceMeetingCacheEntity.abandoned = entity.abandoned
        raceMeetingCacheEntity.hiRaceNo = entity.hiRaceNo
        raceMeetingCacheEntity.meta = false                // specific to the domain model.
        return raceMeetingCacheEntity
    }

    /**
     * Map from the domain model to the network entity model.
     * @param domain: The domain model.
     * @return A database model entity.
     */
    override fun mapToMeetingEntity(domain: MeetingCacheEntity): MeetingDBEntity {
        val raceMeetingDBEntity = MeetingDBEntity()
        raceMeetingDBEntity.id = domain.id
        raceMeetingDBEntity.mtgId = domain.mtgId
        raceMeetingDBEntity.meetingCode = domain.meetingCode
        raceMeetingDBEntity.venueName = domain.venueName
        raceMeetingDBEntity.meetingType = domain.meetingType
        raceMeetingDBEntity.abandoned = domain.abandoned
        raceMeetingDBEntity.hiRaceNo = domain.hiRaceNo
        return raceMeetingDBEntity
    }

    /**
     * Map from a list of Xml entity models to a list of domain entity models.
     */
    fun mapFromEntityList(entities: List<MeetingDBEntity>): List<MeetingCacheEntity> {
        return entities.map { mapFromMeetingEntity(it) }
    }
}