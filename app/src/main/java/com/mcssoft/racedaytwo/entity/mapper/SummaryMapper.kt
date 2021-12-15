package com.mcssoft.racedaytwo.entity.mapper

import com.mcssoft.racedaytwo.entity.cache.SummaryCacheEntity
import com.mcssoft.racedaytwo.entity.database.SummaryDBEntity

/*
  Utility class to map between the network entity model SummaryDBEntity, and the domain model
  SummaryCacheEntity.
 */
class SummaryMapper : ISummaryMapper<SummaryDBEntity, SummaryCacheEntity> {
/* Thought about making this a singleton but sure there would be any real benefit. */

    /**
     * Map from the database entity model to the domain model.
     * @param entity: The database entity model.
     * @return A domain model entity.
     */
    override fun mapFromSummaryEntity(entity: SummaryDBEntity): SummaryCacheEntity {
        val domain = SummaryCacheEntity()
        domain.id = entity.id
        domain.meetingCode = entity.meetingCode
        domain.venueName = entity.venueName
        domain.raceNo = entity.raceNo
        domain.raceTime = entity.raceTime
        domain.runnerId = entity.runnerId
        domain.runnerNo = entity.runnerNo
        domain.runnerName = entity.runnerName
        domain.elapsed = entity.elapsed
        domain.notified = entity.notified
        return domain
    }

    /**
     * Map from the domain model to the network entity model.
     * @param domain: The domain entity model.
     * @return A database entity model.
     */
    override fun mapToSummaryEntity(domain: SummaryCacheEntity): SummaryDBEntity {
        val entity = SummaryDBEntity()
        entity.id = domain.id
        entity.meetingCode = domain.meetingCode
        entity.venueName = domain.venueName
        entity.raceNo = domain.raceNo
        entity.raceTime = domain.raceTime
        entity.runnerId = domain.runnerId
        entity.runnerNo = domain.runnerNo
        entity.runnerName = domain.runnerName
        entity.elapsed = domain.elapsed
        entity.elapsed = domain.notified
        return entity
    }

    /**
     * Map from a list of database entity models to a list of domain entity models.
     */
    fun mapFromEntityList(entities: List<SummaryDBEntity>): List<SummaryCacheEntity> {
        return entities.map { mapFromSummaryEntity(it) }
    }

}
