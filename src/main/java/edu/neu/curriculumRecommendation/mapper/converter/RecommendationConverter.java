package edu.neu.curriculumRecommendation.mapper.converter;

import edu.neu.curriculumRecommendation.dto.RecommendationDTO;
import edu.neu.curriculumRecommendation.entity.Recommendation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationConverter {

    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "course.courseCode", target = "courseCode")
    @Mapping(source = "course.courseName", target = "courseName")
    RecommendationDTO entityToDto(Recommendation recommendation);

    @Mapping(target = "student", ignore = true)
    @Mapping(target = "course", ignore = true)
    Recommendation dtoToEntity(RecommendationDTO dto);

    List<RecommendationDTO> entitiesToDtos(List<Recommendation> recommendations);
}

