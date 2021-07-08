package com.mcssoft.racedaytwo.entity.mapper

import com.mcssoft.racedaytwo.entity.cache.RaceMeetingCacheEntity
import com.mcssoft.racedaytwo.entity.database.RaceMeetingDBEntity

/*
  Utility class to map between the network entity model RaceMeetingDBEntity, and the domain model
  RaceMeetingCacheEntity.
 */
class RaceDayMapper : IEntityMapper<RaceMeetingDBEntity, RaceMeetingCacheEntity> {
/* Thought about making this a singleton but sure there would be any real benefit. */

    /**
     * Map from the network entity model to the domain model.
     * @param entity: The entity model.
     * @return A domain model entity.
     */
    override fun mapFromEntity(entity: RaceMeetingDBEntity): RaceMeetingCacheEntity {
        val raceMeetingCacheEntity = RaceMeetingCacheEntity()
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
     * @return A network entity model.
     */
    override fun mapToEntity(domain: RaceMeetingCacheEntity): RaceMeetingDBEntity {
        val raceMeetingDBEntity = RaceMeetingDBEntity()
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
    fun mapFromEntityList(entities: List<RaceMeetingDBEntity>): List<RaceMeetingCacheEntity> {
        return entities.map { mapFromEntity(it) }
    }
}