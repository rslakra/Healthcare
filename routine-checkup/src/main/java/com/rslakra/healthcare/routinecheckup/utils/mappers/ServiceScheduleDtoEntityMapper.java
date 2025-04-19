package com.rslakra.healthcare.routinecheckup.utils.mappers;

import com.rslakra.healthcare.routinecheckup.dto.ServiceDataDto;
import com.rslakra.healthcare.routinecheckup.entity.ServiceScheduleEntity;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = UUID.class)
public interface ServiceScheduleDtoEntityMapper {

    /**
     * @param entity
     * @return
     */
    ServiceDataDto serviceEntityToDto(ServiceScheduleEntity entity);

    /**
     * @param entity
     * @return
     */
    ServiceScheduleEntity serviceDtoToEntity(ServiceDataDto entity);

}
