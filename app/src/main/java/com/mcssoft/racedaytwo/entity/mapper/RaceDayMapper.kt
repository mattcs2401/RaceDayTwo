package com.mcssoft.racedaytwo.entity.mapper

import com.mcssoft.racedaytwo.entity.cache.RaceMeetingCacheEntity
import com.mcssoft.racedaytwo.entity.database.RaceMeetingDBEntity
import javax.inject.Inject

/*
  Utility class to map between the network entity model RaceMeetingDBEntity, and the domain model
  RaceMeetingCacheEntity.
 */
object RaceDayMapper : IEntityMapper<RaceMeetingDBEntity, RaceMeetingCacheEntity> {

    /**
     * Map from the network entity model to the domain model.
     * @param entity: The entity model.
     * @return A domain model entity.
     */
    override fun mapFromEntity(entity: RaceMeetingDBEntity): RaceMeetingCacheEntity {
        val raceMeetingCacheEntity = RaceMeetingCacheEntity()

        raceMeetingCacheEntity.id = entity.id
        raceMeetingCacheEntity.mtgId = entity.mtgId
        raceMeetingCacheEntity.weatherChanged = entity.weatherChanged
        raceMeetingCacheEntity.meetingCode = entity.meetingCode
        raceMeetingCacheEntity.venueName = entity.venueName
        raceMeetingCacheEntity.hiRaceNo = entity.hiRaceNo
        raceMeetingCacheEntity.meetingType = entity.meetingType
        raceMeetingCacheEntity.trackChanged = entity.trackChanged
        raceMeetingCacheEntity.nextRaceNo = entity.nextRaceNo
        raceMeetingCacheEntity.sortOrder = entity.sortOrder
        raceMeetingCacheEntity.abandoned = entity.abandoned
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
        raceMeetingDBEntity.weatherChanged = domain.weatherChanged
        raceMeetingDBEntity.meetingCode = domain.meetingCode
        raceMeetingDBEntity.venueName = domain.venueName
        raceMeetingDBEntity.hiRaceNo = domain.hiRaceNo
        raceMeetingDBEntity.meetingType = domain.meetingType
        raceMeetingDBEntity.trackChanged = domain.trackChanged
        raceMeetingDBEntity.nextRaceNo = domain.nextRaceNo
        raceMeetingDBEntity.sortOrder = domain.sortOrder
        raceMeetingDBEntity.abandoned = domain.abandoned

        return raceMeetingDBEntity
    }

    /**
     * Map from a list of Xml entity models to a list of domain entity models.
     */
    fun mapFromEntityList(entities: List<RaceMeetingDBEntity>): List<RaceMeetingCacheEntity> {
        return entities.map { mapFromEntity(it) }
    }
}