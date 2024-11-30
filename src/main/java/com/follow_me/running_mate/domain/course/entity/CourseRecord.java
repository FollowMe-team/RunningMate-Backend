package com.follow_me.running_mate.domain.course.entity;

import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.global.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.locationtech.jts.geom.LineString;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is null")
public class CourseRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn()
    private Member runner;

    @ManyToOne
    @JoinColumn()
    private Course course;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Duration duration;

    @Column(nullable = false)
    private Double distance;

    @Column(nullable = false)
    private Double averagePace;

    @Column(nullable = false)
    private Integer caloriesBurned;

    @Column(columnDefinition = "geography(LineString, 4326)")
    private LineString path;

    @Builder.Default
    @OneToMany(mappedBy = "record", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseRecordPoint> recordPoints = new ArrayList<>();

    public void addRecordPoint(CourseRecordPoint recordPoint) {
        recordPoints.add(recordPoint);
    }
}
