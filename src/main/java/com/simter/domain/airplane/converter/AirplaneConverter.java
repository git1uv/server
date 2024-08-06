package com.simter.domain.airplane.converter;

import com.simter.domain.airplane.dto.AirplaneGetResponseDto;
import com.simter.domain.airplane.entity.Airplane;

public class AirplaneConverter {

    public static AirplaneGetResponseDto convertToDataDto(Airplane airplane) {
        return new AirplaneGetResponseDto(
                airplane.getWriterName(),
                airplane.getContent(),
                airplane.getCreatedAt().toLocalDate()
        );
    }
}