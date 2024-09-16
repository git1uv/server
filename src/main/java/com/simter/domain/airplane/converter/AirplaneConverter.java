package com.simter.domain.airplane.converter;

import com.simter.domain.airplane.dto.AirplaneGetResponseDto;
import com.simter.domain.airplane.dto.AirplanePostRequestDto;
import com.simter.domain.airplane.entity.Airplane;
import com.simter.domain.member.entity.Member;
import java.time.LocalDateTime;

public class AirplaneConverter {

    public static AirplaneGetResponseDto convertToDataDto(Airplane airplane) {
        return new AirplaneGetResponseDto(
                airplane.getWriterName(),
                airplane.getContent(),
                airplane.getCreatedAt().toLocalDate()
        );
    }


    public static Airplane convertToEntity(AirplanePostRequestDto requestDto, Member randomReceiver, Member member) {
        return Airplane.builder()
                .writerName(requestDto.getWriterName())
                .content(requestDto.getContent())
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .receiverId(randomReceiver)
                .senderId(member)
                .build();
    }
}