package com.mcssoft.racedaytwo.entity.mapper

import com.mcssoft.racedaytwo.entity.cache.RunnerCacheEntity
import com.mcssoft.racedaytwo.entity.database.RunnerDBEntity

/*
  Utility class to map between the network entity model RunnerDBEntity, and the domain model
  RunnerCacheEntity.
 */
class RunnerMapper : IRunnerMapper<RunnerDBEntity, RunnerCacheEntity> {
/* Thought about making this a singleton but sure there would be any real benefit. */

    /**
     * Map from the database entity model to the domain model.
     * @param entity: The database entity model.
     * @return A domain entity model.
     */
    override fun mapFromRunnerEntity(entity: RunnerDBEntity): RunnerCacheEntity {
        return RunnerCacheEntity().apply {
            id = entity.id
            raceId = entity.raceId
            raceTime = entity.raceTime
            runnerNo = entity.runnerNo
            runnerName = entity.runnerName
            barrier = entity.barrier
            scratched = entity.scratched
            selected = entity.selected
        }
    }

    /**
     * Map from the domain model to the network entity model.
     * @param domain: The domain entity model.
     * @return A database entity model.
     */
    override fun mapToRunnerEntity(domain: RunnerCacheEntity): RunnerDBEntity {
        return RunnerDBEntity().apply {
            id = domain.id
            raceId = domain.raceId
            raceTime = domain.raceTime
            runnerNo = domain.runnerNo
            runnerName = domain.runnerName
            barrier = domain.barrier
            scratched = domain.scratched
            selected = domain.selected
        }
    }

}
