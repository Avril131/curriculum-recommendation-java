package edu.neu.curriculumRecommendation.mapper.converter;

import edu.neu.curriculumRecommendation.dto.EnrollmentDTO;
import edu.neu.curriculumRecommendation.vo.request.EnrollmentRequestVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnrollmentVOConverter {

    EnrollmentDTO requestToDto(EnrollmentRequestVO requestVO);

    List<EnrollmentDTO> requestsToDtos(List<EnrollmentRequestVO> requestVOs);
}

