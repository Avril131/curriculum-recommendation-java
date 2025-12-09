package edu.neu.curriculumRecommendation.mapper.converter;

import edu.neu.curriculumRecommendation.dto.CourseDTO;
import edu.neu.curriculumRecommendation.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseConverter {

    CourseDTO entityToDto(Course course);

    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "prerequisites", ignore = true)
    @Mapping(target = "programRequirements", ignore = true)
    Course dtoToEntity(CourseDTO dto);

    List<CourseDTO> entitiesToDtos(List<Course> courses);
}

