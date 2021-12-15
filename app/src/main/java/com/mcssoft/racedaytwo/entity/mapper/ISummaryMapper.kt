package com.mcssoft.racedaytwo.entity.mapper

interface ISummaryMapper <Entity, Domain> {
    /**
     * Take the entity object and return a domain object.
     */
    fun mapFromSummaryEntity(entity: Entity): Domain

    /**
     * Take the domain object and return an entity object.
     */
    fun mapToSummaryEntity(domain: Domain): Entity
}