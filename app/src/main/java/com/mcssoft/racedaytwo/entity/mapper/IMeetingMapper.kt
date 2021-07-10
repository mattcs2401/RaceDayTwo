package com.mcssoft.racedaytwo.entity.mapper

interface IMeetingMapper <Entity, Domain>{

    /**
     * Take the entity object and return a domain object.
     */
    fun mapFromMeetingEntity(entity: Entity): Domain

    /**
     * Take the domain object and return an entity object.
     */
    fun mapToMeetingEntity(domain: Domain): Entity

}