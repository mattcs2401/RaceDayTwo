package com.mcssoft.racedaytwo.entity.mapper

interface IRunnerMapper <Entity, Domain> {
    /**
     * Take the entity object and return a domain object.
     */
    fun mapFromRunnerEntity(entity: Entity): Domain

    /**
     * Take the domain object and return an entity object.
     */
    fun mapToRunnerEntity(domain: Domain): Entity
}