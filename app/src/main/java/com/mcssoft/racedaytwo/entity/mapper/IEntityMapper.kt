package com.mcssoft.racedaytwo.entity.mapper

interface IEntityMapper <Entity, Domain>{

    /**
     * Take the entity object and return a domain object.
     */
    fun mapFromEntity(entity: Entity): Domain

    /**
     * Take the domain object and return an entity object.
     */
    fun mapToEntity(domain: Domain): Entity
}