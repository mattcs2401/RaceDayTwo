package com.mcssoft.racedaytwo.entity.mapper

import com.mcssoft.racedaytwo.entity.cache.RaceCacheEntity
import com.mcssoft.racedaytwo.entity.database.RaceDBEntity

/*
  Utility class to map between the network entity model RaceDBEntity, and the domain model
  RaceCacheEntity.
 */
class RaceMapper : IRaceMapper<RaceDBEntity, RaceCacheEntity> {
/* Thought about making this a singleton but sure there would be any real benefit. */

    /**
     * Map from the network entity model to the domain model.
     * @param entity: The entity model.
     * @return A domain model entity.
     */
    override fun mapFromRaceEntity(entity: RaceDBEntity): RaceCacheEntity {
        return RaceCacheEntity().apply {
            id = entity.id
            mtgId = entity.mtgId!!
            mtgCode = entity.mtgCode
            mtgVenue = entity.mtgVenue
            raceNo = entity.raceNo
            raceName = entity.raceName
            raceTime = entity.raceTime
            distance = entity.distance
        }
    }

    /**
     * Map from the domain model to the network entity model.
     * @param domain: The domain model.
     * @return A database model entity.
     */
    override fun mapToRaceEntity(domain: RaceCacheEntity): RaceDBEntity {
        return RaceDBEntity().apply {
            id = domain.id
            mtgId = domain.mtgId!!
            mtgCode = domain.mtgCode
            mtgVenue = domain.mtgVenue
            raceNo = domain.raceNo
            raceName = domain.raceName
            raceTime = domain.raceTime
            distance = domain.distance
        }
    }

//    /**
//     * Map from a list of Xml entity models to a list of domain entity models.
//     */
//    fun mapFromRaceEntityList(mtgId: String, mCode: String, entities: List<RaceDBEntity>): RaceCacheEntry {
//        val raceCacheEntry = RaceCacheEntry(mtgId, mCode)
//        raceCacheEntry.listing = entities.map { entity -> mapFromRaceEntity(entity) }
//        return raceCacheEntry
//    }

}
