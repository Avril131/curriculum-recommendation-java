package edu.neu.curriculumRecommendation.mapper.converter;

import edu.neu.curriculumRecommendation.dto.StudentDTO;
import edu.neu.curriculumRecommendation.vo.request.StudentCreateRequestVO;
import edu.neu.curriculumRecommendation.vo.request.StudentUpdateRequestVO;
import edu.neu.curriculumRecommendation.vo.response.StudentResponseVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Student VO Converter
 * Converts between VO (Request/Response) and DTO
 */
@Mapper(componentModel = "spring")
public interface StudentVOConverter {

    /**
     * Convert StudentCreateRequestVO to StudentDTO
     *
     * @param requestVO Create request VO
     * @return StudentDTO
     */
    StudentDTO requestToDto(StudentCreateRequestVO requestVO);

    /**
     * Convert StudentUpdateRequestVO to StudentDTO
     *
     * @param requestVO Update request VO
     * @return StudentDTO
     */
    StudentDTO requestToDto(StudentUpdateRequestVO requestVO);

    /**
     * Convert StudentDTO to StudentResponseVO
     *
     * @param dto Student DTO
     * @return StudentResponseVO
     */
    StudentResponseVO dtoToResponse(StudentDTO dto);

    /**
     * Convert list of StudentDTO to list of StudentResponseVO
     *
     * @param dtos List of StudentDTO
     * @return List of StudentResponseVO
     */
    List<StudentResponseVO> dtosToResponses(List<StudentDTO> dtos);
}

