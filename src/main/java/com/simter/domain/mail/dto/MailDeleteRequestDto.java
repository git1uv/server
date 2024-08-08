package com.simter.domain.mail.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MailDeleteRequestDto {
    private List<Long> mailIds;

}
