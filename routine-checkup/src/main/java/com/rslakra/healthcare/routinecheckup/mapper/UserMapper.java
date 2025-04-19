package com.rslakra.healthcare.routinecheckup.mapper;

import com.rslakra.healthcare.routinecheckup.dto.UserRequestDto;
import com.rslakra.healthcare.routinecheckup.entity.UserEntity;

/**
 * @author Rohtash Lakra
 * @version 1.0.0
 * @since 04/18/2025 3:14 PM
 */
public class UserMapper extends AbstractMapperImpl<UserEntity, UserRequestDto> {

    /**
     * Converts an <code>Entity</code> to <code>Dto</code> object.
     *
     * @param userEntity
     * @return
     */
    @Override
    public UserRequestDto toDto(UserEntity userEntity) {
        return null;
    }

    /**
     * Converts the <code>Dto</code> to <code>Entity</code> object.
     *
     * @param userRequestDto
     * @return
     */
    @Override
    public UserEntity toEntity(UserRequestDto userRequestDto) {
        return null;
    }
}
