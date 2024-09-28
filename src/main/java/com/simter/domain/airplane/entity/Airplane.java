package com.simter.domain.airplane.entity;

import com.simter.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Airplane {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="airplane_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member senderId;

    @Column(nullable = false, length = 40)
    private String writerName;

    @Column(length = 200)
    private String content;

    private LocalDateTime createdAt;

    @Column(nullable = false)
    @ColumnDefault("false")
    @Setter
    private Boolean isRead;

}
