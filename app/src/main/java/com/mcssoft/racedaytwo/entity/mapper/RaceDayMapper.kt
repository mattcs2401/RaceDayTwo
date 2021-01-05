package com.mcssoft.racedaytwo.entity.mapper

import com.mcssoft.racedaytwo.entity.cache.RaceMeetingCacheEntity
import com.mcssoft.racedaytwo.entity.database.RaceMeetingDBEntity
import javax.inject.Inject

/*
  Class to map between the Xml entity model (RaceMeetingDBEntity), and the domain model (RaceMeetingCacheEntity).
 */
class RaceDayMapper @Inject constructor() : IEntityMapper<RaceMeetingDBEntity, RaceMeetingCacheEntity> {

    /**
     * Map from the Xml entity model to the domain model.
     * @param entity: The entity model.
     * @return A domain model entity.
     */
    override fun mapFromEntity(entity: RaceMeetingDBEntity): RaceMeetingCacheEntity {
        val raceDayCacheEntity = RaceMeetingCacheEntity()

        raceDayCacheEntity.id = entity.id
        raceDayCacheEntity.mtgId = entity.mtgId
        raceDayCacheEntity.weatherChanged = entity.weatherChanged
        raceDayCacheEntity.meetingCode = entity.meetingCode
        raceDayCacheEntity.venueName = entity.venueName
        raceDayCacheEntity.hiRaceNo = entity.hiRaceNo
        raceDayCacheEntity.meetingType = entity.meetingType
        raceDayCacheEntity.trackChanged = entity.trackChanged
        raceDayCacheEntity.nextRaceNo = entity.nextRaceNo
        raceDayCacheEntity.sortOrder = entity.sortOrder
        raceDayCacheEntity.abandoned = entity.abandoned
        raceDayCacheEntity.meta = false

        return raceDayCacheEntity
    }

    /**
     * Map from the domain model to the Xml entity model.
     * @param domain: The domain model.
     * @return An Xml entity model.
     */
    override fun mapToEntity(domain: RaceMeetingCacheEntity): RaceMeetingDBEntity {
        val raceDayXmlEntity = RaceMeetingDBEntity()

        raceDayXmlEntity.id = domain.id
        raceDayXmlEntity.mtgId = domain.mtgId
        raceDayXmlEntity.weatherChanged = domain.weatherChanged
        raceDayXmlEntity.meetingCode = domain.meetingCode
        raceDayXmlEntity.venueName = domain.venueName
        raceDayXmlEntity.hiRaceNo = domain.hiRaceNo
        raceDayXmlEntity.meetingType = domain.meetingType
        raceDayXmlEntity.trackChanged = domain.trackChanged
        raceDayXmlEntity.nextRaceNo = domain.nextRaceNo
        raceDayXmlEntity.sortOrder = domain.sortOrder
        raceDayXmlEntity.abandoned = domain.abandoned

        return raceDayXmlEntity
    }

    /**
     * Map from a list of Xml entity models to a list of domain entity models.
     */
    fun mapFromEntityList(entities: List<RaceMeetingDBEntity>): List<RaceMeetingCacheEntity> {
        return entities.map { mapFromEntity(it) }
    }
}