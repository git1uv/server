package com.simter.domain.airplane.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class AirplanePostRequestDto {
    @NotNull
    private String writerName;
    @NotNull
    private String content;

}
