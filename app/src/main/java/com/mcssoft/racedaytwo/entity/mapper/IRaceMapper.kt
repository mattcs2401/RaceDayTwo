package com.mcssoft.racedaytwo.entity.mapper

interface IRaceMapper <Entity, Domain> {
    /**
     * Take the entity object and return a domain object.
     */
    fun mapFromRaceEntity(entity: Entity): Domain

    /**
     * Take the domain object and return an entity object.
     */
    fun mapToRaceEntity(domain: Domain): Entity
}