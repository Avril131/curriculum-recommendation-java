package edu.neu.curriculumRecommendation.mapper.converter;

import edu.neu.curriculumRecommendation.dto.CourseDTO;
import edu.neu.curriculumRecommendation.vo.request.CourseCreateRequestVO;
import edu.neu.curriculumRecommendation.vo.request.CourseUpdateRequestVO;
import edu.neu.curriculumRecommendation.vo.response.CourseResponseVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Course VO Converter
 * Converts between VO (Request/Response) and DTO
 */
@Mapper(componentModel = "spring")
public interface CourseVOConverter {

    /**
     * Convert CourseCreateRequestVO to CourseDTO
     *
     * @param requestVO Create request VO
     * @return CourseDTO
     */
    CourseDTO requestToDto(CourseCreateRequestVO requestVO);

    /**
     * Convert CourseUpdateRequestVO to CourseDTO
     *
     * @param requestVO Update request VO
     * @return CourseDTO
     */
    CourseDTO requestToDto(CourseUpdateRequestVO requestVO);

    /**
     * Convert CourseDTO to CourseResponseVO
     *
     * @param dto Course DTO
     * @return CourseResponseVO
     */
    CourseResponseVO dtoToResponse(CourseDTO dto);

    /**
     * Convert list of CourseDTO to list of CourseResponseVO
     *
     * @param dtos List of CourseDTO
     * @return List of CourseResponseVO
     */
    List<CourseResponseVO> dtosToResponses(List<CourseDTO> dtos);
}

