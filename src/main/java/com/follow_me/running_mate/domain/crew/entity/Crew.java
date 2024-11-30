package com.follow_me.running_mate.domain.crew.entity;

import com.follow_me.running_mate.domain.enums.Ranking;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.global.common.BaseEntity;
import jakarta.persistence.*;
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
public class Crew extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    public void update(String name, String shortDescription, String detailDescription, String openChatUrl) {
        this.name = name;
        this.shortDescription = shortDescription;
        this.detailDescription = detailDescription;
        this.openChatUrl = openChatUrl;
    }

    public void setLeader(Member newLeader) {
        this.leader = newLeader;
    }

    public boolean canJoin(Member member) {
        int memberRank = getRankingValue(member.getRanking());
        int requiredRank = getRankingValue(this.ranking);
        return memberRank >= requiredRank;
    }

    private int getRankingValue(Ranking ranking) {
        return switch (ranking) {
            case BRONZE -> 1;
            case SILVER -> 2;
            case GOLD -> 3;
            case PLATINUM -> 4;
            case DIAMOND -> 5;
            case ELITE -> 6;
            case LEGEND -> 7;
            default -> 0; // 유효하지 않은 경우
        };
    }
    //TODO: 크루 조건 엔티티에 추가하기
}
