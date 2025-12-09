package edu.neu.curriculumRecommendation.mapper.converter;

import edu.neu.curriculumRecommendation.dto.StudentDTO;
import edu.neu.curriculumRecommendation.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentConverter {

    @Mapping(source = "user.id", target = "userId")
    StudentDTO entityToDto(Student student);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    Student dtoToEntity(StudentDTO dto);

    List<StudentDTO> entitiesToDtos(List<Student> students);
}

