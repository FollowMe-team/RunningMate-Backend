package com.follow_me.running_mate.domain.course.entity;

import com.follow_me.running_mate.domain.enums.Difficulty;
import com.follow_me.running_mate.domain.enums.Status;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.global.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is null")
public class Course extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn()
    private Member writer;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(columnDefinition = "geography(Point, 4326)")
    private Point startPoint;

    @Column(columnDefinition = "geography(Point, 4326)")
    private Point endPoint;

    @Column(columnDefinition = "geography(LineString, 4326)")
    private LineString path;

    private Duration duration;

    private Double distance;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String district;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String thumbnailUrl;

    @Setter
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Builder.Default
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseOption> options = new ArrayList<>();

    @Builder.Default
    private Integer runningCount = 0;

    public void updateRunningCount() {
        this.runningCount++;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }

    public void addOption(CourseOption option) {
        this.options.add(option);
    }

    public void addOptions(List<CourseOption> option) {
        this.options.addAll(option);
    }

}
