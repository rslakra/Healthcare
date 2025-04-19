package com.rslakra.healthcare.routinecheckup.mapper;

/**
 * @author Rohtash Lakra
 * @version 1.0.0
 * @since 04/18/2025 3:13 PM
 */
public interface AbstractMapper<Entity, Dto> {

    /**
     * Converts an <code>Entity</code> to <code>Dto</code> object.
     *
     * @param entity
     * @return
     */
    Dto toDto(Entity entity);

    /**
     * Converts the <code>Dto</code> to <code>Entity</code> object.
     *
     * @param dto
     * @return
     */
    Entity toEntity(Dto dto);

}

