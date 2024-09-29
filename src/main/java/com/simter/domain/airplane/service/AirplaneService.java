package com.simter.domain.airplane.service;

import com.simter.apiPayload.code.status.ErrorStatus;
import com.simter.apiPayload.exception.handler.ErrorHandler;
import com.simter.domain.airplane.converter.AirplaneConverter;
import com.simter.domain.airplane.dto.AirplaneGetResponseDto;
import com.simter.domain.airplane.dto.AirplanePostRequestDto;
import com.simter.domain.airplane.entity.Airplane;
import com.simter.domain.airplane.repository.AirplaneRepository;
import com.simter.domain.member.entity.Member;
import com.simter.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AirplaneService {

    private final AirplaneRepository airplaneRepository;
    private final MemberRepository memberRepository;

    public void sendAirplane(AirplanePostRequestDto requestDto, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 종이비행기를 없는 사용자 중에서 랜덤으로 지정
        List<Member> availableMembers = memberRepository.findAllByHasAirplane(false);
        if (availableMembers.isEmpty()) {
            throw new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }

        Member randomReceiver = availableMembers.get(new Random().nextInt(availableMembers.size()));

        Airplane airplane = AirplaneConverter.convertToEntity(requestDto, randomReceiver, member);
        airplaneRepository.save(airplane);

        randomReceiver.setHasAirplane(true);
        memberRepository.save(randomReceiver);
    }

    @Transactional
    public AirplaneGetResponseDto getAirplane(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Airplane airplane = airplaneRepository.findFirstByReceiverId_IdOrderByCreatedAtDesc(member.getId())
                        .orElseThrow(() -> new ErrorHandler(ErrorStatus.AIRPLANE_NOT_FOUND));
        airplane.setIsRead(true);
        member.setHasAirplane(false);
        return AirplaneConverter.convertToDataDto(airplane);
    }

}
