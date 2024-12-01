package com.follow_me.running_mate.domain.crew.entity;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.crew.exception.CrewErrorCode;
import com.follow_me.running_mate.global.common.BaseEntity;
import com.follow_me.running_mate.global.error.exception.CustomException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
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
public class CrewSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Crew crew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Course course;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;
    @Column(nullable = false)
    @Builder.Default
    private Integer memberCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer memberMax = 30;
    public void increaseMemberCount() {
        this.memberCount++;
    }

    @Column(nullable = false)
    private String meetingPlace;

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setMemberMax(Integer memberMax) {
        this.memberMax = memberMax;
    }
    public void decreaseMemberCount() {
        if (this.memberCount > 0) {
            this.memberCount--;
        } else {
            throw new CustomException(CrewErrorCode.INVALID_MEMBER_COUNT);
        }
    }

    public void setMeetingPlace(String meetingPlace) {
        this.meetingPlace=meetingPlace;
    }
}
