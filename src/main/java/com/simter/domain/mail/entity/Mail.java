package com.simter.domain.mail.entity;

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
public class Mail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="mail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false, length = 100)
    private String chatbotType;

    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @Column(nullable = false)
    @Setter
    private Boolean isRead = false;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @Column(nullable = false)
    @ColumnDefault("false")
    @Setter
    private Boolean isStarred = false;


    public void markAsDeleted() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
