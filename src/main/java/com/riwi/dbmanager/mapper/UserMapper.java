package com.riwi.dbmanager.mapper;

import com.riwi.dbmanager.dto.request.RegisterRequest;
import com.riwi.dbmanager.dto.response.ResponseUserDTO;
import com.riwi.dbmanager.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    //Mapper to convert User to ResponseUserDTO
    ResponseUserDTO toResponseUserDTO(User user);

    User registerUserDTOToUser(RegisterRequest registerRequest);

}
