package com.simter.domain.calendar.entity;


import com.simter.domain.member.entity.Member;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Calendars {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private Member userId;

    private LocalDate date;

    @Column(length = 100)
    @Builder.Default
    private String emotion = "none";

    @Column(length = 300)
    @Builder.Default
    private String diary = "";

    public void setDiary(String content) {
        this.diary = content;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }
}
