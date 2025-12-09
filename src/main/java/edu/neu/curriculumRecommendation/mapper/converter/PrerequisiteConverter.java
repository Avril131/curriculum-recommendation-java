package edu.neu.curriculumRecommendation.mapper.converter;

import edu.neu.curriculumRecommendation.dto.PrerequisiteDTO;
import edu.neu.curriculumRecommendation.entity.Prerequisite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PrerequisiteConverter {

    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "prerequisiteCourse.id", target = "prerequisiteCourseId")
    PrerequisiteDTO entityToDto(Prerequisite prerequisite);

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "prerequisiteCourse", ignore = true)
    Prerequisite dtoToEntity(PrerequisiteDTO dto);

    List<PrerequisiteDTO> entitiesToDtos(List<Prerequisite> prerequisites);
}

