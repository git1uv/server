package com.simter.domain.airplane.service;

import com.simter.domain.airplane.dto.AirplaneGetResponseDto;
import com.simter.domain.airplane.dto.AirplanePostRequestDto;
import com.simter.domain.airplane.dto.AirplanePostResponseDto;
import com.simter.domain.airplane.entity.Airplane;
import com.simter.domain.airplane.exception.AirplaneGetException;
import com.simter.domain.airplane.exception.AirplanePostException;
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

    public AirplanePostResponseDto sendAirplane(AirplanePostRequestDto requestDto) {
        List<Member> members = memberRepository.findAll();
        if (members.isEmpty()) {
            throw new AirplanePostException("사용자가 존재하지 않습니다.");
        }

        Random random = new Random();
        Member randomReceiver = members.get(random.nextInt(members.size()));

        Airplane airplane = Airplane.builder()
                .writerName(requestDto.getWriterName())
                .content(requestDto.getContent())
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .receiverId(randomReceiver)
                .build();

        airplaneRepository.save(airplane);

        return new AirplanePostResponseDto("종이 비행기 보내기 성공");
    }

    public AirplaneGetResponseDto getAirplane(Long receiverId) {
        Optional<Airplane> airplaneOpt = airplaneRepository.findFirstByReceiverId_IdOrderByCreatedAtDesc(receiverId);

        if (airplaneOpt.isPresent()) {
            Airplane airplane = airplaneOpt.get();
            AirplaneGetResponseDto.DataDto data = new AirplaneGetResponseDto.DataDto(
                    airplane.getWriterName(),
                    airplane.getContent(),
                    airplane.getCreatedAt().toLocalDate()
            );

            return new AirplaneGetResponseDto("종이 비행기 불러오기 성공", data);
        } else {
            throw new AirplaneGetException("종이 비행기가 없습니다.");
        }
    }
}
