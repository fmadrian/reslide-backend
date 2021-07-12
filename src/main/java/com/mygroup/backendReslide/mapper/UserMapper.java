package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.IndividualDto;
import com.mygroup.backendReslide.dto.request.UserRequest;
import com.mygroup.backendReslide.dto.response.UserResponse;
import com.mygroup.backendReslide.model.Individual;
import com.mygroup.backendReslide.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UserMapper{
    @Autowired
    private IndividualMapper individualMapper;

    @Mapping(target = "individual", expression = "java(mapIndividualToEntity(userRequest.getIndividual()))")
    public abstract User mapToEntity (UserRequest userRequest);

    @Mapping(target = "individual", expression = "java(mapIndividualToDto(user.getIndividual()))")
    public abstract UserResponse mapToDto(User user);

    IndividualDto mapIndividualToDto(Individual individual){
        return individualMapper.mapToDto(individual);
    }
    Individual mapIndividualToEntity(IndividualDto individualDto){
        return individualMapper.mapToEntity(individualDto);
    }

}
