package edu.neu.curriculumRecommendation.mapper.converter;

import edu.neu.curriculumRecommendation.dto.UserDTO;
import edu.neu.curriculumRecommendation.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserConverter {

    UserDTO entityToDto(User user);

    @Mapping(target = "student", ignore = true)
    @Mapping(target = "password", ignore = true)
    User dtoToEntity(UserDTO dto);

    List<UserDTO> entitiesToDtos(List<User> users);
}

