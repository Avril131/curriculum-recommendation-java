package edu.neu.curriculumRecommendation.mapper.converter;

import edu.neu.curriculumRecommendation.dto.ProgramRequirementDTO;
import edu.neu.curriculumRecommendation.entity.ProgramRequirement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProgramRequirementConverter {

    @Mapping(source = "course.id", target = "courseId")
    ProgramRequirementDTO entityToDto(ProgramRequirement programRequirement);

    @Mapping(target = "course", ignore = true)
    ProgramRequirement dtoToEntity(ProgramRequirementDTO dto);

    List<ProgramRequirementDTO> entitiesToDtos(List<ProgramRequirement> programRequirements);
}

