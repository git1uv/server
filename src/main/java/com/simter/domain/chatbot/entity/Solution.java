package com.simter.domain.chatbot.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;



@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "solution_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "counseling_log_id")
    private CounselingLog counselingLog;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isCompleted;

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

}
