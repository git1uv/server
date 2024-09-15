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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member userId;

    private LocalDate date;

    @Column(length = 100)
    @ColumnDefault("none")
    private String emotion;

    @Column(length = 300)
    @ColumnDefault("")
    private String diary;

    public void setDiary(String content) {
        this.diary = content;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }
}
