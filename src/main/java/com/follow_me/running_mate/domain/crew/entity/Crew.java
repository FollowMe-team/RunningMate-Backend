package com.follow_me.running_mate.domain.crew.entity;

import com.follow_me.running_mate.domain.crew.dto.request.CrewRequest;
import com.follow_me.running_mate.domain.enums.Ranking;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is null")
public class Crew extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member leader;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String openChatUrl;

    @Column(nullable = false)
    private String profileImageUrl;

    @Column(nullable = false)
    private String detailDescription;

    @Column(nullable = false)
    private String shortDescription;

    @Column(nullable = false)
    @Builder.Default
    private Integer memberCount = 0;

    @Enumerated(EnumType.STRING)
    private Ranking ranking;

    public void increaseMemberCount() {
        this.memberCount++;
    }

    public void decreaseMemberCount() {
        this.memberCount--;
    }
    public void update(CrewRequest.UpdateCrewRequest request) {
        this.name = request.getName();
        this.shortDescription = request.getShortDescription();
        this.detailDescription = request.getDetailDescription();
        this.openChatUrl = request.getOpenChatUrl();
        this.ranking = request.getRanking();
    }

    public boolean canJoin(Member member) {
        int memberRank = getRankingValue(member.getRanking());
        int requiredRank = getRankingValue(this.ranking);
        return memberRank >= requiredRank;
    }

    public int getRankingValue(Ranking ranking) {
        return switch (ranking) {
            case JOGGER -> 1;            // 조깅러
            case RUNNER -> 2;           // 러너
            case RACER -> 3;            // 레이서
            case SPRINTER -> 4;         // 스프린터
            case MARATHONER -> 5;       // 마라토너
            case ULTRA_RUNNER -> 6;     // 울트라 러너
            case IRON_LEGS -> 7;        // 아이언 레그
            case SPEED_DEMON -> 8;      // 스피드 데몬
        };
    }
}
