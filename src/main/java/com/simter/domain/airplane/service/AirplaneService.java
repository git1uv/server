package com.simter.domain.airplane.service;

import com.simter.domain.airplane.converter.AirplaneConverter;
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

        //has_airplane이 false인 사용자만 필터링
        List<Member> availableMembers = members.stream()
                .filter(member -> !member.getHasAirplane())
                .toList();

        if (availableMembers.isEmpty()) {
            throw new AirplanePostException("종이 비행기를 보낼 수 있는 사용자가 없습니다.");
        }

        Random random = new Random();
        Member randomReceiver = availableMembers.get(random.nextInt(availableMembers.size()));

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

        return new AirplanePostResponseDto("종이 비행기 보내기 성공");
    }

    public AirplaneGetResponseDto getAirplane(Long receiverId) {
        Optional<Airplane> airplaneOpt = airplaneRepository.findFirstByReceiverId_IdOrderByCreatedAtDesc(receiverId);

        if (airplaneOpt.isPresent()) {
            Airplane airplane = airplaneOpt.get();
            AirplaneGetResponseDto.DataDto data = AirplaneConverter.convertToDataDto(airplane);
            return new AirplaneGetResponseDto("종이 비행기 불러오기 성공", data);
        } else {
            throw new AirplaneGetException("종이 비행기가 없습니다.");
        }
    }
}
