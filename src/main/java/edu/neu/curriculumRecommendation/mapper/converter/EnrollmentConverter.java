package edu.neu.curriculumRecommendation.mapper.converter;

import edu.neu.curriculumRecommendation.dto.EnrollmentDTO;
import edu.neu.curriculumRecommendation.entity.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnrollmentConverter {

    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "course.id", target = "courseId")
    EnrollmentDTO entityToDto(Enrollment enrollment);

    @Mapping(target = "student", ignore = true)
    @Mapping(target = "course", ignore = true)
    Enrollment dtoToEntity(EnrollmentDTO dto);

    List<EnrollmentDTO> entitiesToDtos(List<Enrollment> enrollments);
}

