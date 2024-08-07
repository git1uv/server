package com.simter.domain.airplane.service;

import com.simter.apiPayload.ApiResponse;
import com.simter.apiPayload.code.status.ErrorStatus;
import com.simter.apiPayload.exception.handler.ErrorHandler;
import com.simter.domain.airplane.converter.AirplaneConverter;
import com.simter.domain.airplane.dto.AirplaneGetResponseDto;
import com.simter.domain.airplane.dto.AirplanePostRequestDto;
import com.simter.domain.airplane.dto.AirplanePostResponseDto;
import com.simter.domain.airplane.entity.Airplane;
import com.simter.domain.airplane.repository.AirplaneRepository;
import com.simter.domain.member.entity.Member;
import com.simter.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AirplaneService {

    private final AirplaneRepository airplaneRepository;
    private final MemberRepository memberRepository;

    public void sendAirplane(AirplanePostRequestDto requestDto) {
        // 사용자가 있는지 먼저 확인
        List<Member> availableMembers = memberRepository.findAll().stream()
                .filter(member -> !member.getHasAirplane())
                .toList();

        if (availableMembers.isEmpty()) {
            throw new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }

        // 무작위로 사용자 선택
        Member randomReceiver = availableMembers.get(new Random().nextInt(availableMembers.size()));

        Airplane airplane = Airplane.builder()
                .writerName(requestDto.getWriterName())
                .content(requestDto.getContent())
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .receiverId(randomReceiver)
                .build();

        airplaneRepository.save(airplane);

        randomReceiver.setHasAirplane(true);
        memberRepository.save(randomReceiver);
    }


    public AirplaneGetResponseDto getAirplane(Long receiverId) {
        Airplane airplaneOpt =
                airplaneRepository.findFirstByReceiverId_IdOrderByCreatedAtDesc(receiverId)
                        .orElseThrow(() -> new ErrorHandler(ErrorStatus.AIRPLANE_NOT_FOUND));
        return AirplaneConverter.convertToDataDto(airplaneOpt);
    }

}
