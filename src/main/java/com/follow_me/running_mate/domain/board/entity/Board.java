package com.follow_me.running_mate.domain.board.entity;

import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.record.entity.RunningRecord;
import com.follow_me.running_mate.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is null")
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn()
    private Member writer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private RunningRecord record;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn()
    private BoardMusic music;


    @Column(nullable = false)
    private String content;
}
