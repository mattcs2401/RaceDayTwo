package com.mcssoft.racedaytwo.entity.mapper

import com.mcssoft.racedaytwo.entity.cache.RaceCacheEntity
import com.mcssoft.racedaytwo.entity.cache.MeetingCacheEntity
import com.mcssoft.racedaytwo.entity.database.MeetingDBEntity
import com.mcssoft.racedaytwo.entity.database.RaceDBEntity

/*
  Utility class to map between the network entity model MeetingDBEntity, and the domain model
  MeetingCacheEntity.
 */
class RaceMapper : IRaceMapper<RaceDBEntity, RaceCacheEntity> {
/* Thought about making this a singleton but sure there would be any real benefit. */

    /**
     * Map from the network entity model to the domain model.
     * @param entity: The entity model.
     * @return A domain model entity.
     */
    override fun mapFromRaceEntity(entity: RaceDBEntity): RaceCacheEntity {
        val raceCacheEntity = RaceCacheEntity()
        raceCacheEntity.id = entity.id
        raceCacheEntity.mtgId = entity.mtgId
        raceCacheEntity.raceNo = entity.raceNo
        raceCacheEntity.raceName = entity.raceName
        raceCacheEntity.raceTime = entity.raceTime
        raceCacheEntity.distance = entity.distance
        raceCacheEntity.trackDesc = entity.trackDesc
        raceCacheEntity.trackRating = entity.trackRating
        return raceCacheEntity
    }

    /**
     * Map from the domain model to the network entity model.
     * @param domain: The domain model.
     * @return A database model entity.
     */
    override fun mapToRaceEntity(domain: RaceCacheEntity): RaceDBEntity {
        val raceDBEntity = RaceDBEntity()
        raceDBEntity.id = domain.id
        raceDBEntity.mtgId = domain.mtgId
        raceDBEntity.raceNo = domain.raceNo
        raceDBEntity.raceName = domain.raceName
        raceDBEntity.raceTime = domain.raceTime
        raceDBEntity.distance = domain.distance
        raceDBEntity.trackDesc = domain.trackDesc
        raceDBEntity.trackRating = domain.trackRating
        return raceDBEntity
    }

    /**
     * Map from a list of Xml entity models to a list of domain entity models.
     */
    fun mapFromRaceEntityList(entities: List<RaceDBEntity>): List<RaceCacheEntity> {
        return entities.map { entity -> mapFromRaceEntity(entity) }
    }
}